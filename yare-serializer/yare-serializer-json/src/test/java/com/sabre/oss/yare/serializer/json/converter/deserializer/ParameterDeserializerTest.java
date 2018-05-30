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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParameterDeserializerTest {
    private ObjectMapper mapper;
    private ParameterDeserializer deserializer;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        deserializer = new ParameterDeserializer();
    }

    @Test
    void shouldResolveParameterWithValueExpressionProperly() throws IOException {
        // given
        JsonNode node = mapper.readTree("" +
                "{" +
                "  \"name\": \"PARAMETER_NAME\"," +
                "  \"value\": null" +
                "}");

        // when
        Parameter result = deserializer.deserialize(node.traverse(mapper), null);

        // then
        assertThat(result).isInstanceOfSatisfying(Parameter.class, p -> {
            assertThat(p.getName()).isEqualTo("PARAMETER_NAME");
            assertThat(p.getExpression()).isInstanceOf(Value.class);
        });
    }

    @Test
    void shouldResolveParameterWithValuesExpressionProperly() throws IOException {
        // given
        JsonNode node = mapper.readTree("" +
                "{" +
                "  \"name\": \"PARAMETER_NAME\"," +
                "  \"values\": []," +
                "  \"type\": null" +
                "}");

        // when
        Parameter result = deserializer.deserialize(node.traverse(mapper), null);

        // then
        assertThat(result).isInstanceOfSatisfying(Parameter.class, p -> {
            assertThat(p.getName()).isEqualTo("PARAMETER_NAME");
            assertThat(p.getExpression()).isInstanceOf(Values.class);
        });
    }

    @Test
    void shouldResolveParameterWithFunctionExpressionProperly() throws IOException {
        // given
        JsonNode node = mapper.readTree("" +
                "{" +
                "  \"name\": \"PARAMETER_NAME\"," +
                "  \"function\": {}" +
                "}");

        // when
        Parameter result = deserializer.deserialize(node.traverse(mapper), null);

        // then
        assertThat(result).isInstanceOfSatisfying(Parameter.class, p -> {
            assertThat(p.getName()).isEqualTo("PARAMETER_NAME");
            assertThat(p.getExpression()).isInstanceOf(Function.class);
        });
    }

    @Test
    void shouldResolveParameterWithNullNameProperly() throws IOException {
        // given
        JsonNode node = mapper.readTree("" +
                "{" +
                "  \"name\": null," +
                "  \"function\": {}" +
                "}");

        // when
        Parameter result = deserializer.deserialize(node.traverse(mapper), null);

        // then
        assertThat(result).isInstanceOfSatisfying(Parameter.class, p -> {
            assertThat(p.getName()).isNull();
            assertThat(p.getExpression()).isInstanceOf(Function.class);
        });
    }

    @Test
    void shouldRThrowExceptionIfJsonHasNamePropertyOnly() throws IOException {
        // given
        JsonNode node = mapper.readTree("" +
                "{" +
                "  \"name\": null" +
                "}");

        // when / then
        String expectedMessage = "Given node: {\"name\":null} could not be deserialized to any known operand model";
        assertThatThrownBy(() -> deserializer.deserialize(node.traverse(mapper), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }
}
