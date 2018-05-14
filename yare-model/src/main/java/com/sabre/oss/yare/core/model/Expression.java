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

import java.lang.reflect.Type;
import java.util.List;

public interface Expression {
    /**
     * Type to indicate that type is undefined / unknown
     */
    Class<Undefined> UNDEFINED = Undefined.class;

    /**
     * Name of expression if applicable.
     *
     * @return name of expression
     */
    String getName();

    /**
     * Type of expression.
     *
     * @return type of argument
     */
    Type getType();

    @SuppressWarnings("unchecked")
    default <T extends Expression> T as(Class<T> type) {
        return type.isInstance(this)
                ? (T) this
                : null;
    }

    interface Value extends Expression {

        /**
         * Return constant value.
         *
         * @return value;
         */
        Object getValue();
    }

    interface Invocation extends Expression {

        /**
         * Returns call name (i.e. action identifier used in rules definitions).
         *
         * @return call (function / action) name to invoke
         */
        String getCall();

        /**
         * Returns invocation arguments definitions
         * Returns invocation arguments definitions
         *
         * @return list of invocation arguments definitions
         */
        List<Expression> getArguments();
    }

    interface Operator extends Invocation {
    }

    interface Function extends Invocation {
    }

    interface Action extends Invocation {
    }

    final class Undefined {
    }
}

