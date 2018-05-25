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

package com.sabre.oss.yare.serializer.json.deserializer.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabre.oss.yare.serializer.json.model.Operand;
import com.sabre.oss.yare.serializer.json.model.Operator;
import com.sabre.oss.yare.serializer.json.model.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class OperatorDeserializationHandlerTest {
    private ObjectMapper mapper;
    private DeserializationHandler handler;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        handler = new OperatorDeserializationHandler();
    }

    @Test
    void shouldBeApplicableForJsonWithAnyArrayProperty() throws IOException {
        // given
        JsonNode node = mapper.readTree("" +
                "{" +
                "  \"or\" : [" +
                "    {" +
                "      \"value\": \"true\"" +
                "    }" +
                "  ]" +
                "}");

        // when
        Boolean applicable = handler.isApplicable(node);

        // then
        assertThat(applicable).isTrue();
    }

    @Test
    void shouldNotBeApplicableForJsonWithoutArrayProperty() throws IOException {
        // given
        JsonNode node = mapper.readTree("" +
                "{" +
                "  \"or\" : {" +
                "    \"value\": \"true\"" +
                "  }" +
                "}");

        // when
        Boolean applicable = handler.isApplicable(node);

        // then
        assertThat(applicable).isFalse();
    }

    @Test
    void shouldNotBeApplicableForJsonWithNoProperties() throws IOException {
        // given
        JsonNode node = mapper.readTree("{}");

        // when
        Boolean applicable = handler.isApplicable(node);

        // then
        assertThat(applicable).isFalse();
    }

    @Test
    void shouldResolveOperatorProperly() throws IOException {
        // given
        JsonNode node = mapper.readTree("" +
                "{" +
                "  \"or\" : [" +
                "    {" +
                "      \"value\": \"true\"," +
                "      \"type\": \"java.lang.Boolean\"" +
                "    }," +
                "    {" +
                "      \"value\": \"100\"," +
                "      \"type\": \"java.lang.Integer\"" +
                "    }" +
                "  ]" +
                "}");

        // when
        Operand result = handler.deserialize(node, mapper);

        // then
        assertThat(result).isInstanceOfSatisfying(
                Operator.class, o -> {
                    assertThat(o.getType()).isEqualTo("or");
                    assertThat(o.getOperands()).containsExactlyInAnyOrder(
                            createValueExpression(true),
                            createValueExpression(100)
                    );
                });
    }

    private Value createValueExpression(Object o) {
        return new Value()
                .withValue(o)
                .withType(o.getClass().getName());
    }
}
