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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class CallConverter {

    Argument.Invocation apply(Expression.Invocation invocation) {
        List<Argument> arguments = new ArrayList<>(invocation.getArguments().size() + 1);

        return Argument.invocationOf(invocation.getName(), Argument.UNKNOWN, invocation.getCall(),
                invocation.getArguments().stream()
                        .map(this::convert)
                        .collect(Collectors.toCollection(() -> arguments)));
    }

    private Argument convert(Expression param) throws IllegalArgumentException {
        if (param instanceof Expression.Reference) {
            Expression.Reference reference = (Expression.Reference) param;
            return Argument.referenceOf(param.getName(), reference.getReferenceType(), Argument.UNKNOWN, prepareReference(reference.getReference(), reference.getPath()));
        }
        if (param instanceof Expression.Value) {
            Expression.Value value = (Expression.Value) param;
            return Argument.valueOf(param.getName(), value.getType(), value.getValue());
        }
        if (param instanceof Expression.Invocation) {
            Expression.Invocation invocation = (Expression.Invocation) param;
            return apply(invocation);
        }
        throw new IllegalArgumentException(String.format("Parameter of type %s not supported in invocations (functions)", param));
    }

    private String prepareReference(String reference, String path) {
        return path != null && !path.isEmpty()
                ? reference + '.' + path
                : reference;
    }
}
