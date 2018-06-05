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

package com.sabre.oss.yare.example;

import com.sabre.oss.yare.core.RuleSession;
import com.sabre.oss.yare.core.RulesEngine;
import com.sabre.oss.yare.core.RulesEngineBuilder;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.dsl.RuleDsl;
import com.sabre.oss.yare.engine.executor.DefaultRulesExecutorBuilder;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.invoker.java.MethodCallMetadata.method;
import static org.assertj.core.api.Assertions.assertThat;

public class AirlineMatchingTest {

    @Test
    void shouldMatchWithSubsetOfAirlineCodes() {
        // given
        List<Rule> rules = Collections.singletonList(RuleDsl.ruleBuilder()
                .name("Should match airline when airline codes contain given")
                .fact("airline", Airline.class)
                .predicate(
                        contains(
                                castToCollection(value("${airline.airlineCodes}"), String.class),
                                values(String.class, "AAU", "AAV")
                        )
                )
                .action("collect",
                        param("context", value("${ctx}")),
                        param("fact", value("${airline}")))
                .build());
        List<Airline> facts = Arrays.asList(
                new Airline().withAirlineCodes(
                        Arrays.asList("AAU", "AAW", "AAV", "AFU")
                ),
                new Airline().withAirlineCodes(
                        Collections.singletonList("AAU")
                ),
                new Airline().withAirlineCodes(
                        Arrays.asList("PIU", "BRO")
                ));

        RulesEngine engine = new RulesEngineBuilder()
                .withRulesRepository(i -> rules)
                .withActionMapping("collect", method(new Actions(), (action) -> action.collect(null, null)))
                .build();
        RuleSession session = engine.createSession("airlines");

        // when
        List<Airline> matching = session.execute(new ArrayList<>(), facts);

        // then
        assertThat(matching).containsExactly(
                new Airline().withAirlineCodes(Arrays.asList("AAU", "AAW", "AAV", "AFU"))
        );
    }

    @Test
    void shouldMatchWhenContainingAnyFromSet() {
        //given
        List<Rule> rules = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match airline when airline codes contain any of given")
                        .fact("airline", Airline.class)
                        .predicate(
                                containsAny(
                                        castToCollection(value("${airline.airlineCodes}"), String.class),
                                        values(String.class, "AAU", "PIU", "BRO")
                                )
                        )
                        .action("collect",
                                param("context", value("${ctx}")),
                                param("fact", value("${airline}")))
                        .build()
        );
        List<Airline> facts = Arrays.asList(
                new Airline().withAirlineCodes(
                        Arrays.asList("AAU", "AAW", "AAV", "AFU")
                ),
                new Airline().withAirlineCodes(
                        Collections.singletonList("AAU")
                ),
                new Airline().withAirlineCodes(
                        Arrays.asList("AAW", "AAV")
                ));

        RulesEngine engine = new RulesEngineBuilder()
                .withRulesRepository(i -> rules)
                .withActionMapping("collect", method(new Actions(), (action) -> action.collect(null, null)))
                .build();
        RuleSession session = engine.createSession("airlines");

        // when
        List<Airline> matching = session.execute(new ArrayList<>(), facts);

        // then
        assertThat(matching).containsExactly(
                new Airline().withAirlineCodes(Arrays.asList("AAU", "AAW", "AAV", "AFU")),
                new Airline().withAirlineCodes(Collections.singletonList("AAU"))
        );
    }

    @Test
    void shouldMatchNotRejectedAirlines() {
        //given
        List<Rule> rules = Arrays.asList(
                RuleDsl.ruleBuilder()
                        .name("Should mark airline as rejected when its name is equal to given")
                        .fact("airline", Airline.class)
                        .predicate(
                                equal(
                                        value("${airline.name}"),
                                        value("Lufthansa")
                                )
                        )
                        .action("setRejectedFlag",
                                param("airline", value("${airline}"))
                        )
                        .build(),
                RuleDsl.ruleBuilder()
                        .name("Should match not rejected airlines")
                        .fact("airline", Airline.class)
                        .predicate(
                                not(value("${airline.isRejected}"))
                        )
                        .action("collect",
                                param("context", value("${ctx}")),
                                param("airline", value("${airline}")))
                        .build()
        );
        List<Airline> airlines = Arrays.asList(
                new Airline().withName("Lufthansa"),
                new Airline().withName("Lot"),
                new Airline().withName("Wizz Air")
        );

        Actions actions = new Actions();
        RulesEngine engine = new RulesEngineBuilder()
                .withRulesRepository(i -> rules)
                .withActionMapping("setRejectedFlag", method(actions, (action) -> action.reject(null)))
                .withActionMapping("collect", method(actions, (action) -> action.collect(null, null)))
                .withRulesExecutorBuilder(new DefaultRulesExecutorBuilder()
                        .withSequentialMode(true))
                .build();
        RuleSession session = engine.createSession("airlines");

        //when
        List<Airline> matching = session.execute(new ArrayList<>(), airlines);

        //then
        assertThat(matching).containsExactly(
                new Airline().withName("Lot"),
                new Airline().withName("Wizz Air")
        );
    }

    public static class Airline {
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

    public static class Actions {
        public void collect(List<Object> results, Object fact) {
            results.add(fact);
        }

        public void reject(Airline airline) {
            airline.withIsRejected(true);
        }
    }
}
