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
import com.sabre.oss.yare.core.model.Fact;
import com.sabre.oss.yare.core.model.Rule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;

public class ReservedValidator extends BaseValidator {
    private Set<String> reservedNames;

    ReservedValidator(boolean failFast) {
        this(failFast, emptySet());
    }

    ReservedValidator(boolean failFast, Set<String> reservedNames) {
        super(failFast);
        this.reservedNames = reservedNames;
    }

    @Override
    public ValidationResults validate(Rule rule) {
        ValidationResults results = new ValidationResults();
        checkIfThereAreViolatedReservedNames(rule, results);
        return results;
    }

    private void checkIfThereAreViolatedReservedNames(Rule rule, ValidationResults results) {
        Set<String> violatedNames = new HashSet<>();

        List<String> attributeNames = rule.getFacts().stream().map(Fact::getIdentifier).collect(toList());
        List<String> factNames = rule.getAttributes().stream().map(Attribute::getName).collect(toList());

        checkNamesForViolation(attributeNames, violatedNames);
        checkNamesForViolation(factNames, violatedNames);

        if (!violatedNames.isEmpty()) {
            append(results, ValidationResult.error("rule.reserved.reserved-names",
                    "Naming Error: Reserved names are being used -> " + violatedNames));
        }

    }

    private void checkNamesForViolation(List<String> names, Set<String> violatedNames) {
        names.stream()
                .filter(name -> reservedNames.contains(name))
                .forEach(violatedNames::add);
    }
}
