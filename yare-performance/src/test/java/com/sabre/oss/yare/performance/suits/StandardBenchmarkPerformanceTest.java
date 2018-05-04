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

import com.sabre.oss.yare.core.RulesEngine;
import com.sabre.oss.yare.core.RulesEngineBuilder;
import com.sabre.oss.yare.core.RulesRepository;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.dsl.RuleDsl;
import com.sabre.oss.yare.engine.executor.DefaultRulesExecutorBuilder;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.sabre.oss.yare.engine.MethodCallMetadata.method;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StandardBenchmarkPerformanceTest {

    static int testRuleCount = Integer.valueOf(System.getProperty("complexity", "0"));

    @Test
    public void launchBenchmarkFast() throws Exception {
        launchBenchmark("fastBenchmark", 10);
    }

    @Test
    public void launchBenchmarkSlow() throws Exception {
        launchBenchmark("slowBenchmark", 1000);
    }

    private void launchBenchmark(String id, int complexity) throws Exception {
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.println(id + ":");
        System.out.println("------------------------------------------------------------------------------------------");
        testRuleCount = complexity;
        Options options = getOptions(id, complexity);
        new Runner(options).run();
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.println("\n\n\n");
    }

    private Options getOptions(String id, int complexity) {
        return new OptionsBuilder()
                .include(this.getClass().getName() + ".*")
                .mode(Mode.SampleTime)
                .timeUnit(TimeUnit.MILLISECONDS)
                .warmupTime(TimeValue.seconds(30))
                .warmupIterations(1)
                .measurementTime(TimeValue.seconds(30))
                .measurementIterations(1)
                .threads(2)
                .forks(1)
                .shouldFailOnError(true)
                .shouldDoGC(true)
                .jvmArgs("-server", "-Xms1024M", "-Xmx1024M", "-XX:+UseG1GC", "-Did=" + id, "-Dcomplexity=" + complexity)
                .build();
    }

    @Benchmark
    public void benchmark(BenchmarkState state, BenchmarkThreadState threadState) {
        List<SimpleStringValueFact> execute = state.rulesEngine.createSession("test").execute(threadState.context, state.allFacts);
        state.selectedFacts.add(execute);
    }

    @State(Scope.Thread)
    public static class BenchmarkThreadState {
        List<SimpleStringValueFact> context;

        @Setup(Level.Invocation)
        public void initializeTrial() throws Exception {
            context = new ArrayList<>();
        }
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        RulesEngine rulesEngine;
        RulesRepository rulesRepository;
        List<List<SimpleStringValueFact>> selectedFacts = Collections.synchronizedList(new ArrayList<>());
        List<Object> allFacts;

        static List<Object> generateTestFacts(final int testRuleCount) {
            return IntStream.range(0, testRuleCount)
                    .mapToObj(i -> new SimpleStringValueFact("TEST" + i))
                    .collect(Collectors.toList());
        }

        static RulesRepository initializeRuleRepository(int testRuleCount) throws Exception {
            RulesRepository rulesRepository = mock(RulesRepository.class);
            when(rulesRepository.get(anyString())).thenReturn(createTestRules(testRuleCount));
            return rulesRepository;
        }

        static RulesEngine initializeRuleEngine(RulesRepository ruleRepository) {
            return new RulesEngineBuilder()
                    .withRulesRepository(ruleRepository)
                    .withActionMapping("collectValueAction", method(new CollectValueAction(), (action) -> action.execute(null, null)))
                    .withRulesExecutorBuilder(new DefaultRulesExecutorBuilder()
                            .withSequentialMode(true))
                    .build();
        }

        static List<Rule> createTestRules(final int testRuleCount) {
            return IntStream
                    .range(0, testRuleCount)
                    .mapToObj(BenchmarkState::createRule)
                    .collect(Collectors.toList());
        }

        static Rule createRule(int index) {
            return RuleDsl.ruleBuilder()
                    .name(String.valueOf(index))
                    .fact("simpleStringValueFact", SimpleStringValueFact.class)
                    .priority(0L)
                    .predicate(
                            RuleDsl.equal(
                                    RuleDsl.field("simpleStringValueFact", "value", String.class),
                                    RuleDsl.value("TEST" + index)))
                    .action("collectValueAction",
                            RuleDsl.param("context", RuleDsl.reference("ctx")),
                            RuleDsl.param("fact", RuleDsl.reference("simpleStringValueFact")))
                    .build();
        }

        @Setup(Level.Trial)
        public void initializeTrial() throws Exception {
            allFacts = generateTestFacts(testRuleCount);
            rulesRepository = initializeRuleRepository(testRuleCount);
            rulesEngine = initializeRuleEngine(rulesRepository);
        }

        @TearDown(Level.Trial)
        public void doTearDown() {
            int numberOfExecutedRules = testRuleCount;
            assertThat(selectedFacts.stream().flatMap(e -> e.stream().sorted()).distinct().collect(Collectors.toList()))
                    .containsExactlyInAnyOrder(allFacts.toArray(new SimpleStringValueFact[numberOfExecutedRules]));
        }
    }

    public static class SimpleStringValueFact implements Comparable<SimpleStringValueFact> {

        private final String value;

        SimpleStringValueFact(String value) {
            this.value = value;
        }

        @Override
        public int compareTo(SimpleStringValueFact o) {
            return value.compareTo(o.value);
        }

        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SimpleStringValueFact that = (SimpleStringValueFact) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    public static class CollectValueAction {
        public void execute(List<SimpleStringValueFact> context, SimpleStringValueFact fact) {
            if (fact == null) {
                System.out.println("Fact IS NULL!\n");
            }
            context.add(fact);
        }
    }
}
