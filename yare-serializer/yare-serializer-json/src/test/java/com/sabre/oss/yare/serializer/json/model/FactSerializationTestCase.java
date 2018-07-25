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

package com.sabre.oss.yare.serializer.json.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class FactSerializationTestCase {
    private ObjectMapper objectMapper;

    protected abstract ObjectMapper createObjectMapper();

    protected abstract String getTestResource(String fileName);

    @BeforeEach
    void setUp() {
        objectMapper = createObjectMapper();
    }

    @Test
    void shouldSerializeFact() throws JsonProcessingException {
        Fact fact = getFactModel();

        String serialized = objectMapper.writeValueAsString(fact);

        String expected = getSerializedFact();
        assertThat(serialized).isEqualToIgnoringWhitespace(expected);
    }

    @Test
    void shouldDeserializeFact() throws IOException {
        String json = getSerializedFact();

        Fact fact = objectMapper.readValue(json, Fact.class);

        Fact expected = getFactModel();
        assertThat(fact).isEqualTo(expected);
    }

    private Fact getFactModel() {
        return new Fact()
                .withName("fact-name")
                .withType("fact-type");
    }

    private String getSerializedFact() {
        return getTestResource("fact");
    }
}
