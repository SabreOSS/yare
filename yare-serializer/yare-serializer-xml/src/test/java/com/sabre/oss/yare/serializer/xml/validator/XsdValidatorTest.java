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

package com.sabre.oss.yare.serializer.xml.validator;

import com.sabre.oss.yare.serializer.validator.SchemaValidationError;
import com.sabre.oss.yare.serializer.validator.SchemaValidationResults;
import com.sabre.oss.yare.serializer.xml.utils.ResourceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;

import static org.assertj.core.api.Assertions.assertThat;

class XsdValidatorTest {
    private XsdValidator validator;

    @BeforeEach
    void setup() {
        validator = new XsdValidator();
    }

    @Test
    void shouldSucceedForValidRule() {
        // given
        String xml = ResourceUtils.getResourceAsString("/validator/validRule.xml");

        // when
        SchemaValidationResults result = validator.validate(xml);

        // then
        assertThat(result.hasErrors()).isFalse();
    }

    @Test
    void shouldReturnErrorResultsForParsingError() {
        // given
        String invalidXml = "!@#$";

        // when
        SchemaValidationResults result = validator.validate(invalidXml);

        // then
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getResults()).containsExactlyInAnyOrder(
                SchemaValidationError.of(1, 1, "Content is not allowed in prolog.")
        );
    }

    @Nested
    @EnabledOnJre(JRE.JAVA_8)
    class ValidationMessagesOnJdk8Test {
        @Test
        void shouldReturnErrorResultsForInvalidXmlRuleWithSingleViolation() {
            // given
            String xml = ResourceUtils.getResourceAsString("/validator/invalidRuleWithOneViolation.xml");

            // when
            SchemaValidationResults result = validator.validate(xml);

            // then
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getResults()).containsExactly(
                    SchemaValidationError.of(
                            31,
                            36,
                            "cvc-complex-type.2.4.a: Invalid content was found starting with element 'yare:Action'. " +
                                    "One of '{\"http://www.sabre.com/schema/oss/yare/rules/v1\":Fact, \"http://www.sabre.com/schema/oss/yare/rules/v1\":Predicate}' is expected."
                    )
            );
        }

        @Test
        void shouldReturnErrorResultsForInvalidXmlRuleWithMultipleViolations() {
            // given
            String xml = ResourceUtils.getResourceAsString("/validator/invalidRuleWithMultipleViolations.xml");

            // when
            SchemaValidationResults result = validator.validate(xml);

            // then
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getResults()).containsExactly(
                    SchemaValidationError.of(
                            27,
                            40,
                            "cvc-complex-type.2.4.a: Invalid content was found starting with element 'yare:unknown'. " +
                                    "One of '{\"http://www.sabre.com/schema/oss/yare/rules/v1\":Attribute, " +
                                    "\"http://www.sabre.com/schema/oss/yare/rules/v1\":Fact, " +
                                    "\"http://www.sabre.com/schema/oss/yare/rules/v1\":Predicate}' is expected."
                    ),
                    SchemaValidationError.of(
                            30,
                            31,
                            "cvc-complex-type.4: Attribute 'name' must appear on element 'yare:Fact'."
                    )
            );
        }
    }

    @Nested
    @DisabledOnJre(JRE.JAVA_8)
    class ValidationMessagesOnJdkDifferentThan8Test {
        @Test
        void shouldReturnErrorResultsForInvalidXmlRuleWithSingleViolation() {
            // given
            String xml = ResourceUtils.getResourceAsString("/validator/invalidRuleWithOneViolation.xml");

            // when
            SchemaValidationResults result = validator.validate(xml);

            // then
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getResults()).containsExactly(
                    SchemaValidationError.of(
                            31,
                            36,
                            "cvc-complex-type.2.4.a: Invalid content was found starting with element '{\"http://www.sabre.com/schema/oss/yare/rules/v1\":Action}'. " +
                                    "One of '{\"http://www.sabre.com/schema/oss/yare/rules/v1\":Fact, \"http://www.sabre.com/schema/oss/yare/rules/v1\":Predicate}' is expected."
                    )
            );
        }

        @Test
        void shouldReturnErrorResultsForInvalidXmlRuleWithMultipleViolations() {
            // given
            String xml = ResourceUtils.getResourceAsString("/validator/invalidRuleWithMultipleViolations.xml");

            // when
            SchemaValidationResults result = validator.validate(xml);

            // then
            assertThat(result.hasErrors()).isTrue();
            assertThat(result.getResults()).containsExactly(
                    SchemaValidationError.of(
                            27,
                            40,
                            "cvc-complex-type.2.4.a: Invalid content was found starting with element '{\"http://www.sabre.com/schema/oss/yare/rules/v1\":unknown}'. " +
                                    "One of '{\"http://www.sabre.com/schema/oss/yare/rules/v1\":Attribute, " +
                                    "\"http://www.sabre.com/schema/oss/yare/rules/v1\":Fact, " +
                                    "\"http://www.sabre.com/schema/oss/yare/rules/v1\":Predicate}' is expected."
                    ),
                    SchemaValidationError.of(
                            30,
                            31,
                            "cvc-complex-type.4: Attribute 'name' must appear on element 'yare:Fact'."
                    )
            );
        }
    }
}
