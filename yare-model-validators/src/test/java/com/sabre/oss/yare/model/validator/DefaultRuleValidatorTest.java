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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static com.sabre.oss.yare.core.model.ExpressionFactory.operatorOf;
import static com.sabre.oss.yare.core.model.ExpressionFactory.valueOf;
import static com.sabre.oss.yare.model.validator.ValidationResult.error;
import static org.assertj.core.api.Assertions.assertThat;

class DefaultRuleValidatorTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = DefaultRuleValidator.getRuleValidator();
    }

    @Test
    void shouldGatherValidationResultsFromAllValidators() {
        // given
        Rule rule = new Rule(Collections.emptySet(), Collections.emptyList(),
                operatorOf(null, Boolean.class, "is-true",
                        valueOf(null, String.class, "${missing}")
                ),
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                error("rule.attribute.rule-name-attribute-not-set", "Attribute Error: \"ruleName\" was not specified"),
                error("rule.fact.no-facts-defined", "Fact Error: no facts were specified"),
                error("rule.ref.unknown-reference", "Reference Error: unknown reference used -> missing"),
                error("rule.action.not-defined", "Action Error: no actions were specified")
        );
    }
}
