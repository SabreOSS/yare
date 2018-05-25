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
import com.sabre.oss.yare.serializer.json.model.Function;
import com.sabre.oss.yare.serializer.json.model.Operand;
import com.sabre.oss.yare.serializer.json.model.Parameter;
import com.sabre.oss.yare.serializer.json.model.Value;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class FunctionDeserializationHandlerTest extends DeserializationHandlerTestBase {
    private FunctionDeserializationHandler handler = new FunctionDeserializationHandler();

    @Test
    void shouldBeApplicableForJsonWithFunctionProperty()
            throws IOException {
        //given
        JsonNode node = toJsonNode("" +
                "{" +
                "  \"function\" : {" +
                "    \"name\": \"FUNCTION_NAME\"" +
                "  }" +
                "}");

        //when
        Boolean applicable = handler.isApplicable(node);

        //then
        assertThat(applicable).isTrue();
    }

    @Test
    void shouldNotBeApplicableForJsonWithoutFunctionProperty()
            throws IOException {
        //given
        JsonNode node = toJsonNode("" +
                "{" +
                "  \"unknown\" : {" +
                "    \"name\": \"NAME\"" +
                "  }" +
                "}");

        //when
        Boolean applicable = handler.isApplicable(node);

        //then
        assertThat(applicable).isFalse();
    }

    @Test
    void shouldResolveFunctionProperly()
            throws IOException {
        //given
        JsonNode node = toJsonNode("" +
                "{" +
                "  \"function\" : {" +
                "    \"name\": \"FUNCTION_NAME\"," +
                "    \"parameters\": [" +
                "      {" +
                "        \"name\": \"PARAM_NAME_1\"," +
                "        \"value\": \"false\"," +
                "        \"type\": \"java.lang.Boolean\"" +
                "      }," +
                "      {" +
                "        \"name\": \"PARAM_NAME_2\"," +
                "        \"value\": \"true\"," +
                "        \"type\": \"java.lang.Boolean\"" +
                "      }" +
                "    ]" +
                "  }" +
                "}");

        //when
        Operand result = handler.deserialize(node, mapper);

        //then
        assertThat(result).isInstanceOf(Function.class);

        Function resultFunction = (Function) result;
        assertThat(resultFunction.getName()).isEqualTo("FUNCTION_NAME");
        assertThat(resultFunction.getParameters()).containsExactlyInAnyOrder(
                booleanValueParameter("PARAM_NAME_1", false),
                booleanValueParameter("PARAM_NAME_2", true)
        );
    }

    private Parameter booleanValueParameter(String name, Boolean b) {
        return new Parameter()
                .withName(name)
                .withExpression(new Value()
                        .withValue(b)
                        .withType(Boolean.class.getName()));
    }
}
