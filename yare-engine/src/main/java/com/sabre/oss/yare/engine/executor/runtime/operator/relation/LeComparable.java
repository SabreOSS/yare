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
import com.sabre.oss.yare.engine.executor.runtime.operator.BiArgsPredicate;
import com.sabre.oss.yare.engine.executor.runtime.operator.OperatorFactory;
import com.sabre.oss.yare.engine.executor.runtime.predicate.Predicate;
import com.sabre.oss.yare.engine.executor.runtime.predicate.PredicateFactoryContext;
import com.sabre.oss.yare.engine.executor.runtime.value.ValueProvider;
import org.apache.commons.lang3.reflect.TypeUtils;

public class LeComparable extends BiArgsPredicate {

    public LeComparable(ValueProvider lOperandProvider, ValueProvider rOperandProvider) {
        super(lOperandProvider, rOperandProvider);
    }

    @Override
    protected boolean applicable(Object left, Object right) {
        return left instanceof Comparable && right instanceof Comparable;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Boolean evaluate(Object left, Object right) {
        return ((Comparable<Object>) left).compareTo(right) <= 0;
    }

    public static class Factory extends OperatorFactory {

        private boolean isApplicable(Expression.Operator operator, ValueProvider[] valueProviders) {
            return Le.OPERATOR_NAME.equals(operator.getCall()) &&
                    operator.getArguments().size() == 2 &&
                    valueProviders.length == 2 &&
                    Comparable.class.isAssignableFrom(TypeUtils.getRawType(valueProviders[0].getType(), null)) &&
                    Comparable.class.isAssignableFrom(TypeUtils.getRawType(valueProviders[1].getType(), null));
        }

        @Override
        public Predicate create(PredicateFactoryContext context, Expression.Operator operator, ValueProvider[] valueProviders) {
            return isApplicable(operator, valueProviders)
                    ? new LeComparable(valueProviders[0], valueProviders[1])
                    : null;
        }
    }
}
