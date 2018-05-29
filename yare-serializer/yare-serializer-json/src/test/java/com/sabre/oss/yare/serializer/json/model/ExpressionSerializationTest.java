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

class ExpressionSerializationTest {
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.objectMapper = RuleToJsonConverter.getObjectMapper();
    }

    @ParameterizedTest
    @MethodSource("conversionParams")
    void shouldSerializeExpression(Expression expression, String expected) throws JsonProcessingException {
        String serialized = objectMapper.writeValueAsString(expression);

        assertThatJson(serialized).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("conversionParams")
    void shouldDeserializeExpression(Expression expected, String json) throws IOException {
        Expression expression = objectMapper.readValue(json, Expression.class);

        assertThat(expression).isEqualTo(expected);
    }

    private static Stream<Arguments> conversionParams() {
        return Stream.of(
                Arguments.of(createValueExpressionModel(), createValueExpressionJson()),
                Arguments.of(createValuesExpressionModel(), createValuesExpressionJson()),
                Arguments.of(createFunctionExpressionModel(), createFunctionExpressionJson())
        );
    }

    private static Expression createValueExpressionModel() {
        return new Value()
                .withValue("value-value");
    }

    private static String createValueExpressionJson() {
        return "" +
                "{" +
                "  \"value\" : \"value-value\"" +
                "}";
    }

    private static Expression createValuesExpressionModel() {
        return new Values()
                .withValues(Collections.singletonList(
                        new Value().withValue("value-value")
                ))
                .withType("values-type");
    }

    private static String createValuesExpressionJson() {
        return "" +
                "{" +
                "  \"values\": [" +
                "    {" +
                "      \"value\": \"value-value\"" +
                "    }" +
                "  ]," +
                "  \"type\": \"values-type\"" +
                "}";
    }

    private static Expression createFunctionExpressionModel() {
        return new Function()
                .withName("function-name")
                .withReturnType("function-return-type")
                .withParameters(Collections.singletonList(
                        new Parameter().withName("parameter-name")
                                .withExpression(new Value().withValue("value-value"))
                        )
                );
    }

    private static String createFunctionExpressionJson() {
        return "" +
                "{" +
                "  \"function\": {" +
                "    \"name\": \"function-name\"," +
                "    \"returnType\": \"function-return-type\"," +
                "    \"parameters\": [" +
                "      {" +
                "        \"name\": \"parameter-name\"," +
                "        \"value\": \"value-value\"" +
                "      }" +
                "    ]" +
                "  }" +
                "}";
    }
}
