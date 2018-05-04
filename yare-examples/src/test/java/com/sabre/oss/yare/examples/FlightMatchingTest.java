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
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.examples.facts.Flight;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlightMatchingTest {
    private static final String FLIGHT_RULE_SET = "flightRuleSet";

    @Test
    void shouldMatchFlightWithGivenClassOfService() {
        // given
        String classOfService = "First Class";
        List<Rule> rule = RulesBuilder.createRuleMatchingClassOfService(classOfService);
        List<Flight> flight = Collections.singletonList(
                new Flight().withClassOfService("First Class")
        );

        RulesEngine rulesEngine = RulesEngineBuilder.createRulesEngine(ImmutableMap.of(FLIGHT_RULE_SET, rule));
        RuleSession ruleSession = rulesEngine.createSession(FLIGHT_RULE_SET);

        // when
        List<Flight> matchingFlight = ruleSession.execute(new ArrayList<>(), flight);

        // then
        assertThat(matchingFlight).containsExactly(
                new Flight().withClassOfService("First Class")
        );
    }

    @Test
    void shouldNotMatchFlightWithoutGivenClassOfService() {
        // given
        String classOfService = "First Class";
        List<Rule> rule = RulesBuilder.createRuleMatchingClassOfService(classOfService);
        List<Flight> flight = Collections.singletonList(
                new Flight().withClassOfService("Business Class")
        );

        RulesEngine rulesEngine = RulesEngineBuilder.createRulesEngine(ImmutableMap.of(FLIGHT_RULE_SET, rule));
        RuleSession ruleSession = rulesEngine.createSession(FLIGHT_RULE_SET);

        // when
        List<Flight> matchingFlight = ruleSession.execute(new ArrayList<>(), flight);

        // then
        assertThat(matchingFlight).isEmpty();
    }

    @Test
    void shouldMatchFlightWhenTimeUntilDepartureInBounds() {
        // given
        List<Rule> rule = RulesBuilder.createRuleMatchingTimeUntilDepartureLowerThan(24);
        Clock clock = Clock.fixed(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        LocalDateTime departureTime = LocalDateTime.now(clock).plusHours(23);
        List<Flight> flight = Collections.singletonList(
                new Flight().withDateOfDeparture(departureTime)
        );

        RulesEngine rulesEngine = RulesEngineBuilder.createRulesEngine(ImmutableMap.of(FLIGHT_RULE_SET, rule), clock);
        RuleSession ruleSession = rulesEngine.createSession(FLIGHT_RULE_SET);

        // when
        List<Flight> matchingFlight = ruleSession.execute(new ArrayList<>(), flight);

        // then
        assertThat(matchingFlight).containsExactly(
                new Flight().withDateOfDeparture(departureTime)
        );
    }

    @Test
    void shouldNotMatchFlightWhenTimeUntilDepartureOutOfBounds() {
        // given
        List<Rule> rule = RulesBuilder.createRuleMatchingTimeUntilDepartureLowerThan(24);
        Clock clock = Clock.fixed(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        LocalDateTime departureTime = LocalDateTime.now(clock).plusHours(25);
        List<Flight> flight = Collections.singletonList(
                new Flight().withDateOfDeparture(departureTime)
        );

        RulesEngine rulesEngine = RulesEngineBuilder.createRulesEngine(ImmutableMap.of(FLIGHT_RULE_SET, rule), clock);
        RuleSession ruleSession = rulesEngine.createSession(FLIGHT_RULE_SET);

        // when
        List<Flight> matchingFlight = ruleSession.execute(new ArrayList<>(), flight);

        // then
        assertThat(matchingFlight).isEmpty();
    }
}
