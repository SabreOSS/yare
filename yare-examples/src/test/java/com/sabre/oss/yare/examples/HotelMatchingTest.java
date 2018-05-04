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
import com.sabre.oss.yare.examples.facts.Hotel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HotelMatchingTest {
    private static final String HOTEL_RULE_SET = "hotelRuleSet";

    @Test
    void shouldMatchHotelWithGivenChainCode() {
        // given
        String chainCode = "HH";
        List<Rule> rule = RulesBuilder.createRuleMatchingChainCode(chainCode);
        List<Hotel> testHotel = Collections.singletonList(
                new Hotel().withHotelName("Test Hilton hotel").withChainCode("HH")
        );

        RulesEngine rulesEngine = RulesEngineBuilder.createRulesEngine(ImmutableMap.of(HOTEL_RULE_SET, rule));
        RuleSession ruleSession = rulesEngine.createSession(HOTEL_RULE_SET);

        // when
        List<Hotel> matchingHotel = ruleSession.execute(new ArrayList<>(), testHotel);

        // then
        assertThat(matchingHotel).containsExactly(
                new Hotel().withHotelName("Test Hilton hotel").withChainCode("HH")
        );
    }

    @Test
    void shouldNotMatchHotelWithoutGivenChainCode() {
        // given
        String chainCode = "HH";
        List<Rule> rule = RulesBuilder.createRuleMatchingChainCode(chainCode);
        List<Hotel> testHotel = Collections.singletonList(
                new Hotel().withHotelName("Test Harvey hotel").withChainCode("HV")
        );

        RulesEngine rulesEngine = RulesEngineBuilder.createRulesEngine(ImmutableMap.of(HOTEL_RULE_SET, rule));
        RuleSession ruleSession = rulesEngine.createSession(HOTEL_RULE_SET);

        // when
        List<Hotel> matchingHotel = ruleSession.execute(new ArrayList<>(), testHotel);

        // then
        assertThat(matchingHotel).isEmpty();
    }

    @Test
    void shouldMatchPreferredHotelBasedOnProperty() {
        // given
        String chainCode = "HV";
        List<Rule> rule = RulesBuilder.createRuleMatchingPreferredHotel(chainCode);
        List<Hotel> testHotel = Collections.singletonList(
                new Hotel().withHotelName("Test Hilton hotel").withChainCode("HH").withIsPreferred(true)
        );

        RulesEngine rulesEngine = RulesEngineBuilder.createRulesEngine(ImmutableMap.of(HOTEL_RULE_SET, rule));
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
        List<Rule> rule = RulesBuilder.createRuleMatchingPreferredHotel(chainCode);
        List<Hotel> testHotel = Collections.singletonList(
                new Hotel().withHotelName("Test Hilton hotel").withChainCode("HH").withIsPreferred(false)
        );

        RulesEngine rulesEngine = RulesEngineBuilder.createRulesEngine(ImmutableMap.of(HOTEL_RULE_SET, rule));
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
        List<Rule> rule = RulesBuilder.createRuleMatchingPreferredHotel(chainCode);
        List<Hotel> testHotel = Collections.singletonList(
                new Hotel()
                        .withHotelName("Test Harvey hotel")
                        .withChainCode("HV")
                        .withIsPreferred(false)
        );

        RulesEngine rulesEngine = RulesEngineBuilder.createRulesEngine(ImmutableMap.of(HOTEL_RULE_SET, rule));
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
        List<Rule> rule = RulesBuilder.createRuleMatchingHotelWithRoomRateGreaterThan(roomRate, currency);
        List<Hotel> testHotel = Collections.singletonList(
                new Hotel()
                        .withHotelName("Test Harvey hotel")
                        .withChainCode("HV")
                        .withRoomRate(new BigDecimal(150))
                        .withCurrency("USD - DOLLAR")
        );

        RulesEngine rulesEngine = RulesEngineBuilder.createRulesEngine(ImmutableMap.of(HOTEL_RULE_SET, rule));
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
        List<Rule> rule = RulesBuilder.createRuleMatchingHotelWithRoomRateGreaterThan(roomRate, currency);
        List<Hotel> testHotel = Collections.singletonList(
                new Hotel()
                        .withHotelName("Test Harvey hotel")
                        .withChainCode("HV")
                        .withRoomRate(new BigDecimal(70))
                        .withCurrency("USD - DOLLAR")
        );

        RulesEngine rulesEngine = RulesEngineBuilder.createRulesEngine(ImmutableMap.of(HOTEL_RULE_SET, rule));
        RuleSession ruleSession = rulesEngine.createSession(HOTEL_RULE_SET);

        //when
        List<Hotel> matchingHotel = ruleSession.execute(new ArrayList<>(), testHotel);

        //then
        assertThat(matchingHotel).isEmpty();
    }
}
