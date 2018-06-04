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

import java.math.BigDecimal;
import java.util.*;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.engine.MethodCallMetadata.method;
import static org.assertj.core.api.Assertions.assertThat;

class HotelMatchingTest {

    @Test
    void shouldMatchPreferredHotelBasedOnPropertyOrChainCode() {
        // given
        List<Rule> rules = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match preferred hotel basing on preferred property or chain code")
                        .fact("hotel", Hotel.class)
                        .predicate(
                                or(
                                        value("${hotel.isPreferred}"),
                                        equal(
                                                value("${hotel.chainCode}"),
                                                value("HV")
                                        )
                                )
                        )
                        .action("collect",
                                param("context", value("${ctx}")),
                                param("fact", value("${hotel}")))
                        .build()
        );
        List<Hotel> facts = Arrays.asList(
                new Hotel().withChainCode("HH").withIsPreferred(true),
                new Hotel().withChainCode("HV").withIsPreferred(false),
                new Hotel().withChainCode("IN").withIsPreferred(false)
        );

        RulesEngine engine = new RulesEngineBuilder()
                .withRulesRepository(i -> rules)
                .withActionMapping("collect", method(new Actions(), (action) -> action.collect(null, null)))
                .build();
        RuleSession session = engine.createSession("hotels");

        // when
        List<Hotel> matching = session.execute(new ArrayList<>(), facts);

        // then
        assertThat(matching).containsExactly(
                new Hotel().withChainCode("HH").withIsPreferred(true),
                new Hotel().withChainCode("HV").withIsPreferred(false)
        );
    }

    @Test
    void shouldMatchHotelWithHigherRoomRate() {
        // given
        List<Rule> rules = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match hotel with room rate greater than given")
                        .fact("hotel", Hotel.class)
                        .predicate(
                                greater(
                                        function("convertCurrency", BigDecimal.class,
                                                param("amount", value("${hotel.roomRate}")),
                                                param("inputCurrency", value("${hotel.currency}")),
                                                param("outputCurrency", value("Euro"))),
                                        value(new BigDecimal(100))
                                )
                        )
                        .action("collect",
                                param("context", value("${ctx}")),
                                param("fact", value("${hotel}")))
                        .build()
        );
        List<Hotel> facts = Arrays.asList(
                new Hotel()
                        .withChainCode("HH")
                        .withRoomRate(new BigDecimal(150))
                        .withCurrency("USD - DOLLAR"),
                new Hotel()
                        .withChainCode("HV")
                        .withRoomRate(new BigDecimal(70))
                        .withCurrency("EURO")
        );

        RulesEngine engine = new RulesEngineBuilder()
                .withRulesRepository(i -> rules)
                .withActionMapping("collect", method(new Actions(), (action) -> action.collect(null, null)))
                .withFunctionMapping("convertCurrency", method(new Functions(), f -> f.convertCurrency(null, null, null)))
                .build();
        RuleSession session = engine.createSession("hotels");

        // when
        List<Hotel> matching = session.execute(new ArrayList<>(), facts);

        // then
        assertThat(matching).containsExactly(
                new Hotel().withChainCode("HH").withRoomRate(new BigDecimal(150)).withCurrency("USD - DOLLAR")
        );
    }

    public static class Hotel {
        public String chainCode;
        public boolean isPreferred;
        public BigDecimal roomRate;
        public String currency;

        Hotel withChainCode(String chainCode) {
            this.chainCode = chainCode;
            return this;
        }

        Hotel withIsPreferred(boolean isPreferred) {
            this.isPreferred = isPreferred;
            return this;
        }

        Hotel withRoomRate(BigDecimal roomRate) {
            this.roomRate = roomRate;
            return this;
        }

        Hotel withCurrency(String currency) {
            this.currency = currency;
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
            Hotel hotel = (Hotel) o;
            return isPreferred == hotel.isPreferred &&
                    Objects.equals(chainCode, hotel.chainCode) &&
                    Objects.equals(roomRate, hotel.roomRate) &&
                    Objects.equals(currency, hotel.currency);
        }

        @Override
        public int hashCode() {
            return Objects.hash(chainCode, isPreferred, roomRate, currency);
        }
    }

    public static class Actions {
        public void collect(List<Hotel> context, Hotel fact) {
            context.add(fact);
        }
    }

    public static class Functions {
        public BigDecimal convertCurrency(BigDecimal amount, String inputCurrency, String outputCurrency) {
            // Appropriate implementation of currency converter
            return amount;
        }
    }
}
