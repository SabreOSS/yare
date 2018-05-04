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

import com.sabre.oss.yare.core.RulesEngine;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.engine.executor.DefaultRulesExecutorBuilder;
import com.sabre.oss.yare.examples.facts.Airline;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.sabre.oss.yare.engine.MethodCallMetadata.method;
import static com.sabre.oss.yare.examples.RulesBuilder.*;
import static java.time.temporal.ChronoUnit.HOURS;

public final class RulesEngineBuilder {

    private static final String FUNCTIONS_PROVIDER = "testFunction";
    private static final String ACTIONS_PROVIDER = "testAction";

    private RulesEngineBuilder() {
    }

    public static RulesEngine createRulesEngine(Map<String, List<Rule>> ruleSets) {
        return createRulesEngine(ruleSets, Clock.systemDefaultZone());
    }

    public static RulesEngine createRulesEngine(Map<String, List<Rule>> ruleSets, Clock clock) {
        TestFunction testFunction = new TestFunction(clock);
        TestAction testAction = new TestAction();
        return new com.sabre.oss.yare.core.RulesEngineBuilder()
                .withRulesRepository(ruleSets::get)
                .withActionMapping(SET_REJECTED_FLAG, method(testAction, (action) -> action.setRejectedFlagToTrue(null, null)))
                .withActionMapping(COLLECT, method(testAction, (action) -> action.collect(null, null)))
                .withFunctionMapping(GET_AMOUNT_OF_MONEY, method(testFunction, f -> f.getAmountOfMoney(null, null, null)))
                .withFunctionMapping(GET_DATE_DIFF_IN_HOURS, method(testFunction, f -> f.getDateDiffInHours(null)))
                .withFunctionMapping(PRICE_DIFFERENCE, method(testFunction, f -> f.priceDifference(null)))
                .withFunctionMapping(CONTAINS, method(testFunction, f -> f.contains(null, null)))
                .withFunctionMapping(CONTAINS_ANY, method(testFunction, f -> f.containsAny(null, null)))
                .withRulesExecutorBuilder(new DefaultRulesExecutorBuilder()
                        .withSequentialMode(true))
                .build();
    }

    public static class TestFunction {

        private final Clock clock;

        public TestFunction() {
            clock = Clock.systemDefaultZone();
        }

        public TestFunction(Clock clock) {
            this.clock = Objects.requireNonNull(clock, "clock must not be null");
        }

        public Boolean contains(List<Object> outerList, List<Object> innerList) {
            return outerList.containsAll(innerList);
        }

        public Boolean containsAny(List<Object> outerList, List<Object> innerList) {
            return innerList.stream().anyMatch(outerList::contains);
        }

        public BigDecimal priceDifference(BigDecimal carRate) {
            BigDecimal lowestPrice = new BigDecimal(100);
            return carRate.subtract(lowestPrice);
        }

        public Long getDateDiffInHours(LocalDateTime givenDate) {
            return HOURS.between(LocalDateTime.now(clock), givenDate);
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

    public static class TestAction {

        public void collect(List<Object> results, Object fact) {
            results.add(fact);
        }

        public void setRejectedFlagToTrue(Airline airline, Boolean isRejected) {
            airline.withIsRejected(isRejected);
        }
    }
}
