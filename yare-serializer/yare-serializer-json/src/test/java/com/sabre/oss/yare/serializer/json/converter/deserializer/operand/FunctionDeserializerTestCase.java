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
import com.sabre.oss.yare.serializer.json.model.Function;
import com.sabre.oss.yare.serializer.json.model.Operand;
import com.sabre.oss.yare.serializer.json.model.Parameter;
import com.sabre.oss.yare.serializer.json.model.Value;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class FunctionDeserializerTestCase {
    protected ObjectMapper mapper;
    protected Deserializer deserializer;

    protected abstract String getTestResource(String fileName);

    @Test
    void shouldBeApplicableForJsonWithFunctionProperty() throws IOException {
        // given
        String json = getTestResource("functionProperty");
        JsonNode node = mapper.readTree(json);

        // when
        Boolean applicable = deserializer.isApplicable(node);

        // then
        assertThat(applicable).isTrue();
    }

    @Test
    void shouldBeApplicableForJsonWithNullFunctionProperty() throws IOException {
        // given
        String json = getTestResource("nullFunctionProperty");
        JsonNode node = mapper.readTree(json);

        // when
        Boolean applicable = deserializer.isApplicable(node);

        // then
        assertThat(applicable).isTrue();
    }

    @Test
    void shouldNotBeApplicableForJsonWithoutFunctionProperty() throws IOException {
        // given
        String json = getTestResource("noFunctionProperty");
        JsonNode node = mapper.readTree(json);

        // when
        Boolean applicable = deserializer.isApplicable(node);

        // then
        assertThat(applicable).isFalse();
    }

    @Test
    void shouldResolveFunction() throws IOException {
        // given
        String json = getTestResource("function");
        JsonNode node = mapper.readTree(json);

        // when
        Operand result = deserializer.deserialize(node, mapper);

        // then
        assertThat(result).isInstanceOfSatisfying(Function.class, f -> {
            assertThat(f.getName()).isEqualTo("FUNCTION_NAME");
            assertThat(f.getReturnType()).isEqualTo("RETURN_TYPE");
            assertThat(f.getParameters()).containsExactlyInAnyOrder(
                    createParameter("PARAM_NAME_1", false, "Boolean"),
                    createParameter("PARAM_NAME_2", true, "Boolean")
            );
        });
    }

    @Test
    void shouldResolveNullFunctionAsNull() throws IOException {
        // given
        String json = getTestResource("nullFunctionProperty");
        JsonNode node = mapper.readTree(json);

        // when
        Operand result = deserializer.deserialize(node, mapper);

        // then
        assertThat(result).isNull();
    }

    @Test
    void shouldResolveNullFunctionProperties() throws IOException {
        // given
        String json = getTestResource("nullProperties");
        JsonNode node = mapper.readTree(json);

        // when
        Operand result = deserializer.deserialize(node, mapper);

        // then
        assertThat(result).isInstanceOfSatisfying(Function.class, f -> {
            assertThat(f.getName()).isNull();
            assertThat(f.getReturnType()).isNull();
            assertThat(f.getParameters()).isNull();
        });
    }

    private Parameter createParameter(String name, Object o, String type) {
        return new Parameter()
                .withName(name)
                .withExpression(new Value()
                        .withValue(o)
                        .withType(type));
    }
}
