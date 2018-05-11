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

package com.sabre.oss.yare.serializer.json.converter.deserializer.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabre.oss.yare.serializer.json.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class ValuesDeserializationHandlerTest {
    private ObjectMapper mapper;
    private DeserializationHandler handler;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        handler = new ValuesDeserializationHandler();
    }

    @Test
    void shouldBeApplicableForJsonWithValuesAndTypeProperties() throws IOException {
        // given
        JsonNode node = mapper.readTree("" +
                "{" +
                "  \"values\": [" +
                "    \"TEST_VALUE\"" +
                "  ]," +
                "  \"type\": \"java.lang.String\"" +
                "}");

        // when
        Boolean applicable = handler.isApplicable(node);

        // then
        assertThat(applicable).isTrue();
    }

    @Test
    void shouldNotBeApplicableForJsonWithoutValuesProperty() throws IOException {
        // given
        JsonNode node = mapper.readTree("" +
                "{" +
                "  \"type\": \"java.lang.String\"" +
                "}");

        // when
        Boolean applicable = handler.isApplicable(node);

        // then
        assertThat(applicable).isFalse();
    }

    @Test
    void shouldNotBeApplicableForJsonWithoutTypeProperty() throws IOException {
        // given
        JsonNode node = mapper.readTree("" +
                "{" +
                "  \"values\": [" +
                "    {" +
                "      \"value\": \"100\"," +
                "      \"type\": \"java.lang.Integer\"" +
                "    }" +
                "  ]" +
                "}");

        // when
        Boolean applicable = handler.isApplicable(node);

        // then
        assertThat(applicable).isFalse();
    }

    @Test
    void shouldNotBeApplicableForJsonWithNonArrayValuesProperty() throws IOException {
        // given
        JsonNode node = mapper.readTree("" +
                "{" +
                "  \"values\": \"TEST_VALUE\"," +
                "  \"type\": \"java.lang.String\"" +
                "}");

        // when
        Boolean applicable = handler.isApplicable(node);

        // then
        assertThat(applicable).isFalse();
    }

    @Test
    void shouldResolveValueExpressionWithinValues() throws IOException {
        // given
        JsonNode node = mapper.readTree("" +
                "{" +
                "  \"values\": [" +
                "    {" +
                "      \"value\": \"100\"," +
                "      \"type\": \"java.lang.Integer\"" +
                "    }," +
                "    {" +
                "      \"value\": \"VALUE_1\"" +
                "    }" +
                "  ]," +
                "  \"type\": \"java.lang.Object\"" +
                "}");

        // when
        Operand result = handler.deserialize(node, mapper);

        // then
        assertThat(result).isInstanceOfSatisfying(Values.class, v -> {
            assertThat(v.getValues()).containsExactlyInAnyOrder(
                    createValueExpression(100),
                    createValueExpression("VALUE_1")
            );
            assertThat(v.getType()).isEqualTo(Object.class.getName());
        });
    }

    @Test
    void shouldResolveValuesExpressionWithinValues() throws IOException {
        // given
        JsonNode node = mapper.readTree("" +
                "{" +
                "  \"values\": [" +
                "    {" +
                "      \"values\": [" +
                "        {" +
                "          \"value\": \"100\"," +
                "          \"type\": \"java.lang.Integer\"" +
                "        }," +
                "        {" +
                "          \"value\": \"200\"," +
                "          \"type\": \"java.lang.Integer\"" +
                "        }" +
                "      ]," +
                "      \"type\": \"java.lang.Integer\"" +
                "    }," +
                "    {" +
                "      \"values\": [" +
                "        {" +
                "          \"value\": \"VALUE_1\"" +
                "        }," +
                "        {" +
                "          \"value\": \"VALUE_2\"" +
                "        }" +
                "      ]," +
                "      \"type\": \"java.lang.String\"" +
                "    }" +
                "  ]," +
                "  \"type\": \"java.lang.Object\"" +
                "}");

        // when
        Operand result = handler.deserialize(node, mapper);

        // then
        assertThat(result).isInstanceOfSatisfying(Values.class, v -> {
            assertThat(v.getValues()).containsExactlyInAnyOrder(
                    createValuesExpression(100, 200),
                    createValuesExpression("VALUE_1", "VALUE_2")
            );
            assertThat(v.getType()).isEqualTo(Object.class.getName());
        });
    }

    @Test
    void shouldResolveFunctionExpressionWithinValues() throws IOException {
        // given
        JsonNode node = mapper.readTree("" +
                "{" +
                "  \"values\": [" +
                "    {" +
                "      \"function\": {" +
                "        \"name\": \"FUNCTION_NAME_1\"," +
                "        \"returnType\": \"RETURN_TYPE_1\"," +
                "        \"parameters\": [" +
                "          {" +
                "            \"name\": \"PARAMETER_NAME_1\"," +
                "            \"value\": \"true\"," +
                "            \"type\": \"java.lang.Boolean\"" +
                "          }" +
                "        ]" +
                "      }" +
                "    }," +
                "    {" +
                "      \"function\": {" +
                "        \"name\": \"FUNCTION_NAME_2\"," +
                "        \"returnType\": \"RETURN_TYPE_2\"," +
                "        \"parameters\": [" +
                "          {" +
                "            \"name\": \"PARAMETER_NAME_2\"," +
                "            \"value\": \"false\"," +
                "            \"type\": \"java.lang.Boolean\"" +
                "          }" +
                "        ]" +
                "      }" +
                "    }" +
                "  ]," +
                "  \"type\": \"java.lang.Object\"" +
                "}");

        // when
        Operand result = handler.deserialize(node, mapper);

        // then
        assertThat(result).isInstanceOfSatisfying(Values.class, v -> {
            assertThat(v.getValues()).containsExactlyInAnyOrder(
                    createFunctionExpression("FUNCTION_NAME_1", "RETURN_TYPE_1",
                            createParameter("PARAMETER_NAME_1", true)
                    ),
                    createFunctionExpression("FUNCTION_NAME_2", "RETURN_TYPE_2",
                            createParameter("PARAMETER_NAME_2", false)
                    )
            );
            assertThat(v.getType()).isEqualTo(Object.class.getName());
        });
    }

    private Value createValueExpression(Object o) {
        return new Value()
                .withValue(o)
                .withType(o.getClass().getName());
    }

    private Values createValuesExpression(Object o1, Object o2) {
        return new Values()
                .withValues(
                        createValueExpression(o1),
                        createValueExpression(o2)
                )
                .withType(o1.getClass().getName());
    }

    private Function createFunctionExpression(String name, String returnType, Parameter p) {
        return new Function()
                .withName(name)
                .withReturnType(returnType)
                .withParameters(p);
    }

    private Parameter createParameter(String name, Object o) {
        return new Parameter()
                .withName(name)
                .withExpression(createValueExpression(o));
    }
}
