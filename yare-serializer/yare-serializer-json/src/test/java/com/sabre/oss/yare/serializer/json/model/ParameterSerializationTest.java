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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Stream;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

class ParameterSerializationTest {
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.objectMapper = RuleToJsonConverter.getObjectMapper();
    }

    @ParameterizedTest
    @MethodSource("conversionParams")
    void shouldSerializeParameter(Parameter parameter, String expected) throws JsonProcessingException {
        String serialized = objectMapper.writeValueAsString(parameter);

        assertThatJson(serialized).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("conversionParams")
    void shouldDeserializeOperand(Parameter expected, String json) throws IOException {
        Parameter parameter = objectMapper.readValue(json, Parameter.class);

        assertThat(parameter).isEqualTo(expected);
    }

    private static Stream<Arguments> conversionParams() {
        return Stream.of(
                Arguments.of(createValueParameterModel(), createValueParameterJson()),
                Arguments.of(createValuesParameterModel(), createValuesParameterJson()),
                Arguments.of(createFunctionParameterModel(), createFunctionParameterJson())
        );
    }

    private static Parameter createValueParameterModel() {
        return new Parameter()
                .withName("parameter-name")
                .withExpression(new Value().withValue("value-value"));
    }

    private static String createValueParameterJson() {
        return "" +
                "{" +
                "  \"name\": \"parameter-name\"," +
                "  \"value\": \"value-value\"" +
                "}";
    }

    private static Parameter createValuesParameterModel() {
        return new Parameter()
                .withName("parameter-name")
                .withExpression(new Values()
                        .withValues(Collections.singletonList(
                                new Value().withValue("value-value").withType("java.lang.String")
                        ))
                        .withType("values-type"));
    }

    private static String createValuesParameterJson() {
        return "" +
                "{" +
                "  \"name\": \"parameter-name\"," +
                "  \"values\": [" +
                "    {" +
                "      \"value\": \"value-value\"" +
                "    }" +
                "  ]," +
                "  \"type\": \"values-type\"" +
                "}";
    }

    private static Parameter createFunctionParameterModel() {
        return new Parameter()
                .withName("parameter-name-1")
                .withExpression(new Function()
                        .withName("function-name")
                        .withReturnType("function-return-type")
                        .withParameters(Collections.singletonList(
                                new Parameter().withName("parameter-name-2")
                                        .withExpression(new Value().withValue("value-value"))
                                )
                        ));
    }

    private static String createFunctionParameterJson() {
        return "" +
                "{" +
                "  \"name\": \"parameter-name-1\"," +
                "  \"function\": {" +
                "    \"name\": \"function-name\"," +
                "    \"returnType\": \"function-return-type\"," +
                "    \"parameters\": [" +
                "      {" +
                "        \"name\": \"parameter-name-2\"," +
                "        \"value\": \"value-value\"" +
                "      }" +
                "    ]" +
                "  }" +
                "}";
    }
}
