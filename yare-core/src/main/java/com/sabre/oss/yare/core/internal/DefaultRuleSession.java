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

package com.sabre.oss.yare.core.internal;

import com.sabre.oss.yare.core.DefaultContextKey;
import com.sabre.oss.yare.core.ExecutionContext;
import com.sabre.oss.yare.core.RuleSession;
import com.sabre.oss.yare.core.invocation.Invocation;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultRuleSession implements RuleSession {
    private final String uri;
    private final Invocation<ExecutionContext, ExecutionContext> invocation;
    private final AtomicReference<Object> executionSet = new AtomicReference<>();

    public DefaultRuleSession(String uri, Invocation<ExecutionContext, ExecutionContext> invocation) {
        this.uri = Objects.requireNonNull(uri);
        this.invocation = Objects.requireNonNull(invocation);
    }

    @Override
    public String getUri() {
        return uri;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T execute(T result, Collection<?> facts) {
        ExecutionContext inputCtx = new BaseExecutionContext();
        inputCtx.put(DefaultContextKey.RULE_EXECUTION_SET, executionSet);
        inputCtx.put(DefaultContextKey.RULE_EXECUTION_SET_URI, uri);
        inputCtx.put(DefaultContextKey.RESULT, result);
        inputCtx.put(DefaultContextKey.FACTS, facts);

        ExecutionContext outputCtx = invocation.proceed(inputCtx);

        return (T) outputCtx.get(DefaultContextKey.RESULT);
    }
}
