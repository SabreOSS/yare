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

package com.sabre.oss.yare.engine.executor.runtime.predicate;

import com.sabre.oss.yare.core.model.Expression;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.engine.executor.RuntimeRulesBuilder;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * {@link PredicateFactoryContext} using for storing current creation state.
 */
public class PredicateFactoryContext {
    private final Rule rule;
    private final Deque<Expression> stack = new ArrayDeque<>(32);
    private final RuntimeRulesBuilder factoryFacade;

    public PredicateFactoryContext(Rule rule, RuntimeRulesBuilder factoryFacade) {
        this.rule = Objects.requireNonNull(rule);
        this.factoryFacade = factoryFacade;
    }

    public Rule getRule() {
        return rule;
    }

    public Deque<Expression> getExpressionsStack() {
        return stack;
    }

    public RuntimeRulesBuilder getFactoryFacade() {
        return factoryFacade;
    }
}
