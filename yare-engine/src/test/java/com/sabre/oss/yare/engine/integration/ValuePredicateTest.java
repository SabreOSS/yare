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
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.invoker.java.MethodCallMetadata.method;
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

    @Test
    void shouldUseEscapedPlaceholdersDirectly() {
        // given
        Rule rule = RuleDsl.ruleBuilder()
                .name("Rule match based on isValid fact field")
                .fact("fact", NamedFact.class)
                .predicate(
                        or(
                                equal(
                                        value("${fact.name}"),
                                        value("\\${escape}")
                                ),
                                equal(
                                        value("${fact.name}"),
                                        value("\\\\${doubleEscape}")
                                )
                        )
                )
                .action("collect",
                        param("context", value("${ctx}")),
                        param("fact", value("${fact}"))
                )
                .build();

        List<NamedFact> facts = Arrays.asList(
                new NamedFact("${escape}", true),
                new NamedFact("\\${doubleEscape}", true),
                new NamedFact("anyName", true));

        RulesEngine engine = new RulesEngineBuilder()
                .withRulesRepository(uri -> Collections.singletonList(rule))
                .withActionMapping("collect", method(this, a -> a.collect(null, null)))
                .build();
        RuleSession session = engine.createSession("uri");

        // when
        ArrayList<String> result = session.execute(new ArrayList<>(), facts);

        // then
        assertThat(result).containsExactly(
                "${escape}",
                "\\${doubleEscape}"
        );
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void shouldResolveAttributeReferenceInPredicate(boolean expected) {
        // given
        Rule rule = RuleDsl.ruleBuilder()
                .name("Rule match based on attribute value and fact matching")
                .fact("fact", Object.class)
                .attribute("expected", expected)
                .predicate(
                        value("${expected}")
                )
                .action("setBoolean",
                        param("context", value("${ctx}")),
                        param("value", value(true))
                )
                .build();

        // when
        AtomicBoolean result = createSession(rule, new AtomicBoolean(false));

        // then
        assertThat(result.get()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void shouldResolveAttributeReferenceInOperator(boolean expected) {
        // given
        Rule rule = RuleDsl.ruleBuilder()
                .name("Rule match based on attribute value and fact matching")
                .fact("fact", Object.class)
                .attribute("expected", expected)
                .predicate(
                        isTrue(value("${expected}"))
                )
                .action("setBoolean",
                        param("context", value("${ctx}")),
                        param("value", value(true))
                )
                .build();

        // when
        AtomicBoolean result = createSession(rule, new AtomicBoolean(false));

        // then
        assertThat(result.get()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void shouldResolveAttributeReferenceInFunction(boolean expected) {
        // given
        Rule rule = RuleDsl.ruleBuilder()
                .name("Rule match based on attribute value and fact matching")
                .fact("fact", Object.class)
                .attribute("expected", expected)
                .predicate(
                        value(true)
                )
                .action("setBoolean",
                        param("context", value("${ctx}")),
                        param("value", value("${expected}"))
                )
                .build();

        // when
        AtomicBoolean result = createSession(rule, new AtomicBoolean(!expected));

        // then
        assertThat(result.get()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void shouldResolveAttributeFieldReferenceInPredicate(boolean expected) {
        // given
        Rule rule = RuleDsl.ruleBuilder()
                .name("Rule match based on attribute value and fact matching")
                .fact("fact", Object.class)
                .attribute("expected", new BooleanWrapper(expected))
                .predicate(
                        value("${expected.value}")
                )
                .action("setBoolean",
                        param("context", value("${ctx}")),
                        param("value", value(true))
                )
                .build();

        // when
        AtomicBoolean result = createSession(rule, new AtomicBoolean(false));

        // then
        assertThat(result.get()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void shouldResolveAttributeFieldReferenceInOperator(boolean expected) {
        // given
        Rule rule = RuleDsl.ruleBuilder()
                .name("Rule match based on attribute value and fact matching")
                .fact("fact", Object.class)
                .attribute("expected", new BooleanWrapper(expected))
                .predicate(
                        isTrue(value("${expected.value}"))
                )
                .action("setBoolean",
                        param("context", value("${ctx}")),
                        param("value", value(true))
                )
                .build();

        // when
        AtomicBoolean result = createSession(rule, new AtomicBoolean(false));

        // then
        assertThat(result.get()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void shouldResolveAttributeFieldReferenceInFunction(boolean expected) {
        // given
        Rule rule = RuleDsl.ruleBuilder()
                .name("Rule match based on attribute value and fact matching")
                .fact("fact", Object.class)
                .attribute("expected", new BooleanWrapper(expected))
                .predicate(
                        value(true)
                )
                .action("setBoolean",
                        param("context", value("${ctx}")),
                        param("value", value("${expected.value}"))
                )
                .build();

        // when
        AtomicBoolean result = createSession(rule, new AtomicBoolean(!expected));

        // then
        assertThat(result.get()).isEqualTo(expected);
    }

    private AtomicBoolean createSession(Rule rule, AtomicBoolean context) {
        List<Object> facts = Collections.singletonList(new Object());
        return new RulesEngineBuilder()
                .withRulesRepository(uri -> Collections.singletonList(rule))
                .withActionMapping("setBoolean", method(this, a -> a.setBoolean(null, null)))
                .build()
                .createSession("uri")
                .execute(context, facts);
    }


    @ParameterizedTest
    @CsvSource({"true", "false"})
    void shouldResolveFactReferenceInPredicate(boolean expected) {
        // given
        Rule rule = RuleDsl.ruleBuilder()
                .name("Rule match based on attribute value and fact matching")
                .fact("fact", Boolean.class)
                .predicate(
                        value("${fact}")
                )
                .action("setBoolean",
                        param("context", value("${ctx}")),
                        param("value", value(true))
                )
                .build();

        // when
        AtomicBoolean result = createSession(rule, expected, new AtomicBoolean(false));

        // then
        assertThat(result.get()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void shouldResolveFactReferenceInOperator(boolean expected) {
        // given
        Rule rule = RuleDsl.ruleBuilder()
                .name("Rule match based on attribute value and fact matching")
                .fact("fact", Boolean.class)
                .predicate(
                        isTrue(value("${fact}"))
                )
                .action("setBoolean",
                        param("context", value("${ctx}")),
                        param("value", value(true))
                )
                .build();

        // when
        AtomicBoolean result = createSession(rule, expected, new AtomicBoolean(false));

        // then
        assertThat(result.get()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void shouldResolveFactReferenceInFunction(boolean expected) {
        // given
        Rule rule = RuleDsl.ruleBuilder()
                .name("Rule match based on attribute value and fact matching")
                .fact("fact", Boolean.class)
                .predicate(
                        value(true)
                )
                .action("setBoolean",
                        param("context", value("${ctx}")),
                        param("value", value("${fact}"))
                )
                .build();

        // when
        AtomicBoolean result = createSession(rule, expected, new AtomicBoolean(!expected));

        // then
        assertThat(result.get()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void shouldResolveFactFieldReferenceInPredicate(boolean expected) {
        // given
        Rule rule = RuleDsl.ruleBuilder()
                .name("Rule match based on attribute value and fact matching")
                .fact("fact", BooleanWrapper.class)
                .predicate(
                        value("${fact.value}")
                )
                .action("setBoolean",
                        param("context", value("${ctx}")),
                        param("value", value(true))
                )
                .build();

        // when
        AtomicBoolean result = createSession(rule, new BooleanWrapper(expected), new AtomicBoolean(false));

        // then
        assertThat(result.get()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void shouldResolveFactFieldReferenceInOperator(boolean expected) {
        // given
        Rule rule = RuleDsl.ruleBuilder()
                .name("Rule match based on attribute value and fact matching")
                .fact("fact", BooleanWrapper.class)
                .predicate(
                        isTrue(value("${fact.value}"))
                )
                .action("setBoolean",
                        param("context", value("${ctx}")),
                        param("value", value(true))
                )
                .build();

        // when
        AtomicBoolean result = createSession(rule, new BooleanWrapper(expected), new AtomicBoolean(false));

        // then
        assertThat(result.get()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void shouldResolveFactFieldReferenceInFunction(boolean expected) {
        // given
        Rule rule = RuleDsl.ruleBuilder()
                .name("Rule match based on attribute value and fact matching")
                .fact("fact", BooleanWrapper.class)
                .predicate(
                        value(true)
                )
                .action("setBoolean",
                        param("context", value("${ctx}")),
                        param("value", value("${fact.value}"))
                )
                .build();

        // when
        AtomicBoolean result = createSession(rule, new BooleanWrapper(expected), new AtomicBoolean(!expected));

        // then
        assertThat(result.get()).isEqualTo(expected);
    }

    private AtomicBoolean createSession(Rule rule, Object fact, AtomicBoolean context) {
        List<Object> facts = Collections.singletonList(fact);
        return new RulesEngineBuilder()
                .withRulesRepository(uri -> Collections.singletonList(rule))
                .withActionMapping("setBoolean", method(this, a -> a.setBoolean(null, null)))
                .build()
                .createSession("uri")
                .execute(context, facts);
    }

    public void collect(List<String> context, NamedFact fact) {
        context.add(fact.name);
    }

    public void setBoolean(AtomicBoolean context, Boolean value) {
        context.set(value);
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

    public static final class BooleanWrapper {
        public final boolean value;

        public BooleanWrapper(boolean value) {
            this.value = value;
        }
    }
}
