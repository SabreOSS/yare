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

package com.sabre.oss.yare.serializer.json.converter.deserializer.operand;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabre.oss.yare.serializer.json.model.Operand;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChainedDeserializerTest {

    @Test
    void shouldThrowExceptionWhenNoDeserializerSpecified() {
        // given
        JsonNode node = mock(JsonNode.class);
        when(node.toString()).thenReturn("{ TEST NODE }");

        ChainedDeserializer deserializer = new ChainedDeserializer(Collections.emptyList());

        // when / then
        assertThatThrownBy(() -> deserializer.deserialize(node, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Node { TEST NODE } could not be deserialized to any known operand");
    }

    @Test
    void shouldDeserializeWithFirstApplicableDeserializer() throws JsonProcessingException {
        // given
        JsonNode node = mock(JsonNode.class);
        Operand expected = mock(Operand.class);

        ChainedDeserializer deserializer = new ChainedDeserializer(Arrays.asList(
                new MockedDeserializer().withApplicable(true).withResult(expected),
                new MockedDeserializer().withApplicable(false)
        ));

        // when
        Operand result = deserializer.deserialize(node, null);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldDeserializeWithApplicableDeserializer() throws JsonProcessingException {
        // given
        JsonNode node = mock(JsonNode.class);
        Operand expected = mock(Operand.class);

        ChainedDeserializer deserializer = new ChainedDeserializer(Arrays.asList(
                new MockedDeserializer().withApplicable(false),
                new MockedDeserializer().withApplicable(true).withResult(expected)
        ));

        // when
        Operand result = deserializer.deserialize(node, null);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldThrowExceptionWhenNoApplicableDeserializer() {
        // given
        JsonNode node = mock(JsonNode.class);
        when(node.toString()).thenReturn("{ TEST NODE }");

        ChainedDeserializer deserializer = new ChainedDeserializer(Arrays.asList(
                new MockedDeserializer().withApplicable(false),
                new MockedDeserializer().withApplicable(false)
        ));

        // when / then
        assertThatThrownBy(() -> deserializer.deserialize(node, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Node { TEST NODE } could not be deserialized to any known operand");
    }

    private static class MockedDeserializer implements Deserializer {
        private boolean isApplicable;
        private Operand result;

        MockedDeserializer withApplicable(boolean isApplicable) {
            this.isApplicable = isApplicable;
            return this;
        }

        MockedDeserializer withResult(Operand result) {
            this.result = result;
            return this;
        }

        @Override
        public boolean isApplicable(JsonNode jsonNode) {
            return isApplicable;
        }

        @Override
        public Operand deserialize(JsonNode jsonNode, ObjectMapper objectMapper) {
            return result;
        }
    }
}
