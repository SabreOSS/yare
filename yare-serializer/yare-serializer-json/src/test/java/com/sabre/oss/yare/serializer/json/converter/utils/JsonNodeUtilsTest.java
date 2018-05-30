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

package com.sabre.oss.yare.serializer.json.converter.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JsonNodeUtilsTest {
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    void shouldReturnEmptyIfJsonNodeIsNull() {
        // given / when
        Optional<JsonNode> o = JsonNodeUtils.resolveChildNode(null, "property");

        // then
        assertThat(o).isEmpty();
    }

    @Test
    void shouldReturnEmptyIfJsonNodePropertyIsNull() throws IOException {
        // given
        JsonNode node = mapper.readTree("" +
                "{" +
                "  \"property\": null" +
                "}");

        // when
        Optional<JsonNode> o = JsonNodeUtils.resolveChildNode(node, "property");

        // then
        assertThat(o).isEmpty();
    }

    @Test
    void shouldReturnValueIfJsonNodePropertyIsNotNull() throws IOException {
        // given
        JsonNode node = mapper.readTree("" +
                "{" +
                "  \"property\": \"PROPERTY_VALUE\"" +
                "}");

        // when
        Optional<JsonNode> o = JsonNodeUtils.resolveChildNode(node, "property");

        // then
        assertThat(o).isNotEmpty();
        assertThat(o).hasValueSatisfying(
                n -> {
                    assertThat(n).isNotNull();
                    assertThat(n.textValue()).isEqualTo("PROPERTY_VALUE");
                }
        );
    }
}
