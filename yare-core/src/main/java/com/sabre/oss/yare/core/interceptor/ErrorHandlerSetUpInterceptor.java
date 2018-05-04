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

package com.sabre.oss.yare.core.interceptor;

import com.sabre.oss.yare.core.ErrorHandler;
import com.sabre.oss.yare.core.ExecutionContext;
import com.sabre.oss.yare.core.invocation.Interceptor;
import com.sabre.oss.yare.core.invocation.Invocation;

public class ErrorHandlerSetUpInterceptor implements Interceptor<ExecutionContext, ExecutionContext> {
    public static final ExecutionContext.Key<ErrorHandler> ERROR_HANDLER = ExecutionContext.Key.create(ErrorHandlerSetUpInterceptor.class, "ERROR_HANDLER");

    private final ErrorHandler errorHandler;

    public ErrorHandlerSetUpInterceptor(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public ExecutionContext invoke(Invocation<ExecutionContext, ExecutionContext> invocation, ExecutionContext context) {
        context.put(ERROR_HANDLER, errorHandler);
        return invocation.proceed(context);
    }
}
