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

import com.sabre.oss.yare.core.*;
import com.sabre.oss.yare.core.invocation.InterceptedInvocation;
import com.sabre.oss.yare.core.invocation.Interceptor;
import com.sabre.oss.yare.core.invocation.Invocation;

import java.util.List;
import java.util.Objects;

public class DefaultRulesEngine implements RulesEngine, Wrapper {
    private final RulesExecutor rulesExecutor;
    private final Invocation<ExecutionContext, ExecutionContext> invocation;

    public DefaultRulesEngine(RulesExecutor rulesExecutor, List<Interceptor<ExecutionContext, ExecutionContext>> interceptors) {
        this.rulesExecutor = Objects.requireNonNull(rulesExecutor, "rulesExecutor must not be null");
        this.invocation = new InterceptedInvocation<>(interceptors, rulesExecutor);
    }

    @Override
    public RuleSession createSession(String uri) {
        return new DefaultRuleSession(uri, invocation);
    }

    @Override
    public <T> T unwrap(Class<T> expected) {
        return rulesExecutor instanceof Wrapper
                ? ((Wrapper) rulesExecutor).unwrap(expected)
                : null;
    }
}
