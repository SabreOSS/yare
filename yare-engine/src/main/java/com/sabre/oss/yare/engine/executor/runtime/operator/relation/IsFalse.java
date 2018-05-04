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

package com.sabre.oss.yare.engine.executor.runtime.operator.relation;

import com.sabre.oss.yare.core.model.Expression;
import com.sabre.oss.yare.engine.executor.runtime.operator.OperatorFactory;
import com.sabre.oss.yare.engine.executor.runtime.operator.UniArgPredicate;
import com.sabre.oss.yare.engine.executor.runtime.predicate.Predicate;
import com.sabre.oss.yare.engine.executor.runtime.predicate.PredicateFactoryContext;
import com.sabre.oss.yare.engine.executor.runtime.value.ValueProvider;

import java.lang.reflect.Type;

public class IsFalse extends UniArgPredicate {

    private static final String OPERATOR_NAME = "is-false";

    IsFalse(ValueProvider operandProvider) {
        super(operandProvider);
    }

    @Override
    protected Boolean evaluate(Object operand) {
        return operand.equals(Boolean.FALSE);
    }

    public static class Factory extends OperatorFactory {

        @Override
        public Predicate create(PredicateFactoryContext context, Expression.Operator operator, ValueProvider[] valueProviders) {
            return isApplicable(operator, valueProviders)
                    ? new IsFalse(valueProviders[0])
                    : null;
        }

        private boolean isApplicable(Expression.Operator operator, ValueProvider[] valueProviders) {
            return OPERATOR_NAME.equals(operator.getCall()) &&
                    operator.getArguments().size() == 1 &&
                    valueProviders.length == 1 &&
                    (isBoolean(valueProviders[0].getType()) || valueProviders[0].getType().equals(Object.class));
        }

        private boolean isBoolean(Type type) {
            return type.equals(Boolean.class) || type.equals(boolean.class);
        }
    }
}
