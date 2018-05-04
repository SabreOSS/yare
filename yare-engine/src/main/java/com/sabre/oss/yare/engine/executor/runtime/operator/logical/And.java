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
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * {@link And} is a three-valued logical AND operator:
 *
 * <pre>
 *  +------------+------------------------+
 *  |            |           B            |
 *  |  AND(A,B)  +------------------------+
 *  |            |  true  | false | null  |
 *  +------------+------------------------+
 *  |    | true  |  true  | false | null  |
 *  | A  | false |  false | false | false |
 *  |    | null  |  null  | false | null  |
 *  +------------+------------------------+
 * </pre>
 */
public final class And extends Predicate {

    public static final String OPERATOR_NAME = "and";

    private final List<Predicate> predicates;

    public And(List<Predicate> predicates) {
        this.predicates = Validate.noNullElements(predicates, "Predicate(s) must not be null");
    }

    @Override
    public Boolean evaluate(PredicateContext ctx) {
        boolean anyUnknownResult = false;
        for (Predicate predicate : predicates) {
            Boolean result = predicate.evaluate(ctx);
            if (result == null) {
                anyUnknownResult = true;
            } else if (result == Boolean.FALSE) {
                return false;
            }
        }
        return anyUnknownResult ? null : true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof And)) {
            return false;
        }
        And that = (And) o;
        return Objects.equals(predicates, that.predicates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(predicates);
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
            return new And(predicates);
        }

        private boolean isApplicable(Expression.Operator operator) {
            return OPERATOR_NAME.equals(operator.getCall());
        }
    }
}
