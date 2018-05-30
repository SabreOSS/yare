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

package com.sabre.oss.yare.serializer.json.converter.deserializer.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.sabre.oss.yare.serializer.json.model.Operand;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DeserializationHandlerTest {
    @Test
    void shouldHandleJsonWithApplicableHandler() throws JsonProcessingException {
        // given
        JsonNode node = mock(JsonNode.class);
        Operand expected = mock(Operand.class);

        DeserializationHandler handler = mockDeserializationHandler(true, expected);

        // when
        Operand result = handler.handle(node, null);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldHandleJsonWithSecondApplicableHandler() throws JsonProcessingException {
        // given
        JsonNode node = mock(JsonNode.class);
        Operand expected = mock(Operand.class);

        DeserializationHandler handler = mockNotApplicableDeserializationHandler()
                .withNext(mockApplicableDeserializationHandler(expected));

        // when
        Operand result = handler.handle(node, null);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldDeserializeNullJsonIntoNullValue() throws JsonProcessingException {
        // given
        JsonNode node = null;

        DeserializationHandler handler = mockNotApplicableDeserializationHandler()
                .withNext(mockNotApplicableDeserializationHandler());

        // when
        Operand result = handler.handle(node, null);

        // then
        assertThat(result).isNull();
    }

    @Test
    void shouldThrownAnExceptionWhenApplicableHandlerCannotBeFound() throws JsonProcessingException {
        // given
        JsonNode node = mock(JsonNode.class);
        when(node.toString()).thenReturn("{ TEST NODE }");

        DeserializationHandler handler = mockNotApplicableDeserializationHandler()
                .withNext(mockNotApplicableDeserializationHandler());

        // when / then
        String expectedMessage = "Given node: { TEST NODE } could not be deserialized to any known operand model";
        assertThatThrownBy(() -> handler.handle(node, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    private DeserializationHandler mockNotApplicableDeserializationHandler() throws JsonProcessingException {
        return mockDeserializationHandler(false, null);
    }

    private DeserializationHandler mockApplicableDeserializationHandler(Operand producedResult) throws JsonProcessingException {
        return mockDeserializationHandler(true, producedResult);
    }

    private DeserializationHandler mockDeserializationHandler(Boolean isApplicable, Operand producedResult) throws JsonProcessingException {
        DeserializationHandler handler = mock(DeserializationHandler.class, Mockito.CALLS_REAL_METHODS);
        when(handler.isApplicable(any())).thenReturn(isApplicable);
        when(handler.deserialize(any(), any())).thenReturn(producedResult);
        return handler;
    }
}
