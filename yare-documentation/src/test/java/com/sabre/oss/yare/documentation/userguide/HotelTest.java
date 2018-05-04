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
import static com.sabre.oss.yare.engine.MethodCallMetadata.method;
import static org.assertj.core.api.Assertions.assertThat;

class HotelTest {

    @Test
    void shouldCollectHotelsWithSpecifiedChainCode() {
        // given
        List<Hotel> facts = Arrays.asList(
                new Hotel("HH"),
                new Hotel("BV"),
                new Hotel("SE"),
                new Hotel("BW"),
                new Hotel("HH")
        );
        List<Rule> rule = Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Rule matching when hotel has specified chain code")
                        .fact("hotel", Hotel.class)
                        .predicate(
                                equal(
                                        field("hotel.chainCode", String.class),
                                        value("HH")
                                )
                        )
                        .action("collect",
                                param("context", reference("ctx")),
                                param("fact", reference("hotel")))
                        .build()
        );
        RulesEngine rulesEngine = new RulesEngineBuilder()
                .withRulesRepository(i -> rule)
                .withActionMapping("collect", method(new TestAction(), (action) -> action.collect(null, null)))
                .build();
        RuleSession ruleSession = rulesEngine.createSession("collectExample");

        // when
        List<Hotel> matchingHotels = ruleSession.execute(new ArrayList<>(), facts);

        // then
        assertThat(matchingHotels).containsExactly(
                new Hotel("HH"),
                new Hotel("HH")
        );
    }
}
