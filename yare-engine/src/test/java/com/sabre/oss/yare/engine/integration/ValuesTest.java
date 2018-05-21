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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.engine.MethodCallMetadata.method;
import static org.assertj.core.api.Assertions.assertThat;

public class ValuesTest {

    @Test
    void shouldResolveValuesInAction() {
        Rule rule = RuleDsl.ruleBuilder()
                .name("Should resolve values in action")
                .fact("fact", Fact.class)
                .predicate(
                        value(true)
                )
                .action("return",
                        param("context", value("${ctx}")),
                        param("values", expressions(String.class,
                                value("${fact.name}"),
                                value("${fact.field}"),
                                function("stringFunction"),
                                value("constantValue"))
                        )
                )
                .build();
        List<Fact> facts = Collections.singletonList(new Fact("first", "second"));

        ArrayList<String> result = execute(facts, rule);

        assertThat(result).containsExactly(
                "first",
                "second",
                getString(),
                "constantValue");
    }

    @Test
    void shouldResolveValuesInOperators() {
        Rule rule = RuleDsl.ruleBuilder()
                .name("Should resolve values in action")
                .fact("fact", Fact.class)
                .predicate(
                        contains(
                                expressions(String.class,
                                        value("${fact.name}"),
                                        value("${fact.field}"),
                                        function("stringFunction"),
                                        value("third")
                                ),
                                values(String.class,
                                        value("first"),
                                        value("second"),
                                        value("third"),
                                        value(getString())
                                )
                        )
                )
                .action("return",
                        param("context", value("${ctx}")),
                        param("values", values(String.class, "matched"))
                )
                .build();
        List<Fact> facts = Collections.singletonList(new Fact("first", "second"));

        ArrayList<String> result = execute(facts, rule);

        assertThat(result).containsExactly("matched");
    }

    private ArrayList<String> execute(List<Fact> facts, Rule rule) {
        RulesEngine engine = new RulesEngineBuilder()
                .withRulesRepository(i -> Collections.singletonList(rule))
                .withActionMapping("return", method(this, a -> a.append(null, null)))
                .withFunctionMapping("stringFunction", method(this, ValuesTest::getString))
                .build();

        RuleSession session = engine.createSession("session");

        return session.execute(new ArrayList<>(), facts);
    }

    public String getString() {
        return "function value";
    }

    public void append(List<String> context, List<String> values) {
        context.addAll(values);
    }

    public static final class Fact {
        private final String name;
        private final String field;

        private Fact(String name, String field) {
            this.name = name;
            this.field = field;
        }

        public String getName() {
            return name;
        }

        public String getField() {
            return field;
        }
    }
}
