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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

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

    public static Expression.Reference referenceOf(String name, Type referenceType, String reference) {
        return new ExpressionFactory.Reference(name, referenceType, reference, referenceType, null);
    }

    public static Expression.Reference referenceOf(String name, Type referenceType, String reference, Type type, String path) {
        return new ExpressionFactory.Reference(name, referenceType, reference, type, path);
    }

    public static Expression.Value valueOf(String name, Object nonNullValue) {
        return new ExpressionFactory.Value(name, determineType(nonNullValue), nonNullValue);
    }

    public static Expression.Value valueOf(String name, Type type, Object value) {
        return new ExpressionFactory.Value(name, type, value);
    }

    public static Expression.Raw rawOf(String name, Type type, String value) {
        return new ExpressionFactory.Raw(name, type, value);
    }

    private static Type determineType(Object value) {
        Class<?> clazz = value.getClass();
        if (value instanceof Collection) {
            if (value instanceof List) {
                clazz = List.class;
            } else if (value instanceof Set) {
                clazz = Set.class;
            }
            if (((Collection) value).size() > 0) {
                Object item = ((Collection) value).iterator().next();
                Class<?> itemClazz = item != null ? item.getClass() : Object.class;
                return new InternalParameterizedType(null, clazz, itemClazz);
            }
        }
        return clazz;
    }

    abstract static class InternalOperand<V> implements Expression {
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

    static class BaseInvocation extends InternalOperand<String> {
        protected final List<Expression> arguments;
        private final int hashCode;

        BaseInvocation(String name, Type type, String value, List<Expression> arguments) {
            super(name, type, value);
            this.arguments = arguments;
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
            return "Action{" +
                    "name='" + name + '\'' +
                    ", type=" + type +
                    ", value=" + value +
                    ", arguments=" + arguments +
                    '}';
        }
    }

    static final class Operator extends BaseInvocation implements Expression.Operator {

        Operator(String name, Type type, String value, List<Expression> arguments) {
            super(name, type, value, arguments);
        }

        @Override
        public String toString() {
            return "Operator{" +
                    "name='" + name + '\'' +
                    ", type=" + type +
                    ", value=" + value +
                    ", arguments=" + arguments +
                    '}';
        }
    }

    static final class Function extends BaseInvocation implements Expression.Function {

        Function(String name, Type type, String value, List<Expression> arguments) {
            super(name, type, value, arguments);
        }

        @Override
        public String toString() {
            return "Function{" +
                    "name='" + name + '\'' +
                    ", type=" + type +
                    ", value=" + value +
                    ", arguments=" + arguments +
                    '}';
        }
    }

    static final class Raw extends InternalOperand<String> implements Expression.Raw {

        Raw(String name, Type type, String value) {
            super(name, type, value);
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public String getValue() {
            return value;
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
    }

    static final class Reference implements Expression.Reference {
        protected final String name;
        protected final String reference;
        protected final String path;
        protected final Type referenceType;
        protected final Type type;

        private final int hashCode;

        Reference(String name, Type referenceType, String reference, Type type, String path) {
            this.name = name;
            this.referenceType = referenceType;
            this.reference = reference;
            this.type = type;
            this.path = path;
            this.hashCode = Objects.hash(name, referenceType, reference, type, path);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Type getReferenceType() {
            return referenceType;
        }

        @Override
        public String getReference() {
            return reference;
        }

        @Override
        public String getPath() {
            return path;
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
            if (!(o instanceof ExpressionFactory.Reference)) {
                return false;
            }
            ExpressionFactory.Reference reference1 = (ExpressionFactory.Reference) o;
            return Objects.equals(name, reference1.name) &&
                    Objects.equals(referenceType, reference1.referenceType) &&
                    Objects.equals(reference, reference1.reference) &&
                    Objects.equals(type, reference1.type) &&
                    Objects.equals(path, reference1.path);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public String toString() {
            return "Reference{" +
                    "name='" + name + '\'' +
                    ", type=" + type +
                    ", reference='" + reference + '\'' +
                    ", path='" + path + '\'' +
                    '}';
        }
    }

    static class InternalParameterizedType implements ParameterizedType {
        private final Type[] actualTypeArguments;
        private final Class<?> rawType;
        private final Type ownerType;

        InternalParameterizedType(Type ownerType, Class<?> rawType, Type... actualTypeArguments) {
            this.rawType = requireNonNull(rawType);
            this.actualTypeArguments = requireNonNull(actualTypeArguments);
            this.ownerType = ownerType;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return actualTypeArguments;
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return ownerType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ParameterizedType)) {
                return false;
            }
            ParameterizedType that = (ParameterizedType) o;
            return Objects.equals(rawType, that.getRawType()) &&
                    Arrays.equals(actualTypeArguments, that.getActualTypeArguments()) &&
                    Objects.equals(ownerType, that.getOwnerType());
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(this.actualTypeArguments) ^ Objects.hashCode(this.ownerType) ^ Objects.hashCode(this.rawType);
        }

        @Override
        public String toString() {
            return rawType.getTypeName() + Arrays.stream(actualTypeArguments)
                    .map(Type::getTypeName)
                    .collect(Collectors.joining(",", "<", ">"));
        }
    }

}
