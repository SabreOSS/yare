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

import com.sabre.oss.yare.core.invocation.Invocation;

import java.util.List;
import java.util.Objects;

public abstract class ProcessingInvocation<R> implements Invocation<ProcessingContext, R> {
    protected final Argument.Invocation invocation;
    private final ArgumentValueResolver argumentValueResolver;
    private final Object[] constantArguments;

    protected ProcessingInvocation(Argument.Invocation invocation, ArgumentValueResolver argumentValueResolver) {
        this.invocation = Objects.requireNonNull(invocation);
        this.argumentValueResolver = Objects.requireNonNull(argumentValueResolver);
        this.constantArguments = invocation.getArguments().stream().allMatch(a -> a instanceof Argument.Value)
                ? prepareArgumentValues(null)
                : null;
    }

    @Override
    public final R proceed(ProcessingContext processingContext) {
        return call(processingContext, prepareArgumentValues(processingContext));
    }

    public abstract R call(ProcessingContext ctx, Object[] args);

    private Object[] prepareArgumentValues(ProcessingContext processingContext) {
        if (constantArguments != null) {
            return constantArguments;
        }

        List<Argument> arguments = invocation.getArguments();
        Object[] args = new Object[arguments.size()];
        int index = 0;
        for (Argument arg : arguments) {
            args[index++] = argumentValueResolver.resolve(processingContext, arg);
        }
        return args;
    }
}
