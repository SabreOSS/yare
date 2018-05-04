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
import com.sabre.oss.yare.engine.MethodCallMetadata;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.sabre.oss.yare.dsl.RuleDsl.value;
import static com.sabre.oss.yare.engine.MethodCallMetadata.method;
import static org.assertj.core.api.Assertions.assertThat;

class HelloWorldTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUpStreams() {
        outContent.reset();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void cleanUpStreams() {
        System.setOut(System.out);
    }

    @Test
    void shouldPrintHelloWorld() {
        // given
        // tag::part-of-first-example-fact-creation[]
        List<TestFact> fact = Collections.singletonList(
                new TestFact()
        );
        // end::part-of-first-example-fact-creation[]
        List<Rule> rule = createRule();
        RulesEngine rulesEngine = createRulesEngine(rule);
        // tag::part-of-first-example-session[]
        RuleSession ruleSession = rulesEngine.createSession("helloWorldExample");
        // end::part-of-first-example-session[]

        // when
        // tag::part-of-first-example-execute[]
        ruleSession.execute(new ArrayList<>(), fact);
        // end::part-of-first-example-execute[]

        // then
        assertThat(outContent.toString()).isEqualTo("Hello World!");
    }

    @Test
    void shouldNotPrintHelloWorld() {
        // given
        // tag::part-of-first-example-empty[]
        List<TestFact> fact = Collections.emptyList();
        // end::part-of-first-example-empty[]
        List<Rule> rule = createRule();
        RulesEngine rulesEngine = createRulesEngine(rule);
        RuleSession ruleSession = rulesEngine.createSession("helloWorldExample");

        // when
        ruleSession.execute(new ArrayList<>(), fact);

        // then
        assertThat(outContent.toString()).isNotEqualTo("Hello World!");
    }

    @Test
    void shouldProperlyCreateMetadataExplicitly() {
        // tag::part-of-first-example-metadata[]
        MethodCallMetadata callMetadata = method(new HelloWorldAction(), "printHelloWorld");
        // end::part-of-first-example-metadata[]
    }

    private List<Rule> createRule() {
        // tag::part-of-first-example-rule[]
        List<Rule> rule = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Always matching rule, printing HelloWorld")
                        .fact("fact", TestFact.class)
                        .predicate(
                                value(true)
                        )
                        .action("printHelloWorld")
                        .build()
        );
        // end::part-of-first-example-rule[]
        return rule;
    }

    private RulesEngine createRulesEngine(List<Rule> rule) {
        // tag::part-of-first-example-engine[]
        RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository(i -> rule)
                .withActionMapping("printHelloWorld", method(new HelloWorldAction(), HelloWorldAction::printHelloWorld))
                .build();
        // end::part-of-first-example-engine[]
        return rulesEngine;
    }
}
