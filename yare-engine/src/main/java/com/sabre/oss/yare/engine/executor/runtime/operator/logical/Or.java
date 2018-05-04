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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * {@link Or} is a three-valued logical OR operator:
 * <pre>
 *  +------------+------------------------+
 *  |            |           B            |
 *  |  OR(A,B)   +------------------------+
 *  |            |  true  | false | null  |
 *  +------------+------------------------+
 *  |    | true  |  true  | true  | true  |
 *  | A  | false |  true  | false | null  |
 *  |    | null  |  true  | null  | null  |
 *  +------------+------------------------+
 * </pre>
 */
public final class Or extends Predicate {

    public static final String OPERATOR_NAME = "or";

    private final List<Predicate> predicates;

    public Or(List<Predicate> predicates) {
        this.predicates = Validate.noNullElements(predicates, "Predicate(s) must not be null");
    }

    @Override
    public Boolean evaluate(PredicateContext ctx) {
        boolean anyUnknownResult = false;
        for (Predicate predicate : predicates) {
            Boolean result = predicate.evaluate(ctx);
            if (result == null) {
                anyUnknownResult = true;
            } else if (result) {
                return true;
            }
        }
        return anyUnknownResult ? null : false;
    }

    public int hashCode() {
        return Objects.hashCode(predicates);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Or)) {
            return false;
        }
        Or other = (Or) obj;
        return CollectionUtils.isEqualCollection(other.predicates, this.predicates);
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
            ArrayList<Predicate> predicates = operator.getArguments().stream()
                    .map(e -> factoryFacade.createPredicate(context, e))
                    .collect(Collectors.toCollection(ArrayList::new));
            return new Or(predicates);
        }

        private boolean isApplicable(Expression.Operator operator) {
            return OPERATOR_NAME.equals(operator.getCall());
        }
    }
}
