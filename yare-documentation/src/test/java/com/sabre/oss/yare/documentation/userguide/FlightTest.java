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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.engine.MethodCallMetadata.method;
import static org.assertj.core.api.Assertions.assertThat;

class FlightTest {

    @Test
    void shouldMatchProperFlights() {
        // given
        // tag::part-of-functions-example-rule[]
        List<Rule> rule = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Rule matching when flight departs in 24h and it's price is less or equal 100$")
                        .fact("flight", Flight.class)
                        .predicate(
                                and(
                                        lessOrEqual(
                                                field("flight.price"),
                                                value(new BigDecimal(100))
                                        ),
                                        less(
                                                function("getDiffInHours", Long.class,
                                                        param("date", field("flight", "dateOfDeparture"))),
                                                value(24L)
                                        )
                                )
                        )
                        .action("collect",
                                param("context", reference("ctx")),
                                param("fact", reference("flight")))
                        .build()
        );
        // end::part-of-functions-example-rule[]

        // tag::part-of-functions-example-engine[]
        Clock clock = Clock.fixed(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository(i -> rule)
                .withActionMapping("collect", method(new TestAction(), (action) -> action.collect(null, null)))
                .withFunctionMapping("getDiffInHours", method(new TestFunction(clock), (function) -> function.getDiffInHours(null)))
                .build();
        // end::part-of-functions-example-engine[]

        // tag::part-of-functions-example-fact-creation[]
        List<Flight> facts = Arrays.asList(
                new Flight(new BigDecimal(100), LocalDateTime.now(clock).plusHours(23)),
                new Flight(new BigDecimal(120), LocalDateTime.now(clock).plusHours(10)),
                new Flight(new BigDecimal(50), LocalDateTime.now(clock).plusHours(25)),
                new Flight(new BigDecimal(250), LocalDateTime.now(clock).plusHours(30))
        );
        // end::part-of-functions-example-fact-creation[]
        RuleSession ruleSession = rulesEngine.createSession("functionsExample");

        // when
        List<Flight> matchingFacts = ruleSession.execute(new LinkedList<>(), facts);

        // then
        assertThat(matchingFacts).containsExactly(
                new Flight(new BigDecimal(100), LocalDateTime.now(clock).plusHours(23))
        );
    }
}
