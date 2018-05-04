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

package com.sabre.oss.yare.engine.executor.runtime.operator.logical;

import com.sabre.oss.yare.core.model.Expression;
import com.sabre.oss.yare.engine.executor.runtime.predicate.Predicate;
import com.sabre.oss.yare.engine.executor.runtime.predicate.PredicateContext;
import com.sabre.oss.yare.engine.executor.runtime.predicate.PredicateFactory;
import com.sabre.oss.yare.engine.executor.runtime.predicate.PredicateFactoryContext;

public class False extends Predicate {

    @Override
    public Boolean evaluate(PredicateContext context) {
        return Boolean.FALSE;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof False;
    }

    @Override
    public int hashCode() {
        return 13;
    }

    public static class Factory implements PredicateFactory {
        private final False inner = new False();

        @Override
        public Predicate create(PredicateFactoryContext context, Expression expression) {
            if (!isApplicable(expression)) {
                return null;
            }

            return inner;
        }

        private boolean isApplicable(Expression expression) {
            if (!(expression instanceof Expression.Value)) {
                return false;
            }
            Expression.Value value = (Expression.Value) expression;
            return value.getValue() instanceof Boolean && !(Boolean) value.getValue();
        }
    }
}
