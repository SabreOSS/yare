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
import com.sabre.oss.yare.core.model.Rule;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

public class AttributeValidator extends BaseValidator {

    AttributeValidator(boolean failFast) {
        super(failFast);
    }

    @Override
    public ValidationResults validate(Rule rule) {
        ValidationResults results = new ValidationResults();
        checkEmptyOrNullNames(rule, results);
        checkName(rule, results);
        checkUniquenessOfAttributes(rule, results);
        checkDates(rule, results);
        return results;
    }

    private void checkEmptyOrNullNames(Rule rule, ValidationResults results) {
        if (rule.getAttributes().stream().map(Attribute::getName).anyMatch(Objects::isNull)) {
            append(results, ValidationResult.error("rule.attribute.attributes-null-name", "Attribute Error: attribute name is null"));
        }
        if (rule.getAttributes().stream().map(Attribute::getName).anyMatch(""::equals)) {
            append(results, ValidationResult.error("rule.attribute.attributes-empty-name", "Attribute Error: attribute name is empty"));
        }
    }

    private void checkName(Rule rule, ValidationResults results) {
        if (rule.getAttributes().stream().noneMatch(a -> "ruleName".equals(a.getName()))) {
            append(results, ValidationResult.error("rule.attribute.rule-name-attribute-not-set", "Attribute Error: \"ruleName\" was not specified"));
        }
    }

    private void checkUniquenessOfAttributes(Rule rule, ValidationResults results) {
        if (rule.getAttributes().stream().map(Attribute::getName).collect(Collectors.toSet()).size() < rule.getAttributes().size()) {
            append(results, ValidationResult.error("rule.attribute.attributes-not-unique", "Attribute Error: attributes are not unique"));
        }
    }

    private void checkDates(Rule rule, ValidationResults results) {
        boolean hasStartDate = rule.getAttributes().stream().map(Attribute::getName).anyMatch("startDate"::equals);
        boolean hasExpireDate = rule.getAttributes().stream().map(Attribute::getName).anyMatch("expireDate"::equals);
        if (hasStartDate && !hasExpireDate) {
            append(results, ValidationResult.error("rule.attribute.expire-date-attribute-not-defined", "Attribute Error: \"expireDate\" was not specified while \"startDate\" was"));
        } else if (!hasStartDate && hasExpireDate) {
            append(results, ValidationResult.error("rule.attribute.start-date-attribute-not-defined", "Attribute Error: \"startDate\"  was not specified while \"expireDate\" was"));
        } else if (hasStartDate) {
            Date startDate = Date.from(ZonedDateTime.parse(rule.getAttribute("startDate").getValue().toString()).toInstant());
            Date expireDate = Date.from(ZonedDateTime.parse(rule.getAttribute("expireDate").getValue().toString()).toInstant());
            if (startDate.after(expireDate)) {
                append(results, ValidationResult.error("rule.attribute.start-date-after-expire-date", "Attribute Error: \"startDate\" is after \"expireDate\""));
            }
        }
    }
}
