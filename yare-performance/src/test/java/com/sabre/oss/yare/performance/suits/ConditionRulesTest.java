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
import com.sabre.oss.yare.dsl.RuleDsl;
import com.sabre.oss.yare.core.model.Rule;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.engine.MethodCallMetadata.method;

public class ConditionRulesTest extends AbstractPerformanceTest {

    public ConditionRulesTest() {
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
                    .name("ruleNameOR")
                    .fact("conditionsFact", ConditionsFact.class)
                    .predicate(
                            or(
                                    equal(
                                            field("conditionsFact.field", String.class),
                                            value("nonExistingValue")
                                    ),
                                    match(
                                            field("conditionsFact.field", String.class),
                                            value("value")
                                    )
                            )
                    )
                    .action("collect",
                            param("context",
                                    reference("ctx", List.class)),
                            param("ruleName",
                                    value("name", String.class)),
                            param("fact", reference("conditionsFact")))
                    .build()
            );

            rules.add(RuleDsl.ruleBuilder()
                    .name("ruleNameAND")
                    .fact("conditionsFact", ConditionsFact.class)
                    .predicate(
                            and(
                                    equal(
                                            field("conditionsFact.field", String.class),
                                            value("value")
                                    ),
                                    equal(
                                            field("conditionsFact.field2", String.class),
                                            value("value2")
                                    ),
                                    not(equal(
                                            field("conditionsFact.field2", String.class),
                                            value("nonExistingValue")
                                    ))
                            )
                    )
                    .action("collect",
                            param("context",
                                    reference("ctx", List.class)),
                            param("ruleName",
                                    value("name", String.class)),
                            param("fact", reference("conditionsFact")))
                    .build()
            );

            rules.add(RuleDsl.ruleBuilder()
                    .name("ruleNameNOT")
                    .fact("conditionsFact", ConditionsFact.class)
                    .predicate(
                            not(
                                    equal(
                                            field("conditionsFact.field2", String.class),
                                            value("nonExistingValue")
                                    )
                            )
                    )
                    .action("collect",
                            param("context",
                                    reference("ctx", List.class)),
                            param("ruleName",
                                    value("name", String.class)),
                            param("fact", reference("conditionsFact")))
                    .build()
            );

            return rules;
        }

        @Override
        protected Object getFact() {
            ConditionsFact fact = new ConditionsFact();
            fact.put("field", "value");
            fact.put("field2", "value2");
            fact.put("field3", null);
            return fact;
        }

    }

    private static class ConditionsFact extends HashMap<String, Object> {
    }
}
