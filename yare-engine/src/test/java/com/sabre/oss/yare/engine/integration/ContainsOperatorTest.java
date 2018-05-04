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

import com.sabre.oss.yare.core.RuleSession;
import com.sabre.oss.yare.core.RulesEngine;
import com.sabre.oss.yare.core.RulesEngineBuilder;
import com.sabre.oss.yare.dsl.RuleDsl;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.engine.executor.DefaultRulesExecutorBuilder;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.engine.MethodCallMetadata.method;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class ContainsOperatorTest {
    private static final String ACTION_NAME = "testAction";

    public RulesEngine createRuleEngine(RulesExecutionConfig config) {
        return new RulesEngineBuilder()
                .withRulesRepository(i -> config.getRules())
                .withActionMapping(ACTION_NAME, method(new TestAction(), a -> a.execute(null, null)))
                .withRulesExecutorBuilder(new DefaultRulesExecutorBuilder()
                        .withSequentialMode(config.isSequenceMode()))
                .build();
    }

    @Test
    void shouldMatchWhenContainsAllOfGiven() {
        // given
        List<Object> fact = Collections.singletonList(
                new TestFact(Arrays.asList("firstString", "secondString", "thirdString"), null)
        );
        List<Rule> rule = singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match when testFact.strings contains all of given")
                        .fact("testFact", TestFact.class)
                        .predicate(
                                contains(
                                        castToCollection(field("testFact.strings", List.class), String.class),
                                        values(String.class, value("firstString"), value("secondString"))
                                )
                        )
                        .action(ACTION_NAME,
                                param("context", reference("ctx")),
                                param("fact", reference("testFact")))
                        .build());
        RulesExecutionConfig config = createConfig(rule);
        RulesEngine rulesEngine = createRuleEngine(config);
        RuleSession ruleSession = rulesEngine.createSession("testSession");

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), fact);

        // then
        assertThat(matchingFacts).containsExactly(
                new TestFact(Arrays.asList("firstString", "secondString", "thirdString"), null)
        );
    }

    @Test
    void shouldMatchWhenGivenContainsInstance() {
        // given
        List<Object> fact = Collections.singletonList(
                new TestFact(null, "firstString")
        );
        List<Rule> rule = singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match when given collection contains testFact.string")
                        .fact("testFact", TestFact.class)
                        .predicate(
                                contains(
                                        values(String.class, value("firstString"), value("secondString")),
                                        field("testFact.string", String.class)
                                )
                        )
                        .action(ACTION_NAME,
                                param("context", reference("ctx")),
                                param("fact", reference("testFact")))
                        .build());
        RulesExecutionConfig config = createConfig(rule);
        RulesEngine rulesEngine = createRuleEngine(config);
        RuleSession ruleSession = rulesEngine.createSession("testSession");

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), fact);

        // then
        assertThat(matchingFacts).containsExactly(
                new TestFact(null, "firstString")
        );
    }

    @Test
    void shouldNotMatchWhenNotContainsAllOfGiven() {
        // given
        List<Object> fact = Collections.singletonList(
                new TestFact(Arrays.asList("firstString", "secondString", "thirdString"), null)
        );
        List<Rule> rule = singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match when testFact.strings contains all of given")
                        .fact("testFact", TestFact.class)
                        .predicate(
                                contains(
                                        castToCollection(field("testFact.strings", List.class), String.class),
                                        values(String.class, value("fourthString"), value("fifthString"))
                                )
                        )
                        .action(ACTION_NAME,
                                param("context", reference("ctx")),
                                param("fact", reference("testFact")))
                        .build());
        RulesExecutionConfig config = createConfig(rule);
        RulesEngine rulesEngine = createRuleEngine(config);
        RuleSession ruleSession = rulesEngine.createSession("testSession");

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), fact);

        // then
        assertThat(matchingFacts).isEmpty();
    }

    @Test
    void shouldNotMatchWhenGivenNotContainsInstance() {
        // given
        List<Object> fact = Collections.singletonList(
                new TestFact(null, "firstString")
        );
        List<Rule> rule = singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match when given collection contains testFact.string")
                        .fact("testFact", TestFact.class)
                        .predicate(
                                contains(
                                        values(String.class, value("secondString"), value("thirdString")),
                                        field("testFact.string", String.class)
                                )
                        )
                        .action(ACTION_NAME,
                                param("context", reference("ctx")),
                                param("fact", reference("testFact")))
                        .build());
        RulesExecutionConfig config = createConfig(rule);
        RulesEngine rulesEngine = createRuleEngine(config);
        RuleSession ruleSession = rulesEngine.createSession("testSession");

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), fact);

        // then
        assertThat(matchingFacts).isEmpty();
    }

    @Test
    void shouldNotMatchWhenNullContainsAllOfGiven() {
        // given
        List<Object> fact = Collections.singletonList(
                new TestFact(null, null)
        );
        List<Rule> rule = singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match when null contains all of given")
                        .fact("testFact", TestFact.class)
                        .predicate(
                                contains(
                                        castToCollection(field("testFact.nullList", List.class), String.class),
                                        values(String.class, value("fourthString"), value("fifthString"))
                                )
                        )
                        .action(ACTION_NAME,
                                param("context", reference("ctx")),
                                param("fact", reference("testFact")))
                        .build());
        RulesExecutionConfig config = createConfig(rule);
        RulesEngine rulesEngine = createRuleEngine(config);
        RuleSession ruleSession = rulesEngine.createSession("testSession");

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), fact);

        // then
        assertThat(matchingFacts).isEmpty();
    }

    @Test
    void shouldNotMatchWhenContainsAllOfNull() {
        // given
        List<Object> fact = Collections.singletonList(
                new TestFact(Arrays.asList("firstString", "secondString", "thirdString"), null)
        );
        List<Rule> rule = singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match when testFact.strings contains all of null")
                        .fact("testFact", TestFact.class)
                        .predicate(
                                contains(
                                        castToCollection(field("testFact.strings", List.class), String.class),
                                        castToCollection(field("testFact.nullList", List.class), String.class)
                                )
                        )
                        .action(ACTION_NAME,
                                param("context", reference("ctx")),
                                param("fact", reference("testFact")))
                        .build());
        RulesExecutionConfig config = createConfig(rule);
        RulesEngine rulesEngine = createRuleEngine(config);
        RuleSession ruleSession = rulesEngine.createSession("testSession");

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), fact);

        // then
        assertThat(matchingFacts).isEmpty();
    }

    @Test
    void shouldNotMatchWhenNullContainsAllOfNull() {
        // given
        List<Object> fact = Collections.singletonList(
                new TestFact(null, null)
        );
        List<Rule> rule = singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match when null contains all of null")
                        .fact("testFact", TestFact.class)
                        .predicate(
                                contains(
                                        castToCollection(field("testFact.nullList", List.class), String.class),
                                        castToCollection(field("testFact.nullList", List.class), String.class)
                                )
                        )
                        .action(ACTION_NAME,
                                param("context", reference("ctx")),
                                param("fact", reference("testFact")))
                        .build());
        RulesExecutionConfig config = createConfig(rule);
        RulesEngine rulesEngine = createRuleEngine(config);
        RuleSession ruleSession = rulesEngine.createSession("testSession");

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), fact);

        // then
        assertThat(matchingFacts).isEmpty();
    }

    @Test
    void shouldNotMatchWhenContainsNullInstance() {
        // given
        List<Object> fact = Collections.singletonList(
                new TestFact(Arrays.asList("firstString", "secondString", "thirdString"), null)
        );
        List<Rule> rule = singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match when testFact.strings contains null")
                        .fact("testFact", TestFact.class)
                        .predicate(
                                contains(
                                        castToCollection(field("testFact.strings", List.class), String.class),
                                        field("testFact.string", String.class)
                                )
                        )
                        .action(ACTION_NAME,
                                param("context", reference("ctx")),
                                param("fact", reference("testFact")))
                        .build());
        RulesExecutionConfig config = createConfig(rule);
        RulesEngine rulesEngine = createRuleEngine(config);
        RuleSession ruleSession = rulesEngine.createSession("testSession");

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), fact);

        // then
        assertThat(matchingFacts).isEmpty();
    }

    @Test
    void shouldNotMatchWhenNullContainsNullInstance() {
        // given
        List<Object> fact = Collections.singletonList(
                new TestFact(null, null)
        );
        List<Rule> rule = singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match when null contains null")
                        .fact("testFact", TestFact.class)
                        .predicate(
                                contains(
                                        castToCollection(field("testFact.nullList", List.class), String.class),
                                        field("testFact.string", String.class)
                                )
                        )
                        .action(ACTION_NAME,
                                param("context", reference("ctx")),
                                param("fact", reference("testFact")))
                        .build());
        RulesExecutionConfig config = createConfig(rule);
        RulesEngine rulesEngine = createRuleEngine(config);
        RuleSession ruleSession = rulesEngine.createSession("testSession");

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), fact);

        // then
        assertThat(matchingFacts).isEmpty();
    }

    @Test
    void shouldNotMatchWhenNullContainsInstance() {
        // given
        List<Object> fact = Collections.singletonList(
                new TestFact(null, "firstString")
        );
        List<Rule> rule = singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match when null contains testFact.string")
                        .fact("testFact", TestFact.class)
                        .predicate(
                                contains(
                                        castToCollection(field("testFact.nullList", List.class), String.class),
                                        field("testFact.string", String.class)
                                )
                        )
                        .action(ACTION_NAME,
                                param("context", reference("ctx")),
                                param("fact", reference("testFact")))
                        .build());
        RulesExecutionConfig config = createConfig(rule);
        RulesEngine rulesEngine = createRuleEngine(config);
        RuleSession ruleSession = rulesEngine.createSession("testSession");

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), fact);

        // then
        assertThat(matchingFacts).isEmpty();
    }

    @Test
    void shouldMatchWhenContainsAnyOfGiven() {
        // given
        List<Object> fact = Collections.singletonList(
                new TestFact(Arrays.asList("firstString", "secondString", "thirdString"), "firstString")
        );
        List<Rule> rule = singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match when testFact.strings contains any of given")
                        .fact("testFact", TestFact.class)
                        .predicate(
                                containsAny(
                                        castToCollection(field("testFact.strings", List.class), String.class),
                                        values(String.class, value("firstString"), value("fourthString"), value("fifthString"))
                                )
                        )
                        .action(ACTION_NAME,
                                param("context", reference("ctx")),
                                param("fact", reference("testFact")))
                        .build());
        RulesExecutionConfig config = createConfig(rule);
        RulesEngine rulesEngine = createRuleEngine(config);
        RuleSession ruleSession = rulesEngine.createSession("testSession");

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), fact);

        // then
        assertThat(matchingFacts).containsExactly(
                new TestFact(Arrays.asList("firstString", "secondString", "thirdString"), "firstString")
        );
    }

    @Test
    void shouldNotMatchWhenNotContainsAnyOfGiven() {
        // given
        List<Object> fact = Collections.singletonList(
                new TestFact(Arrays.asList("firstString", "secondString", "thirdString"), "firstString")
        );
        List<Rule> rule = singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match when testFact.strings contains any of given")
                        .fact("testFact", TestFact.class)
                        .predicate(
                                containsAny(
                                        castToCollection(field("testFact.strings", List.class), String.class),
                                        values(String.class, value("fourthString"), value("fifthString"))
                                )
                        )
                        .action(ACTION_NAME,
                                param("context", reference("ctx")),
                                param("fact", reference("testFact")))
                        .build());
        RulesExecutionConfig config = createConfig(rule);
        RulesEngine rulesEngine = createRuleEngine(config);
        RuleSession ruleSession = rulesEngine.createSession("testSession");

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), fact);

        // then
        assertThat(matchingFacts).isEmpty();
    }

    @Test
    void shouldNotMatchWhenNullContainsAnyOfGiven() {
        // given
        List<Object> fact = Collections.singletonList(
                new TestFact(null, null)
        );
        List<Rule> rule = singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match when null contains any of given")
                        .fact("testFact", TestFact.class)
                        .predicate(
                                containsAny(
                                        castToCollection(field("testFact.nullList", List.class), String.class),
                                        values(String.class, value("fourthString"), value("fifthString"))
                                )
                        )
                        .action(ACTION_NAME,
                                param("context", reference("ctx")),
                                param("fact", reference("testFact")))
                        .build());
        RulesExecutionConfig config = createConfig(rule);
        RulesEngine rulesEngine = createRuleEngine(config);
        RuleSession ruleSession = rulesEngine.createSession("testSession");

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), fact);

        // then
        assertThat(matchingFacts).isEmpty();
    }

    @Test
    void shouldNotMatchWhenContainsAnyOfNull() {
        // given
        List<Object> fact = Collections.singletonList(
                new TestFact(Arrays.asList("firstString", "secondString", "thirdString"), null)
        );
        List<Rule> rule = singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match when testFact.strings contains any of null")
                        .fact("testFact", TestFact.class)
                        .predicate(
                                containsAny(
                                        castToCollection(field("testFact.strings", List.class), String.class),
                                        castToCollection(field("testFact.nullList", List.class), String.class)
                                )
                        )
                        .action(ACTION_NAME,
                                param("context", reference("ctx")),
                                param("fact", reference("testFact")))
                        .build());
        RulesExecutionConfig config = createConfig(rule);
        RulesEngine rulesEngine = createRuleEngine(config);
        RuleSession ruleSession = rulesEngine.createSession("testSession");

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), fact);

        // then
        assertThat(matchingFacts).isEmpty();
    }

    @Test
    void shouldNotMatchWhenNullContainsAnyOfNull() {
        // given
        List<Object> fact = Collections.singletonList(
                new TestFact(null, null)
        );
        List<Rule> rule = singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match when null contains any of null")
                        .fact("testFact", TestFact.class)
                        .attribute("ignored", false)
                        .predicate(
                                containsAny(
                                        castToCollection(field("testFact.nullList", List.class), String.class),
                                        castToCollection(field("testFact.nullList", List.class), String.class)
                                )
                        )
                        .action(ACTION_NAME,
                                param("context", reference("ctx")),
                                param("fact", reference("testFact")))
                        .build());
        RulesExecutionConfig config = createConfig(rule);
        RulesEngine rulesEngine = createRuleEngine(config);
        RuleSession ruleSession = rulesEngine.createSession("testSession");

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), fact);

        // then
        assertThat(matchingFacts).isEmpty();
    }

    private RulesExecutionConfig createConfig(List<Rule> rule) {
        return RulesExecutionConfig.builder()
                .withFactTypes(Collections.singletonList(TestFact.class))
                .withInputType(List.class)
                .withRules(rule)
                .build();
    }

    private static final class TestFact {
        private final List<String> strings;
        private final List<String> nullList = null;
        private final String string;

        private TestFact(List<String> strings, String string) {
            this.strings = strings;
            this.string = string;
        }

        public List<String> getStrings() {
            return strings;
        }

        public List<String> getNullList() {
            return nullList;
        }

        public String getString() {
            return string;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TestFact testFact = (TestFact) o;
            return Objects.equals(strings, testFact.strings) &&
                    Objects.equals(string, testFact.string);
        }

        @Override
        public int hashCode() {
            return Objects.hash(strings, string);
        }
    }

    public static class TestAction {
        public void execute(List<Object> results, Object fact) {
            results.add(fact);
        }
    }
}
