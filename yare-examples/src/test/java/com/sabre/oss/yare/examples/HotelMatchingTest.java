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

import java.math.BigDecimal;
import java.util.*;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.engine.MethodCallMetadata.method;
import static org.assertj.core.api.Assertions.assertThat;

class HotelMatchingTest {
    private static final String COLLECT = "collect";
    private static final String HOTEL_RULE_SET = "hotelRuleSet";
    private static final String GET_AMOUNT_OF_MONEY = "getAmountOfMoney";


    @Test
    void shouldMatchHotelWithGivenChainCode() {
        // given
        String chainCode = "HH";
        List<Rule> rule = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match hotel with given chain code")
                        .fact("hotel", Hotel.class)
                        .predicate(
                                equal(
                                        value("${hotel.chainCode}"),
                                        value(chainCode)
                                )
                        )
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("fact", value("${hotel}")))
                        .build()
        );
        List<Hotel> testHotel = Arrays.asList(
                new Hotel().withHotelName("Test Hilton hotel").withChainCode("HH"),
                new Hotel().withHotelName("Test Harvey hotel").withChainCode("HV")
        );

        RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository(((Map<String, List<Rule>>) ImmutableMap.of(HOTEL_RULE_SET, rule))::get)
                .withActionMapping(COLLECT, method(new TestAction(), (action) -> action.collect(null, null)))
                .build();
        RuleSession ruleSession = rulesEngine.createSession(HOTEL_RULE_SET);

        // when
        List<Hotel> matchingHotel = ruleSession.execute(new ArrayList<>(), testHotel);

        // then
        assertThat(matchingHotel).containsExactly(
                new Hotel().withHotelName("Test Hilton hotel").withChainCode("HH")
        );
    }

    @Test
    void shouldMatchPreferredHotelBasedOnProperty() {
        // given
        String chainCode = "HV";
        List<Rule> rule = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match preferred hotel basing on preferred property or chain code")
                        .fact("hotel", Hotel.class)
                        .predicate(or(
                                equal(
                                        value("${hotel.isPreferred}"),
                                        value(true)
                                ),
                                equal(
                                        value("${hotel.chainCode}"),
                                        value(chainCode)
                                )
                        ))
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("fact", value("${hotel}")))
                        .build()
        );
        List<Hotel> testHotel = Collections.singletonList(
                new Hotel().withHotelName("Test Hilton hotel").withChainCode("HH").withIsPreferred(true)
        );

        RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository(((Map<String, List<Rule>>) ImmutableMap.of(HOTEL_RULE_SET, rule))::get)
                .withActionMapping(COLLECT, method(new TestAction(), (action) -> action.collect(null, null)))
                .build();
        RuleSession ruleSession = rulesEngine.createSession(HOTEL_RULE_SET);

        // when
        List<Hotel> matchingHotel = ruleSession.execute(new ArrayList<>(), testHotel);

        // then
        assertThat(matchingHotel).containsExactly(
                new Hotel().withHotelName("Test Hilton hotel").withChainCode("HH").withIsPreferred(true)
        );
    }

    @Test
    void shouldMatchPreferredHotelBasedOnChainCode() {
        // given
        String chainCode = "HH";
        List<Rule> rule = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match preferred hotel basing on preferred property or chain code")
                        .fact("hotel", Hotel.class)
                        .predicate(or(
                                equal(
                                        value("${hotel.isPreferred}"),
                                        value(true)
                                ),
                                equal(
                                        value("${hotel.chainCode}"),
                                        value(chainCode)
                                )
                        ))
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("fact", value("${hotel}")))
                        .build()
        );
        List<Hotel> testHotel = Collections.singletonList(
                new Hotel().withHotelName("Test Hilton hotel").withChainCode("HH").withIsPreferred(false)
        );

        RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository(((Map<String, List<Rule>>) ImmutableMap.of(HOTEL_RULE_SET, rule))::get)
                .withActionMapping(COLLECT, method(new TestAction(), (action) -> action.collect(null, null)))
                .build();
        RuleSession ruleSession = rulesEngine.createSession(HOTEL_RULE_SET);

        // when
        List<Hotel> matchingHotel = ruleSession.execute(new ArrayList<>(), testHotel);

        // then
        assertThat(matchingHotel).containsExactly(
                new Hotel().withHotelName("Test Hilton hotel").withChainCode("HH").withIsPreferred(false)
        );
    }

    @Test
    void shouldNotMatchNotPreferredHotel() {
        // given
        String chainCode = "HH";
        List<Rule> rule = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match preferred hotel basing on preferred property or chain code")
                        .fact("hotel", Hotel.class)
                        .predicate(or(
                                equal(
                                        value("${hotel.isPreferred}"),
                                        value(true)
                                ),
                                equal(
                                        value("${hotel.chainCode}"),
                                        value(chainCode)
                                )
                        ))
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("fact", value("${hotel}")))
                        .build()
        );
        List<Hotel> testHotel = Collections.singletonList(
                new Hotel()
                        .withHotelName("Test Harvey hotel")
                        .withChainCode("HV")
                        .withIsPreferred(false)
        );

        RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository(((Map<String, List<Rule>>) ImmutableMap.of(HOTEL_RULE_SET, rule))::get)
                .withActionMapping(COLLECT, method(new TestAction(), (action) -> action.collect(null, null)))
                .build();
        RuleSession ruleSession = rulesEngine.createSession(HOTEL_RULE_SET);

        // when
        List<Hotel> matchingHotel = ruleSession.execute(new ArrayList<>(), testHotel);

        // then
        assertThat(matchingHotel).isEmpty();
    }

    @Test
    void shouldMatchHotelWithHigherRoomRate() {
        //given
        BigDecimal roomRate = new BigDecimal(100);
        String currency = "Euro";
        List<Rule> rule = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match hotel with room rate greater than given")
                        .fact("hotel", Hotel.class)
                        .predicate(
                                greater(
                                        function(GET_AMOUNT_OF_MONEY, BigDecimal.class,
                                                param("amount", value("${hotel.roomRate}")),
                                                param("inputCurrency", value("${hotel.currency}")),
                                                param("outputCurrency", value(currency))),
                                        value(roomRate)
                                )
                        )
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("fact", value("${hotel}")))
                        .build()
        );
        List<Hotel> testHotel = Collections.singletonList(
                new Hotel()
                        .withHotelName("Test Harvey hotel")
                        .withChainCode("HV")
                        .withRoomRate(new BigDecimal(150))
                        .withCurrency("USD - DOLLAR")
        );

        RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository(((Map<String, List<Rule>>) ImmutableMap.of(HOTEL_RULE_SET, rule))::get)
                .withActionMapping(COLLECT, method(new TestAction(), (action) -> action.collect(null, null)))
                .withFunctionMapping(GET_AMOUNT_OF_MONEY, method(new TestFunction(), f -> f.getAmountOfMoney(null, null, null)))
                .build();
        RuleSession ruleSession = rulesEngine.createSession(HOTEL_RULE_SET);

        //when
        List<Hotel> matchingHotel = ruleSession.execute(new ArrayList<>(), testHotel);

        //then
        assertThat(matchingHotel).containsExactly(
                new Hotel().withHotelName("Test Harvey hotel").withChainCode("HV").withRoomRate(new BigDecimal(150)).withCurrency("USD - DOLLAR")
        );
    }

    @Test
    void shouldMatchHotelWithLowerRoomRate() {
        //given
        BigDecimal roomRate = new BigDecimal(100);
        String currency = "USD - DOLLAR";
        List<Rule> rule = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match hotel with room rate greater than given")
                        .fact("hotel", Hotel.class)
                        .predicate(
                                greater(
                                        function(GET_AMOUNT_OF_MONEY, BigDecimal.class,
                                                param("amount", value("${hotel.roomRate}")),
                                                param("inputCurrency", value("${hotel.currency}")),
                                                param("outputCurrency", value(currency))),
                                        value(roomRate)
                                )
                        )
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("fact", value("${hotel}")))
                        .build()
        );
        List<Hotel> testHotel = Collections.singletonList(
                new Hotel()
                        .withHotelName("Test Harvey hotel")
                        .withChainCode("HV")
                        .withRoomRate(new BigDecimal(70))
                        .withCurrency("USD - DOLLAR")
        );

        RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository(((Map<String, List<Rule>>) ImmutableMap.of(HOTEL_RULE_SET, rule))::get)
                .withActionMapping(COLLECT, method(new TestAction(), (action) -> action.collect(null, null)))
                .withFunctionMapping(GET_AMOUNT_OF_MONEY, method(new TestFunction(), f -> f.getAmountOfMoney(null, null, null)))
                .build();
        RuleSession ruleSession = rulesEngine.createSession(HOTEL_RULE_SET);

        //when
        List<Hotel> matchingHotel = ruleSession.execute(new ArrayList<>(), testHotel);

        //then
        assertThat(matchingHotel).isEmpty();
    }

    public static class TestAction {

        public void collect(List<Object> results, Object fact) {
            results.add(fact);
        }

    }

    public static class TestFunction {

        TestFunction() {
        }

        public BigDecimal getAmountOfMoney(BigDecimal amount, String inputCurrency, String outputCurrency) {
            return inputCurrency.equals(outputCurrency)
                    ? amount
                    : convertBetweenCurrencies(amount, inputCurrency, outputCurrency);
        }

        private BigDecimal convertBetweenCurrencies(BigDecimal amount, String inputCurrency, String outputCurrency) {
            // Appropriate implementation of currency converter
            return amount;
        }
    }

    public class Hotel {
        public String hotelName;
        public String chainCode;
        public boolean isPreferred;
        public BigDecimal roomRate;
        public String currency;

        public Hotel withHotelName(final String hotelName) {
            this.hotelName = hotelName;
            return this;
        }

        public Hotel withChainCode(final String chainCode) {
            this.chainCode = chainCode;
            return this;
        }

        public Hotel withIsPreferred(final boolean isPreferred) {
            this.isPreferred = isPreferred;
            return this;
        }

        public Hotel withRoomRate(final BigDecimal roomRate) {
            this.roomRate = roomRate;
            return this;
        }

        public Hotel withCurrency(final String currency) {
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
                    Objects.equals(hotelName, hotel.hotelName) &&
                    Objects.equals(chainCode, hotel.chainCode) &&
                    Objects.equals(roomRate, hotel.roomRate) &&
                    Objects.equals(currency, hotel.currency);
        }

        @Override
        public int hashCode() {
            return Objects.hash(hotelName, chainCode, isPreferred, roomRate, currency);
        }
    }
}
