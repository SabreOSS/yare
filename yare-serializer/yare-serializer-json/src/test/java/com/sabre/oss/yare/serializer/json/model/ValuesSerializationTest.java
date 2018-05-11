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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

class ValuesSerializationTest {
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.objectMapper = RuleToJsonConverter.getObjectMapper();
    }

    @Test
    void shouldProperlySerializeValues() throws JsonProcessingException {
        Values values = getValuesModel();

        String serialized = objectMapper.writeValueAsString(values);

        String expected = getValuesJson();
        assertThatJson(serialized).isEqualTo(expected);
    }

    @Test
    void shouldProperlyDeserializeValues() throws IOException {
        String json = getValuesJson();

        Values values = objectMapper.readValue(json, Values.class);

        Values expected = getValuesModel();
        assertThat(values).isEqualTo(expected);
    }

    private Values getValuesModel() {
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
                                                .withExpression(new Value().withValue(10).withType(Integer.class.getName()))
                                )
                )
                .withType("values-type-2");
    }

    private String getValuesJson() {
        return "" +
                "{" +
                "  \"values\" : [ {" +
                "    \"value\" : \"value-value\"" +
                "  }, {" +
                "    \"values\" : [ {" +
                "      \"value\" : {" +
                "        \"property\" : \"testclass-property\"" +
                "      }," +
                "      \"type\" : \"com.sabre.oss.yare.serializer.json.model.ValuesSerializationTest$TestClass\"" +
                "    } ]," +
                "    \"type\" : \"values-type-1\"" +
                "  }, {" +
                "    \"function\" : {" +
                "      \"name\" : \"function-name\"," +
                "      \"returnType\" : \"function-return-type\"," +
                "      \"parameters\" : [ {" +
                "        \"name\" : \"parameter-name\"," +
                "        \"value\" : 10," +
                "        \"type\" : \"java.lang.Integer\"" +
                "      } ]" +
                "    }" +
                "  } ]," +
                "  \"type\" : \"values-type-2\"" +
                "}";
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
