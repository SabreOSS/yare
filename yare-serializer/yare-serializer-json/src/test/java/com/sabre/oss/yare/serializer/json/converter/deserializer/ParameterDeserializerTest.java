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
import com.sabre.oss.yare.serializer.json.model.Function;
import com.sabre.oss.yare.serializer.json.model.Parameter;
import com.sabre.oss.yare.serializer.json.model.Value;
import com.sabre.oss.yare.serializer.json.model.Values;
import com.sabre.oss.yare.serializer.json.utils.JsonResourceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParameterDeserializerTest {
    private static final String TEST_RESOURCES_DIRECTORY = "/converter/deserializer/parameter";

    private ObjectMapper mapper;
    private ParameterDeserializer deserializer;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        deserializer = new ParameterDeserializer();
    }

    @Test
    void shouldResolveParameterWithValueExpression() throws IOException {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/valueExpression.json");
        JsonNode node = mapper.readTree(json);

        // when
        Parameter parameter = deserializer.deserialize(node.traverse(mapper), null);

        // then
        assertThat(parameter.getName()).isEqualTo("PARAMETER_NAME");
        assertThat(parameter.getExpression()).isInstanceOf(Value.class);
    }

    @Test
    void shouldResolveParameterWithValuesExpression() throws IOException {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/valuesExpression.json");
        JsonNode node = mapper.readTree(json);

        // when
        Parameter parameter = deserializer.deserialize(node.traverse(mapper), null);

        // then
        assertThat(parameter.getName()).isEqualTo("PARAMETER_NAME");
        assertThat(parameter.getExpression()).isInstanceOf(Values.class);
    }

    @Test
    void shouldResolveParameterWithFunctionExpression() throws IOException {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/functionExpression.json");
        JsonNode node = mapper.readTree(json);

        // when
        Parameter parameter = deserializer.deserialize(node.traverse(mapper), null);

        // then
        assertThat(parameter.getName()).isEqualTo("PARAMETER_NAME");
        assertThat(parameter.getExpression()).isInstanceOf(Function.class);
    }

    @Test
    void shouldResolveParameterWithNullName() throws IOException {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/nullNameProperty.json");
        JsonNode node = mapper.readTree(json);

        // when
        Parameter parameter = deserializer.deserialize(node.traverse(mapper), null);

        // then
        assertThat(parameter.getName()).isNull();
        assertThat(parameter.getExpression()).isInstanceOf(Function.class);
    }

    @Test
    void shouldRThrowExceptionIfJsonHasNamePropertyOnly() throws IOException {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/noExpressionProperty.json");
        JsonNode node = mapper.readTree(json);

        // when / then
        String expectedMessage = "Given node: {\"name\":null} could not be deserialized to any known operand model";
        assertThatThrownBy(() -> deserializer.deserialize(node.traverse(mapper), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }
}
