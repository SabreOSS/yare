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

package com.sabre.oss.yare.core.error;

import com.sabre.oss.yare.core.ErrorHandler;
import com.sabre.oss.yare.core.model.Expression;

import java.util.Objects;

public class ConsequenceCreationError implements ErrorHandler.ConfigurationErrorEvent {
    private final String ruleName;
    private final Expression.Invocation action;
    private final Throwable throwable;

    public ConsequenceCreationError(String ruleName, Expression.Invocation action, Throwable throwable) {
        this.ruleName = Objects.requireNonNull(ruleName);
        this.action = Objects.requireNonNull(action);
        this.throwable = throwable;
    }

    public String getRuleName() {
        return ruleName;
    }

    public Expression.Invocation getAction() {
        return action;
    }

    @Override
    public Throwable getThrowable() {
        return throwable;
    }
}
