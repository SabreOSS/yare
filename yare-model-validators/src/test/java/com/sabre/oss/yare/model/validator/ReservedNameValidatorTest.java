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

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class ReservedNameValidatorTest {
    private ReservedValidator validator;
    private Set<String> reservedNames = new HashSet<>(Arrays.asList("ctx", "reservedName"));

    @BeforeEach
    void setUp() {
        validator = new ReservedValidator(false, reservedNames);
    }

    @Test
    void shouldFailWhenThereIsViolatedReservedName() {
        // given
        Fact fact = new Fact("ctx", null);
        Rule rule = new Rule(Collections.emptySet(),
                Collections.singletonList(fact),
                null,
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.reserved.reserved-names", "Naming Error: Reserved names are being used -> [ctx]")
        );
    }

    @Test
    void shouldFailWhenThereAreViolatedReservedNames() {
        // given
        Attribute attribute = new Attribute("ctx", null, null);
        Fact fact = new Fact("reservedName", null);
        Rule rule = new Rule(new LinkedHashSet<>(Collections.singletonList(attribute)),
                Collections.singletonList(fact),
                null,
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.reserved.reserved-names", "Naming Error: Reserved names are being used -> [ctx, reservedName]")
        );
    }

    @Test
    void shouldPassOnNonViolatedNames() {
        // given
        Fact fact = new Fact("testFact", null);
        Rule rule = new Rule(Collections.emptySet(),
                Collections.singletonList(fact),
                null,
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).isEmpty();
    }
}
