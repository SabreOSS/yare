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

import com.sabre.oss.yare.core.model.Attribute;
import com.sabre.oss.yare.core.model.Expression;
import com.sabre.oss.yare.core.model.Fact;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.core.reference.ChainedTypeExtractor;
import com.sabre.oss.yare.core.reference.ValuePlaceholderConverter;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
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
        Expression.Value value = expression.as(Expression.Value.class);
        if (value != null) {
            if (ValuePlaceholderConverter.isReferenceCandidate(value)) {
                Matcher matcher = ValuePlaceholderConverter.PLACEHOLDER_PATTERN.matcher(value.getValue().toString());
                if (matcher.find()) {
                    String ref = matcher.group(1);
                    int dotIndex = ref.indexOf('.');
                    String reference = ref;
                    String path = null;
                    if (dotIndex > -1) {
                        reference = ref.substring(0, dotIndex);
                        path = ref.substring(dotIndex + 1);
                    }

                    checkReference(reference, path, results, localReferences);
                }
            }
        }
    }

    private void checkReference(String reference, String path, ValidationResults results, Map<String, Type> localReferences) {
        if (StringUtils.isEmpty(reference)) {
            append(results, ValidationResult.error("rule.ref.empty-reference", "Reference Error: empty reference used"));
        } else if (!localReferences.keySet().contains(reference)) {
            append(results, ValidationResult.error("rule.ref.unknown-reference", "Reference Error: unknown reference used -> " + reference));
        } else if (path != null) {
            if (hasEmptyPathSegment(path)) {
                append(results, ValidationResult.error("rule.ref.empty-field", "Reference Error: field cannot have empty segments"));
            } else {
                checkPath(reference, path, results, localReferences);
            }
        }
    }

    private boolean hasEmptyPathSegment(String path) {
        String[] pathParts = path.split("\\.", -1);
        return Stream.of(pathParts).anyMatch(StringUtils::isEmpty);
    }

    private void checkPath(String reference, String path, ValidationResults results, Map<String, Type> localReferences) {
        try {
            checkCollectionOperator(path, results, localReferences.get(reference));
        } catch (ChainedTypeExtractor.InvalidPathException e) {
            append(results, ValidationResult.error("rule.ref.unknown-field", "Reference Error: unknown field used -> " + reference + "." + path));
        }
    }

    private void checkCollectionOperator(String path, ValidationResults results, Type referenceType) {
        String[] pathParts = path.split("\\.", -1);
        Type currentType = referenceType;
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
