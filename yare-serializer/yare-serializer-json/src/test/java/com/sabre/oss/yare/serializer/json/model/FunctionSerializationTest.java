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

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

class FunctionSerializationTest {
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.objectMapper = RuleToJsonConverter.getObjectMapper();
    }

    @Test
    void shouldProperlySerializeFunction() throws JsonProcessingException {
        Function function = getFunctionModel();

        String serialized = objectMapper.writeValueAsString(function);

        String expected = getFunctionJson();
        assertThatJson(serialized).isEqualTo(expected);
    }

    @Test
    void shouldProperlyDeserializeFunction() throws IOException {
        String json = getFunctionJson();

        Function function = objectMapper.readValue(json, Function.class);

        Function expected = getFunctionModel();
        assertThat(function).isEqualTo(expected);
    }

    private Function getFunctionModel() {
        return new Function()
                .withName("function-name-1")
                .withParameters(
                        new Parameter()
                                .withName("param-name-1")
                                .withExpression(new Value()
                                        .withValue(10)
                                        .withType(Integer.class.getName())),
                        new Parameter()
                                .withName("param-name-2")
                                .withExpression(new Values()
                                        .withType("values-type")
                                        .withValues(
                                                new Value().withValue("value-value"),
                                                new Value().withValue(10).withType(Integer.class.getName())
                                        )),
                        new Parameter()
                                .withName("param-name-3")
                                .withExpression(new Function()
                                        .withName("function-name-2")
                                        .withParameters())
                );
    }

    private String getFunctionJson() {
        return "" +
                "{" +
                "  \"function\" : {" +
                "    \"name\" : \"function-name-1\"," +
                "    \"parameters\" : [ {" +
                "      \"name\" : \"param-name-1\"," +
                "      \"value\" : 10," +
                "      \"type\" : \"java.lang.Integer\"" +
                "    }, {" +
                "      \"name\" : \"param-name-2\"," +
                "      \"values\" : [ {" +
                "        \"value\" : \"value-value\"" +
                "      }, {" +
                "        \"value\" : 10," +
                "        \"type\" : \"java.lang.Integer\"" +
                "      } ]," +
                "      \"type\" : \"values-type\"" +
                "    }, {" +
                "      \"name\" : \"param-name-3\"," +
                "      \"function\" : {" +
                "        \"name\" : \"function-name-2\"," +
                "        \"parameters\" : [ ]" +
                "      }" +
                "    } ]" +
                "  }" +
                "}";
    }
}
