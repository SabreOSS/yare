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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;

import static org.assertj.core.api.Assertions.assertThat;

class DuplicateValidatorTest {
    private DuplicateValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DuplicateValidator(false);
    }

    @Test
    void shouldFailWhenThereAreDuplicatedNames() {
        // given
        Attribute attribute = new Attribute("duplicatedName", null, null);
        Fact fact = new Fact("duplicatedName", null);
        Rule rule = new Rule(new LinkedHashSet<>(Collections.singletonList(attribute)),
                Collections.singletonList(fact),
                null,
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.duplicate.duplicate-names", "Naming Error: There are duplicate names -> [duplicatedName]")
        );
    }

    @Test
    void shouldFailWhenThereAreManyDuplicatedNames() {
        // given
        Attribute attribute1 = new Attribute("duplicatedName1", null, null);
        Attribute attribute2 = new Attribute("duplicatedName2", null, null);
        Fact fact1 = new Fact("duplicatedName1", null);
        Fact fact2 = new Fact("duplicatedName2", null);
        Rule rule = new Rule(new LinkedHashSet<>(Arrays.asList(attribute1, attribute2)),
                Arrays.asList(fact1, fact2),
                null,
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.duplicate.duplicate-names", "Naming Error: There are duplicate names -> [duplicatedName1, duplicatedName2]")
        );
    }

    @Test
    void shouldPassOnNonDuplicatedNames() {
        // given
        Attribute attribute = new Attribute("nonDuplicatedAttributeName", null, null);
        Fact fact = new Fact("nonDuplicatedFactName", null);
        Rule rule = new Rule(new LinkedHashSet<>(Collections.singletonList(attribute)),
                Collections.singletonList(fact),
                null,
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).isEmpty();
    }
}
