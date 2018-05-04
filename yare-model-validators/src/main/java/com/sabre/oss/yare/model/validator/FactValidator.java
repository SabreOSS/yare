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

import com.sabre.oss.yare.core.model.Fact;
import com.sabre.oss.yare.core.model.Rule;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class FactValidator extends BaseValidator {

    FactValidator(boolean failFast) {
        super(failFast);
    }

    @Override
    public ValidationResults validate(Rule rule) {
        ValidationResults results = new ValidationResults();
        checkIfAnyFactsWhereDefined(rule, results);
        checkIfFactsAreNotNull(rule, results);
        checkIfFactsNamesAreNotNull(rule, results);
        checkIfFactsTypesAreNotNull(rule, results);
        checkIfFactsTypesAreUnique(rule, results);
        checkIfFactsNamesAreUnique(rule, results);
        checkIfFactsTypeAndNameAreUnique(rule, results);
        return results;
    }

    private void checkIfAnyFactsWhereDefined(Rule rule, ValidationResults results) {
        if (rule.getFacts().isEmpty()) {
            append(results, ValidationResult.error("rule.fact.no-facts-defined", "Fact Error: no facts were specified"));
        }
    }

    private void checkIfFactsAreNotNull(Rule rule, ValidationResults results) {
        if (rule.getFacts().stream().anyMatch(Objects::isNull)) {
            append(results, ValidationResult.error("rule.fact.fact-null", "Fact Error: fact cannot be null"));
        }
    }

    private void checkIfFactsNamesAreNotNull(Rule rule, ValidationResults results) {
        boolean hasNullNames = rule.getFacts().stream()
                .filter(Objects::nonNull)
                .map(Fact::getIdentifier)
                .anyMatch(Objects::isNull);
        if (hasNullNames) {
            append(results, ValidationResult.error("rule.fact.fact-name-null", "Fact Error: name cannot be null"));
        }
    }

    private void checkIfFactsTypesAreNotNull(Rule rule, ValidationResults results) {
        boolean hasNullTypes = rule.getFacts().stream()
                .filter(Objects::nonNull)
                .map(Fact::getType)
                .anyMatch(Objects::isNull);
        if (hasNullTypes) {
            append(results, ValidationResult.error("rule.fact.fact-type-null", "Fact Error: type cannot be null"));
        }
    }

    private void checkIfFactsTypesAreUnique(Rule rule, ValidationResults results) {
        Map<String, Set<String>> typeToNames = new HashMap<>();
        for (Fact fact : rule.getFacts()) {
            if (fact != null) {
                String identifier = fact.getIdentifier();
                Type type = fact.getType();
                if (identifier != null && type != null) {
                    typeToNames
                            .computeIfAbsent(type.getTypeName(), t -> new LinkedHashSet<>())
                            .add(identifier);
                }
            }
        }
        for (Map.Entry<String, Set<String>> e : typeToNames.entrySet()) {
            String type = e.getKey();
            Set<String> names = e.getValue();
            if (names.size() > 1) {
                append(results, ValidationResult.error("rule.fact.fact-type-duplicated",
                        format("Fact Error: multiple names defined for \"%s\" type: %s", type, names)));
            }
        }
    }

    private void checkIfFactsNamesAreUnique(Rule rule, ValidationResults results) {
        Map<String, Set<String>> nameToTypes = new HashMap<>();
        for (Fact fact : rule.getFacts()) {
            if (fact != null) {
                String identifier = fact.getIdentifier();
                Type type = fact.getType();
                if (identifier != null && type != null) {
                    nameToTypes
                            .computeIfAbsent(fact.getIdentifier(), t -> new LinkedHashSet<>())
                            .add(fact.getType().getTypeName());
                }
            }
        }
        for (Map.Entry<String, Set<String>> e : nameToTypes.entrySet()) {
            String name = e.getKey();
            Set<String> types = e.getValue();
            if (types.size() > 1) {
                append(results, ValidationResult.error("rule.fact.fact-name-duplicated",
                        format("Fact Error: multiple types defined for \"%s\" name: %s", name, types)));
            }
        }
    }

    private void checkIfFactsTypeAndNameAreUnique(Rule rule, ValidationResults results) {
        Map<Fact, Integer> factDuplications = rule.getFacts().stream()
                .filter(Objects::nonNull)
                .filter(f -> f.getIdentifier() != null)
                .filter(f -> f.getType() != null)
                .collect(Collectors.toMap(
                        Function.identity(),
                        f -> 1,
                        Math::addExact
                ));
        for (Map.Entry<Fact, Integer> e : factDuplications.entrySet()) {
            Fact fact = e.getKey();
            Integer declarations = e.getValue();
            if (declarations > 1) {
                append(results, ValidationResult.warning("rule.fact.fact-duplicated",
                        format("Fact Error: fact (type: %s, name \"%s\") defined %d times", fact.getType().getTypeName(), fact.getIdentifier(), declarations)));
            }
        }
    }
}
