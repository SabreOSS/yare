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

import com.sabre.oss.yare.core.model.Rule;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CombinedValidatorTest {

    @Test
    void shouldReturnEmptyValidationResultsOnEmptyValidators() {
        // given
        CombinedValidator validator = new CombinedValidator(Collections.emptyList());

        // when
        ValidationResults results = validator.validate(null);

        // then
        assertThat(results.getResults()).isEmpty();
    }

    @Test
    void shouldReturnMessagesFromAllValidators() {
        // given
        CombinedValidator validator = new CombinedValidator(Arrays.asList(
                r -> new ValidationResults(Arrays.asList(
                        ValidationResult.info("info", "Info"),
                        ValidationResult.warning("warning", "Warning")
                )),
                r -> new ValidationResults(Collections.singletonList(
                        ValidationResult.error("error", "Error")
                ))
        ));

        // when
        ValidationResults results = validator.validate(null);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.info("info", "Info"),
                ValidationResult.warning("warning", "Warning"),
                ValidationResult.error("error", "Error")
        );
    }

    @Test
    void shouldPassRuleReference() {
        // given
        Rule rule = new Rule(Collections.emptySet(), Collections.emptyList(), null, Collections.emptyList());
        List<Rule> storedRules = new ArrayList<>();
        CombinedValidator validator = new CombinedValidator(Arrays.asList(
                r -> {
                    storedRules.add(r);
                    return new ValidationResults();
                },
                r -> {
                    storedRules.add(r);
                    return new ValidationResults();
                }
        ));

        // when
        validator.validate(rule);

        // then
        assertThat(storedRules).containsExactly(rule, rule);
    }
}
