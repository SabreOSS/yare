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

package com.sabre.oss.yare.core.reference;

import com.sabre.oss.yare.core.model.Attribute;
import com.sabre.oss.yare.core.model.Expression;
import com.sabre.oss.yare.core.model.Fact;
import com.sabre.oss.yare.core.model.Rule;

import java.lang.reflect.Type;
import java.util.Optional;

public class ValueConverter<R> {
    private static final ChainedTypeExtractor chainedTypeExtractor = new ChainedTypeExtractor();
    private static final PlaceholderExtractor placeholderExtractor = new PlaceholderExtractor();
    private static final String CONTEXT_PATH = "ctx";

    private final ReferenceFactory<? extends R> referenceFactory;
    private final ValueFactory<? extends R> valueFactory;

    public ValueConverter(ReferenceFactory<? extends R> referenceFactory, ValueFactory<? extends R> valueFactory) {
        this.referenceFactory = referenceFactory;
        this.valueFactory = valueFactory;
    }

    public R create(Rule rule, Expression.Value value) {
        Optional<R> reference = tryCreateReference(rule, value);
        if (reference.isPresent()) {
            return reference.get();
        }
        Object v = placeholderExtractor.unescapePlaceholder(value)
                .orElse(value.getValue());
        return valueFactory.create(value.getName(), value.getType(), v);
    }

    private Optional<R> tryCreateReference(Rule rule, Expression.Value value) {
        return placeholderExtractor.extractPlaceholder(value)
                .map(s -> createReference(rule, value.getName(), s));
    }

    private R createReference(Rule rule, String expressionName, String path) {
        return extractType(rule, path)
                .map(type -> tryCreateRuleBasedReference(expressionName, path, type))
                .orElseGet(() -> tryCreateContextReference(expressionName, path));
    }

    private Optional<Type> extractType(Rule rule, String path) {
        String reference = extractReference(path);

        Attribute attribute = rule.getAttribute(reference);
        if (attribute != null) {
            return Optional.of(attribute)
                    .map(Attribute::getType);
        }
        return Optional.ofNullable(rule.getFact(reference))
                .map(Fact::getType);
    }

    private R tryCreateRuleBasedReference(String expressionName, String path, Type type) {
        Type referenceType = extractReferenceType(type, path);
        if (referenceType != null) {
            return referenceFactory.create(expressionName, type, referenceType, path);
        }
        return null;
    }

    private R tryCreateContextReference(String expressionName, String path) {
        if (CONTEXT_PATH.equals(path)) {
            return referenceFactory.create(expressionName, Expression.UNDEFINED, Expression.UNDEFINED, path);
        }
        return null;
    }

    private String extractReference(String path) {
        int dotIndex = path.indexOf('.');
        return dotIndex > -1 ? path.substring(0, dotIndex) : path;
    }

    private Type extractReferenceType(Type type, String path) {
        int dotIndex = path.indexOf('.');
        if (dotIndex > -1) {
            String fieldPath = path.substring(dotIndex + 1);
            try {
                return chainedTypeExtractor.findPathType(type, fieldPath);
            } catch (ChainedTypeExtractor.InvalidPathException e) {
                return null;
            }
        }
        return type;
    }
}
