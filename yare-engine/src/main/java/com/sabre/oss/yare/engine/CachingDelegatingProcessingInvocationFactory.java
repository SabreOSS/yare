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

package com.sabre.oss.yare.engine;

import com.sabre.oss.yare.core.call.*;
import com.sabre.oss.yare.core.invocation.Invocation;
import com.sabre.oss.yare.engine.executor.ExecutorConfiguration;

import java.util.Objects;
import java.util.function.Supplier;

public class CachingDelegatingProcessingInvocationFactory<R> implements ProcessingInvocationFactory<R> {
    private final Supplier<ArgumentValueResolver> argumentValueResolverSupplier;
    private final ProcessingInvocationFactory<R> delegate;
    private final CallInvocationResultCache invocationCache;
    private final ExecutorConfiguration configuration;

    public CachingDelegatingProcessingInvocationFactory(ProcessingInvocationFactory<R> delegate,
                                                        Supplier<ArgumentValueResolver> argumentValueResolverSupplier,
                                                        CallInvocationResultCache invocationCache,
                                                        ExecutorConfiguration configuration) {
        this.argumentValueResolverSupplier = Objects.requireNonNull(argumentValueResolverSupplier);
        this.delegate = Objects.requireNonNull(delegate);
        this.invocationCache = Objects.requireNonNull(invocationCache);
        this.configuration = Objects.requireNonNull(configuration);
    }

    @Override
    public Invocation<ProcessingContext, R> create(Argument.Invocation invocation) {
        Invocation<ProcessingContext, R> processingInvocation = delegate.create(invocation);
        if (processingInvocation == null) {
            return null;
        }
        return configuration.isFunctionCacheable(invocation.getCall()) && processingInvocation instanceof ProcessingInvocation
                ? new CachingDelegatingProcessingInvocation<>(invocation, argumentValueResolverSupplier.get(), (ProcessingInvocation<R>) processingInvocation, invocationCache)
                : processingInvocation;
    }
}
