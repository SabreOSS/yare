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

package com.sabre.oss.yare.serializer.xml.mapper.converter.rule;

import com.sabre.oss.yare.common.converter.TypeConverter;
import com.sabre.oss.yare.common.mapper.ByClassRegistry;
import com.sabre.oss.yare.core.model.Expression;
import com.sabre.oss.yare.serializer.model.*;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.sabre.oss.yare.core.model.ExpressionFactory.*;

class NodeConverter {
    private final ByClassRegistry<Class<?>, Converter<?>> converterRegistry = new ByClassRegistry<>();
    private final TypeConverter typeConverter;

    NodeConverter(TypeConverter typeConverter) {
        this.typeConverter = typeConverter;

        converterRegistry.add(CustomValueSer.class, createCustomValueConverter());
        converterRegistry.add(ValueSer.class, createValueConverter());
        converterRegistry.add(ValuesSer.class, createValuesConverter());
        converterRegistry.add(FunctionSer.class, createFunctionConverter());
        converterRegistry.add(OperatorSer.class, createOperatorConverter());
        converterRegistry.add(AndSer.class, createAndConverter());
        converterRegistry.add(OrSer.class, createOrConverter());
        converterRegistry.add(NotSer.class, createNotConverter());
        converterRegistry.add(ParameterSer.class, createParameterConverter());
    }

    Expression convert(String name, Object node) {
        return ((Converter<Object>) converterRegistry.get(node.getClass())).convert(name, node);
    }

    List<Expression> convert(List<?> nodes) {
        return nodes.stream()
                .map(n -> convert(null, n))
                .collect(Collectors.toList());
    }

    private Converter<CustomValueSer> createCustomValueConverter() {
        return (name, input) -> {
            Type type = typeConverter.fromString(Type.class, input.getType());
            return valueOf(name, type, input.getAny());
        };
    }

    private Converter<ValueSer> createValueConverter() {
        return (name, input) -> {
            String typeName = input.getType();
            String value = input.getValue();
            Type type = typeName != null ? typeConverter.fromString(Type.class, typeName) : String.class;
            return valueOf(name, type, typeConverter.fromString(type, value));
        };
    }

    private Converter<ValuesSer> createValuesConverter() {
        return (name, input) -> {
            Type globalItemType = typeConverter.fromString(Type.class, input.getType());
            return valuesOf(name, globalItemType, convert(input.getOperand()));
        };
    }

    private Converter<FunctionSer> createFunctionConverter() {
        return (String name, FunctionSer input) -> {
            Type returnType = typeConverter.fromString(Type.class, input.getReturnType());
            return functionOf(name != null ? name : input.getName(), returnType, input.getName(), convert(input.getParameter()));
        };
    }

    private Converter<OperatorSer> createOperatorConverter() {
        return (name, input) -> operatorOf(name, Boolean.class, input.getType(), convert(input.getOperand()));
    }

    private Converter<AndSer> createAndConverter() {
        return (name, input) -> operatorOf(name, Boolean.class, "and", convert(input.getBooleanExpression()));
    }

    private Converter<OrSer> createOrConverter() {
        return (name, input) -> operatorOf(name, Boolean.class, "or", convert(input.getBooleanExpression()));
    }

    private Converter<NotSer> createNotConverter() {
        List<Function<NotSer, ?>> getters = Arrays.asList(
                NotSer::getValue,
                NotSer::getOperator,
                NotSer::getFunction,
                NotSer::getNot,
                NotSer::getAnd,
                NotSer::getOr);

        return (name, input) -> {
            Object booleanExpression = getters.stream()
                    .map(g -> g.apply(input))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Operator NOT seems to be input properly defined."));
            return operatorOf(name, Boolean.class, "not", convert(null, booleanExpression));
        };
    }

    private Converter<ParameterSer> createParameterConverter() {
        List<Function<ParameterSer, ?>> getters = Arrays.asList(
                ParameterSer::getValue,
                ParameterSer::getValues,
                ParameterSer::getCustomValue,
                ParameterSer::getFunction);

        return (name, input) -> {
            Object p = getters.stream()
                    .map(f -> f.apply(input))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Parameter '%s' seems to be not defined.", input.getName())));
            return convert(input.getName(), p);
        };
    }

    private interface Converter<I> {
        Expression convert(String name, I input);
    }
}
