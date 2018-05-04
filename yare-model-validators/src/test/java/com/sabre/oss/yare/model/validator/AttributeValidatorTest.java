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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;

import static org.assertj.core.api.Assertions.assertThat;

class AttributeValidatorTest {
    private AttributeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new AttributeValidator(false);
    }

    @Test
    void shouldFailOnMissingRuleName() {
        // given
        Rule rule = new Rule(Collections.emptySet(), Collections.emptyList(), null, Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.attribute.rule-name-attribute-not-set", "Attribute Error: \"ruleName\" was not specified")
        );
    }

    @Test
    void shouldFailOnDuplicatedAttribute() {
        // given
        Rule rule = new Rule(new LinkedHashSet<>(Arrays.asList(
                new Attribute("ruleName", String.class, "ruleName"),
                new Attribute("duplicated", String.class, "value1"),
                new Attribute("duplicated", String.class, "value2")
        )), Collections.emptyList(), null, Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.attribute.attributes-not-unique", "Attribute Error: attributes are not unique")
        );
    }

    @Test
    void shouldFailOnNullAttributeName() {
        // given
        Rule rule = new Rule(new LinkedHashSet<>(Arrays.asList(
                new Attribute("ruleName", String.class, "ruleName"),
                new Attribute(null, String.class, "empty")
        )), Collections.emptyList(), null, Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.attribute.attributes-null-name", "Attribute Error: attribute name is null")
        );
    }

    @Test
    void shouldFailOnEmptyAttributeName() {
        // given
        Rule rule = new Rule(new LinkedHashSet<>(Arrays.asList(
                new Attribute("ruleName", String.class, "ruleName"),
                new Attribute("", String.class, "empty")
        )), Collections.emptyList(), null, Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.attribute.attributes-empty-name", "Attribute Error: attribute name is empty")
        );
    }

    @Test
    void shouldFailOnStartDateOnlySpecified() {
        // given
        Rule rule = new Rule(new LinkedHashSet<>(Arrays.asList(
                new Attribute("ruleName", String.class, "ruleName"),
                new Attribute("startDate", ZonedDateTime.class, ZonedDateTime.parse("2012-01-05T00:00:00+12:34"))
        )), Collections.emptyList(), null, Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.attribute.expire-date-attribute-not-defined", "Attribute Error: \"expireDate\" was not specified while \"startDate\" was")
        );
    }

    @Test
    void shouldFailOnExpireDateOnlySpecified() {
        // given
        Rule rule = new Rule(new LinkedHashSet<>(Arrays.asList(
                new Attribute("ruleName", String.class, "ruleName"),
                new Attribute("expireDate", ZonedDateTime.class, ZonedDateTime.parse("2012-01-05T00:00:00+12:34"))
        )), Collections.emptyList(), null, Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.attribute.start-date-attribute-not-defined", "Attribute Error: \"startDate\"  was not specified while \"expireDate\" was")
        );
    }

    @Test
    void shouldFailOnStartDateAfterExpireDate() {
        // given
        Rule rule = new Rule(new LinkedHashSet<>(Arrays.asList(
                new Attribute("ruleName", String.class, "ruleName"),
                new Attribute("startDate", ZonedDateTime.class, ZonedDateTime.parse("2012-01-06T00:00:00+12:34")),
                new Attribute("expireDate", ZonedDateTime.class, ZonedDateTime.parse("2012-01-05T00:00:00+12:34"))
        )), Collections.emptyList(), null, Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.attribute.start-date-after-expire-date", "Attribute Error: \"startDate\" is after \"expireDate\"")
        );
    }

    @Test
    void shouldPassStartDateBeforeExpireDate() {
        // given
        Rule rule = new Rule(new LinkedHashSet<>(Arrays.asList(
                new Attribute("ruleName", String.class, "ruleName"),
                new Attribute("startDate", ZonedDateTime.class, ZonedDateTime.parse("2012-01-04T00:00:00+12:34")),
                new Attribute("expireDate", ZonedDateTime.class, ZonedDateTime.parse("2012-01-05T00:00:00+12:34"))
        )), Collections.emptyList(), null, Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).isEmpty();
    }
}
