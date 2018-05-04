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

package com.sabre.oss.yare.engine.executor.runtime.operator;

import com.sabre.oss.yare.core.model.Expression;
import com.sabre.oss.yare.engine.executor.RuntimeRulesBuilder;
import com.sabre.oss.yare.engine.executor.runtime.predicate.Predicate;
import com.sabre.oss.yare.engine.executor.runtime.predicate.PredicateFactory;
import com.sabre.oss.yare.engine.executor.runtime.predicate.PredicateFactoryContext;
import com.sabre.oss.yare.engine.executor.runtime.value.ValueProvider;

import java.util.List;

public abstract class OperatorFactory implements PredicateFactory {

    @Override
    public Predicate create(PredicateFactoryContext context, Expression expression) {
        return expression instanceof Expression.Operator
                ? create(context, (Expression.Operator) expression, prepareValueProviders(context, ((Expression.Operator) expression).getArguments()))
                : null;
    }

    public abstract Predicate create(PredicateFactoryContext context, Expression.Operator operator, ValueProvider[] valueProviders);

    protected boolean isApplicable(ValueProvider[] valueProviders) {
        return true;
    }

    protected Class<?> getArgumentType(Expression argument) {
        return argument instanceof Expression.Value && ((Expression.Value) argument).getValue() != null
                ? ((Expression.Value) argument).getValue().getClass()
                : (argument.getType() instanceof Class<?> ? (Class<?>) argument.getType() : null);
    }

    private ValueProvider[] prepareValueProviders(PredicateFactoryContext context, List<Expression> arguments) {
        ValueProvider[] providers = new ValueProvider[arguments.size()];
        RuntimeRulesBuilder factoryFacade = context.getFactoryFacade();
        for (int i = 0, len = arguments.size(); i < len; i++) {
            providers[i] = factoryFacade.createValueProvider(context, arguments.get(i));
        }
        return providers;
    }
}
