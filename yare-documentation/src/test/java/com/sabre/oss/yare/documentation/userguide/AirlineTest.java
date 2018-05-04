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
import com.sabre.oss.yare.engine.executor.DefaultRulesExecutorBuilder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.engine.MethodCallMetadata.method;
import static org.assertj.core.api.Assertions.assertThat;

class AirlineTest {

    @Test
    void shouldProperlyMatchAirlines() {
        // given
        List<Airline> facts = Arrays.asList(
                new Airline("Wizz Air"),
                new Airline("Lufthansa"),
                new Airline("American Airlines"),
                new Airline("Lufthansa")
        );
        // tag::part-of-changing-example-rules[]
        List<Rule> rules = Arrays.asList(
                RuleDsl.ruleBuilder()
                        .name("Rule matching when airline name is equal to \"Lufthansa\"")
                        .fact("airline", Airline.class)
                        .predicate(
                                equal(
                                        field("airline.name", String.class),
                                        value("Lufthansa")
                                )
                        )
                        .action("setFlag",
                                param("airlineFact", reference("airline")),
                                param("value", value(true)))
                        .build(),
                RuleDsl.ruleBuilder()
                        .name("Rule matching when rejected flag set to true")
                        .fact("airline", Airline.class)
                        .predicate(
                                isTrue(
                                        field("airline.rejected", Boolean.class)
                                )
                        )
                        .action("collect",
                                param("context", reference("ctx")),
                                param("fact", reference("airline")))
                        .build()
        );
        // end::part-of-changing-example-rules[]

        // tag::part-of-changing-example-engine[]
        AirlineTestAction airlineTestAction = new AirlineTestAction();
        RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository(i -> rules)
                .withActionMapping("setFlag", method(airlineTestAction, (action) -> action.setFlag(null, null)))
                .withActionMapping("collect", method(airlineTestAction, (action) -> action.collect(null, null)))
                .withRulesExecutorBuilder(new DefaultRulesExecutorBuilder()
                        .withSequentialMode(true))
                .build();

        RuleSession ruleSession = rulesEngine.createSession("airlineExample");
        // end::part-of-changing-example-engine[]

        // when
        List<Airline> matchingFacts = ruleSession.execute(new ArrayList<>(), facts);

        // then
        assertThat(matchingFacts).containsExactly(
                new Airline("Lufthansa").withRejected(true),
                new Airline("Lufthansa").withRejected(true)
        );
    }
}
