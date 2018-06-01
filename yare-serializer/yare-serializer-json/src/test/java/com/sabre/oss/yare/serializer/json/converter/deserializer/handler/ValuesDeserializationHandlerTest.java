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
import com.sabre.oss.yare.serializer.json.utils.JsonResourceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class ValuesDeserializationHandlerTest {
    private static final String TEST_RESOURCES_DIRECTORY = "/converter/deserializer/handler/values";

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
        String json = JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/valuesAndTypeProperties.json");
        JsonNode node = mapper.readTree(json);

        // when
        Boolean applicable = handler.isApplicable(node);

        // then
        assertThat(applicable).isTrue();
    }

    @Test
    void shouldBeApplicableForJsonWithNullTypeProperty() throws IOException {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/nullTypeProperty.json");
        JsonNode node = mapper.readTree(json);

        // when
        Boolean applicable = handler.isApplicable(node);

        // then
        assertThat(applicable).isTrue();
    }

    @Test
    void shouldNotBeApplicableForJsonWithoutValuesProperty() throws IOException {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/noValuesProperty.json");
        JsonNode node = mapper.readTree(json);

        // when
        Boolean applicable = handler.isApplicable(node);

        // then
        assertThat(applicable).isFalse();
    }

    @Test
    void shouldNotBeApplicableForJsonWithoutTypeProperty() throws IOException {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/noTypeProperty.json");
        JsonNode node = mapper.readTree(json);

        // when
        Boolean applicable = handler.isApplicable(node);

        // then
        assertThat(applicable).isFalse();
    }

    @Test
    void shouldNotBeApplicableForJsonWithNonArrayValuesProperty() throws IOException {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/nonArrayValuesProperty.json");
        JsonNode node = mapper.readTree(json);

        // when
        Boolean applicable = handler.isApplicable(node);

        // then
        assertThat(applicable).isFalse();
    }

    @Test
    void shouldResolveValueExpressionWithinValues() throws IOException {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/valueExpression.json");
        JsonNode node = mapper.readTree(json);

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
        String json = JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/valuesExpression.json");
        JsonNode node = mapper.readTree(json);

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
        String json = JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/functionExpression.json");
        JsonNode node = mapper.readTree(json);

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

    @Test
    void shouldResolveNullWithinValuesAsNullValue() throws IOException {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/nullExpressions.json");
        JsonNode node = mapper.readTree(json);

        // when
        Operand result = handler.deserialize(node, mapper);

        // then
        assertThat(result).isInstanceOfSatisfying(Values.class, v -> {
            assertThat(v.getValues()).containsExactly(null, null);
            assertThat(v.getType()).isEqualTo(Object.class.getName());
        });
    }

    @Test
    void shouldResolveNullValuesProperty() throws IOException {
        // given
        String json = JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/nullProperties.json");
        JsonNode node = mapper.readTree(json);

        // when
        Operand result = handler.deserialize(node, mapper);

        // then
        assertThat(result).isInstanceOfSatisfying(Values.class, v -> {
            assertThat(v.getValues()).isNull();
            assertThat(v.getType()).isNull();
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
