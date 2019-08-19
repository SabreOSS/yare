package com.sabre.oss.yare.performance.suits;

import com.sabre.oss.yare.core.RuleSession;
import com.sabre.oss.yare.core.RulesEngine;
import com.sabre.oss.yare.core.RulesEngineBuilder;
import com.sabre.oss.yare.core.RulesRepository;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.dsl.Expression;
import com.sabre.oss.yare.dsl.RuleDsl;
import com.sabre.oss.yare.engine.executor.DefaultRulesExecutorBuilder;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.sabre.oss.yare.invoker.java.MethodCallMetadata.method;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MultithreadedTest {

    @Test
    public void launchBenchmark() throws Exception {
        new Runner(
                new OptionsBuilder().include(MultithreadedTest.class.getSimpleName())
                        .shouldFailOnError(true)
                        .mode(Mode.AverageTime)
                        .timeUnit(TimeUnit.MILLISECONDS)
                        .warmupIterations(2)
                        .warmupTime(TimeValue.seconds(2))
                        .measurementIterations(5)
                        .measurementTime(TimeValue.seconds(10))
                        .threads(Runtime.getRuntime().availableProcessors())
                        .forks(1)
                        .jvmArgs("-server", "-Xms2048M", "-Xmx2048M", "-XX:+UseG1GC")
                        .shouldDoGC(true)
                        .build()
        ).run();
    }

    @Benchmark
    public void benchmark(EngineState state, FactsState threadState) {
        //given
        String uri = UUID.randomUUID().toString();
        RuleSession session = state.engine.createSession(uri);
        //when
        ArrayList<Object> result = session.execute(new ArrayList<>(), threadState.facts);
        //then
        int expectedResultSize = EngineState.NUMBER_OF_RULES * FactsState.NUMBER_OF_FACTS;
        assertThat(result.size()).isEqualTo(expectedResultSize);
    }

    @State(Scope.Thread)
    public static class FactsState {
        public static int NUMBER_OF_FACTS = 1000;
        private List<SimpleFact> facts;

        @Setup(Level.Invocation)
        public void init() {
            facts = IntStream
                    .range(0, NUMBER_OF_FACTS)
                    .mapToObj(i -> new SimpleFact("1"))
                    .collect(Collectors.toList());
        }
    }

    @State(Scope.Thread)
    public static class EngineState {
        public static int NUMBER_OF_RULES = 500;
        private RulesEngine engine;

        @Setup(Level.Invocation)
        public void init() {
            engine = new RulesEngineBuilder()
                    .withRulesRepository(initializeRuleRepository(NUMBER_OF_RULES))
                    .withRulesExecutorBuilder(new DefaultRulesExecutorBuilder())
                    .withActionMapping("collectValueAction",
                            method(new CollectValueAction(), (action) -> action.execute(null, null)))
                    .build();
        }

        static RulesRepository initializeRuleRepository(int testRuleCount) {
            RulesRepository rulesRepository = mock(RulesRepository.class);
            when(rulesRepository.get(anyString())).thenReturn(createTestRules(testRuleCount));
            return rulesRepository;
        }

        static List<Rule> createTestRules(final int testRuleCount) {
            return IntStream
                    .range(0, testRuleCount)
                    .mapToObj(EngineState::createRule)
                    .collect(Collectors.toList());
        }

        static Rule createRule(int index) {
            return RuleDsl.ruleBuilder()
                    .name(String.valueOf(index))
                    .fact("simpleStringValueFact", SimpleFact.class)
                    .priority(0L)
                    .predicate(
                            RuleDsl.and(
                                    IntStream.range(0, 500).mapToObj(x -> RuleDsl.equal(
                                            RuleDsl.value("${simpleStringValueFact.value}"),
                                            RuleDsl.value("1")
                                    )).toArray(Expression[]::new)
                            )
                    )
                    .action("collectValueAction",
                            RuleDsl.param("context", RuleDsl.value("${ctx}")),
                            RuleDsl.param("fact", RuleDsl.value("${simpleStringValueFact}")))
                    .build();
        }
    }

    public static class SimpleFact {
        private String value;

        public SimpleFact(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SimpleFact that = (SimpleFact) o;
            return value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    public static class CollectValueAction {
        public void execute(List<SimpleFact> context, SimpleFact fact) {
            if (fact == null) {
                System.out.println("Fact IS NULL!\n");
            }
            context.add(fact);
        }
    }
}
