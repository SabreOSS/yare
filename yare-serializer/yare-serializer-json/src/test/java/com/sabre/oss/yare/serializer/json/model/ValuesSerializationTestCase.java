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
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class ValuesSerializationTestCase {
    protected ObjectMapper objectMapper;

    protected abstract String getTestResource(String fileName);

    @Test
    void shouldSerializeValues() throws JsonProcessingException {
        Values values = createValuesModel();

        String serialized = objectMapper.writeValueAsString(values);

        String expected = getSerializedValues();
        assertThat(serialized).isEqualToIgnoringWhitespace(expected);
    }

    @Test
    void shouldDeserializeValues() throws IOException {
        String json = getSerializedValues();

        Values values = objectMapper.readValue(json, Values.class);

        Values expected = createValuesModel();
        assertThat(values).isEqualTo(expected);
    }

    private Values createValuesModel() {
        return new Values()
                .withValues(
                        new Value().withValue("value-value"),
                        new Values()
                                .withType("values-type-1")
                                .withValues(
                                        new Value().withValue(new TestClass().withProperty("testclass-property")).withType(TestClass.class.getName())
                                ),
                        new Function()
                                .withName("function-name")
                                .withReturnType("function-return-type")
                                .withParameters(
                                        new Parameter()
                                                .withName("parameter-name")
                                                .withExpression(new Value().withValue(10).withType("Integer"))
                                )
                )
                .withType("values-type-2");
    }

    private String getSerializedValues() {
        return getTestResource("values");
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
