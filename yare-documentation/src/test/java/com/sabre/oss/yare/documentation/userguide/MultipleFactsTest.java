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

package com.sabre.oss.yare.documentation.userguide;

import com.sabre.oss.yare.core.RuleSession;
import com.sabre.oss.yare.core.RulesEngine;
import com.sabre.oss.yare.core.RulesEngineBuilder;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.dsl.RuleDsl;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.engine.MethodCallMetadata.method;
import static org.assertj.core.api.Assertions.assertThat;

class MultipleFactsTest {

    @Test
    void shouldProperlyEvaluateHotelFacts() {
        // given
        List<Hotel> hotels = Arrays.asList(
                new Hotel("AH"),
                new Hotel("HH"),
                new Hotel("AH"),
                new Hotel("AJ")
        );
        Map<String, List<Rule>> ruleSets = createRuleSets();
        RulesEngine rulesEngine = createRulesEngine(ruleSets);

        // tag::part-of-rule-sets-example-session[]
        RuleSession ruleSession = rulesEngine.createSession("hotelRuleSet");
        // end::part-of-rule-sets-example-session[]

        // when
        List<Hotel> matchingHotels = ruleSession.execute(new ArrayList<>(), hotels);

        // then
        assertThat(matchingHotels).containsExactly(
                new Hotel("HH"),
                new Hotel("AJ")
        );
    }

    @Test
    void shouldProperlyEvaluateFlightFacts() {
        // given
        Clock clock = Clock.fixed(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        List<Flight> flights = Arrays.asList(
                new Flight(new BigDecimal(150), LocalDateTime.now(clock).plusHours(24)),
                new Flight(new BigDecimal(100), LocalDateTime.now(clock).plusHours(25)),
                new Flight(new BigDecimal(300), LocalDateTime.now(clock).plusHours(20)),
                new Flight(new BigDecimal(80), LocalDateTime.now(clock).plusHours(10))
        );
        Map<String, List<Rule>> ruleSets = createRuleSets();

        RulesEngine rulesEngine = createRulesEngine(ruleSets, clock);

        RuleSession ruleSession = rulesEngine.createSession("flightRuleSet");

        // when
        List<Flight> matchingFlights = ruleSession.execute(new ArrayList<>(), flights);

        // then
        assertThat(matchingFlights).containsExactly(
                new Flight(new BigDecimal(150), LocalDateTime.now(clock).plusHours(24))
        );
    }

    private Map<String, List<Rule>> createRuleSets() {
        List<Rule> hotelRule = createHotelRule();
        List<Rule> flightRule = createFlightRule();
        // tag::part-of-rule-sets-example-map[]
        Map<String, List<Rule>> ruleSets = new HashMap<>();
        ruleSets.put("hotelRuleSet", hotelRule);
        ruleSets.put("flightRuleSet", flightRule);
        // end::part-of-rule-sets-example-map[]
        return ruleSets;
    }

    private List<Rule> createHotelRule() {
        // tag::part-of-rule-sets-example-rules[]
        List<Rule> hotelRule = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Rule matching when hotel has chain code different from specified")
                        .fact("hotel", Hotel.class)
                        .predicate(
                                not(
                                        equal(
                                                field("hotel.chainCode", String.class),
                                                value("AH")
                                        )
                                )

                        )
                        .action("collect",
                                param("context", reference("ctx")),
                                param("fact", reference("hotel")))
                        .build()
        );
        // end::part-of-rule-sets-example-rules[]
        return hotelRule;
    }

    private List<Rule> createFlightRule() {
        // tag::part-of-rule-sets-example-rules[]
        List<Rule> flightRule = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Rule matching when flight departs in 24h and it's price is less or equal 100$")
                        .fact("flight", Flight.class)
                        .predicate(
                                not(
                                        or(
                                                lessOrEqual(
                                                        field("flight.price", BigDecimal.class),
                                                        value(new BigDecimal(100))
                                                ),
                                                less(
                                                        function("getDiffInHours", Long.class,
                                                                param("date", field("flight", "dateOfDeparture"))),
                                                        value(24L)
                                                )
                                        )
                                )
                        )
                        .action("collect",
                                param("context", reference("ctx")),
                                param("fact", reference("flight")))
                        .build()
        );
        // end::part-of-rule-sets-example-rules[]
        return flightRule;
    }

    private RulesEngine createRulesEngine(Map<String, List<Rule>> ruleSets) {
        // tag::part-of-rule-sets-example-engine[]
        RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository(ruleSets::get)
                .withActionMapping("collect", method(new TestAction(), (action) -> action.collect(null, null)))
                .withFunctionMapping("getDiffInHours", method(new TestFunction(), (function) -> function.getDiffInHours(null)))
                .build();
        // end::part-of-rule-sets-example-engine[]
        return rulesEngine;
    }

    private RulesEngine createRulesEngine(Map<String, List<Rule>> ruleSets, Clock clock) {
        RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository(ruleSets::get)
                .withActionMapping("collect", method(new TestAction(), (action) -> action.collect(null, null)))
                .withFunctionMapping("getDiffInHours", method(new TestFunction(clock), (function) -> function.getDiffInHours(null)))
                .build();
        return rulesEngine;
    }
}
