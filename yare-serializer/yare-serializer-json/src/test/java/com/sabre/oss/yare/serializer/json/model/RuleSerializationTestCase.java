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
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class RuleSerializationTestCase {
    protected ObjectMapper objectMapper;

    protected abstract String getTestResource(String fileName);

    @TestFactory
    Stream<DynamicTest> ruleSerializationTestFactory() {
        return Stream.of(
                DynamicTest.dynamicTest(
                        "Should serialize rule",
                        () -> shouldSerializeRule(createRuleModel(), getSerializedRule())
                ),
                DynamicTest.dynamicTest(
                        "Should serialize empty rule",
                        () -> shouldSerializeRule(createEmptyRuleModel(), getSerializedEmptyRule())
                )
        );
    }

    @TestFactory
    Stream<DynamicTest> ruleDeserializationTestFactory() {
        return Stream.of(
                DynamicTest.dynamicTest(
                        "Should deserialize rule",
                        () -> shouldDeserializeRule(createRuleModel(), getSerializedRule())
                ),
                DynamicTest.dynamicTest(
                        "Should deserialize empty rule",
                        () -> shouldDeserializeRule(createEmptyRuleModel(), getSerializedEmptyRule())
                )
        );
    }

    private void shouldSerializeRule(Rule rule, String expected) throws JsonProcessingException {
        String serialized = objectMapper.writeValueAsString(rule);

        assertThat(serialized).isEqualToIgnoringWhitespace(expected);
    }

    private void shouldDeserializeRule(Rule expected, String json) throws IOException {
        Rule rule = objectMapper.readValue(json, Rule.class);

        assertThat(rule).isEqualTo(expected);
    }

    private Rule createRuleModel() {
        return new Rule()
                .withAttributes(
                        new Attribute()
                                .withName("ruleName")
                                .withValue("Should match preferred hotel basing on preferred property or chain code")
                                .withType("String"),
                        new Attribute()
                                .withName("active")
                                .withValue(true)
                                .withType("Boolean")
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
                                                                .withType("Boolean")
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

    private String getSerializedRule() {
        return getTestResource("rule");
    }

    private Rule createEmptyRuleModel() {
        return new Rule();
    }

    private String getSerializedEmptyRule() {
        return getTestResource("emptyRule");
    }
}
