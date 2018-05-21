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

package com.sabre.oss.yare.core.call;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

final class ArgumentFactory {
    private ArgumentFactory() {
    }

    static Argument.Invocation invocationOf(String name, Type returnType, String call, Argument... arguments) {
        return new ArgumentFactory.Invocation(name, returnType, call, asList(arguments));
    }

    static Argument.Invocation invocationOf(String name, Type returnType, String call, List<Argument> arguments) {
        return new ArgumentFactory.Invocation(name, returnType, call, arguments);
    }

    static Argument.Reference referenceOf(String name, Type referenceType, Type type, String reference) {
        return new ArgumentFactory.Reference(name, referenceType, type, reference);
    }

    static Argument.Value valueOf(String name, Object nonNullValue) {
        return new ArgumentFactory.Value(name, nonNullValue.getClass(), nonNullValue);
    }

    static Argument.Value valueOf(String name, Type type, Object value) {
        return new ArgumentFactory.Value(name, type, value);
    }

    static Argument.Values valuesOf(String name, Type type, List<Argument> values) {
        return new ArgumentFactory.Values(name, type, values);
    }

    abstract static class InternalArgument<V> implements Argument {
        protected final String name;
        protected final Type type;
        protected final V value;
        private final int hashCode;

        InternalArgument(String name, Type type, V value) {
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
            if (!(o instanceof InternalArgument)) {
                return false;
            }
            InternalArgument<?> that = (InternalArgument<?>) o;
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

    static final class Invocation extends InternalArgument<String> implements Argument.Invocation {
        private final List<Argument> arguments;
        private final int hashCode;

        Invocation(String name, Type type, String value, List<Argument> arguments) {
            super(name, type, value);
            this.arguments = Objects.requireNonNull(arguments);
            this.hashCode = Objects.hash(super.hashCode(), arguments);
        }

        @Override
        public String getCall() {
            return value;
        }

        @Override
        public List<Argument> getArguments() {
            return arguments;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ArgumentFactory.Invocation)) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            ArgumentFactory.Invocation that = (ArgumentFactory.Invocation) o;
            return this.hashCode == that.hashCode && Objects.equals(arguments, that.arguments);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public String toString() {
            return getCall() + "(" + arguments + ")";
        }
    }

    static final class Reference extends InternalArgument<String> implements Argument.Reference {
        private final Type referenceType;

        Reference(String name, Type referenceType, Type type, String value) {
            super(name, type, value);
            this.referenceType = Objects.requireNonNull(referenceType);
        }

        @Override
        public String getReference() {
            return value;
        }

        @Override
        public Type getReferenceType() {
            return referenceType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Reference)) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            Reference reference = (Reference) o;
            return Objects.equals(referenceType, reference.getReferenceType());
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), referenceType);
        }

        @Override
        public String toString() {
            return "ref(\"" + value + "\")";
        }
    }

    static final class Value extends InternalArgument<Object> implements Argument.Value {
        Value(String name, Type type, Object value) {
            super(name, type, value);
        }

        @Override
        public Object getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "value(\"" + Objects.toString(value, "null") + "\")";
        }
    }

    static final class Values extends InternalArgument<List<Argument>> implements Argument.Values {
        Values(String name, Type type, List<Argument> value) {
            super(name, type, value);
        }

        @Override
        public List<Argument> getArguments() {
            return value;
        }

        @Override
        public String toString() {
            return "value(\"" + Objects.toString(value, "null") + "\")";
        }
    }
}
