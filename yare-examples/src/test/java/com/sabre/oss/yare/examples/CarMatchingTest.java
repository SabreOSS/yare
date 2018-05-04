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
import com.sabre.oss.yare.examples.facts.Car;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CarMatchingTest {
    private static final String CAR_RULE_SET = "carRuleSet";

    @Test
    void shouldMatchWhenPriceOverLowestByGiven() {
        //given
        BigDecimal howMuchOverLowestPrice = new BigDecimal(50);
        List<Rule> rule = RulesBuilder.createRuleMatchingWithPriceOverLowest(howMuchOverLowestPrice);
        List<Car> testCar = Collections.singletonList(
                new Car().withTotalRate(new BigDecimal(200))
        );

        RulesEngine rulesEngine = RulesEngineBuilder.createRulesEngine(ImmutableMap.of(CAR_RULE_SET, rule));
        RuleSession ruleSession = rulesEngine.createSession(CAR_RULE_SET);

        //when
        List<Car> matchingCar = ruleSession.execute(new ArrayList<>(), testCar);

        //then
        assertThat(matchingCar).containsExactly(
                new Car().withTotalRate(new BigDecimal(200))
        );
    }

    @Test
    void shouldNotMatchWhenPriceBelowLowestByGiven() {
        //given
        BigDecimal howMuchOverLowestPrice = new BigDecimal(50);
        List<Rule> rule = RulesBuilder.createRuleMatchingWithPriceOverLowest(howMuchOverLowestPrice);
        List<Car> testCar = Collections.singletonList(
                new Car().withTotalRate(new BigDecimal(145))
        );

        RulesEngine rulesEngine = RulesEngineBuilder.createRulesEngine(ImmutableMap.of(CAR_RULE_SET, rule));
        RuleSession ruleSession = rulesEngine.createSession(CAR_RULE_SET);

        //when
        List<Car> matchingCar = ruleSession.execute(new ArrayList<>(), testCar);

        //then
        assertThat(matchingCar).isEmpty();
    }
}
