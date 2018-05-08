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

package com.sabre.oss.yare.model.validator;

import com.sabre.oss.yare.core.reference.ChainedTypeExtractor;
import com.sabre.oss.yare.core.model.Attribute;
import com.sabre.oss.yare.core.model.Expression;
import com.sabre.oss.yare.core.model.Fact;
import com.sabre.oss.yare.core.model.Rule;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ReferenceValidator extends BaseValidator {
    private final ChainedTypeExtractor chainedTypeExtractor;

    public ReferenceValidator(boolean failFast, ChainedTypeExtractor chainedTypeExtractor) {
        super(failFast);
        this.chainedTypeExtractor = chainedTypeExtractor;
    }

    @Override
    public ValidationResults validate(Rule rule) {
        ValidationResults results = new ValidationResults();
        Map<String, Type> localReferences = initReferences(rule);
        checkReferencesInPredicate(rule, results, localReferences);
        checkReferencesInActions(rule, results, localReferences);
        return results;
    }

    private Map<String, Type> initReferences(Rule rule) {
        Map<String, Type> localReferences = new HashMap<>();
        for (Fact fact : rule.getFacts()) {
            localReferences.put(fact.getIdentifier(), fact.getType());
        }
        for (Attribute attribute : rule.getAttributes()) {
            localReferences.put(attribute.getName(), attribute.getType());
        }
        localReferences.put("ruleName", String.class);
        localReferences.put("ctx", Object.class);
        return localReferences;
    }

    private void checkReferencesInPredicate(Rule rule, ValidationResults results, Map<String, Type> localReferences) {
        Expression predicate = rule.getPredicate();
        if (predicate != null) {
            checkExpression(predicate, results, localReferences);
        } else {
            append(results, ValidationResult.error("rule.predicate.not-defined", "Predicate Error: predicate was not specified"));
        }
    }

    private void checkReferencesInActions(Rule rule, ValidationResults results, Map<String, Type> localReferences) {
        if (rule.getActions() != null) {
            for (Expression.Action action : rule.getActions()) {
                checkExpression(action, results, localReferences);
            }
        }
    }

    private void checkExpression(Expression expression, ValidationResults results, Map<String, Type> localReferences) {
        Expression.Invocation invocation = expression.as(Expression.Invocation.class);
        if (invocation != null) {
            for (Expression e : invocation.getArguments()) {
                checkExpression(e, results, localReferences);
            }
        }
        Expression.Reference reference = expression.as(Expression.Reference.class);
        if (reference != null) {
            checkReference(reference, results, localReferences);
        }
    }

    private void checkReference(Expression.Reference reference, ValidationResults results, Map<String, Type> localReferences) {
        if (StringUtils.isEmpty(reference.getReference())) {
            append(results, ValidationResult.error("rule.ref.empty-reference", "Reference Error: empty reference used"));
        } else if (!localReferences.keySet().contains(reference.getReference())) {
            append(results, ValidationResult.error("rule.ref.unknown-reference", "Reference Error: unknown reference used -> " + reference.getReference()));
        } else if (!StringUtils.isEmpty(reference.getPath())) {
            if (hasEmptyPathSegment(reference.getPath())) {
                append(results, ValidationResult.error("rule.ref.empty-field", "Reference Error: field cannot have empty segments"));
            } else {
                checkPath(reference, results);
            }
        }
    }

    private boolean hasEmptyPathSegment(String path) {
        String[] pathParts = path.split("\\.", -1);
        return Stream.of(pathParts).anyMatch(StringUtils::isEmpty);
    }

    private void checkPath(Expression.Reference reference, ValidationResults results) {
        try {
            checkCollectionOperator(reference, results);
        } catch (ChainedTypeExtractor.InvalidPathException e) {
            append(results, ValidationResult.error("rule.ref.unknown-field", "Reference Error: unknown field used -> " + reference.getReference() + "." + reference.getPath()));
        }
    }

    private void checkCollectionOperator(Expression.Reference reference, ValidationResults results) {
        String[] pathParts = reference.getPath().split("\\.", -1);
        Type currentType = reference.getReferenceType();
        for (String pathPart : pathParts) {
            currentType = chainedTypeExtractor.findPathType(currentType, pathPart);
            if (!isCollection(currentType) && pathPart.contains("[*]")) {
                append(results, ValidationResult.error("rule.ref.non-collection-field", "Reference Error: field is not collection type"));
            }
        }
    }

    private boolean isCollection(Type type) {
        return type instanceof ParameterizedType &&
                Collection.class.isAssignableFrom((Class<?>) ((ParameterizedType) type).getRawType());
    }
}
