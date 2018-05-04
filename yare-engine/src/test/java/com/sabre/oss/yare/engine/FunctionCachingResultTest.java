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

package com.sabre.oss.yare.engine;

import com.sabre.oss.yare.core.RuleSession;
import com.sabre.oss.yare.core.RulesEngine;
import com.sabre.oss.yare.core.RulesEngineBuilder;
import com.sabre.oss.yare.core.feature.Feature;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.dsl.RuleDsl;
import com.sabre.oss.yare.engine.feature.DefaultEngineFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.engine.MethodCallMetadata.method;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class FunctionCachingResultTest {
    private static final String RETURN_TEST_STRING = "returnTestString";

    private TestFunction testFunction;

    private List<Rule> rules = Arrays.asList(
            RuleDsl.ruleBuilder()
                    .name("test")
                    .fact("testFact", TestFact.class)
                    .predicate(
                            match(
                                    function(RETURN_TEST_STRING, String.class),
                                    value("test")
                            )
                    )
                    .action("testAction", param("context", reference("ctx")), param("fact", reference("testFact")))
                    .build()
    );

    @BeforeEach
    void setUp() {
        testFunction = new TestFunction();
    }

    @Test
    void shouldNotCacheFunctionResultIfRequested() {
        // given
        RulesEngine rulesEngine = createRulesEngine(DefaultEngineFeature.DISABLE_CACHE_FUNCTION_RESULT);
        List<Object> testFact = new ArrayList<>();
        testFact.add(new TestFact());
        RuleSession session = rulesEngine.createSession("any");

        // when
        session.execute(new ArrayList<>(), testFact);
        session.execute(new ArrayList<>(), testFact);

        // then
        assertThat(testFunction.getExecutionsCount()).isEqualTo(2);
    }

    @Test
    void shouldCacheFunctionResultByDefault() {
        // given
        RulesEngine rulesEngine = createRulesEngine();
        List<Object> testFact = new ArrayList<>();
        testFact.add(new TestFact());
        RuleSession session = rulesEngine.createSession("any");

        // when
        session.execute(new ArrayList<>(), testFact);
        session.execute(new ArrayList<>(), testFact);

        // then
        assertThat(testFunction.getExecutionsCount()).isEqualTo(1);
    }

    private RulesEngine createRulesEngine(Feature... features) {
        return new RulesEngineBuilder()
                .withRulesRepository(i -> rules)
                .withActionMapping("testAction", method(new TestAction(), (action) -> action.execute(null, null)))
                .withFunctionMapping(RETURN_TEST_STRING, method(testFunction, TestFunction::returnTestString), features)
                .build();
    }

    private static class TestFact {
    }

    public static class TestFunction {
        private int executionsCount = 0;

        public String returnTestString() {
            executionsCount++;
            return "test";
        }

        public int getExecutionsCount() {
            return executionsCount;
        }
    }

    public static class TestAction {

        public void execute(List<TestFact> results, TestFact testFact) {
            results.add(testFact);
        }
    }
}
