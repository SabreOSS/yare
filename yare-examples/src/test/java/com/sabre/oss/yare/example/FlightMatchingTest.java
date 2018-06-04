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
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.engine.MethodCallMetadata.method;
import static org.assertj.core.api.Assertions.assertThat;

class FlightMatchingTest {

    @Test
    void shouldMatchFlightWhenTimeUntilDepartureInBounds() {
        // given
        List<Rule> rules = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match flight with time until departure in hours lower than given")
                        .fact("flight", Flight.class)
                        .predicate(
                                less(
                                        function("calculateHoursToDate", Long.class,
                                                param("givenDate", value("${flight.dateOfDeparture}"))),
                                        value(24L)
                                )
                        )
                        .action("collect",
                                param("context", value("${ctx}")),
                                param("fact", value("${flight}")))
                        .build()
        );
        LocalDateTime currentTime = LocalDateTime.now();
        List<Flight> facts = Arrays.asList(
                new Flight().withDateOfDeparture(currentTime.plusHours(23)),
                new Flight().withDateOfDeparture(currentTime.plusHours(25))
        );

        RulesEngine engine = new RulesEngineBuilder()
                .withRulesRepository(i -> rules)
                .withActionMapping("collect", method(new Actions(), (action) -> action.collect(null, null)))
                .withFunctionMapping("calculateHoursToDate", method(new Functions(), f -> f.calculateHoursToDate(null)))
                .build();
        RuleSession session = engine.createSession("flights");

        // when
        List<Flight> matching = session.execute(new ArrayList<>(), facts);

        // then
        assertThat(matching).containsExactly(
                new Flight().withDateOfDeparture(currentTime.plusHours(23))
        );
    }

    public class Flight {
        private LocalDateTime dateOfDeparture;

        public LocalDateTime getDateOfDeparture() {
            return dateOfDeparture;
        }

        Flight withDateOfDeparture(final LocalDateTime dateOfDeparture) {
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
            return Objects.equals(dateOfDeparture, that.dateOfDeparture);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dateOfDeparture);
        }
    }

    public static class Actions {
        public void collect(List<Flight> context, Flight fact) {
            context.add(fact);
        }
    }

    public static class Functions {
        public Long calculateHoursToDate(LocalDateTime givenDate) {
            return ChronoUnit.HOURS.between(LocalDateTime.now(), givenDate);
        }
    }
}
