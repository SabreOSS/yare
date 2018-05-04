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
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

public class ReferenceValidator extends BaseValidator {
    private final ConcurrentMap<Type, ConcurrentMap<String, Type>> typeProperties = new ConcurrentHashMap<>();

    public ReferenceValidator(boolean failFast) {
        super(failFast);
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
            String[] path = reference.getPath().split("\\.", -1);

            if (hasEmptyPathSegment(path)) {
                append(results, ValidationResult.error("rule.ref.empty-field", "Reference Error: field cannot have empty segments"));
            } else if (findPathType(reference.getReferenceType(), results, path) == null) {
                append(results, ValidationResult.error("rule.ref.unknown-field", "Reference Error: unknown field used -> " + reference.getReference() + "." + reference.getPath()));
            }
        }
    }

    private boolean hasEmptyPathSegment(String[] path) {
        return Stream.of(path).anyMatch(StringUtils::isEmpty);
    }

    private Type findPathType(Type type, ValidationResults results, String[] path) {
        Type currentType = type;
        for (String pathPart : path) {
            Type finalCurrentType = currentType;
            currentType = typeProperties.computeIfAbsent(currentType, k -> new ConcurrentHashMap<>())
                    .computeIfAbsent(pathPart, s -> computeTypeProperties(finalCurrentType, s));
            if (currentType == null) {
                break;
            }
            if (!isCollection(currentType) && pathPart.contains("[*]")) {
                append(results, ValidationResult.error("rule.ref.non-collection-field", "Reference Error: field is not collection type"));
            }
        }
        return currentType;
    }

    private Type computeTypeProperties(Type type, String pathPart) {
        return computeTypeOfReference(unwrapCollectionType(type), pathPart.replace("[*]", ""));
    }

    private Type unwrapCollectionType(Type type) {
        return isCollection(type) ? getTypeOfElementInList(type) : type;
    }

    private boolean isCollection(Type type) {
        return type instanceof ParameterizedType &&
                Collection.class.isAssignableFrom((Class<?>) ((ParameterizedType) type).getRawType());
    }

    private Type getTypeOfElementInList(Type listType) {
        return ((ParameterizedType) listType).getActualTypeArguments()[0];
    }

    private Type computeTypeOfReference(Type type, String pathPart) {
        if (isTypeImplementingMap(type)) {
            return getTypeOfValueInMap(type);
        }
        Optional<java.lang.reflect.Field> fieldOptional = getFieldOptional((Class<?>) type, pathPart);
        if (fieldOptional.isPresent()) {
            return fieldOptional.get().getGenericType();
        }
        Optional<Method> methodOptional = getMethodOptional((Class<?>) type, pathPart);
        if (methodOptional.isPresent()) {
            return methodOptional.get().getGenericReturnType();
        }
        return null;
    }

    private boolean isTypeImplementingMap(Type type) {
        return Map.class.isAssignableFrom((Class<?>) type);
    }

    private Type getTypeOfValueInMap(Type type) {
        return ((ParameterizedType) ((Class<?>) type).getGenericSuperclass()).getActualTypeArguments()[1];
    }

    private Optional<java.lang.reflect.Field> getFieldOptional(Class<?> type, String fieldName) {
        return Arrays.stream(type.getFields())
                .filter(f -> Modifier.isPublic(f.getModifiers()))
                .filter(f -> f.getName().equals(fieldName))
                .findFirst();
    }

    private Optional<Method> getMethodOptional(Class<?> type, String fieldName) {
        return Arrays.stream(type.getMethods())
                .filter(m -> m.getParameterCount() == 0)
                .filter(m -> (m.getName().equals("get" + capitalize(fieldName)) ||
                        m.getName().equals("is" + capitalize(fieldName))))
                .findFirst();
    }

    private String capitalize(String fieldName) {
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }
}
