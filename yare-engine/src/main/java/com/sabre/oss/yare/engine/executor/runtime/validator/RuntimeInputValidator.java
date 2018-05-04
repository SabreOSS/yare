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

package com.sabre.oss.yare.engine.executor.runtime.validator;

import com.sabre.oss.yare.core.model.Fact;
import com.sabre.oss.yare.engine.executor.runtime.predicate.Predicate;
import com.sabre.oss.yare.engine.executor.runtime.predicate.PredicateContext;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks (in runtime) if {@code context} contains all facts required by rule - if not, rule is not evaluated.
 */
public final class RuntimeInputValidator extends Predicate {
    private static final Object NOT_DEFINED = new Object();

    private final Predicate delegate;
    private final Set<String> requiredFactIdentifiers;

    private RuntimeInputValidator(Predicate delegate, List<Fact> requiredFacts) {
        this.delegate = Objects.requireNonNull(delegate);
        this.requiredFactIdentifiers = requiredFacts.stream().map(Fact::getIdentifier).collect(Collectors.toSet());
    }

    public static Predicate of(List<Fact> requiredFacts, Predicate delegate) {
        return new RuntimeInputValidator(delegate, requiredFacts);
    }

    @Override
    public Boolean evaluate(PredicateContext context) {
        for (String identifier : requiredFactIdentifiers) {
            if (NOT_DEFINED.equals(context.resolve(identifier, NOT_DEFINED))) {
                return Boolean.FALSE;
            }
        }
        return delegate.evaluate(context);
    }
}
