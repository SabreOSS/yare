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

package com.sabre.oss.yare.serializer.json.deserializer.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.sabre.oss.yare.serializer.json.model.Operand;
import com.sabre.oss.yare.serializer.json.model.Value;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValueDeserializationHandlerTest extends DeserializationHandlerTestBase {
    private ValueDeserializationHandler handler = new ValueDeserializationHandler();

    @Test
    void shouldBeApplicableForJsonWithValueProperty()
            throws IOException {
        //given
        JsonNode node = toJsonNode(
                "{\"value\": \"TEST_VALUE\"}");

        //when
        Boolean applicable = handler.isApplicable(node);

        //then
        assertThat(applicable).isTrue();
    }

    @Test
    void shouldNotBeApplicableForJsonWithoutValueProperty()
            throws IOException {
        //given
        JsonNode node = toJsonNode("{}");

        //when
        Boolean applicable = handler.isApplicable(node);

        //then
        assertThat(applicable).isFalse();
    }

    @Test
    void shouldResolveValueAccordingToTheGivenType()
            throws IOException {
        //given
        JsonNode node = toJsonNode(
                "{\"value\": \"100\", \"type\": \"java.lang.Integer\"}");

        //when
        Operand result = handler.deserialize(node, mapper);

        //then
        assertThat(result).isInstanceOf(Value.class);

        Value resultValue = (Value) result;
        assertThat(resultValue.getValue()).isEqualTo(100);
        assertThat(resultValue.getType()).isEqualTo(Integer.class.getName());
    }

    @Test
    void shouldResolveValueAsStringWhenTypeIsNotSpecified()
            throws IOException {
        //given
        JsonNode node = toJsonNode(
                "{\"value\": \"100\"}");

        //when
        Operand result = handler.deserialize(node, mapper);

        //then
        assertThat(result).isInstanceOf(Value.class);

        Value resultValue = (Value) result;
        assertThat(resultValue.getValue()).isEqualTo("100");
        assertThat(resultValue.getType()).isEqualTo(String.class.getName());
    }

    @Test
    void shouldThrowExceptionIfTypeCannotBeResolved()
            throws IOException {
        //given
        JsonNode node = toJsonNode(
                "{\"value\": \"100\", \"type\": \"java.lang.Unknown\"}");

        //when / then
        String expectedMessage = "Could not resolve type from string: java.lang.Unknown";
        assertThatThrownBy(() -> handler.deserialize(node, mapper))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }
}
