/*
 * MIT License
 *
 * Copyright 2018 Sabre GLBL c.
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

public abstract class ActionSerializationTestCase {
    private ObjectMapper objectMapper;

    protected abstract ObjectMapper createObjectMapper();

    protected abstract String getTestResource(String fileName);

    @BeforeEach
    void setUp() {
        objectMapper = createObjectMapper();
    }

    @Test
    void shouldSerializeAction() throws JsonProcessingException {
        Action action = getActionModel();

        String serialized = objectMapper.writeValueAsString(action);

        String expected = getSerializedAction();
        assertThat(serialized).isEqualToIgnoringWhitespace(expected);
    }

    @Test
    void shouldDeserializeAction() throws IOException {
        String json = getSerializedAction();

        Action action = objectMapper.readValue(json, Action.class);

        Action expected = getActionModel();
        assertThat(action).isEqualTo(expected);
    }

    private Action getActionModel() {
        return new Action()
                .withName("action-name")
                .withParameters(
                        new Parameter()
                                .withName("param-name-1")
                                .withExpression(new Value()
                                        .withValue(10)
                                        .withType("Integer")),
                        new Parameter()
                                .withName("param-name-2")
                                .withExpression(new Values()
                                        .withType("values-type")
                                        .withValues(
                                                new Value().withValue("value-value"),
                                                new Value().withValue(10).withType("Integer")
                                        )),
                        new Parameter()
                                .withName("param-name-3")
                                .withExpression(new Function()
                                        .withName("function-name")
                                        .withReturnType("function-return-type")
                                        .withParameters())
                );
    }

    private String getSerializedAction() {
        return getTestResource("action");
    }
}
