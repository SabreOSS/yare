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
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public abstract class ValueSerializationTestCase {
    private ObjectMapper objectMapper;

    protected abstract ObjectMapper createObjectMapper();

    protected abstract String getTestResource(String fileName);

    @BeforeEach
    void setUp() {
        objectMapper = createObjectMapper();
    }

    @TestFactory
    Stream<DynamicTest> valueSerializationTestFactory() {
        return Stream.of(
                DynamicTest.dynamicTest(
                        "Should serialize build-in type value",
                        () -> shouldSerializeValue(createBuildInTypeValueModel(), getSerializedBuildInTypeValue())
                ),
                DynamicTest.dynamicTest(
                        "Should serialize string type value",
                        () -> shouldSerializeValue(createStringTypeValueModel(), getSerializedStringTypeValue())
                ),
                DynamicTest.dynamicTest(
                        "Should serialize custom type value",
                        () -> shouldSerializeValue(createCustomTypeValueModel(), getSerializedCustomTypeValue())
                )
        );
    }

    @TestFactory
    Stream<DynamicTest> valueDeserializationTestFactory() {
        return Stream.of(
                DynamicTest.dynamicTest(
                        "Should deserialize build-in type value",
                        () -> shouldDeserializeValue(createBuildInTypeValueModel(), getSerializedBuildInTypeValue())
                ),
                DynamicTest.dynamicTest(
                        "Should deserialize string type value",
                        () -> shouldDeserializeValue(createStringTypeValueModel(), getSerializedStringTypeValue())
                ),
                DynamicTest.dynamicTest(
                        "Should deserialize custom type value",
                        () -> shouldDeserializeValue(createCustomTypeValueModel(), getSerializedCustomTypeValue())
                )
        );
    }

    private void shouldSerializeValue(Value value, String expected) throws JsonProcessingException {
        String serialized = objectMapper.writeValueAsString(value);

        assertThat(serialized).isEqualToIgnoringWhitespace(expected);
    }

    private void shouldDeserializeValue(Value expected, String json) throws IOException {
        Value value = objectMapper.readValue(json, Value.class);

        assertThat(value).isEqualTo(expected);
    }

    private Value createBuildInTypeValueModel() {
        return new Value()
                .withValue(10)
                .withType("Integer");
    }

    private String getSerializedBuildInTypeValue() {
        return getTestResource("valueWithBuildInType");
    }

    private Value createStringTypeValueModel() {
        return new Value()
                .withValue("test-value");
    }

    private String getSerializedStringTypeValue() {
        return getTestResource("valueWithStringType");
    }

    private Value createCustomTypeValueModel() {
        return new Value()
                .withValue(new TestClass().withProperty("test-property"))
                .withType(TestClass.class.getName());
    }

    private String getSerializedCustomTypeValue() {
        return getTestResource("valueWithCustomType");
    }

    @Test
    void shouldThrowExceptionWhenUnknownTypeUsed() {
        String json = getTestResource("valueWithUnknownType");

        assertThatThrownBy(() -> objectMapper.readValue(json, Value.class))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Can't convert 'UNKNOWN' into Java type");
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
