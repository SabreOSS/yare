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
import com.sabre.oss.yare.serializer.json.model.Operand;
import com.sabre.oss.yare.serializer.json.model.Value;
import com.sabre.oss.yare.serializer.json.model.Values;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class ValuesDeserializationHandlerTest extends DeserializationHandlerTestBase {
    private ValuesDeserializationHandler handler = new ValuesDeserializationHandler();

    @Test
    void shouldBeApplicableForJsonWithValuesAndTypeProperties()
            throws IOException {
        //given
        JsonNode node = toJsonNode(
                "{\"values\": [\"TEST_VALUE\"], \"type\": \"java.lang.String\"}");

        //when
        Boolean applicable = handler.isApplicable(node);

        //then
        assertThat(applicable).isTrue();
    }

    @Test
    void shouldNotBeApplicableForJsonWithoutValuesProperty()
            throws IOException {
        //given
        JsonNode node = toJsonNode(
                "{\"type\": \"java.lang.String\"}");

        //when
        Boolean applicable = handler.isApplicable(node);

        //then
        assertThat(applicable).isFalse();
    }

    @Test
    void shouldNotBeApplicableForJsonWithoutTypeProperty()
            throws IOException {
        //given
        JsonNode node = toJsonNode(
                "{\"values\": [\"TEST_VALUE\"]}");

        //when
        Boolean applicable = handler.isApplicable(node);

        //then
        assertThat(applicable).isFalse();
    }

    @Test
    void shouldNotBeApplicableForJsonWithNonArrayValuesProperty()
            throws IOException {
        //given
        JsonNode node = toJsonNode(
                "{\"values\": \"TEST_VALUE\", \"type\": \"java.lang.String\"}");

        //when
        Boolean applicable = handler.isApplicable(node);

        //then
        assertThat(applicable).isFalse();
    }

    @Test
    void shouldResolveTypeAsNullWhenTypeIsNotSpecified()
            throws IOException {
        //given
        JsonNode node = toJsonNode(
                "{\"values\": [{\"value\": \"100\", \"type\": \"java.lang.Integer\"}]}");

        //when
        Operand result = handler.deserialize(node, mapper);

        //then
        assertThat(result).isInstanceOf(Values.class);

        Values resultValues = (Values) result;
        assertThat(resultValues.getValues()).containsExactlyInAnyOrder(
                integerValueExpression(100)
        );
        assertThat(resultValues.getType()).isNull();
    }

    @Test
    void shouldResolveValuesAsExpressions()
            throws IOException {
        //given
        JsonNode node = toJsonNode(
                "{\"values\": [" +
                        "{\"value\": \"100\", \"type\": \"java.lang.Integer\"}," +
                        "{\"value\": \"200\", \"type\": \"java.lang.Integer\"}" +
                        "], \"type\": \"java.lang.Integer\"}");

        //when
        Operand result = handler.deserialize(node, mapper);

        //then
        assertThat(result).isInstanceOf(Values.class);

        Values resultValues = (Values) result;
        assertThat(resultValues.getValues()).containsExactlyInAnyOrder(
                integerValueExpression(100),
                integerValueExpression(200)
        );
        assertThat(resultValues.getType()).isEqualTo(Integer.class.getName());
    }

    private Value integerValueExpression(Integer i) {
        return new Value()
                .withValue(i)
                .withType(Integer.class.getName());
    }
}
