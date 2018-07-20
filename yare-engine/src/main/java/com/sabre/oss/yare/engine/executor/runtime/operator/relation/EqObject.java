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

/**
 * {@link EqObject} is a three-valued logic EQUALS operator, that behaves like the below:
 * <pre>
 *  +------------+----------------------+
 *  |            |          B           |
 *  |  A == B    +----------------------+
 *  |            |  value       | null  |
 *  +------------+----------------------+
 *  | A  | value |  true/false  | null  |
 *  |    | null  |  null        | null  |
 *  +------------+----------------------+
 * </pre>
 */
public class EqObject extends BiArgsPredicate {

    public EqObject(ValueProvider lOperandProvider, ValueProvider rOperandProvider) {
        super(lOperandProvider, rOperandProvider);
    }

    @Override
    protected final boolean applicable(Object left, Object right) {
        return true;
    }

    @Override
    protected final Boolean evaluate(Object left, Object right) {
        return left.equals(right);
    }

    public static class Factory extends OperatorFactory {
        private final boolean acceptObjectType;

        public Factory() {
            this(true);
        }

        public Factory(boolean acceptObjectType) {
            this.acceptObjectType = acceptObjectType;
        }

        private boolean isApplicable(Expression.Operator operator, ValueProvider[] valueProviders) {
            return Eq.OPERATOR_NAME.equals(operator.getCall()) &&
                    operator.getArguments().size() == 2 &&
                    (!Object.class.equals(TypeUtils.getRawType(valueProviders[0].getType(), null)) || acceptObjectType);
        }

        @Override
        public Predicate create(PredicateFactoryContext context, Expression.Operator operator, ValueProvider[] valueProviders) {
            if (!isApplicable(operator, valueProviders)) {
                return null;
            }
            return new EqObject(valueProviders[0], valueProviders[1]);
        }
    }
}
