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

class CarMatchingTest {
    private static final String COLLECT = "collect";
    private static final String PRICE_DIFFERENCE = "priceDifference";
    private static final String CAR_RULE_SET = "carRuleSet";

    @Test
    void shouldMatchWhenPriceOverLowestByGiven() {
        //given
        BigDecimal howMuchOverLowestPrice = new BigDecimal(50);
        List<Rule> rule = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match car with total rate greater than given over lowest price")
                        .fact("car", Car.class)
                        .predicate(
                                greater(
                                        function(PRICE_DIFFERENCE, BigDecimal.class,
                                                param("carRate", value("${car.totalRate}"))),
                                        value(howMuchOverLowestPrice)
                                )
                        )
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("fact", value("${car}")))
                        .build()
        );
        List<Car> testCar = Arrays.asList(
                new Car().withTotalRate(new BigDecimal(200)),
                new Car().withTotalRate(new BigDecimal(145))
        );

        RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository(((Map<String, List<Rule>>) ImmutableMap.of(CAR_RULE_SET, rule))::get)
                .withActionMapping(COLLECT, method(new TestAction(), (action) -> action.collect(null, null)))
                .withFunctionMapping(PRICE_DIFFERENCE, method(new TestFunction(), f -> f.priceDifference(null)))
                .build();
        RuleSession ruleSession = rulesEngine.createSession(CAR_RULE_SET);

        //when
        List<Car> matchingCar = ruleSession.execute(new ArrayList<>(), testCar);

        //then
        assertThat(matchingCar).containsExactly(
                new Car().withTotalRate(new BigDecimal(200))
        );
    }

    public static class TestAction {

        public void collect(List<Object> results, Object fact) {
            results.add(fact);
        }

    }

    public static class TestFunction {

        TestFunction() {
        }

        public BigDecimal priceDifference(BigDecimal carRate) {
            BigDecimal lowestPrice = new BigDecimal(100);
            return carRate.subtract(lowestPrice);
        }
    }

    public class Car {
        public BigDecimal totalRate;


        public Car withTotalRate(BigDecimal totalRate) {
            this.totalRate = totalRate;
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
            Car car = (Car) o;
            return Objects.equals(totalRate, car.totalRate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(totalRate);
        }
    }
}
