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

package com.sabre.oss.yare.core.model;

import com.sabre.oss.yare.core.model.type.InternalParameterizedType;

import java.lang.reflect.Type;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

public final class ExpressionFactory {
    private ExpressionFactory() {
    }

    public static Expression.Action actionOf(String name, String call, Expression... arguments) {
        return new Action(name, call, asList(arguments));
    }

    public static Expression.Action actionOf(String name, String call, List<Expression> arguments) {
        return new Action(name, call, arguments);
    }

    public static Expression.Function functionOf(String name, Type returnType, String call, Expression... arguments) {
        return new Function(name, returnType, call, asList(arguments));
    }

    public static Expression.Function functionOf(String name, Type returnType, String call, List<Expression> arguments) {
        return new Function(name, returnType, call, arguments);
    }

    public static Expression.Operator operatorOf(String name, Type returnType, String call, Expression... arguments) {
        return new Operator(name, returnType, call, asList(arguments));
    }

    public static Expression.Operator operatorOf(String name, Type returnType, String call, List<Expression> arguments) {
        return new Operator(name, returnType, call, arguments);
    }

    public static Expression.Value valueOf(String name, Object nonNullValue) {
        return new ExpressionFactory.Value(name, determineType(nonNullValue), nonNullValue);
    }

    public static Expression.Value valueOf(String name, Type type, Object value) {
        return new ExpressionFactory.Value(name, type, value);
    }

    public static Expression.Values valuesOf(String name, Type type, List<Expression> expressions) {
        return new ExpressionFactory.Values(name, type, expressions);
    }

    private static Type determineType(Object value) {
        Class<?> clazz = value.getClass();
        if (value instanceof Collection) {
            if (value instanceof List) {
                clazz = List.class;
            } else if (value instanceof Set) {
                clazz = Set.class;
            }
            if (!((Collection) value).isEmpty()) {
                Object item = ((Collection) value).iterator().next();
                Class<?> itemClazz = item != null ? item.getClass() : Object.class;
                return new InternalParameterizedType(null, clazz, itemClazz);
            }
        }
        return clazz;
    }

    private abstract static class InternalOperand<V> implements Expression {
        protected final String name;
        protected final Type type;
        protected final V value;
        private final int hashCode;

        InternalOperand(String name, Type type, V value) {
            this.name = name;
            this.type = requireNonNull(type);
            this.value = value;
            this.hashCode = Objects.hash(name, type, value);
        }

        @Override
        public final String getName() {
            return name;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof InternalOperand)) {
                return false;
            }
            InternalOperand<?> that = (InternalOperand<?>) o;
            return this.hashCode == that.hashCode &&
                    Objects.equals(name, that.name) &&
                    Objects.equals(type, that.type) &&
                    Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }

    private static class BaseInvocation extends InternalOperand<String> {
        protected final List<Expression> arguments;
        private final int hashCode;

        BaseInvocation(String name, Type type, String value, List<Expression> arguments) {
            super(name, type, value);
            this.arguments = arguments == null ? null : Collections.unmodifiableList(arguments);
            this.hashCode = Objects.hash(super.hashCode(), arguments);
        }

        public String getCall() {
            return value;
        }

        public List<Expression> getArguments() {
            return arguments;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof BaseInvocation)) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            BaseInvocation that = (BaseInvocation) o;
            return this.hashCode == that.hashCode && Objects.equals(arguments, that.arguments);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }

    static final class Action extends BaseInvocation implements Expression.Action {
        Action(String name, String value, List<Expression> arguments) {
            super(name, Void.class, value, arguments);
        }

        @Override
        public String toString() {
            return String.format("Action{name='%s', type=%s, value=%s, arguments=%s}", name, type, value, arguments);
        }
    }

    static final class Operator extends BaseInvocation implements Expression.Operator {

        Operator(String name, Type type, String value, List<Expression> arguments) {
            super(name, type, value, arguments);
        }

        @Override
        public String toString() {
            return String.format("Operator{name='%s', type=%s, value=%s, arguments=%s}", name, type, value, arguments);
        }
    }

    static final class Function extends BaseInvocation implements Expression.Function {

        Function(String name, Type type, String value, List<Expression> arguments) {
            super(name, type, value, arguments);
        }

        @Override
        public String toString() {
            return String.format("Function{name='%s', type=%s, value=%s, arguments=%s}", name, type, value, arguments);
        }
    }

    static final class Value extends InternalOperand<Object> implements Expression.Value {

        Value(String name, Type type, Object value) {
            super(name, type, value);
        }

        @Override
        public Object getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.format("Value{name='%s', type=%s, value=%s}", name, type, value);
        }
    }

    static final class Values extends InternalOperand<List<Expression>> implements Expression.Values {

        Values(String name, Type type, List<Expression> expressions) {
            super(name, type, expressions == null ? null : Collections.unmodifiableList(expressions));
        }

        @Override
        public List<Expression> getValues() {
            return value;
        }

        @Override
        public String toString() {
            return String.format("Values{name='%s', type=%s, value=%s}", name, type, value);
        }
    }
}
