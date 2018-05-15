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

import com.sabre.oss.yare.core.model.Expression;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.core.reference.ValueConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class CallConverter {
    private final ValueConverter<Argument> valueConverter;

    CallConverter() {
        this.valueConverter = new ValueConverter<>(Argument::referenceOf, Argument::valueOf);
    }

    Argument.Invocation convert(Rule rule, Expression.Invocation invocation) {
        List<Argument> arguments = new ArrayList<>(invocation.getArguments().size() + 1);

        return Argument.invocationOf(invocation.getName(), Argument.UNKNOWN, invocation.getCall(),
                invocation.getArguments().stream()
                        .map(param -> convertExpression(rule, param))
                        .collect(Collectors.toCollection(() -> arguments)));
    }

    private Argument convertExpression(Rule rule, Expression param) throws IllegalArgumentException {
        if (param instanceof Expression.Value) {
            Expression.Value value = (Expression.Value) param;
            return valueConverter.create(rule, value);
        }
        if (param instanceof Expression.Invocation) {
            Expression.Invocation invocation = (Expression.Invocation) param;
            return convert(rule, invocation);
        }
        throw new IllegalArgumentException(String.format("Parameter of type %s not supported in invocations (functions)", param));
    }
}
