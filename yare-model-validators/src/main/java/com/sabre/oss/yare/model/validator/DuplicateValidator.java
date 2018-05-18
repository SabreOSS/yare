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

import java.util.*;

import static java.util.stream.Collectors.toSet;


public class DuplicateValidator extends BaseValidator {

    DuplicateValidator(boolean failFast) {
        super(failFast);
    }

    @Override
    public ValidationResults validate(Rule rule) {
        ValidationResults results = new ValidationResults();
        checkIfThereAreDuplicateNames(rule, results);
        return results;
    }

    private void checkIfThereAreDuplicateNames(Rule rule, ValidationResults results) {
        Set<String> attributeNames = rule.getAttributes().stream()
                .map(Attribute::getName)
                .collect(toSet());
        Set<String> factNames = rule.getFacts().stream()
                .map(Fact::getIdentifier)
                .collect(toSet());
        Set<String> duplicatedNames = attributeNames.stream()
                .filter(factNames::contains)
                .collect(toSet());
        if (!duplicatedNames.isEmpty()) {
            append(results, ValidationResult.error("rule.duplicate.duplicate-names",
                    "Naming Error: There are duplicate names -> " + duplicatedNames));
        }
    }
}
