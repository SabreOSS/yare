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

import com.sabre.oss.yare.core.model.Expression.Action;
import com.sabre.oss.yare.core.model.Rule;

import java.util.Objects;

public class ActionValidator extends BaseValidator {

    ActionValidator(boolean failFast) {
        super(failFast);
    }

    @Override
    public ValidationResults validate(Rule rule) {
        ValidationResults results = new ValidationResults();
        checkIfActionExists(rule, results);
        checkIfActionsAreNotNull(rule, results);
        checkIfActionsNamesAreNotNull(rule, results);
        checkIfActionsCallsAreNotNull(rule, results);
        checkIfActionsArgumentsAreNotNull(rule, results);
        checkIfActionsArgumentsDoNotContainNulls(rule, results);
        return results;
    }

    private void checkIfActionExists(Rule rule, ValidationResults results) {
        if (rule.getActions().isEmpty()) {
            append(results, ValidationResult.error("rule.action.not-defined", "Action Error: no actions were specified"));
        }
    }

    private void checkIfActionsAreNotNull(Rule rule, ValidationResults results) {
        if (rule.getActions().stream().anyMatch(Objects::isNull)) {
            append(results, ValidationResult.error("rule.action.action-null", "Action Error: action cannot be null"));
        }
    }

    private void checkIfActionsNamesAreNotNull(Rule rule, ValidationResults results) {
        boolean hasNullNames = rule.getActions().stream()
                .filter(Objects::nonNull)
                .map(Action::getName)
                .anyMatch(Objects::isNull);
        if (hasNullNames) {
            append(results, ValidationResult.error("rule.action.action-name-null", "Action Error: name cannot be null"));
        }
    }

    private void checkIfActionsCallsAreNotNull(Rule rule, ValidationResults results) {
        boolean hasNullCalls = rule.getActions().stream()
                .filter(Objects::nonNull)
                .map(Action::getCall)
                .anyMatch(Objects::isNull);
        if (hasNullCalls) {
            append(results, ValidationResult.error("rule.action.action-call-null", "Action Error: call cannot be null"));
        }
    }

    private void checkIfActionsArgumentsAreNotNull(Rule rule, ValidationResults results) {
        boolean hasNullArguments = rule.getActions().stream()
                .filter(Objects::nonNull)
                .map(Action::getArguments)
                .anyMatch(Objects::isNull);
        if (hasNullArguments) {
            append(results, ValidationResult.error("rule.action.action-arguments-null", "Action Error: arguments cannot be null"));
        }
    }

    private void checkIfActionsArgumentsDoNotContainNulls(Rule rule, ValidationResults results) {
        boolean containsNullArguments = rule.getActions().stream()
                .filter(Objects::nonNull)
                .filter(a -> a.getArguments() != null)
                .flatMap(a -> a.getArguments().stream())
                .anyMatch(Objects::isNull);
        if (containsNullArguments) {
            append(results, ValidationResult.error("rule.action.action-arguments-contain-null", "Action Error: arguments cannot contain null elements"));
        }
    }
}
