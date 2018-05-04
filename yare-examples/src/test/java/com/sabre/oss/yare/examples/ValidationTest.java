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

package com.sabre.oss.yare.examples;

import com.google.common.io.Resources;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.dsl.RuleDsl;
import com.sabre.oss.yare.examples.facts.Flight;
import com.sabre.oss.yare.model.validator.DefaultRuleValidator;
import com.sabre.oss.yare.model.validator.ValidationResult;
import com.sabre.oss.yare.model.validator.ValidationResults;
import com.sabre.oss.yare.serializer.xml.RuleToXmlConverter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

class ValidationTest {

    @Test
    void shouldNotThrowExceptionWithMalformedRuleAndDisabledValidation() {
        // given / when /then
        Rule rule = RuleDsl.ruleBuilder()
                .fact("flight", Flight.class)
                .predicate(
                        and(
                                lessOrEqual(
                                        field("flight.price", BigDecimal.class),
                                        value(new BigDecimal(100))
                                ),
                                less(
                                        function("getDiffInHours", Long.class,
                                                param("date", field("flight", "dateOfDeparture"))),
                                        value(24L)
                                )
                        )
                )
                .action("collect",
                        param("context", reference("ctx")),
                        param("fact", reference("flight")))
                .build(false);
    }

    @Test
    void shouldThrowExceptionWhenValidationEnabledAndRuleInvalid() {
        // given / when /then
        assertThatThrownBy(() ->
                RuleDsl.ruleBuilder()
                        .fact("flight", Flight.class)
                        .predicate(
                                and(
                                        lessOrEqual(
                                                field("flight.price", BigDecimal.class),
                                                value(new BigDecimal(100))
                                        ),
                                        less(
                                                function("getDiffInHours", Long.class,
                                                        param("date", field("flight", "dateOfDeparture"))),
                                                value(24L)
                                        )
                                )
                        )
                        .action("collect",
                                param("context", reference("ctx")),
                                param("fact", reference("flight")))
                        .build()
        ).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldValidateRuleOriginatingFromXmlUsingDefaultRuleValidator() throws IOException {
        // given
        String invalidRuleInXmlString = Resources.toString(
                Resources.getResource("exampleRules/invalidRule.xml"),
                Charset.defaultCharset());

        Rule unmarshalledRule = RuleToXmlConverter.getInstance().unmarshal(invalidRuleInXmlString);

        // when
        ValidationResults validationResults = DefaultRuleValidator.getRuleValidator().validate(unmarshalledRule);

        // then
        assertThat(validationResults.getResults()).containsExactly(
                ValidationResult.error("rule.attribute.rule-name-attribute-not-set", "Attribute Error: \"ruleName\" was not specified")
        );
    }
}
