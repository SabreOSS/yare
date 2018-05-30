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
import com.sabre.oss.yare.model.validator.DefaultRuleValidator;
import com.sabre.oss.yare.model.validator.ValidationResult;
import com.sabre.oss.yare.model.validator.ValidationResults;
import com.sabre.oss.yare.serializer.xml.RuleToXmlConverter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

class ValidationTest {
    private static final String COLLECT = "collect";

    @Test
    void shouldNotThrowExceptionWithMalformedRuleAndDisabledValidation() {
        // given / when /then
        RuleDsl.ruleBuilder()
                .fact("flight", Flight.class)
                .predicate(
                        and(
                                lessOrEqual(
                                        value("${flight.price}"),
                                        value(new BigDecimal(100))
                                ),
                                less(
                                        function("getDiffInHours", Long.class,
                                                param("date", value("${flight.dateOfDeparture}"))),
                                        value(24L)
                                )
                        )
                )
                .action(COLLECT,
                        param("context", value("${ctx}")),
                        param("fact", value("${flight}")))
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
                                                value("${flight.price}"),
                                                value(new BigDecimal(100))
                                        ),
                                        less(
                                                function("getDiffInHours", Long.class,
                                                        param("date", value("${flight.dateOfDeparture}"))),
                                                value(24L)
                                        )
                                )
                        )
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("fact", value("${flight}")))
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

    public class Flight {
        public String classOfService;
        public LocalDateTime dateOfDeparture;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Flight that = (Flight) o;
            return Objects.equals(classOfService, that.classOfService) &&
                    Objects.equals(dateOfDeparture, that.dateOfDeparture);
        }

        @Override
        public int hashCode() {
            return Objects.hash(classOfService, dateOfDeparture);
        }
    }

}
