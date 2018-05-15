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
import java.util.function.Function;

import static com.sabre.oss.yare.core.model.ExpressionFactory.*;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

class BooleanExpressionConverter implements ContextualConverter<Object, Expression> {
    private final ByClassRegistry<Class<?>, ContextualConverter<?, ? extends Expression>> converterRegistry = new ByClassRegistry<>();
    private final ParameterConverter parameterConverter;
    private final TypeConverter typeConverter;

    BooleanExpressionConverter(ParameterConverter parameterConverter, TypeConverter typeConverter) {
        this.parameterConverter = requireNonNull(parameterConverter, "parameterConverter cannot be null");
        this.typeConverter = requireNonNull(typeConverter, "typeConverter cannot be null");

        converterRegistry.add(CustomValueSer.class, new CustomValueConverter());
        converterRegistry.add(ValueSer.class, new ValueConverter());
        converterRegistry.add(ValuesSer.class, new ValuesConverter());
        converterRegistry.add(FunctionSer.class, new FunctionConverter());
        converterRegistry.add(OperatorSer.class, new OperatorConverter());
        converterRegistry.add(AndSer.class, new AndConverter());
        converterRegistry.add(OrSer.class, new OrConverter());
        converterRegistry.add(NotSer.class, new NotConverter());
    }

    @Override
    public Expression convert(Context ctx, Object input) {
        return ((ContextualConverter<Object, Expression>) converterRegistry.get(input.getClass())).convert(ctx, input);
    }

    private class CustomValueConverter implements ContextualConverter<CustomValueSer, Expression.Value> {
        @Override
        public Expression.Value convert(Context ctx, CustomValueSer input) {
            Type type = typeConverter.fromString(Type.class, input.getType());
            return valueOf(null, type, input.getAny());
        }
    }

    private class ValueConverter implements ContextualConverter<ValueSer, Expression.Value> {
        @Override
        public Expression.Value convert(Context ctx, ValueSer input) {
            String typeName = input.getType();
            String value = input.getValue();
            if (typeName != null) {
                Type type = typeConverter.fromString(Type.class, typeName);
                return valueOf(null, type, typeConverter.fromString(type, value));
            } else {
                return valueOf(null, String.class, value);
            }
        }
    }

    private class ValuesConverter implements ContextualConverter<ValuesSer, Expression.Value> {
        @Override
        public Expression.Value convert(Context ctx, ValuesSer input) {
            Type globalItemType = typeConverter.fromString(Type.class, input.getType());
            Collection<Object> collection = new ArrayList<>(input.getValue().size());
            for (ValueSer value : input.getValue()) {
                Type itemType = isNull(value.getType()) ? globalItemType : typeConverter.fromString(Type.class, value.getType());
                Object item = typeConverter.fromString(itemType, value.getValue());
                collection.add(item);
            }
            return valueOf(null, globalItemType, collection);
        }
    }

    private class FunctionConverter implements ContextualConverter<FunctionSer, Expression.Function> {
        @Override
        public Expression.Function convert(Context ctx, FunctionSer input) {
            Type returnType = typeConverter.fromString(Type.class, input.getReturnType());
            return functionOf(input.getName(), returnType, input.getName(), parameterConverter.convert(ctx, input.getParameter()));
        }
    }

    private class OperatorConverter implements ContextualConverter<OperatorSer, Expression.Operator> {
        @Override
        public Expression.Operator convert(Context ctx, OperatorSer input) {
            return operatorOf(null, Boolean.class, input.getType(), BooleanExpressionConverter.this.convert(ctx, input.getOperand()));
        }
    }

    private class AndConverter implements ContextualConverter<AndSer, Expression.Operator> {
        @Override
        public Expression.Operator convert(Context ctx, AndSer input) {
            return operatorOf(null, Boolean.class, "and", BooleanExpressionConverter.this.convert(ctx, input.getBooleanExpression()));
        }
    }

    private class OrConverter implements ContextualConverter<OrSer, Expression.Operator> {
        @Override
        public Expression.Operator convert(Context ctx, OrSer input) {
            return operatorOf(null, Boolean.class, "or", BooleanExpressionConverter.this.convert(ctx, input.getBooleanExpression()));
        }
    }

    private class NotConverter implements ContextualConverter<NotSer, Expression.Operator> {
        private final List<Function<NotSer, ?>> getters = asList(
                NotSer::getValue,
                NotSer::getOperator,
                NotSer::getFunction,
                NotSer::getNot,
                NotSer::getAnd,
                NotSer::getOr);

        @Override
        public Expression.Operator convert(Context ctx, NotSer input) {
            Object booleanExpression = getters.stream()
                    .map(g -> g.apply(input))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Operator NOT seems to be input properly defined."));
            return operatorOf(null, Boolean.class, "not", BooleanExpressionConverter.this.convert(ctx, booleanExpression));
        }
    }
}
