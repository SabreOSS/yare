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
import com.sabre.oss.yare.core.RulesEngineBuilder;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.dsl.RuleDsl;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.invoker.js.JavaScriptCallMetadata.js;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class JavaScriptFunctionTest {

    @Test
    void shouldMatchFactsWhenUsingJavaScriptFunction() {
        // given
        List<Rule> rules = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Rule matching when hotel has specified chain code")
                        .fact("hotel", Hotel.class)
                        .predicate(
                                equal(
                                        function("concat", String.class,
                                                param("str1", value("Chain code:")),
                                                param("str2", value("${hotel.chainCode}"))
                                        ),
                                        value("Chain code:HH")
                                )
                        )
                        .action("collect",
                                param("context", value("${ctx}")),
                                param("fact", value("${hotel}")))
                        .build()
        );
        List<Hotel> facts = Arrays.asList(
                new Hotel().withChainCode("HH"),
                new Hotel().withChainCode("BV"),
                new Hotel().withChainCode("SE"),
                new Hotel().withChainCode("BW"),
                new Hotel().withChainCode("HH")
        );

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
        RuleSession ruleSession = rulesEngine.createSession("test");

        // when
        List<Hotel> result = ruleSession.execute(new ArrayList<>(), facts);

        // then
        assertThat(result).containsExactly(
                new Hotel().withChainCode("HH"),
                new Hotel().withChainCode("HH")
        );
    }

    public static class Hotel {
        public String chainCode;

        Hotel withChainCode(String chainCode) {
            this.chainCode = chainCode;
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
            Hotel hotel = (Hotel) o;
            return Objects.equals(chainCode, hotel.chainCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(chainCode);
        }
    }
}
