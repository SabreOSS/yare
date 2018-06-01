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

package com.sabre.oss.yare.serializer.json;

import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.dsl.RuleDsl;
import com.sabre.oss.yare.serializer.json.utils.JsonResourceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Objects;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

class RuleToJsonConverterTest {
    private RuleToJsonConverter converter;

    @BeforeEach
    void setUp() {
        converter = new RuleToJsonConverter();
    }

    @Test
    void shouldSerializeRule() {
        Rule rule = createRuleModel();

        String converted = converter.marshal(rule);

        String expected = createRuleJson();
        assertThatJson(converted).isEqualTo(expected);
    }

    @Test
    void shouldDeserializeRule() {
        String json = createRuleJson();

        Rule converted = converter.unmarshal(json);

        Rule expected = createRuleModel();
        assertThat(converted).isEqualTo(expected);
    }

    private Rule createRuleModel() {
        return RuleDsl.ruleBuilder()
                .attribute("ruleName", "Name of the rule")
                .attribute("customObject", new TestClass("testclass-property-1", 100))
                .fact("fact-1", Object.class)
                .fact("fact-2", BigDecimal.class)
                .predicate(
                        and(
                                or(
                                        equal(
                                                values(String.class,
                                                        value("test"),
                                                        function("function-1", String.class)),
                                                function("function-1", collectionTypeOf(String.class),
                                                        param("param-1", function("function-2")))
                                        )
                                ),
                                function("function-3", Boolean.class,
                                        param("param-2", value(new TestClass("testclass-property-2", 200))),
                                        param("param-3", values(collectionTypeOf(String.class), values(String.class, value("test"))))),
                                not(
                                        isNull(
                                                value(null)
                                        )
                                )
                        )
                )
                .action("action-name",
                        param("param-1", value(new BigDecimal(100))),
                        param("param-2", values(String.class, value("test"))),
                        param("param-3", function("function-4")))
                .build(false);
    }

    private String createRuleJson() {
        return JsonResourceUtils.getJsonResourceAsString("/rule.json");
    }

    public static class TestClass {
        private String stringProperty;
        private Integer integerProperty;

        TestClass() {
        }

        TestClass(String stringProperty, Integer integerProperty) {
            this.stringProperty = stringProperty;
            this.integerProperty = integerProperty;
        }

        public String getStringProperty() {
            return stringProperty;
        }

        public void setStringProperty(String stringProperty) {
            this.stringProperty = stringProperty;
        }

        public Integer getIntegerProperty() {
            return integerProperty;
        }

        public void setIntegerProperty(Integer integerProperty) {
            this.integerProperty = integerProperty;
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
            return Objects.equals(stringProperty, testClass.stringProperty) &&
                    Objects.equals(integerProperty, testClass.integerProperty);
        }

        @Override
        public int hashCode() {
            return Objects.hash(stringProperty, integerProperty);
        }
    }
}
