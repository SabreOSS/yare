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

import com.google.common.util.concurrent.UncheckedExecutionException;
import com.sabre.oss.yare.core.RuleSession;
import com.sabre.oss.yare.core.RulesEngine;
import com.sabre.oss.yare.core.RulesEngineBuilder;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.dsl.Expression;
import com.sabre.oss.yare.dsl.RuleDsl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.sabre.oss.yare.dsl.RuleDsl.param;
import static com.sabre.oss.yare.dsl.RuleDsl.value;
import static com.sabre.oss.yare.engine.MethodCallMetadata.method;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ValuePredicateTest {

    @ParameterizedTest
    @MethodSource("predicates")
    void shouldResolveValuePlaceholderToBoolean(Expression<Boolean> predicate) {
        // given
        Rule rule = RuleDsl.ruleBuilder()
                .name("Rule match based on isValid fact field")
                .fact("fact", NamedFact.class)
                .predicate(predicate)
                .action("collect",
                        param("context", value("${ctx}")),
                        param("fact", value("${fact}"))
                )
                .build();

        List<NamedFact> facts = Arrays.asList(
                new NamedFact("first", true),
                new NamedFact("second", false),
                new NamedFact("third", true));

        RulesEngine engine = new RulesEngineBuilder()
                .withRulesRepository(uri -> Collections.singletonList(rule))
                .withActionMapping("collect", method(this, a -> a.collect(null, null)))
                .build();
        RuleSession session = engine.createSession("uri");

        // when
        ArrayList<String> result = session.execute(new ArrayList<>(), facts);

        // then
        assertThat(result).containsExactly(
                "first",
                "third"
        );
    }

    private static Stream<Expression<Boolean>> predicates() {
        return Stream.of(
                value("${fact.isValid}"),
                value("${fact.boxedIsValid}")
        );
    }

    @Test
    void shouldFailWithValuePlaceholderNotResolvedToBoolean() {
        // given
        Rule rule = RuleDsl.ruleBuilder()
                .name("Rule match based on isValid fact field")
                .fact("fact", NamedFact.class)
                .predicate(
                        value("${fact.name}")
                )
                .action("collect",
                        param("context", value("${ctx}")),
                        param("fact", value("${fact}"))
                )
                .build();

        RulesEngine engine = new RulesEngineBuilder()
                .withRulesRepository(uri -> Collections.singletonList(rule))
                .withActionMapping("collect", method(this, a -> a.collect(null, null)))
                .build();
        RuleSession session = engine.createSession("uri");

        // when / then
        assertThatThrownBy(() -> session.execute(null, Collections.emptyList()))
                .isExactlyInstanceOf(UncheckedExecutionException.class)
                .hasMessage("java.lang.IllegalArgumentException: Only references of boolean type can be translated directly to predicate");
    }

    public void collect(List<String> context, NamedFact fact) {
        context.add(fact.name);
    }

    public static final class NamedFact {
        public String name;
        public boolean isValid;
        public Boolean boxedIsValid;

        private NamedFact(String name, boolean isValid) {
            this.name = name;
            this.isValid = isValid;
            this.boxedIsValid = isValid;
        }
    }
}
