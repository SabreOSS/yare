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
import com.sabre.oss.yare.engine.executor.runtime.predicate.Predicate;
import com.sabre.oss.yare.engine.executor.runtime.predicate.PredicateContext;
import com.sabre.oss.yare.engine.executor.runtime.predicate.PredicateFactoryContext;
import com.sabre.oss.yare.engine.executor.runtime.value.ConstantValueProvider;
import com.sabre.oss.yare.engine.executor.runtime.value.ValueProvider;

import java.util.Objects;
import java.util.regex.Pattern;

public class MatchString extends Predicate {
    private static final String OPERATOR_NAME = "match";

    private final ValueProvider valueProvider;
    private final Pattern pattern;

    public MatchString(ValueProvider lOperandProvider, String regexp) {
        this.valueProvider = Objects.requireNonNull(lOperandProvider);
        this.pattern = Pattern.compile(Objects.requireNonNull(regexp));
    }

    @Override
    public Boolean evaluate(PredicateContext context) {
        Object value = valueProvider.get(context);
        return value instanceof String
                ? pattern.matcher((String) value).matches()
                : null;
    }

    public static class Factory extends OperatorFactory {

        private boolean isApplicable(Expression.Operator operator, ValueProvider[] valueProviders) {
            return OPERATOR_NAME.equals(operator.getCall()) &&
                    operator.getArguments().size() == 2 &&
                    operator.getArguments().get(1) instanceof Expression.Value &&
                    valueProviders.length == 2 &&
                    valueProviders[1] instanceof ConstantValueProvider &&
                    valueProviders[1].get(null) instanceof String;
        }

        @Override
        public Predicate create(PredicateFactoryContext context, Expression.Operator operator, ValueProvider[] valueProviders) {
            if (!isApplicable(operator, valueProviders)) {
                return null;
            }
            return new MatchString(valueProviders[0], (String) valueProviders[1].get(null));
        }
    }
}
