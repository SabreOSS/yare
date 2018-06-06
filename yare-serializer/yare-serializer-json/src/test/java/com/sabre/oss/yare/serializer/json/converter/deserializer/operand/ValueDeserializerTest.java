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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabre.oss.yare.common.converter.TypeTypeConverter;
import com.sabre.oss.yare.serializer.json.RuleToJsonConverter;
import com.sabre.oss.yare.serializer.json.model.Operand;
import com.sabre.oss.yare.serializer.json.model.Value;
import com.sabre.oss.yare.serializer.json.utils.JsonResourceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValueDeserializerTest {
    private static final String TEST_RESOURCES_DIRECTORY = "/converter/deserializer/handler/value";

    private ObjectMapper mapper;
    private Deserializer deserializer;

    @BeforeEach
    void setUp() {
        mapper = RuleToJsonConverter.createObjectMapper();
        deserializer = new ValueDeserializer(new TypeTypeConverter());
    }

    @Test
    void shouldBeApplicableForJsonWithValueProperty() throws IOException {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/valueProperty.json");
        JsonNode node = mapper.readTree(json);

        // when
        Boolean applicable = deserializer.isApplicable(node);

        // then
        assertThat(applicable).isTrue();
    }

    @Test
    void shouldNotBeApplicableForJsonWithoutValueProperty() throws IOException {
        // given
        JsonNode node = mapper.readTree("{}");

        // when
        Boolean applicable = deserializer.isApplicable(node);

        // then
        assertThat(applicable).isFalse();
    }

    @Test
    void shouldResolveValueAccordingToTheGivenType() throws IOException {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/valueAndTypeProperties.json");
        JsonNode node = mapper.readTree(json);

        // when
        Operand result = deserializer.deserialize(node, mapper);

        // then
        assertThat(result).isInstanceOfSatisfying(Value.class, v -> {
            assertThat(v.getValue()).isEqualTo(100);
            assertThat(v.getType()).isEqualTo("Integer");
        });
    }

    @Test
    void shouldResolveValueAsStringWhenTypeIsNotSpecified() throws IOException {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/valueProperty.json");
        JsonNode node = mapper.readTree(json);

        // when
        Operand result = deserializer.deserialize(node, mapper);

        // then
        assertThat(result).isInstanceOfSatisfying(Value.class, v -> {
            assertThat(v.getValue()).isEqualTo("TEST_VALUE");
            assertThat(v.getType()).isEqualTo("String");
        });
    }

    @Test
    void shouldResolveNullValue() throws IOException {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/nullValueProperty.json");
        JsonNode node = mapper.readTree(json);

        // when
        Operand result = deserializer.deserialize(node, mapper);

        // then
        assertThat(result).isInstanceOfSatisfying(Value.class, v -> {
            assertThat(v.getValue()).isNull();
            assertThat(v.getType()).isEqualTo("String");
        });
    }

    @Test
    void shouldThrowExceptionIfTypeCannotBeResolved() throws IOException {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/unknownTypeProperty.json");
        JsonNode node = mapper.readTree(json);

        // when / then
        String expectedMessage = "Can't convert 'java.lang.Unknown' into Java type";
        assertThatThrownBy(() -> deserializer.deserialize(node, mapper))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }
}
