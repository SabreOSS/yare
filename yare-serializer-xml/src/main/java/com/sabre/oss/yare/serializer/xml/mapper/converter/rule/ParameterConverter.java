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
import com.sabre.oss.yare.serializer.xml.mapper.converter.rule.ToRuleConverter.Context;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.sabre.oss.yare.core.model.ExpressionFactory.functionOf;
import static com.sabre.oss.yare.core.model.ExpressionFactory.valueOf;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

class ParameterConverter implements ContextualConverter<ParameterSer, Expression> {
    private static final List<java.util.function.Function<ParameterSer, ?>> getters = asList(
            ParameterSer::getValue,
            ParameterSer::getValues,
            ParameterSer::getCustomValue,
            ParameterSer::getFunction);

    private final ByClassRegistry<Class<?>, ContextualConverter<ParameterSer, ? extends Expression>> converterRegistry = new ByClassRegistry<>();
    private final TypeConverter typeConverter;

    ParameterConverter(TypeConverter typeConverter) {
        this.typeConverter = requireNonNull(typeConverter, "typeConverter cannot be null");

        converterRegistry.add(CustomValueSer.class, new CustomValueParameterConverter());
        converterRegistry.add(ValueSer.class, new ValueParameterConverter());
        converterRegistry.add(ValuesSer.class, new ValuesParameterConverter());
        converterRegistry.add(FunctionSer.class, new FunctionParameterConverter());
    }

    @Override
    public Expression convert(Context ctx, ParameterSer parameter) {
        return getters.stream()
                .map(f -> f.apply(parameter))
                .filter(Objects::nonNull)
                .map(Object::getClass)
                .map(converterRegistry::get)
                .map(c -> c.convert(ctx, parameter))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(format("Parameter '%s' seems to be not defined.", parameter.getName())));
    }

    private class CustomValueParameterConverter implements ContextualConverter<ParameterSer, Expression.Value> {
        @Override
        public Expression.Value convert(Context ctx, ParameterSer input) {
            CustomValueSer customValue = input.getCustomValue();
            Type type = typeConverter.fromString(Type.class, customValue.getType());
            return valueOf(input.getName(), type, customValue.getAny());
        }
    }

    private class ValueParameterConverter implements ContextualConverter<ParameterSer, Expression.Value> {
        @Override
        public Expression.Value convert(Context ctx, ParameterSer input) {
            ValueSer valueContainer = input.getValue();
            String typeName = valueContainer.getType();
            String value = valueContainer.getValue();
            if (typeName != null) {
                Type type = typeConverter.fromString(Type.class, typeName);
                return valueOf(input.getName(), type, typeConverter.fromString(type, value));
            }
            return valueOf(input.getName(), String.class, value);
        }
    }

    private class ValuesParameterConverter implements ContextualConverter<ParameterSer, Expression.Value> {
        @Override
        public Expression.Value convert(Context ctx, ParameterSer input) {
            ValuesSer values = input.getValues();
            Type globalItemType = typeConverter.fromString(Type.class, values.getType());
            Collection<Object> collection = new ArrayList<>(values.getValue().size());
            for (ValueSer value : values.getValue()) {
                Type itemType = isNull(value.getType())
                        ? globalItemType
                        : typeConverter.fromString(Type.class, value.getType());
                Object item = typeConverter.fromString(itemType, value.getValue());
                collection.add(item);
            }
            return valueOf(input.getName(), globalItemType, collection);
        }
    }

    private class FunctionParameterConverter implements ContextualConverter<ParameterSer, Expression.Function> {
        @Override
        public Expression.Function convert(Context ctx, ParameterSer parameter) {
            FunctionSer function = parameter.getFunction();
            Type returnType = typeConverter.fromString(Type.class, function.getReturnType());
            return functionOf(parameter.getName(), returnType, function.getName(), ParameterConverter.this.convert(ctx, function.getParameter()));
        }
    }
}
