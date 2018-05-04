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
import com.sabre.oss.yare.examples.facts.Airline;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class AirlineMatchingTest {
    private static final String AIRLINE_RULE_SET = "airlineRuleSet";

    @Test
    void shouldMatchWithSubsetOfAirlineCodes() {
        // given
        List<String> airlineCodes = Arrays.asList("AAU", "AAV");
        List<Rule> rule = RulesBuilder.createRuleMatchingWhenAirlineCodesContainsGiven(airlineCodes);
        List<Airline> airline = Collections.singletonList(
                new Airline().withAirlineCodes(
                        Arrays.asList("AAU", "AAW", "AAV", "AFU")
                ));

        RulesEngine rulesEngine = RulesEngineBuilder.createRulesEngine(ImmutableMap.of(AIRLINE_RULE_SET, rule));
        RuleSession ruleSession = rulesEngine.createSession(AIRLINE_RULE_SET);

        // when
        List<Airline> matchingAirline = ruleSession.execute(new ArrayList<>(), airline);

        // then
        assertThat(matchingAirline).containsExactly(
                new Airline().withAirlineCodes(Arrays.asList("AAU", "AAW", "AAV", "AFU"))
        );
    }

    @Test
    void shouldNotMatchWithDisjointSubsetOfAirlineCodes() {
        // given
        List<String> airlineCodes = Arrays.asList("TTF", "PIU", "BRO");
        List<Rule> rule = RulesBuilder.createRuleMatchingWhenAirlineCodesContainsGiven(airlineCodes);
        List<Airline> airline = Collections.singletonList(
                new Airline().withAirlineCodes(
                        Arrays.asList("AAU", "AAW", "AAV", "AFU")
                ));

        RulesEngine rulesEngine = RulesEngineBuilder.createRulesEngine(ImmutableMap.of(AIRLINE_RULE_SET, rule));
        RuleSession ruleSession = rulesEngine.createSession(AIRLINE_RULE_SET);

        // when
        List<Airline> matchingAirline = ruleSession.execute(new ArrayList<>(), airline);

        // then
        assertThat(matchingAirline).isEmpty();
    }

    @Test
    void shouldMatchWhenSubsetOfGiven() {
        //given
        List<String> airlineCodes = Arrays.asList("AAU", "AAW", "AAV", "AFU", "TTF", "PIU", "BRO");
        List<Rule> rule = RulesBuilder.createRuleMatchingWhenAirlineCodesInGiven(airlineCodes);
        List<Airline> airline = Collections.singletonList(
                new Airline().withAirlineCodes(
                        Arrays.asList("AAU", "AAW", "AAV", "AFU")
                ));

        RulesEngine rulesEngine = RulesEngineBuilder.createRulesEngine(ImmutableMap.of(AIRLINE_RULE_SET, rule));
        RuleSession ruleSession = rulesEngine.createSession(AIRLINE_RULE_SET);

        // when
        List<Airline> matchingAirline = ruleSession.execute(new ArrayList<>(), airline);

        // then
        assertThat(matchingAirline).containsExactly(
                new Airline().withAirlineCodes(Arrays.asList("AAU", "AAW", "AAV", "AFU"))
        );
    }

    @Test
    void shouldNotMatchWhenDisjointSubsetWithGiven() {
        //given
        List<String> airlineCodes = Arrays.asList("AAU", "AAW", "TTF", "PIU", "BRO", "ATT", "IWY");
        List<Rule> rule = RulesBuilder.createRuleMatchingWhenAirlineCodesInGiven(airlineCodes);
        List<Airline> airline = Collections.singletonList(
                new Airline().withAirlineCodes(
                        Arrays.asList("AAU", "AAW", "AAV", "AFU")
                ));

        RulesEngine rulesEngine = RulesEngineBuilder.createRulesEngine(ImmutableMap.of(AIRLINE_RULE_SET, rule));
        RuleSession ruleSession = rulesEngine.createSession(AIRLINE_RULE_SET);

        // when
        List<Airline> matchingAirline = ruleSession.execute(new ArrayList<>(), airline);

        // then
        assertThat(matchingAirline).isEmpty();
    }

    @Test
    void shouldMatchWhenContainingAnyFromSet() {
        //given
        List<String> airlineCodes = Arrays.asList("AAU", "PIU", "BRO");
        List<Rule> rule = RulesBuilder.createRuleMatchingWhenAirlineCodesContainsAnyOfGiven(airlineCodes);
        List<Airline> airline = Collections.singletonList(
                new Airline().withAirlineCodes(
                        Arrays.asList("AAU", "AAW", "AAV", "AFU")
                ));

        RulesEngine rulesEngine = RulesEngineBuilder.createRulesEngine(ImmutableMap.of(AIRLINE_RULE_SET, rule));
        RuleSession ruleSession = rulesEngine.createSession(AIRLINE_RULE_SET);

        // when
        List<Airline> matchingAirline = ruleSession.execute(new ArrayList<>(), airline);

        // then
        assertThat(matchingAirline).containsExactly(
                new Airline().withAirlineCodes(Arrays.asList("AAU", "AAW", "AAV", "AFU"))
        );
    }

    @Test
    void shouldNotMatchWhenNotContainingAnyFromSet() {
        //given
        List<String> airlineCodes = Arrays.asList("PIU", "BRO");
        List<Rule> rule = RulesBuilder.createRuleMatchingWhenAirlineCodesContainsAnyOfGiven(airlineCodes);
        List<Airline> airline = Collections.singletonList(
                new Airline().withAirlineCodes(
                        Arrays.asList("AAU", "AAW", "AAV", "AFU")
                ));

        RulesEngine rulesEngine = RulesEngineBuilder.createRulesEngine(ImmutableMap.of(AIRLINE_RULE_SET, rule));
        RuleSession ruleSession = rulesEngine.createSession(AIRLINE_RULE_SET);

        // when
        List<Airline> matchingAirline = ruleSession.execute(new ArrayList<>(), airline);

        // then
        assertThat(matchingAirline).isEmpty();
    }

    @Test
    void shouldMatchNotRejectedAirlines() {
        //given
        List<Rule> rules = new LinkedList<>();
        rules.addAll(RulesBuilder.createRuleSettingIsRejectedWhenNameEqualToGiven("Lufthansa"));
        rules.addAll(RulesBuilder.createRuleCollectingNotRejectedAirlines());
        List<Airline> airlines = Arrays.asList(
                new Airline().withName("Lufthansa"),
                new Airline().withName("Lot"),
                new Airline().withName("Wizz Air"),
                new Airline().withName("Lufthansa")
        );

        RulesEngine rulesEngine = RulesEngineBuilder.createRulesEngine(ImmutableMap.of(AIRLINE_RULE_SET, rules));
        RuleSession ruleSession = rulesEngine.createSession(AIRLINE_RULE_SET);

        //when
        List<Airline> matchingAirlines = ruleSession.execute(new ArrayList<>(), airlines);

        //then
        assertThat(matchingAirlines).containsExactly(
                new Airline().withName("Lot"),
                new Airline().withName("Wizz Air")
        );
    }

    @Test
    void shouldNotMatchRejectedAirlines() {
        //given
        List<Rule> rules = new LinkedList<>();
        rules.addAll(RulesBuilder.createRuleSettingIsRejectedWhenNameEqualToGiven("Wizz Air"));
        rules.addAll(RulesBuilder.createRuleCollectingNotRejectedAirlines());
        List<Airline> airlines = Arrays.asList(
                new Airline().withName("Wizz Air"),
                new Airline().withName("Wizz Air"),
                new Airline().withName("Wizz Air")
        );

        RulesEngine rulesEngine = RulesEngineBuilder.createRulesEngine(ImmutableMap.of(AIRLINE_RULE_SET, rules));
        RuleSession ruleSession = rulesEngine.createSession(AIRLINE_RULE_SET);

        //when
        List<Airline> matchingAirlines = ruleSession.execute(new ArrayList<>(), airlines);

        //then
        assertThat(matchingAirlines).isEmpty();
    }
}
