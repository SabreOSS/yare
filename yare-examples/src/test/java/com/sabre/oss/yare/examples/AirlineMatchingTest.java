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

package com.sabre.oss.yare.examples;

import com.google.common.collect.ImmutableMap;
import com.sabre.oss.yare.core.RuleSession;
import com.sabre.oss.yare.core.RulesEngine;
import com.sabre.oss.yare.core.RulesEngineBuilder;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.dsl.RuleDsl;
import com.sabre.oss.yare.engine.executor.DefaultRulesExecutorBuilder;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.engine.MethodCallMetadata.method;
import static org.assertj.core.api.Assertions.assertThat;

class AirlineMatchingTest {
    private static final String COLLECT = "collect";
    private static final String SET_REJECTED_FLAG = "setRejectedFlag";
    private static final String AIRLINE_RULE_SET = "airlineRuleSet";

    @Test
    void shouldMatchWithSubsetOfAirlineCodes() {
        // given
        List<String> airlineCodes = Arrays.asList("AAU", "AAV");
        List<Rule> rule = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match airline when airline codes contain given")
                        .fact("airline", Airline.class)
                        .predicate(
                                contains(
                                        castToCollection(value("${airline.airlineCodes}"), String.class),
                                        values(String.class, airlineCodes.toArray(new String[airlineCodes.size()]))
                                )
                        )
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("fact", value("${airline}")))
                        .build()
        );
        List<Airline> airline = Collections.singletonList(
                new Airline().withAirlineCodes(
                        Arrays.asList("AAU", "AAW", "AAV", "AFU")
                ));

        RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository(((Map<String, List<Rule>>) ImmutableMap.of(AIRLINE_RULE_SET, rule))::get)
                .withActionMapping(COLLECT, method(new TestAction(), (action) -> action.collect(null, null)))
                .build();
        RuleSession ruleSession = rulesEngine.createSession(AIRLINE_RULE_SET);

        // when
        List<Airline> matchingAirline = ruleSession.execute(new ArrayList<>(), airline);

        // then
        assertThat(matchingAirline).containsExactly(
                new Airline().withAirlineCodes(Arrays.asList("AAU", "AAW", "AAV", "AFU"))
        );
    }

    @Test
    void shouldNotMatchWithDisjointSubsetOfAirlineCodes() {
        // given
        List<String> airlineCodes = Arrays.asList("TTF", "PIU", "BRO");
        List<Rule> rule = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match airline when airline codes contain given")
                        .fact("airline", Airline.class)
                        .predicate(
                                contains(
                                        castToCollection(value("${airline.airlineCodes}"), String.class),
                                        values(String.class, airlineCodes.toArray(new String[airlineCodes.size()]))
                                )
                        )
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("fact", value("${airline}")))
                        .build()
        );
        List<Airline> airline = Collections.singletonList(
                new Airline().withAirlineCodes(
                        Arrays.asList("AAU", "AAW", "AAV", "AFU")
                ));

        RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository(((Map<String, List<Rule>>) ImmutableMap.of(AIRLINE_RULE_SET, rule))::get)
                .withActionMapping(COLLECT, method(new TestAction(), (action) -> action.collect(null, null)))
                .build();
        RuleSession ruleSession = rulesEngine.createSession(AIRLINE_RULE_SET);

        // when
        List<Airline> matchingAirline = ruleSession.execute(new ArrayList<>(), airline);

        // then
        assertThat(matchingAirline).isEmpty();
    }

    @Test
    void shouldMatchWhenSubsetOfGiven() {
        //given
        List<String> airlineCodes = Arrays.asList("AAU", "AAW", "AAV", "AFU", "TTF", "PIU", "BRO");
        List<Rule> rule = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match airline when airline codes are in given")
                        .fact("airline", Airline.class)
                        .predicate(
                                contains(
                                        values(String.class, airlineCodes.toArray(new String[airlineCodes.size()])),
                                        castToCollection(value("${airline.airlineCodes}"), String.class)
                                )
                        )
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("fact", value("${airline}")))
                        .build()
        );
        List<Airline> airline = Collections.singletonList(
                new Airline().withAirlineCodes(
                        Arrays.asList("AAU", "AAW", "AAV", "AFU")
                ));

        RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository(((Map<String, List<Rule>>) ImmutableMap.of(AIRLINE_RULE_SET, rule))::get)
                .withActionMapping(COLLECT, method(new TestAction(), (action) -> action.collect(null, null)))
                .build();
        RuleSession ruleSession = rulesEngine.createSession(AIRLINE_RULE_SET);

        // when
        List<Airline> matchingAirline = ruleSession.execute(new ArrayList<>(), airline);

        // then
        assertThat(matchingAirline).containsExactly(
                new Airline().withAirlineCodes(Arrays.asList("AAU", "AAW", "AAV", "AFU"))
        );
    }

    @Test
    void shouldNotMatchWhenDisjointSubsetWithGiven() {
        //given
        List<String> airlineCodes = Arrays.asList("AAU", "AAW", "TTF", "PIU", "BRO", "ATT", "IWY");
        List<Rule> rule = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match airline when airline codes are in given")
                        .fact("airline", Airline.class)
                        .predicate(
                                contains(
                                        values(String.class, airlineCodes.toArray(new String[airlineCodes.size()])),
                                        castToCollection(value("${airline.airlineCodes}"), String.class)
                                )
                        )
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("fact", value("${airline}")))
                        .build()
        );
        List<Airline> airline = Collections.singletonList(
                new Airline().withAirlineCodes(
                        Arrays.asList("AAU", "AAW", "AAV", "AFU")
                ));

        RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository(((Map<String, List<Rule>>) ImmutableMap.of(AIRLINE_RULE_SET, rule))::get)
                .withActionMapping(COLLECT, method(new TestAction(), (action) -> action.collect(null, null)))
                .build();
        RuleSession ruleSession = rulesEngine.createSession(AIRLINE_RULE_SET);

        // when
        List<Airline> matchingAirline = ruleSession.execute(new ArrayList<>(), airline);

        // then
        assertThat(matchingAirline).isEmpty();
    }

    @Test
    void shouldMatchWhenContainingAnyFromSet() {
        //given
        List<String> airlineCodes = Arrays.asList("AAU", "PIU", "BRO");
        List<Rule> rule = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match airline when airline codes contain any of given")
                        .fact("airline", Airline.class)
                        .predicate(
                                containsAny(
                                        castToCollection(value("${airline.airlineCodes}"), String.class),
                                        values(String.class, airlineCodes.toArray(new String[airlineCodes.size()]))
                                )
                        )
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("fact", value("${airline}")))
                        .build()
        );
        List<Airline> airline = Collections.singletonList(
                new Airline().withAirlineCodes(
                        Arrays.asList("AAU", "AAW", "AAV", "AFU")
                ));

        RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository(((Map<String, List<Rule>>) ImmutableMap.of(AIRLINE_RULE_SET, rule))::get)
                .withActionMapping(COLLECT, method(new TestAction(), (action) -> action.collect(null, null)))
                .build();
        RuleSession ruleSession = rulesEngine.createSession(AIRLINE_RULE_SET);

        // when
        List<Airline> matchingAirline = ruleSession.execute(new ArrayList<>(), airline);

        // then
        assertThat(matchingAirline).containsExactly(
                new Airline().withAirlineCodes(Arrays.asList("AAU", "AAW", "AAV", "AFU"))
        );
    }

    @Test
    void shouldMatchNotRejectedAirlines() {
        //given
        List<Rule> rules = new LinkedList<>();
        rules.addAll(Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should mark airline as rejected when its name is equal to given")
                        .fact("airline", Airline.class)
                        .predicate(
                                equal(
                                        value("${airline.name}"),
                                        value("Lufthansa")
                                )
                        )
                        .action(SET_REJECTED_FLAG,
                                param("airline", value("${airline}")),
                                param("isRejected", value(true)))
                        .build()
        ));
        rules.addAll(Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match not rejected airlines")
                        .fact("airline", Airline.class)
                        .predicate(
                                equal(
                                        value("${airline.isRejected}"),
                                        value(false)
                                )
                        )
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("airline", value("${airline}")))
                        .build()
        ));
        List<Airline> airlines = Arrays.asList(
                new Airline().withName("Lufthansa"),
                new Airline().withName("Lot"),
                new Airline().withName("Wizz Air"),
                new Airline().withName("Lufthansa")
        );

        TestAction testAction = new TestAction();
        RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository(((Map<String, List<Rule>>) ImmutableMap.of(AIRLINE_RULE_SET, rules))::get)
                .withActionMapping(SET_REJECTED_FLAG, method(testAction, (action) -> action.setRejectedFlagToTrue(null, null)))
                .withActionMapping(COLLECT, method(testAction, (action) -> action.collect(null, null)))
                .withRulesExecutorBuilder(new DefaultRulesExecutorBuilder()
                        .withSequentialMode(true))
                .build();
        RuleSession ruleSession = rulesEngine.createSession(AIRLINE_RULE_SET);

        //when
        List<Airline> matchingAirlines = ruleSession.execute(new ArrayList<>(), airlines);

        //then
        assertThat(matchingAirlines).containsExactly(
                new Airline().withName("Lot"),
                new Airline().withName("Wizz Air")
        );
    }

    public static class TestAction {

        public void collect(List<Object> results, Object fact) {
            results.add(fact);
        }

        public void setRejectedFlagToTrue(Airline airline, Boolean isRejected) {
            airline.withIsRejected(isRejected);
        }
    }

    public class Airline {
        public String name;
        public List<String> airlineCodes;
        public boolean isRejected = false;

        public Airline withName(String name) {
            this.name = name;
            return this;
        }

        public Airline withAirlineCodes(final List<String> airlineCodes) {
            this.airlineCodes = airlineCodes;
            return this;
        }

        public Airline withIsRejected(boolean isRejected) {
            this.isRejected = isRejected;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Airline airline = (Airline) o;
            return isRejected == airline.isRejected &&
                    Objects.equals(name, airline.name) &&
                    Objects.equals(airlineCodes, airline.airlineCodes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, airlineCodes, isRejected);
        }
    }

}
