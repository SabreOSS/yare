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

import com.sabre.oss.yare.core.ErrorHandler;
import com.sabre.oss.yare.core.error.ConsequenceCreationError;
import com.sabre.oss.yare.core.invocation.Invocation;
import com.sabre.oss.yare.core.model.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class FunctionFactory {
    private static final Logger log = LoggerFactory.getLogger(FunctionFactory.class);

    private final ProcessingInvocationFactory<Object> invocationFactory;
    private final CallConverter callConverter;
    private final ErrorHandler errorHandler;

    public FunctionFactory(ProcessingInvocationFactory<Object> invocationFactory) {
        this(invocationFactory, null, new CallConverter());
    }

    public FunctionFactory(ProcessingInvocationFactory<Object> invocationFactory, ErrorHandler errorHandler, CallConverter callConverter) {
        this.invocationFactory = Objects.requireNonNull(invocationFactory);
        this.errorHandler = errorHandler;
        this.callConverter = Objects.requireNonNull(callConverter);
    }

    public Invocation<ProcessingContext, Object> create(String ruleId, Expression.Invocation function) {
        try {
            return invocationFactory.create(callConverter.apply(function));
        } catch (Exception e) {
            if (Objects.isNull(errorHandler) || !errorHandler.handleError(new ConsequenceCreationError(ruleId, function, e))) {
                throw e;
            }
        }
        log.warn(String.format("[%s][%s] : Due to handled error, skipped creation of function invocation", ruleId, function.getName()));
        return null;
    }
}
