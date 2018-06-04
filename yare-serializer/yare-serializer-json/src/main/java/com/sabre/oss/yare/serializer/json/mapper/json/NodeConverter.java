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

package com.sabre.oss.yare.serializer.json.mapper.json;

import com.sabre.oss.yare.common.converter.TypeConverter;
import com.sabre.oss.yare.common.mapper.ByClassRegistry;
import com.sabre.oss.yare.core.model.Expression;
import com.sabre.oss.yare.serializer.json.model.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

class NodeConverter {
    private final ByClassRegistry<Class<?>, Converter<?>> converterRegistry = new ByClassRegistry<>();
    private final TypeConverter typeConverter;

    NodeConverter(TypeConverter typeConverter) {
        this.typeConverter = typeConverter;

        converterRegistry.add(Expression.Value.class, createValueConverter());
        converterRegistry.add(Expression.Values.class, createValuesConverter());
        converterRegistry.add(Expression.Function.class, createFunctionConverter());
        converterRegistry.add(Expression.Operator.class, createOperatorConverter());
    }

    Operand convert(Object node) {
        if (node == null) {
            return null;
        }
        return ((Converter<Object>) converterRegistry.get(node.getClass())).convert(node);
    }

    List<Operand> convert(List<?> nodes) {
        if (nodes == null) {
            return null;
        }
        return nodes.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    List<Parameter> convertParameters(List<Expression> expressions) {
        return expressions.stream()
                .map(this::convertParameter)
                .collect(Collectors.toList());
    }

    private Converter<Expression.Value> createValueConverter() {
        return (input) -> {
            Object value = input.getValue();
            String type = typeConverter.toString(Type.class, input.getType());
            return new Value()
                    .withValue(value)
                    .withType(type);
        };
    }

    private Converter<Expression.Values> createValuesConverter() {
        return (input) -> {
            String type = typeConverter.toString(Type.class, input.getType());
            return new Values()
                    .withType(type)
                    .withValues(convertValues(input.getValues()));
        };
    }

    private Converter<Expression.Function> createFunctionConverter() {
        return (input) -> {
            String returnType = typeConverter.toString(Type.class, input.getType());
            return new Function()
                    .withName(input.getCall())
                    .withReturnType(returnType)
                    .withParameters(convertParameters(input.getArguments()));
        };
    }

    private Converter<Expression.Operator> createOperatorConverter() {
        return (input) -> new Operator()
                .withType(input.getCall())
                .withOperands(convert(input.getArguments()));
    }

    private List<com.sabre.oss.yare.serializer.json.model.Expression> convertValues(List<Expression> expressions) {
        if (expressions == null) {
            return null;
        }
        return convert(expressions).stream()
                .map(com.sabre.oss.yare.serializer.json.model.Expression.class::cast)
                .collect(Collectors.toList());
    }

    private Parameter convertParameter(Expression expression) {
        if (expression == null) {
            return null;
        }
        String name = expression.getName();
        Operand converted = convert(expression);
        return new Parameter()
                .withName(name)
                .withExpression((com.sabre.oss.yare.serializer.json.model.Expression) converted);
    }

    @FunctionalInterface
    private interface Converter<I> {
        Operand convert(I input);
    }
}
