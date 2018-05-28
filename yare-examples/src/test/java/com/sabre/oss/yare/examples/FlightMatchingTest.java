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
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.engine.MethodCallMetadata.method;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;

class FlightMatchingTest {
    private static final String COLLECT = "collect";
    private static final String GET_DATE_DIFF_IN_HOURS = "getDateDiffInHours";
    private static final String FLIGHT_RULE_SET = "flightRuleSet";

    @Test
    void shouldMatchFlightWithGivenClassOfService() {
        // given
        String classOfService = "First Class";
        List<Rule> rule = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match flight with given class of service")
                        .fact("flight", Flight.class)
                        .predicate(
                                equal(
                                        value("${flight.classOfService}"),
                                        value(classOfService)
                                )
                        )
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("fact", value("${flight}")))
                        .build()
        );
        List<Flight> flight = Arrays.asList(
                new Flight().withClassOfService("First Class"),
                new Flight().withClassOfService("Business Class")
        );

        RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository(((Map<String, List<Rule>>) ImmutableMap.of(FLIGHT_RULE_SET, rule))::get)
                .withActionMapping(COLLECT, method(new TestAction(), (action) -> action.collect(null, null)))
                .build();
        RuleSession ruleSession = rulesEngine.createSession(FLIGHT_RULE_SET);

        // when
        List<Flight> matchingFlight = ruleSession.execute(new ArrayList<>(), flight);

        // then
        assertThat(matchingFlight).containsExactly(
                new Flight().withClassOfService("First Class")
        );
    }

    @Test
    void shouldMatchFlightWhenTimeUntilDepartureInBounds() {
        // given
        List<Rule> rule = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match flight with time until departure in hours lower than given")
                        .fact("flight", Flight.class)
                        .predicate(
                                less(
                                        function(GET_DATE_DIFF_IN_HOURS, Long.class,
                                                param("givenDate", value("${flight.dateOfDeparture}"))),
                                        value((long) 24)
                                )
                        )
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("fact", value("${flight}")))
                        .build()
        );
        Clock clock = Clock.fixed(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        LocalDateTime acceptableDepartureTime = LocalDateTime.now(clock).plusHours(23);
        LocalDateTime notAcceptableDepartureTime = LocalDateTime.now(clock).plusHours(25);
        List<Flight> flight = Arrays.asList(
                new Flight().withDateOfDeparture(acceptableDepartureTime),
                new Flight().withDateOfDeparture(notAcceptableDepartureTime)
        );

        RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository(((Map<String, List<Rule>>) ImmutableMap.of(FLIGHT_RULE_SET, rule))::get)
                .withActionMapping(COLLECT, method(new TestAction(), (action) -> action.collect(null, null)))
                .withFunctionMapping(GET_DATE_DIFF_IN_HOURS, method(new TestFunction(clock), f -> f.getDateDiffInHours(null)))
                .build();
        RuleSession ruleSession = rulesEngine.createSession(FLIGHT_RULE_SET);

        // when
        List<Flight> matchingFlight = ruleSession.execute(new ArrayList<>(), flight);

        // then
        assertThat(matchingFlight).containsExactly(
                new Flight().withDateOfDeparture(acceptableDepartureTime)
        );
    }

    public static class TestAction {

        public void collect(List<Object> results, Object fact) {
            results.add(fact);
        }

    }

    public static class TestFunction {

        private final Clock clock;

        TestFunction() {
            clock = Clock.systemDefaultZone();
        }

        TestFunction(Clock clock) {
            this.clock = Objects.requireNonNull(clock, "clock must not be null");
        }


        public Long getDateDiffInHours(LocalDateTime givenDate) {
            return HOURS.between(LocalDateTime.now(clock), givenDate);
        }
    }

    public class Flight {
        public String classOfService;
        public LocalDateTime dateOfDeparture;

        public Flight withClassOfService(final String classOfService) {
            this.classOfService = classOfService;
            return this;
        }

        public Flight withDateOfDeparture(final LocalDateTime dateOfDeparture) {
            this.dateOfDeparture = dateOfDeparture;
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
            Flight that = (Flight) o;
            return Objects.equals(classOfService, that.classOfService) &&
                    Objects.equals(dateOfDeparture, that.dateOfDeparture);
        }

        @Override
        public int hashCode() {
            return Objects.hash(classOfService, dateOfDeparture);
        }
    }
}
