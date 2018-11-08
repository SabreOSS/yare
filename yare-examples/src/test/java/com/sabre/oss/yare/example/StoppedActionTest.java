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

import com.sabre.oss.yare.core.EngineController;
import com.sabre.oss.yare.core.RuleSession;
import com.sabre.oss.yare.core.RulesEngine;
import com.sabre.oss.yare.core.RulesEngineBuilder;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.dsl.RuleDsl;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.invoker.java.MethodCallMetadata.method;
import static org.assertj.core.api.Assertions.assertThat;

public class StoppedActionTest {

    @Test
    void shouldEvaluatingUpToTwoMatchingFacts() {
        // given
        List<Rule> rules = Collections.singletonList(RuleDsl.ruleBuilder()
                .name("Should match airline when airline codes contain given")
                .fact("airline", Airline.class)
                .predicate(
                        contains(
                                castToCollection(value("${airline.airlineCodes}"), String.class),
                                values(String.class, "AAU")
                        )
                )
                .action("collect",
                        param("context", value("${ctx}")),
                        param("fact", value("${airline}")),
                        param("engineController", value("${engineController}")))
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
                ),
                new Airline().withAirlineCodes(
                        Arrays.asList("AAU", "PIU", "BRO")
                ));

        RulesEngine engine = new RulesEngineBuilder()
                .withRulesRepository(i -> rules)
                .withActionMapping("collect", method(new StoppedActionTest.Actions(2), (action) -> action.collectUpToTwo(null, null, null)))
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
    void shouldNotStopFlow() {
        // given
        List<Rule> rules = Collections.singletonList(RuleDsl.ruleBuilder()
                .name("Should match airline when airline codes contain given")
                .fact("airline", Airline.class)
                .predicate(
                        contains(
                                castToCollection(value("${airline.airlineCodes}"), String.class),
                                values(String.class, "AAU")
                        )
                )
                .action("collect",
                        param("context", value("${ctx}")),
                        param("fact", value("${airline}")),
                        param("engineController", value("${engineController}")))
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
                ),
                new Airline().withAirlineCodes(
                        Arrays.asList("AAU", "PIU", "BRO")
                ));

        RulesEngine engine = new RulesEngineBuilder()
                .withRulesRepository(i -> rules)
                .withActionMapping("collect", method(new StoppedActionTest.Actions(4), (action) -> action.collectUpToTwo(null, null, null)))
                .build();

        RuleSession session = engine.createSession("airlines");

        // when
        List<Airline> matching = session.execute(new ArrayList<>(), facts);

        // then
        assertThat(matching).containsExactly(
                new Airline().withAirlineCodes(Arrays.asList("AAU", "AAW", "AAV", "AFU")),
                new Airline().withAirlineCodes(Collections.singletonList("AAU")),
                new Airline().withAirlineCodes(Arrays.asList("AAU", "PIU", "BRO"))
        );
    }

    public static class Airline {
        public String name;
        public List<String> airlineCodes;
        public boolean isRejected = false;

        public Airline withAirlineCodes(final List<String> airlineCodes) {
            this.airlineCodes = airlineCodes;
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
        private int maxMatchFacts;

        public Actions() {
        }

        public Actions(int maxMatchFacts) {
            this.maxMatchFacts = maxMatchFacts;
        }

        public void collectUpToTwo(List<Object> results, Object fact, EngineController engineController) {
            results.add(fact);
            if (results.size() == maxMatchFacts) {
                engineController.stopProcessing();
            }
        }
    }
}
