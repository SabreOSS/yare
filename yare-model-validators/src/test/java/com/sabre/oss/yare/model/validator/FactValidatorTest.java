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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class FactValidatorTest {
    private FactValidator validator;

    @BeforeEach
    void setUp() {
        validator = new FactValidator(false);
    }

    @Test
    void shouldFailWhenNoFactsAreDefined() {
        // given
        Rule rule = new Rule(Collections.emptySet(), Collections.emptyList(), null, Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.fact.no-facts-defined", "Fact Error: no facts were specified")
        );
    }

    @Test
    void shouldFailWhenFactsAreNull() {
        // given
        Rule rule = new Rule(Collections.emptySet(), Arrays.asList(
                null,
                null
        ), null, Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.fact.fact-null", "Fact Error: fact cannot be null")
        );
    }

    @Test
    void shouldFailWhenFactsNamesAreNull() {
        // given
        Rule rule = new Rule(Collections.emptySet(), Arrays.asList(
                new Fact(null, String.class),
                new Fact(null, Integer.class)
        ), null, Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.fact.fact-name-null", "Fact Error: name cannot be null")
        );
    }

    @Test
    void shouldFailWhenFactsTypesAreNull() {
        // given
        Rule rule = new Rule(Collections.emptySet(), Arrays.asList(
                new Fact("fact1", null),
                new Fact("fact2", null)
        ), null, Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.fact.fact-type-null", "Fact Error: type cannot be null")
        );
    }

    @Test
    void shouldFailWhenFactsWithNullNamesAndNullTypes() {
        // given
        Rule rule = new Rule(Collections.emptySet(), Arrays.asList(
                new Fact(null, null),
                new Fact(null, null)
        ), null, Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.fact.fact-name-null", "Fact Error: name cannot be null"),
                ValidationResult.error("rule.fact.fact-type-null", "Fact Error: type cannot be null")
        );
    }

    @Test
    void shouldFailWhenMultipleNamesReferenceSameType() {
        // given
        Rule rule = new Rule(Collections.emptySet(), Arrays.asList(
                new Fact("fact1", String.class),
                new Fact("fact2", String.class)
        ), null, Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.fact.fact-type-duplicated", "Fact Error: multiple names defined for \"java.lang.String\" type: [fact1, fact2]")
        );
    }

    @Test
    void shouldFailWhenFactNamesAreNotUnique() {
        // given
        Rule rule = new Rule(Collections.emptySet(), Arrays.asList(
                new Fact("fact", String.class),
                new Fact("fact", Integer.class)
        ), null, Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.fact.fact-name-duplicated", "Fact Error: multiple types defined for \"fact\" name: [java.lang.String, java.lang.Integer]")
        );
    }

    @Test
    void shouldGenerateWarningWhenMultipleNameTypeFactsAreDefined() {
        // given
        Rule rule = new Rule(Collections.emptySet(), Arrays.asList(
                new Fact("fact", String.class),
                new Fact("fact", String.class),
                new Fact("fact", String.class)
        ), null, Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.warning("rule.fact.fact-duplicated", "Fact Error: fact (type: java.lang.String, name \"fact\") defined 3 times")
        );
    }

    @Test
    void shouldFailWithAllNameAndTypeRelatedMessages() {
        // given
        Rule rule = new Rule(Collections.emptySet(), Arrays.asList(
                new Fact("fact", String.class),
                new Fact("fact", String.class),
                new Fact("fact2", String.class),
                new Fact("fact", Integer.class)
        ), null, Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.fact.fact-type-duplicated", "Fact Error: multiple names defined for \"java.lang.String\" type: [fact, fact2]"),
                ValidationResult.error("rule.fact.fact-name-duplicated", "Fact Error: multiple types defined for \"fact\" name: [java.lang.String, java.lang.Integer]"),
                ValidationResult.warning("rule.fact.fact-duplicated", "Fact Error: fact (type: java.lang.String, name \"fact\") defined 2 times")
        );
    }
}
