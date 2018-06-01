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
import com.sabre.oss.yare.serializer.json.RuleToJsonConverter;
import com.sabre.oss.yare.serializer.json.utils.JsonResourceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValueSerializationTest {
    private static final String TEST_RESOURCES_DIRECTORY = "/model/value";

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.objectMapper = RuleToJsonConverter.getObjectMapper();
    }

    @ParameterizedTest
    @MethodSource("conversionParams")
    void shouldSerializeValue(Value value, String expected) throws JsonProcessingException {
        String serialized = objectMapper.writeValueAsString(value);

        assertThatJson(serialized).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("conversionParams")
    void shouldDeserializeValue(Value expected, String json) throws IOException {
        Value value = objectMapper.readValue(json, Value.class);

        assertThat(value).isEqualTo(expected);
    }

    private static Stream<Arguments> conversionParams() {
        return Stream.of(
                Arguments.of(createBuildInTypeValueModel(), createBuildInTypeValueJson()),
                Arguments.of(createStringTypeValueModel(), createStringTypeValueJson()),
                Arguments.of(createCustomTypeValueModel(), createCustomTypeValueJson())
        );
    }

    private static Value createBuildInTypeValueModel() {
        return new Value()
                .withValue(10)
                .withType(Integer.class.getName());
    }

    private static String createBuildInTypeValueJson() {
        return JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/valueWithBuildInType.json");
    }

    private static Value createStringTypeValueModel() {
        return new Value()
                .withValue("test-value");
    }

    private static String createStringTypeValueJson() {
        return JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/valueWithStringType.json");
    }

    private static Value createCustomTypeValueModel() {
        return new Value()
                .withValue(new TestClass().withProperty("test-property"))
                .withType(TestClass.class.getName());
    }

    private static String createCustomTypeValueJson() {
        return JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/valueWithCustomType.json");
    }

    @Test
    void shouldThrowExceptionWhenUnknownTypeUsed() {
        String json = JsonResourceUtils.getJsonResourceAsString(TEST_RESOURCES_DIRECTORY + "/valueWithUnknownType.json");

        assertThatThrownBy(() -> objectMapper.readValue(json, Value.class))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unable to deserialize \"test-value\", cannot find UNKNOWN class");
    }

    public static class TestClass {
        private String property;

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public TestClass withProperty(String property) {
            this.property = property;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TestClass testClass = (TestClass) o;
            return Objects.equals(property, testClass.property);
        }

        @Override
        public int hashCode() {
            return Objects.hash(property);
        }
    }
}
