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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class ExpressionSerializationTestCase {
    private ObjectMapper objectMapper;

    protected abstract ObjectMapper createObjectMapper();

    protected abstract String getTestResource(String fileName);

    @BeforeEach
    void setUp() {
        objectMapper = createObjectMapper();
    }

    @TestFactory
    Stream<DynamicTest> expressionSerializationTestFactory() {
        return Stream.of(
                DynamicTest.dynamicTest(
                        "Should serialize value expression",
                        () -> shouldSerializeExpression(createValueExpressionModel(), getSerializedValueExpression())
                ),
                DynamicTest.dynamicTest(
                        "Should serialize values expression",
                        () -> shouldSerializeExpression(createValuesExpressionModel(), getSerializedValuesExpression())
                ),
                DynamicTest.dynamicTest(
                        "Should serialize function expression",
                        () -> shouldSerializeExpression(createFunctionExpressionModel(), getSerializedFunctionExpression())
                )
        );
    }

    @TestFactory
    Stream<DynamicTest> expressionDeserializationTestFactory() {
        return Stream.of(
                DynamicTest.dynamicTest(
                        "Should deserialize value expression",
                        () -> shouldDeserializeExpression(createValueExpressionModel(), getSerializedValueExpression())
                ),
                DynamicTest.dynamicTest(
                        "Should deserialize values expression",
                        () -> shouldDeserializeExpression(createValuesExpressionModel(), getSerializedValuesExpression())
                ),
                DynamicTest.dynamicTest(
                        "Should deserialize function expression",
                        () -> shouldDeserializeExpression(createFunctionExpressionModel(), getSerializedFunctionExpression())
                )
        );
    }

    private void shouldSerializeExpression(Expression expression, String expected) throws JsonProcessingException {
        String serialized = objectMapper.writeValueAsString(expression);

        assertThat(serialized).isEqualToIgnoringWhitespace(expected);
    }

    private void shouldDeserializeExpression(Expression expected, String json) throws IOException {
        Expression expression = objectMapper.readValue(json, Expression.class);

        assertThat(expression).isEqualTo(expected);
    }

    private Expression createValueExpressionModel() {
        return new Value()
                .withValue("value-value");
    }

    private String getSerializedValueExpression() {
        return getTestResource("valueExpression");
    }

    private Expression createValuesExpressionModel() {
        return new Values()
                .withValues(Collections.singletonList(
                        new Value().withValue("value-value")
                ))
                .withType("values-type");
    }

    private String getSerializedValuesExpression() {
        return getTestResource("valuesExpression");
    }

    private Expression createFunctionExpressionModel() {
        return new Function()
                .withName("function-name")
                .withReturnType("function-return-type")
                .withParameters(Collections.singletonList(
                        new Parameter().withName("parameter-name")
                                .withExpression(new Value().withValue("value-value"))
                        )
                );
    }

    private String getSerializedFunctionExpression() {
        return getTestResource("functionExpression");
    }
}
