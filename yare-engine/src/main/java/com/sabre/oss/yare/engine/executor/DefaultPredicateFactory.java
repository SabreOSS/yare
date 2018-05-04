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

package com.sabre.oss.yare.engine.executor;

import com.sabre.oss.yare.core.model.Expression;
import com.sabre.oss.yare.engine.executor.runtime.operator.logical.And;
import com.sabre.oss.yare.engine.executor.runtime.operator.logical.Not;
import com.sabre.oss.yare.engine.executor.runtime.operator.logical.Or;
import com.sabre.oss.yare.engine.executor.runtime.operator.relation.*;
import com.sabre.oss.yare.engine.executor.runtime.predicate.ChainedPredicateFactory;
import com.sabre.oss.yare.engine.executor.runtime.predicate.Predicate;
import com.sabre.oss.yare.engine.executor.runtime.predicate.PredicateFactory;
import com.sabre.oss.yare.engine.executor.runtime.predicate.PredicateFactoryContext;

import java.util.Objects;

import static java.util.Arrays.asList;

public class DefaultPredicateFactory implements PredicateFactory {

    private final PredicateFactory delegate;

    public DefaultPredicateFactory() {
        this(new ChainedPredicateFactory(asList(
                new Not.Factory(),
                new And.Factory(),
                new Or.Factory(),

                new IsTrue.Factory(),
                new IsFalse.Factory(),
                new IsNull.Factory(),

                new Eq(),
                new Lt(),
                new Le(),
                new Gt(),
                new Ge(),

                new MatchString.Factory(),

                new ContainsAll(),
                new ContainsAny.Factory()
        )));
    }

    public DefaultPredicateFactory(PredicateFactory delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public Predicate create(PredicateFactoryContext context, Expression expression) {
        return delegate.create(context, expression);
    }
}
