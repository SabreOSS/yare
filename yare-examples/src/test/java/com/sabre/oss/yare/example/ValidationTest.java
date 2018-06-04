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

package com.sabre.oss.yare.example;

import com.google.common.io.Resources;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.dsl.RuleDsl;
import com.sabre.oss.yare.example.fact.Flight;
import com.sabre.oss.yare.model.validator.DefaultRuleValidator;
import com.sabre.oss.yare.model.validator.ValidationResult;
import com.sabre.oss.yare.model.validator.ValidationResults;
import com.sabre.oss.yare.serializer.xml.RuleToXmlConverter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

class ValidationTest {

    @Test
    void shouldNotThrowExceptionWithMalformedRuleAndDisabledValidation() {
        RuleDsl.ruleBuilder()
                .name("Should match flight with given class of service - incorrect field name")
                .fact("flight", Flight.class)
                .predicate(
                        equal(
                                value("${flight.missingField}"),
                                value("First Class")
                        )
                )
                .action("collect",
                        param("context", value("${ctx}")),
                        param("fact", value("${flight}")))
                .build(false);
    }

    @Test
    void shouldThrowExceptionWhenValidationEnabledAndRuleInvalid() {
        assertThatThrownBy(() ->
                RuleDsl.ruleBuilder()
                        .name("Should match flight with given class of service - incorrect field name")
                        .fact("flight", Flight.class)
                        .predicate(
                                equal(
                                        value("${flight.missingField}"),
                                        value("First Class")
                                )
                        )
                        .action("collect",
                                param("context", value("${ctx}")),
                                param("fact", value("${flight}")))
                        .build()
        ).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldValidateRuleOriginatingFromXmlUsingDefaultRuleValidator() throws IOException {
        // given
        String invalidRuleInXmlString = Resources.toString(
                Resources.getResource("rules/invalidRule.xml"),
                Charset.defaultCharset());

        Rule unmarshalledRule = RuleToXmlConverter.getInstance().unmarshal(invalidRuleInXmlString);

        // when
        ValidationResults validationResults = DefaultRuleValidator.getRuleValidator().validate(unmarshalledRule);

        // then
        assertThat(validationResults.getResults()).containsExactly(
                ValidationResult.error("rule.ref.unknown-field", "Reference Error: unknown field used -> flight.missingField")
        );
    }
}
