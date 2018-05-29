/*
 * MIT License
 *
 * Copyright 2018 Sabre GLBL Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sabre.oss.yare.serializer.json.mapper.rule;

import com.sabre.oss.yare.common.converter.TypeConverter;
import com.sabre.oss.yare.common.mapper.ByClassRegistry;
import com.sabre.oss.yare.core.model.Expression;
import com.sabre.oss.yare.serializer.json.model.Function;
import com.sabre.oss.yare.serializer.json.model.Operator;
import com.sabre.oss.yare.serializer.json.model.*;
import com.sabre.oss.yare.serializer.json.model.Value;
import com.sabre.oss.yare.serializer.json.model.Values;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import static com.sabre.oss.yare.core.model.ExpressionFactory.*;

class NodeConverter {
    private final ByClassRegistry<Class<?>, Converter<?>> converterRegistry = new ByClassRegistry<>();
    private final TypeConverter typeConverter;

    NodeConverter(TypeConverter typeConverter) {
        this.typeConverter = typeConverter;

        converterRegistry.add(Value.class, createValueConverter());
        converterRegistry.add(Values.class, createValuesConverter());
        converterRegistry.add(Function.class, createFunctionConverter());
        converterRegistry.add(Operator.class, createOperatorConverter());
        converterRegistry.add(Parameter.class, createParameterConverter());
    }

    Expression convert(String name, Object node) {
        if (node == null) {
            return null;
        }
        Converter<?> converter = converterRegistry.get(node.getClass());
        if (converter == null) {
            return null;
        }
        return ((Converter<Object>) converter).convert(name, node);
    }

    List<Expression> convert(List<?> nodes) {
        if (nodes == null) {
            return null;
        }
        return nodes.stream()
                .map(n -> convert(null, n))
                .collect(Collectors.toList());
    }

    private Converter<Value> createValueConverter() {
        return (name, input) -> {
            Object value = input.getValue();
            Type type = typeConverter.fromString(Type.class, input.getType());
            return valueOf(name, type, value);
        };
    }

    private Converter<Values> createValuesConverter() {
        return (name, input) -> {
            Type type = typeConverter.fromString(Type.class, input.getType());
            return valuesOf(name, type, convert(input.getValues()));
        };
    }

    private Converter<Function> createFunctionConverter() {
        return (name, input) -> {
            Type returnType = typeConverter.fromString(Type.class, input.getReturnType());
            return functionOf(name != null ? name : input.getName(), returnType, input.getName(), convert(input.getParameters()));
        };
    }

    private Converter<Operator> createOperatorConverter() {
        return (name, input) -> operatorOf(name, Boolean.class, input.getType(), convert(input.getOperands()));
    }

    private Converter<Parameter> createParameterConverter() {
        return (name, input) -> convert(input.getName(), input.getExpression());
    }

    @FunctionalInterface
    private interface Converter<I> {
        Expression convert(String name, I input);
    }
}
