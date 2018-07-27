/*
 * MIT License
 *
 * Copyright 2018 Sabre GLBL c.
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

package com.sabre.oss.yare.serializer.json.schema;

import com.sabre.oss.yare.serializer.json.utils.JsonResourceUtils;
import com.sabre.oss.yare.serializer.json.validator.JsonSchemaValidator;
import com.sabre.oss.yare.serializer.validator.SchemaValidationError;
import com.sabre.oss.yare.serializer.validator.SchemaValidationResults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class JsonSchemaTest {
    private static final String TEST_RESOURCES_DIRECTORY = "/json/schema";

    private JsonSchemaValidator validator;

    @BeforeEach
    void setUp() {
        validator = new JsonSchemaValidator();
    }

    @ParameterizedTest
    @MethodSource("validRuleJsonCases")
    void shouldRecognizeNoSchemaViolationsForGivenRulesJson(String testResource) {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString(String.format("%s/valid/%s", TEST_RESOURCES_DIRECTORY, testResource));

        // when
        SchemaValidationResults results = validator.validate(json);

        // then
        assertThat(results).isNotNull();
        assertThat(results.getResults()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("invalidRuleJsonCases")
    void shouldRecognizeSchemaViolationForGivenInvalidRulesJson(String testResource, String expectedViolation) {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString(String.format("%s/invalid/%s", TEST_RESOURCES_DIRECTORY, testResource));

        // when
        SchemaValidationResults results = validator.validate(json);

        // then
        assertThat(results).isNotNull();
        assertThat(results.getResults()).contains(
                SchemaValidationError.ofViolation(expectedViolation)
        );
    }

    private static Stream<Arguments> validRuleJsonCases() {
        return Stream.of(
                Arguments.of("ruleWithCustomOperand.json"),
                Arguments.of("ruleWithNestedCustomOperand.json"),
                Arguments.of("ruleWithValueOperand.json"),
                Arguments.of("ruleWithValuesOperand.json"),
                Arguments.of("ruleWithFunctionOperand.json"),
                Arguments.of("ruleWithFunctionOperandWithDefaultReturnType.json"),

                Arguments.of("ruleWithValueParameter.json"),
                Arguments.of("ruleWithValuesParameter.json"),
                Arguments.of("ruleWithFunctionParameter.json"),

                Arguments.of("ruleWithBooleanValue.json"),
                Arguments.of("ruleWithCustomObjectValue.json"),
                Arguments.of("ruleWithNullValue.json"),
                Arguments.of("ruleWithNumberValue.json"),
                Arguments.of("ruleWithStringValue.json"),

                Arguments.of("/ruleWithBooleanAttribute.json"),
                Arguments.of("/ruleWithCustomObjectAttribute.json"),
                Arguments.of("/ruleWithNullAttribute.json"),
                Arguments.of("/ruleWithNumberAttribute.json"),
                Arguments.of("/ruleWithStringAttribute.json")
        );
    }

    private static Stream<Arguments> invalidRuleJsonCases() {
        return Stream.of(
                Arguments.of("ruleWithUnknownSection.json", "#: extraneous key [unknown] is not permitted"),

                Arguments.of("ruleWithMissingAttributesSection.json", "#: required key [attributes] not found"),
                Arguments.of("ruleWithNonArrayAttributesSection.json", "#/attributes: expected type: JSONArray, found: JSONObject"),
                Arguments.of("ruleWithInvalidAttributeWithInvalidNameType.json", "#/attributes/0/name: expected type: String, found: Integer"),
                Arguments.of("ruleWithInvalidAttributeWithInvalidValueType.json", "#/attributes/0/value: expected: null, found: JSONArray"),
                Arguments.of("ruleWithInvalidAttributeWithInvalidValueType.json", "#/attributes/0/value: expected type: JSONObject, found: JSONArray"),
                Arguments.of("ruleWithInvalidAttributeWithInvalidValueType.json", "#/attributes/0/value: expected type: Boolean, found: JSONArray"),
                Arguments.of("ruleWithInvalidAttributeWithInvalidValueType.json", "#/attributes/0/value: expected type: Number, found: JSONArray"),
                Arguments.of("ruleWithInvalidAttributeWithInvalidValueType.json", "#/attributes/0/value: expected type: String, found: JSONArray"),
                Arguments.of("ruleWithInvalidAttributeWithMissingName.json", "#/attributes/0: required key [name] not found"),
                Arguments.of("ruleWithInvalidAttributeWithMissingValue.json", "#/attributes/0: required key [value] not found"),
                Arguments.of("ruleWithInvalidAttributeWithUnknownProperty.json", "#/attributes/0: extraneous key [unknown] is not permitted"),

                Arguments.of("ruleWithMissingFactsSection.json", "#: required key [facts] not found"),
                Arguments.of("ruleWithNonArrayFactsSection.json", "#/facts: expected type: JSONArray, found: JSONObject"),
                Arguments.of("ruleWithInvalidFactWithInvalidNameType.json", "#/facts/0/name: expected type: String, found: Integer"),
                Arguments.of("ruleWithInvalidFactWithInvalidTypeType.json", "#/facts/0/type: expected type: String, found: Integer"),
                Arguments.of("ruleWithInvalidFactWithMissingName.json", "#/facts/0: required key [name] not found"),
                Arguments.of("ruleWithInvalidFactWithMissingType.json", "#/facts/0: required key [type] not found"),
                Arguments.of("ruleWithInvalidFactWithUnknownProperty.json", "#/facts/0: extraneous key [unknown] is not permitted"),

                Arguments.of("ruleWithMissingActionsSection.json", "#: required key [actions] not found"),
                Arguments.of("ruleWithNonArrayActionsSection.json", "#/actions: expected type: JSONArray, found: JSONObject"),
                Arguments.of("ruleWithInvalidActionWithInvalidNameType.json", "#/actions/0/name: expected type: String, found: Integer"),
                Arguments.of("ruleWithInvalidActionWithInvalidParametersType.json", "#/actions/0/parameters: expected type: JSONArray, found: JSONObject"),
                Arguments.of("ruleWithInvalidActionWithMissingName.json", "#/actions/0: required key [name] not found"),
                Arguments.of("ruleWithInvalidActionWithMissingParameters.json", "#/actions/0: required key [parameters] not found"),
                Arguments.of("ruleWithInvalidActionWithUnknownProperty.json", "#/actions/0: extraneous key [unknown] is not permitted"),

                Arguments.of("ruleWithInvalidParameterWithMissingName.json", "#/actions/0/parameters/0: required key [name] not found"),
                Arguments.of("ruleWithInvalidParameterWithInvalidNameType.json", "#/actions/0/parameters/0/name: expected type: String, found: Integer"),
                Arguments.of("ruleWithInvalidParameterWithInvalidExpressionStructure.json", "#/actions/0/parameters/0: #: 0 subschemas matched instead of one"),

                Arguments.of("ruleWithInvalidPredicateOperand.json", "#/predicate/equal: expected type: JSONArray, found: JSONObject"),
                Arguments.of("ruleWithInvalidPredicateOperand.json", "#/predicate: #: 0 subschemas matched instead of one"),

                Arguments.of("ruleWithMissingPredicateSection.json", "#: required key [predicate] not found"),

                Arguments.of("ruleWithInvalidValueOperatorType.json", "#/predicate: extraneous key [value] is not permitted"),
                Arguments.of("ruleWithInvalidValueOperatorType.json", "#/predicate: #: 0 subschemas matched instead of one"),
                Arguments.of("ruleWithInvalidValuesOperatorType.json", "#/predicate: extraneous key [values] is not permitted"),
                Arguments.of("ruleWithInvalidValuesOperatorType.json", "#/predicate: #: 0 subschemas matched instead of one"),
                Arguments.of("ruleWithInvalidFunctionOperatorType.json", "#/predicate: extraneous key [function] is not permitted"),
                Arguments.of("ruleWithInvalidFunctionOperatorType.json", "#/predicate: #: 0 subschemas matched instead of one")
        );
    }
}
