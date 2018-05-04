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
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.invoker.js.JavaScriptCallMetadata.js;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class JavaScriptFunctionTest {

    @Test
    void shouldMatchFactsWhenUsingJavaScriptFunction() {
        // given
        List<Hotel> facts = Arrays.asList(
                new Hotel("HH"),
                new Hotel("BV"),
                new Hotel("SE"),
                new Hotel("BW"),
                new Hotel("HH")
        );
        // tag::part-of-javascript-functions-example-rule[]
        List<Rule> rules = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Rule matching when hotel has specified chain code")
                        .fact("hotel", Hotel.class)
                        .predicate(
                                equal(
                                        function("concat", String.class,
                                                param("str1", value("Chain code:")),
                                                param("str2", field("hotel.chainCode"))),
                                        value("Chain code:HH")
                                )
                        )
                        .action("collect",
                                param("context", reference("ctx")),
                                param("fact", reference("hotel")))
                        .build()
        );
        // end::part-of-javascript-functions-example-rule[]

        RulesEngine rulesEngine = getRulesEngine(rules);
        RuleSession ruleSession = rulesEngine.createSession("test");

        // when
        List<Hotel> result = ruleSession.execute(new ArrayList<>(), facts);

        // then
        assertThat(result).containsExactly(
                new Hotel("HH"),
                new Hotel("HH")
        );
    }

    private RulesEngine getRulesEngine(List<Rule> rules) {
        // tag::part-of-javascript-functions-example-engine[]
        String script = "" +
                "function concat(str1, str2) { " +
                "   return str1 + str2; " +
                "}" +
                "function collect(ctx, fact) {" +
                "   ctx.add(fact);" +
                "}";
        RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository(i -> rules)
                .withActionMapping("collect", js("collect", script))
                .withFunctionMapping("concat", js("concat", script))
                .build();
        // end::part-of-javascript-functions-example-engine[]
        return rulesEngine;
    }
}
