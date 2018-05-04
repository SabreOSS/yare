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

package com.sabre.oss.yare.core.invocation;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * {@code InterceptedInvocation} delegates {@link #proceed(Object)} method to the
 * {@code interceptedInvocation} delegate but with possibility of intercepting such a call
 * with interceptors.
 *
 * @param <C> context type
 * @param <R> return type
 */
public class InterceptedInvocation<C, R> implements Invocation<C, R> {
    private final ChainHelper<C, R> chainHelper;
    private final Invocation<C, R> interceptedInvocation;

    public InterceptedInvocation(List<Interceptor<C, R>> interceptors, Invocation<C, R> interceptedInvocation) {
        this(new ChainHelper<>(interceptors), interceptedInvocation);
    }

    private InterceptedInvocation(ChainHelper<C, R> chainHelper, Invocation<C, R> interceptedInvocation) {
        this.chainHelper = requireNonNull(chainHelper, "chainHelper must not be null");
        this.interceptedInvocation = requireNonNull(interceptedInvocation, "interceptedInvocation must not be null");
    }

    @Override
    public R proceed(C context) {
        return chainHelper.isEmpty()
                ? interceptedInvocation.proceed(context)
                : chainHelper.getTopInterceptor().invoke(new InterceptedInvocation<>(chainHelper.nextChainHelper(), interceptedInvocation), context);
    }

    private static final class ChainHelper<C, R> {
        private final List<Interceptor<C, R>> interceptors;

        private ChainHelper(List<Interceptor<C, R>> interceptors) {
            this.interceptors = interceptors;
        }

        private ChainHelper<C, R> nextChainHelper() {
            return new ChainHelper<>(interceptors.subList(1, interceptors.size()));
        }

        private Interceptor<C, R> getTopInterceptor() {
            return interceptors.get(0);
        }

        private boolean isEmpty() {
            return interceptors.isEmpty();
        }
    }
}
