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

public abstract class OperandSerializationTestCase {
    protected ObjectMapper objectMapper;

    protected abstract String getTestResource(String fileName);

    @TestFactory
    Stream<DynamicTest> operandSerializationTestFactory() {
        return Stream.of(
                DynamicTest.dynamicTest(
                        "Should serialize value operand",
                        () -> shouldSerializeOperand(createValueOperandModel(), getSerializedValueOperand())
                ),
                DynamicTest.dynamicTest(
                        "Should serialize values operand",
                        () -> shouldSerializeOperand(createValuesOperandModel(), getSerializedValuesOperand())
                ),
                DynamicTest.dynamicTest(
                        "Should serialize function operand",
                        () -> shouldSerializeOperand(createFunctionOperandModel(), getSerializedFunctionOperand())
                ),
                DynamicTest.dynamicTest(
                        "Should serialize operator operand",
                        () -> shouldSerializeOperand(createOperatorOperandModel(), getSerializedOperatorOperand())
                )
        );
    }

    @TestFactory
    Stream<DynamicTest> operandDeserializationTestFactory() {
        return Stream.of(
                DynamicTest.dynamicTest(
                        "Should deserialize value operand",
                        () -> shouldDeserializeOperand(createValueOperandModel(), getSerializedValueOperand())
                ),
                DynamicTest.dynamicTest(
                        "Should deserialize values operand",
                        () -> shouldDeserializeOperand(createValuesOperandModel(), getSerializedValuesOperand())
                ),
                DynamicTest.dynamicTest(
                        "Should deserialize function operand",
                        () -> shouldDeserializeOperand(createFunctionOperandModel(), getSerializedFunctionOperand())
                ),
                DynamicTest.dynamicTest(
                        "Should deserialize operator operand",
                        () -> shouldDeserializeOperand(createOperatorOperandModel(), getSerializedOperatorOperand())
                )
        );
    }

    private void shouldSerializeOperand(Operand operand, String expected) throws JsonProcessingException {
        String serialized = objectMapper.writeValueAsString(operand);

        assertThat(serialized).isEqualToIgnoringWhitespace(expected);
    }

    private void shouldDeserializeOperand(Operand expected, String json) throws IOException {
        Operand operand = objectMapper.readValue(json, Operand.class);

        assertThat(operand).isEqualTo(expected);
    }

    private Operand createValueOperandModel() {
        return new Value()
                .withValue("value-value");
    }

    private String getSerializedValueOperand() {
        return getTestResource("valueOperand");
    }

    private Operand createValuesOperandModel() {
        return new Values()
                .withValues(Collections.singletonList(
                        new Value().withValue("value-value")
                ))
                .withType("values-type");
    }

    private String getSerializedValuesOperand() {
        return getTestResource("valuesOperand");
    }

    private Operand createFunctionOperandModel() {
        return new Function()
                .withName("function-name")
                .withReturnType("function-return-type")
                .withParameters(Collections.singletonList(
                        new Parameter().withName("parameter-name")
                                .withExpression(new Value().withValue("value-value"))
                        )
                );
    }

    private String getSerializedFunctionOperand() {
        return getTestResource("functionOperand");
    }

    private Operand createOperatorOperandModel() {
        return new Operator()
                .withType("operator-type")
                .withOperands(Collections.singletonList(new Value().withValue("value-value")));
    }

    private String getSerializedOperatorOperand() {
        return getTestResource("operatorOperand");
    }
}
