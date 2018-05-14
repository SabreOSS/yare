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

package com.sabre.oss.yare.invoker.js;

import com.sabre.oss.yare.core.RuleSession;
import com.sabre.oss.yare.core.RulesEngine;
import com.sabre.oss.yare.core.RulesEngineBuilder;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.dsl.RuleDsl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.invoker.js.JavaScriptCallMetadata.js;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class JavaScriptBasedFunctionTest {
    private static final String RULE_NAME = "NAME";

    private String script;

    @BeforeEach
    void setUp() {
        script = "" +
                "function collect(context, ruleName) {" +
                "   context.add(ruleName);" +
                "}" +
                "function upperCase(value) {" +
                "   return value.toUpperCase();" +
                '}' +
                "function concat(str1, str2) {" +
                "   return str1 + str2;" +
                "}";
    }

    @Test
    void shouldMatchFunctionResultToConstant() {
        // given
        List<Object> facts = singletonList(new FactOne("the_value"));
        Rule rule = RuleDsl.ruleBuilder()
                .name(RULE_NAME)
                .fact("factOne", FactOne.class)
                .attribute("ruleType", "anyType")
                .predicate(
                        match(
                                function("upperCase", String.class,
                                        param("value", value("${factOne.aString}"))),
                                value("THE_VALUE")
                        ))
                .action("collect",
                        param("context", value("${ctx}")),
                        param("ruleName", value(RULE_NAME)))
                .build();
        RuleSession ruleSession = createRuleSession(rule);

        // when
        List<Object> results = ruleSession.execute(new ArrayList<>(), facts);

        // then
        assertThat(results).containsExactly(RULE_NAME);
    }

    @Test
    void shouldMatchFunctionResultToConstantInMuchComplexRule() {
        // given
        List<Object> facts = singletonList(new FactOne("the_value"));
        Rule rule = RuleDsl.ruleBuilder()
                .name(RULE_NAME)
                .fact("factOne", FactOne.class)
                .attribute("ruleType", "anyType")
                .predicate(
                        and(
                                match(
                                        value("${factOne.aString}"),
                                        value("the_value")
                                ),
                                match(
                                        function("upperCase", String.class,
                                                param("value", value("${factOne.aString}"))),
                                        value("THE_VALUE")
                                )))
                .action("collect",
                        param("context", value("${ctx}")),
                        param("ruleName", value(RULE_NAME)))
                .build();
        RuleSession ruleSession = createRuleSession(rule);

        // when
        List<Object> results = ruleSession.execute(new ArrayList<>(), facts);

        // then
        assertThat(results).containsExactly(RULE_NAME);
    }

    @Test
    void shouldMatchTwoFunctionCallsToConstants() {
        // given
        List<Object> facts = singletonList(new FactOne("the_value"));
        Rule rule = RuleDsl.ruleBuilder()
                .name(RULE_NAME)
                .fact("factOne", FactOne.class)
                .attribute("ruleType", "anyType")
                .predicate(
                        and(
                                match(
                                        function("upperCase", String.class,
                                                param("value", value("${factOne.aString}"))),
                                        value("THE_VALUE")
                                )))
                .action("collect",
                        param("context", value("${ctx}")),
                        param("ruleName", value(RULE_NAME)))
                .build();
        RuleSession ruleSession = createRuleSession(rule);

        // when
        List<Object> results = ruleSession.execute(new ArrayList<>(), facts);

        // then
        assertThat(results).containsExactly(RULE_NAME);
    }

    @Test
    void shouldPassWhenFunctionFromConstantOnly() {
        // given
        List<Object> facts = singletonList(new FactOne("the_value"));
        Rule rule = RuleDsl.ruleBuilder()
                .name(RULE_NAME)
                .fact("factOne", FactOne.class)
                .attribute("ruleType", "anyType")
                .predicate(
                        and(
                                match(
                                        function("upperCase", String.class,
                                                param("value", value("the_value"))),
                                        value("THE_VALUE")
                                )))
                .action("collect",
                        param("context", value("${ctx}")),
                        param("ruleName", value(RULE_NAME)))
                .build();
        RuleSession ruleSession = createRuleSession(rule);

        // when
        List<Object> results = ruleSession.execute(new ArrayList<>(), facts);

        // then
        assertThat(results).containsExactly(RULE_NAME);
    }

    @Test
    void shouldMatchFunctionWithVariableFromContext() {
        // given
        List<Object> facts = singletonList(new FactOne("the_value"));
        Rule rule = RuleDsl.ruleBuilder()
                .name(RULE_NAME)
                .fact("factOne", FactOne.class)
                .attribute("ruleType", "anyType")
                .predicate(
                        and(
                                equal(
                                        function("concat", String.class,
                                                param("a", value("${factOne.aString}")),
                                                param("b", value("${ruleName}"))),
                                        value("the_value" + RULE_NAME)
                                )))
                .action("collect",
                        param("context", value("${ctx}")),
                        param("ruleName", value(RULE_NAME)))
                .build();
        RuleSession ruleSession = createRuleSession(rule);

        // when
        List<Object> results = ruleSession.execute(new ArrayList<>(), facts);

        // then
        assertThat(results).containsExactly(RULE_NAME);
    }

    @Test
    void shouldPass2FactsBasedArgumentsToFunction() {
        // given
        List<Object> facts = asList(
                new FactOne("abc"),
                new FactTwo("123"));
        Rule rule = RuleDsl.ruleBuilder()
                .name(RULE_NAME)
                .fact("factOne", FactOne.class)
                .fact("factTwo", FactTwo.class)
                .attribute("ruleType", "anyType")
                .predicate(
                        and(
                                match(
                                        function("concat", String.class,
                                                param("first", value("${factOne.aString}")),
                                                param("second", value("${factTwo.value}"))),
                                        value("abc123")
                                )))
                .action("collect",
                        param("context", value("${ctx}")),
                        param("ruleName", value(RULE_NAME)))
                .build();
        RuleSession ruleSession = createRuleSession(rule);

        // when
        List<Object> results = ruleSession.execute(new ArrayList<>(), facts);

        // then
        assertThat(results).containsExactly(RULE_NAME);
    }

    private RuleSession createRuleSession(Rule rule) {
        RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository(i -> singletonList(rule))
                .withActionMapping("collect", js("collect", script))
                .withFunctionMapping("upperCase", js("upperCase", script))
                .withFunctionMapping("concat", js("concat", script))
                .build();

        return rulesEngine.createSession("test");
    }

    private static class FactOne {
        private final String aString;

        FactOne(String aString) {
            this.aString = aString;
        }

        public String getAString() {
            return aString;
        }
    }

    private static class FactTwo {
        private final String value;

        FactTwo(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
