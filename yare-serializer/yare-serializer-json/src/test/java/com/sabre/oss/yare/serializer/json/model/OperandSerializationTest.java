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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Stream;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

class OperandSerializationTest {
    private static final String TEST_RESOURCES_DIRECTORY = "/model/operand";

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.objectMapper = RuleToJsonConverter.getObjectMapper();
    }

    @ParameterizedTest
    @MethodSource("conversionParams")
    void shouldSerializeOperand(Operand operand, String expected) throws JsonProcessingException {
        String serialized = objectMapper.writeValueAsString(operand);

        assertThatJson(serialized).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("conversionParams")
    void shouldDeserializeOperand(Operand expected, String json) throws IOException {
        Operand operand = objectMapper.readValue(json, Operand.class);

        assertThat(operand).isEqualTo(expected);
    }

    private static Stream<Arguments> conversionParams() {
        return Stream.of(
                Arguments.of(createValueOperandModel(), createValueOperandJson()),
                Arguments.of(createValuesOperandModel(), createValuesOperandJson()),
                Arguments.of(createFunctionOperandModel(), createFunctionOperandJson()),
                Arguments.of(createOperatorOperandModel(), createOperatorOperandJson())
        );
    }

    private static Operand createValueOperandModel() {
        return new Value()
                .withValue("value-value");
    }

    private static String createValueOperandJson() {
        return JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/valueOperand.json");
    }

    private static Operand createValuesOperandModel() {
        return new Values()
                .withValues(Collections.singletonList(
                        new Value().withValue("value-value")
                ))
                .withType("values-type");
    }

    private static String createValuesOperandJson() {
        return JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/valuesOperand.json");
    }

    private static Operand createFunctionOperandModel() {
        return new Function()
                .withName("function-name")
                .withReturnType("function-return-type")
                .withParameters(Collections.singletonList(
                        new Parameter().withName("parameter-name")
                                .withExpression(new Value().withValue("value-value"))
                        )
                );
    }

    private static String createFunctionOperandJson() {
        return JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/functionOperand.json");
    }

    private static Operand createOperatorOperandModel() {
        return new Operator()
                .withType("operator-type")
                .withOperands(Collections.singletonList(new Value().withValue("value-value")));
    }

    private static String createOperatorOperandJson() {
        return JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/operatorOperand.json");
    }
}
