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

package com.sabre.oss.yare.performance.suits;

import com.sabre.oss.yare.core.RulesEngineBuilder;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.dsl.RuleDsl;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.invoker.java.MethodCallMetadata.method;

public class FactWithChainOperatorTest extends AbstractPerformanceTest {

    public FactWithChainOperatorTest() {
        Context.setRulesEngineProvider(config -> new RulesEngineBuilder()
                .withRulesRepository(i -> config.getRules())
                .withActionMapping("collect", method(new ResultCollectingAction(), (action) -> action.collect(null, null, null)))
                .build());
    }

    @Benchmark
    public void benchmarkTest(Context benchmarkContext) {
        super.benchmarkTest(benchmarkContext);
    }

    @State(Scope.Benchmark)
    public static class Context extends AbstractPerformanceTest.Context {

        @Setup
        public void setup() {
            super.setup();
        }

        @Override
        protected List<Rule> getRules() {
            List<Rule> rules = new ArrayList<>(2);

            rules.add(RuleDsl.ruleBuilder()
                    .name("ruleName")
                    .fact("chainingFact", ChainingFact.class)
                    .predicate(
                            equal(
                                    value("${chainingFact.innerFact.field}"),
                                    value("testValue")
                            )
                    )
                    .action("collect",
                            param("context",
                                    value("${ctx}")),
                            param("ruleName",
                                    value("name", String.class)),
                            param("fact", value("${chainingFact}")))
                    .build()
            );

            rules.add(RuleDsl.ruleBuilder()
                    .name("ruleName2")
                    .fact("chainingFact", ChainingFact.class)
                    .predicate(
                            equal(
                                    value("${chainingFact.innerFact.field2}"),
                                    value("testValue2")
                            )
                    )
                    .action("collect",
                            param("context",
                                    value("${ctx}")),
                            param("ruleName",
                                    value("name", String.class)),
                            param("fact", value("${chainingFact}")))
                    .build()
            );

            return rules;
        }

        @Override
        protected Object getFact() {
            ChainingFact fact = new ChainingFact();
            fact.innerFact.put("field", "testValue");
            fact.innerFact.put("field2", "testValue2");
            return fact;
        }

    }

    public static class ChainingFact {
        public InnerFact innerFact = new InnerFact();

        public class InnerFact extends HashMap<String, String> {
        }
    }
}
