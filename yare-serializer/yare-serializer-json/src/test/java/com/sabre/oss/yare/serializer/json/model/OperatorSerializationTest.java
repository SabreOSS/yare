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

package com.sabre.oss.yare.serializer.json.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabre.oss.yare.serializer.json.RuleToJsonConverter;
import com.sabre.oss.yare.serializer.json.utils.JsonResourceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

class OperatorSerializationTest {
    private static final String TEST_RESOURCES_DIRECTORY = "/model/operator";

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.objectMapper = RuleToJsonConverter.createObjectMapper();
    }

    @Test
    void shouldSerializeOperator() throws JsonProcessingException {
        Operator operator = getOperatorModel();

        String serialized = objectMapper.writeValueAsString(operator);

        String expected = getOperatorJson();
        assertThatJson(serialized).isEqualTo(expected);
    }

    @Test
    void shouldDeserializeOperator() throws IOException {
        String json = getOperatorJson();

        Operator operator = objectMapper.readValue(json, Operator.class);

        Operator expected = getOperatorModel();
        assertThat(operator).isEqualTo(expected);
    }

    private Operator getOperatorModel() {
        return new Operator()
                .withType("operator-type-1")
                .withOperands(
                        new Value().withValue("value-type"),
                        new Values()
                                .withType("values-type")
                                .withValues(
                                        new Value().withValue("value-value"),
                                        new Value().withValue(10L).withType("Long")
                                ),
                        new Function()
                                .withName("function-name")
                                .withReturnType("function-return-type")
                                .withParameters(),
                        new Operator()
                                .withType("operator-type-2")
                                .withOperands()
                );
    }

    private String getOperatorJson() {
        return JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/operator.json");
    }
}
