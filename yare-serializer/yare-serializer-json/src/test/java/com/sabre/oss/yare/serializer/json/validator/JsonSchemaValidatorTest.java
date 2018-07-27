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

package com.sabre.oss.yare.serializer.json.validator;

import com.sabre.oss.yare.serializer.json.utils.JsonResourceUtils;
import com.sabre.oss.yare.serializer.validator.SchemaValidationError;
import com.sabre.oss.yare.serializer.validator.SchemaValidationResults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonSchemaValidatorTest {
    private JsonSchemaValidator validator;

    @BeforeEach
    void setUp() {
        validator = new JsonSchemaValidator();
    }

    @Test
    void shouldReturnEmptyResultSetForValidRuleJson() {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString("/json/validator/validRule.json");

        // when
        SchemaValidationResults results = validator.validate(json);

        // then
        assertThat(results).isNotNull();
        assertThat(results.hasErrors()).isFalse();
        assertThat(results.getResults()).isEmpty();
    }

    @Test
    void shouldReturnSingleErrorResultForInvalidRuleJsonWithOneViolation() {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString("/json/validator/invalidRuleWithOneViolation.json");

        // when
        SchemaValidationResults results = validator.validate(json);

        // then
        assertThat(results).isNotNull();
        assertThat(results.hasErrors()).isTrue();
        assertThat(results.getResults()).containsExactly(
                SchemaValidationError.ofViolation("#: required key [actions] not found")
        );
    }

    @Test
    void shouldReturnErrorResultSetForInvalidRuleJsonWithMultipleViolations() {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString("/json/validator/invalidRuleWithMultipleViolations.json");

        // when
        SchemaValidationResults results = validator.validate(json);

        // then
        assertThat(results).isNotNull();
        assertThat(results.hasErrors()).isTrue();
        assertThat(results.getResults()).containsExactlyInAnyOrder(
                SchemaValidationError.ofViolation("#: required key [attributes] not found"),
                SchemaValidationError.ofViolation("#: required key [actions] not found"),
                SchemaValidationError.ofViolation("#: extraneous key [unknown] is not permitted"),
                SchemaValidationError.ofViolation("#/facts/0: required key [name] not found")
        );
    }

    @Test
    void shouldReturnSingleErrorResultWhenJsonCannotBeValidated() {
        // given
        String invalidJson = "!@#$";

        // when
        SchemaValidationResults results = validator.validate(invalidJson);

        // then
        assertThat(results).isNotNull();
        assertThat(results.getResults()).containsExactly(
                SchemaValidationError.ofViolation("Could not validate JSON. Exception JSONException has been thrown with message: " +
                        "A JSONObject text must begin with '{' at 1 [character 2 line 1]")
        );
    }
}
