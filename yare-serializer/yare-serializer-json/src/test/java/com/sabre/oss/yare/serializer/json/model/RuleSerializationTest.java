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
import java.math.BigDecimal;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

class RuleSerializationTest {
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.objectMapper = RuleToJsonConverter.getObjectMapper();
    }

    @Test
    void shouldSerializeRule() throws JsonProcessingException {
        Rule rule = getRuleModel();

        String serialized = objectMapper.writeValueAsString(rule);

        String expected = getRuleJson();
        assertThatJson(serialized).isEqualTo(expected);
    }

    @Test
    void shouldDeserializeRule() throws IOException {
        String json = getRuleJson();

        Rule rule = objectMapper.readValue(json, Rule.class);

        Rule expected = getRuleModel();
        assertThat(rule).isEqualTo(expected);
    }

    private Rule getRuleModel() {
        return new Rule()
                .withAttributes(
                        new Attribute()
                                .withName("ruleName")
                                .withValue("Should match preferred hotel basing on preferred property or chain code")
                                .withType("java.lang.String"),
                        new Attribute()
                                .withName("active")
                                .withValue(true)
                                .withType("java.lang.Boolean")
                )
                .withFacts(
                        new Fact()
                                .withName("hotel")
                                .withType("com.sabre.sp.ere.demo.facts.Hotel")
                )
                .withPredicate(
                        new Operator()
                                .withType("or")
                                .withOperands(
                                        new Operator()
                                                .withType("equal")
                                                .withOperands(
                                                        new Value()
                                                                .withValue("${hotel.isPreferred}"),
                                                        new Value()
                                                                .withValue(true)
                                                                .withType(Boolean.class.getName())
                                                ),
                                        new Operator()
                                                .withType("equal")
                                                .withOperands(
                                                        new Function()
                                                                .withName("getAmountOfMoney")
                                                                .withReturnType(BigDecimal.class.getName())
                                                                .withParameters(
                                                                        new Parameter()
                                                                                .withName("amount")
                                                                                .withExpression(new Value()
                                                                                        .withValue("${hotel.roomRate}")),
                                                                        new Parameter()
                                                                                .withName("inputCurrency")
                                                                                .withExpression(new Value()
                                                                                        .withValue("${hotel.currency}")),
                                                                        new Parameter()
                                                                                .withName("outputCurrency")
                                                                                .withExpression(new Value().withValue("USD"))
                                                                ),
                                                        new Value()
                                                                .withValue(new BigDecimal(100))
                                                                .withType(BigDecimal.class.getName())
                                                )
                                )
                )
                .withActions(
                        new Action()
                                .withName("collect")
                                .withParameters(
                                        new Parameter()
                                                .withName("collect")
                                                .withExpression(new Value()
                                                        .withValue("${ctx}")),
                                        new Parameter()
                                                .withName("fact")
                                                .withExpression(new Value()
                                                        .withValue("${hotel}"))
                                )
                );
    }

    private String getRuleJson() {
        return "" +
                "{" +
                "  \"attributes\" : [ {" +
                "    \"name\" : \"ruleName\"," +
                "    \"value\" : \"Should match preferred hotel basing on preferred property or chain code\"," +
                "    \"type\" : \"java.lang.String\"" +
                "  }, {" +
                "    \"name\" : \"active\"," +
                "    \"value\" : true," +
                "    \"type\" : \"java.lang.Boolean\"" +
                "  } ]," +
                "  \"facts\" : [ {" +
                "    \"name\" : \"hotel\"," +
                "    \"type\" : \"com.sabre.sp.ere.demo.facts.Hotel\"" +
                "  } ]," +
                "  \"predicate\" : {" +
                "    \"or\" : [ {" +
                "      \"equal\" : [ {" +
                "        \"value\" : \"${hotel.isPreferred}\"" +
                "      }, {" +
                "        \"value\" : true," +
                "        \"type\" : \"java.lang.Boolean\"" +
                "      } ]" +
                "    }, {" +
                "      \"equal\" : [ {" +
                "        \"function\" : {" +
                "          \"name\" : \"getAmountOfMoney\"," +
                "          \"returnType\" : \"java.math.BigDecimal\"," +
                "          \"parameters\" : [ {" +
                "            \"name\" : \"amount\"," +
                "            \"value\" : \"${hotel.roomRate}\"" +
                "          }, {" +
                "            \"name\" : \"inputCurrency\"," +
                "            \"value\" : \"${hotel.currency}\"" +
                "          }, {" +
                "            \"name\" : \"outputCurrency\"," +
                "            \"value\" : \"USD\"" +
                "          } ]" +
                "        }" +
                "      }, {" +
                "        \"value\" : 100," +
                "        \"type\" : \"java.math.BigDecimal\"" +
                "      } ]" +
                "    } ]" +
                "  }," +
                "  \"actions\" : [ {" +
                "    \"name\" : \"collect\"," +
                "    \"parameters\" : [ {" +
                "      \"name\" : \"collect\"," +
                "      \"value\" : \"${ctx}\"" +
                "    }, {" +
                "      \"name\" : \"fact\"," +
                "      \"value\" : \"${hotel}\"" +
                "    } ]" +
                "  } ]" +
                "}";
    }
}
