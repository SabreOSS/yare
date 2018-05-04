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
import com.sabre.oss.yare.engine.executor.RuntimeRulesBuilder;
import com.sabre.oss.yare.engine.executor.runtime.predicate.Predicate;
import com.sabre.oss.yare.engine.executor.runtime.predicate.PredicateContext;
import com.sabre.oss.yare.engine.executor.runtime.predicate.PredicateFactory;
import com.sabre.oss.yare.engine.executor.runtime.predicate.PredicateFactoryContext;

import java.util.Objects;

/**
 * {@link Not} is a three-valued logic NOT implementation that behaves like the below:
 *
 * <pre>
 *  +------------+--------+
 *  |            | NOT(A) |
 *  +------------+--------+
 *  |    | true  |  false |
 *  | A  | false |  true  |
 *  |    | null  |  null  |
 *  +------------+--------+
 * </pre>
 */
public class Not extends Predicate {

    public static final String OPERATOR_NAME = "not";

    private final Predicate predicate;

    public Not(Predicate predicate) {
        this.predicate = Objects.requireNonNull(predicate);
    }

    @Override
    public Boolean evaluate(PredicateContext ctx) {
        Boolean result = predicate.evaluate(ctx);
        return result == null ? null : !result;
    }

    public static class Factory implements PredicateFactory {

        @Override
        public Predicate create(PredicateFactoryContext context, Expression expression) {
            if (!(expression instanceof Expression.Operator)) {
                return null;
            }

            Expression.Operator operator = (Expression.Operator) expression;
            if (!isApplicable(operator)) {
                return null;
            }

            RuntimeRulesBuilder factoryFacade = context.getFactoryFacade();
            Predicate predicate = factoryFacade.createPredicate(context, operator.getArguments().get(0));
            return new Not(predicate);
        }

        private boolean isApplicable(Expression.Operator operator) {
            return OPERATOR_NAME.equals(operator.getCall()) && operator.getArguments().size() == 1;
        }
    }
}
