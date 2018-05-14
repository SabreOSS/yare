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

package com.sabre.oss.yare.engine.integration;

import com.sabre.oss.yare.core.RuleSession;
import com.sabre.oss.yare.core.RulesEngine;
import com.sabre.oss.yare.core.RulesEngineBuilder;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.dsl.RuleDsl;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.engine.MethodCallMetadata.method;
import static org.assertj.core.api.Assertions.assertThat;

class CustomFactNameTest {

    @Test
    void shouldProperlyMatchFacts() {
        // given
        List<Object> facts = Arrays.asList(
                new Fact("First"),
                new Fact("Second"),
                new Fact("Third"),
                new Fact("Fourth")
        );
        List<Rule> rules = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Find Facts with matching value")
                        .fact("customFactName", Fact.class)
                        .predicate(
                                or(
                                        equal(
                                                value("${customFactName.value}"),
                                                value("Second")
                                        ),
                                        function("matches", Boolean.class,
                                                param("fact", value("${customFactName}")),
                                                param("value", value("Third"))),
                                        function("equals", Boolean.class,
                                                param("actual", value("${customFactName.value}")),
                                                param("expected", value("Fourth")))
                                )
                        )
                        .action("collect",
                                param("context", value("${ctx}")),
                                param("fact", value("${customFactName}")))
                        .build()
        );

        RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository(i -> rules)
                .withActionMapping("collect", method(new Actions(), (action) -> action.collect(null, null)))
                .withFunctionMapping("matches", method(new Functions(), (function) -> function.matches(null, null)))
                .withFunctionMapping("equals", method(new Functions(), "equals", String.class, String.class))
                .build();

        RuleSession ruleSession = rulesEngine.createSession("customFactNameExample");

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), facts);

        // then
        assertThat(matchingFacts).containsExactly(
                new Fact("Second"),
                new Fact("Third"),
                new Fact("Fourth")
        );
    }

    private static final class Fact {
        private final String value;

        private Fact(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            return this == o || (o != null && getClass() == o.getClass()) && Objects.equals(value, ((Fact) o).value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return String.format("Fact{value='%s'}", value);
        }
    }

    public static class Actions {
        public void collect(List<Fact> context, Fact currentFact) {
            context.add(currentFact);
        }
    }

    public static class Functions {
        public boolean matches(Fact fact, String value) {
            return value.equals(fact.getValue());
        }

        public boolean equals(String actual, String expected) {
            return Objects.equals(actual, expected);
        }
    }
}
