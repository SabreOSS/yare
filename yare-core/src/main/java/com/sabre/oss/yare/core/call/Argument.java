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

import static java.util.Arrays.asList;

/**
 * Represents argument of invocation.
 */
public interface Argument {

    Type UNKNOWN = UnknownType.class;

    static Invocation invocationOf(String name, Type returnType, String call, Argument... arguments) {
        return ArgumentFactory.invocationOf(name, returnType, call, asList(arguments));
    }

    static Invocation invocationOf(String name, Type returnType, String call, List<Argument> arguments) {
        return ArgumentFactory.invocationOf(name, returnType, call, arguments);
    }

    static Reference referenceOf(String name, Type baseReferenceType, Type argumentType, String reference) {
        return ArgumentFactory.referenceOf(name, baseReferenceType, argumentType, reference);
    }

    static Value valueOf(String name, Object nonNullValue) {
        return ArgumentFactory.valueOf(name, nonNullValue.getClass(), nonNullValue);
    }

    static Value valueOf(String name, Type type, Object value) {
        return ArgumentFactory.valueOf(name, type, value);
    }

    String getName();

    Type getType();

    @SuppressWarnings("unchecked")
    default <T extends Argument> T as(Class<T> type) {
        return type.isInstance(this)
                ? (T) this
                : null;
    }

    interface Value extends Argument {

        Object getValue();
    }

    interface Reference extends Argument {

        String getReference();

        Type getReferenceType();
    }

    interface Invocation extends Argument {

        String getCall();

        List<Argument> getArguments();
    }

    class UnknownType {
    }
}

