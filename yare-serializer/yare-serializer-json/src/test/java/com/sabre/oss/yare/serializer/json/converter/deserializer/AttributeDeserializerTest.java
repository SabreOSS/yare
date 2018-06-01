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

package com.sabre.oss.yare.serializer.json.converter.deserializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabre.oss.yare.serializer.json.model.Attribute;
import com.sabre.oss.yare.serializer.json.utils.JsonResourceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AttributeDeserializerTest {
    private static final String TEST_RESOURCES_DIRECTORY = "/converter/deserializer/attribute";

    private ObjectMapper mapper;
    private AttributeDeserializer deserializer;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        deserializer = new AttributeDeserializer();
    }

    @Test
    void shouldResolveAttribute() throws IOException {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/attribute.json");
        JsonNode node = mapper.readTree(json);

        // when
        Attribute result = deserializer.deserialize(node.traverse(mapper), null);

        // then
        assertThat(result).isInstanceOfSatisfying(Attribute.class, a -> {
            assertThat(a.getName()).isEqualTo("ATTRIBUTE_NAME");
            assertThat(a.getValue()).isEqualTo(100);
            assertThat(a.getType()).isEqualTo(Integer.class.getName());
        });
    }

    @Test
    void shouldThrowExceptionIfTypeIsNull() throws IOException {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/nullTypeProperty.json");
        JsonNode node = mapper.readTree(json);

        // when / then
        String expectedMessage = "Unable to deserialize {\"type\":null}, type must not be null";
        assertThatThrownBy(() -> deserializer.deserialize(node.traverse(mapper), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    void shouldThrowExceptionIfTypeCannotBeResolved() throws IOException {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/unknownTypeProperty.json");
        JsonNode node = mapper.readTree(json);

        // when / then
        String expectedMessage = "Can't convert 'java.lang.Unknown' into Java type";
        assertThatThrownBy(() -> deserializer.deserialize(node.traverse(mapper), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }
}
