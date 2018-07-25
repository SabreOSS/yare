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
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class ParameterSerializationTestCase {
    protected ObjectMapper objectMapper;

    protected abstract String getTestResource(String fileName);

    @TestFactory
    Stream<DynamicTest> parameterSerializationTestFactory() {
        return Stream.of(
                DynamicTest.dynamicTest(
                        "Should serialize value parameter",
                        () -> shouldSerializeParameter(createValueParameterModel(), getSerializedValueParameter())
                ),
                DynamicTest.dynamicTest(
                        "Should serialize values parameter",
                        () -> shouldSerializeParameter(createValuesParameterModel(), getSerializedValuesParameter())
                ),
                DynamicTest.dynamicTest(
                        "Should serialize function parameter",
                        () -> shouldSerializeParameter(createFunctionParameterModel(), getSerializedFunctionParameter())
                )
        );
    }

    @TestFactory
    Stream<DynamicTest> parameterDeserializeTestFactory() {
        return Stream.of(
                DynamicTest.dynamicTest(
                        "Should deserialize value parameter",
                        () -> shouldDeserializeParameter(createValueParameterModel(), getSerializedValueParameter())
                ),
                DynamicTest.dynamicTest(
                        "Should deserialize values parameter",
                        () -> shouldDeserializeParameter(createValuesParameterModel(), getSerializedValuesParameter())
                ),
                DynamicTest.dynamicTest(
                        "Should deserialize function parameter",
                        () -> shouldDeserializeParameter(createFunctionParameterModel(), getSerializedFunctionParameter())
                )
        );
    }

    private void shouldSerializeParameter(Parameter parameter, String expected) throws JsonProcessingException {
        String serialized = objectMapper.writeValueAsString(parameter);

        assertThat(serialized).isEqualToIgnoringWhitespace(expected);
    }

    private void shouldDeserializeParameter(Parameter expected, String json) throws IOException {
        Parameter parameter = objectMapper.readValue(json, Parameter.class);

        assertThat(parameter).isEqualTo(expected);
    }

    private Parameter createValueParameterModel() {
        return new Parameter()
                .withName("parameter-name")
                .withExpression(new Value().withValue("value-value"));
    }

    private String getSerializedValueParameter() {
        return getTestResource("valueParameter");
    }

    private Parameter createValuesParameterModel() {
        return new Parameter()
                .withName("parameter-name")
                .withExpression(new Values()
                        .withValues(Collections.singletonList(
                                new Value().withValue("value-value").withType("String")
                        ))
                        .withType("values-type"));
    }

    private String getSerializedValuesParameter() {
        return getTestResource("valuesParameter");
    }

    private Parameter createFunctionParameterModel() {
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

    private String getSerializedFunctionParameter() {
        return getTestResource("functionParameter");
    }
}
