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
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.dsl.RuleDsl;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.invoker.java.MethodCallMetadata.method;
import static org.assertj.core.api.Assertions.assertThat;

public class CarMatchingTest {

    @Test
    void shouldMatchWhenPriceOverLowestByGiven() {
        // given
        List<Rule> rules = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match car with total rate greater than given over lowest price")
                        .fact("car", Car.class)
                        .predicate(
                                greater(
                                        function("subtract", BigDecimal.class,
                                                param("carRate", value("${car.totalRate}")),
                                                param("constantPrice", value(new BigDecimal(100)))
                                        ),
                                        value(new BigDecimal(50))
                                )
                        )
                        .action("collect",
                                param("context", value("${ctx}")),
                                param("fact", value("${car}")))
                        .build()
        );
        List<Car> facts = Arrays.asList(
                new Car().withTotalRate(new BigDecimal(200)),
                new Car().withTotalRate(new BigDecimal(145))
        );

        RulesEngine engine = new com.sabre.oss.yare.core.RulesEngineBuilder()
                .withRulesRepository(i -> rules)
                .withActionMapping("collect", method(new Actions(), (action) -> action.collect(null, null)))
                .withFunctionMapping("subtract", method(new Functions(), f -> f.subtract(null, null)))
                .build();
        RuleSession session = engine.createSession("cars");

        // when
        List<Car> matching = session.execute(new ArrayList<>(), facts);

        // then
        assertThat(matching).containsExactly(
                new Car().withTotalRate(new BigDecimal(200))
        );
    }

    public class Car {
        public BigDecimal totalRate;

        Car withTotalRate(BigDecimal totalRate) {
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

    public static class Actions {
        public void collect(List<Car> context, Car fact) {
            context.add(fact);
        }
    }

    public static class Functions {
        public BigDecimal subtract(BigDecimal x, BigDecimal y) {
            return x.subtract(y);
        }
    }
}
