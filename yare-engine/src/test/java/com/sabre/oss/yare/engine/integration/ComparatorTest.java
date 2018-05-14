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

package com.sabre.oss.yare.engine.integration;

import com.sabre.oss.yare.core.RulesEngine;
import com.sabre.oss.yare.core.RulesEngineBuilder;
import com.sabre.oss.yare.core.interceptor.InputOutputLogger;
import com.sabre.oss.yare.dsl.RuleDsl;
import com.sabre.oss.yare.engine.executor.DefaultRulesExecutorBuilder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.engine.MethodCallMetadata.method;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class ComparatorTest {
    private static final String ACTION_NAME = "testAction";
    private static final String RULE_NAME = "NAME";

    private RulesEngine createRuleEngine(RulesExecutionConfig config) {
        return new RulesEngineBuilder()
                .withRulesRepository(i -> config.getRules())
                .withActionMapping(ACTION_NAME, method(new TestAction(), "execute", List.class, String.class))
                .withInterceptor(new InputOutputLogger())
                .withRulesExecutorBuilder(new DefaultRulesExecutorBuilder()
                        .withSequentialMode(config.isSequenceMode()))
                .build();
    }

    @Test
    void shouldEvaluateAction() {
        // given
        String theStringValue = "THE_VALUE";
        int theIntValue = 124555;
        RulesExecutionConfig config = RulesExecutionConfig.builder()
                .withFactTypes(singletonList(TestClass.class))
                .withInputType(List.class)
                .withRules(singletonList(
                        RuleDsl.ruleBuilder()
                                .name(RULE_NAME)
                                .fact("testClass", TestClass.class)
                                .attribute("ruleType", "anyType")
                                .predicate(
                                        and(
                                                match(
                                                        value("${testClass.aString}"),
                                                        value(theStringValue)
                                                ),
                                                less(
                                                        value("${testClass.aLong}"),
                                                        value((long) theIntValue)
                                                )))
                                .action(ACTION_NAME,
                                        param("context", value("${ctx}")),
                                        param("ruleName", value(RULE_NAME)))
                                .build()))
                .build();

        RulesEngine rulesEngine = createRuleEngine(config);

        // when
        List<Object> facts = Collections.singletonList(new TestClass(theStringValue, theIntValue - 10));
        List<Object> result = new ArrayList<>();
        List<Object> results = rulesEngine.createSession("test").execute(result, facts);

        // then
        assertThat(results).isNotEmpty();
    }

    private static final class TestClass {
        private final String aString;
        private final long aLong;

        private TestClass(String aString, long aLong) {
            this.aString = aString;
            this.aLong = aLong;
        }

        public String getAString() {
            return aString;
        }

        public long getALong() {
            return aLong;
        }
    }

    public static class TestAction {
        public void execute(List<String> results, String ruleName) {
            results.add(ruleName);
        }
    }
}
