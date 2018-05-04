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

package com.sabre.oss.yare.engine.executor;

import org.junit.jupiter.api.Test;

import java.util.*;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CrossProductFactTupleIteratorTest {

    @Test
    void shouldProperlyBehaveForNoData() {
        // given
        Map<String, List<Object>> facts = new HashMap<>();

        DefaultRulesExecutor.CrossProductFactTupleIterator iterator = new DefaultRulesExecutor.CrossProductFactTupleIterator(facts);

        // when
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    void shouldProperlyBehaveForEmptyGroup() {
        // given
        Map<String, List<Object>> facts = new HashMap<>();
        facts.put("a", Collections.emptyList());

        // when / then
        assertThatThrownBy(() -> new DefaultRulesExecutor.CrossProductFactTupleIterator(facts))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldProperlyIterateThroughSingleFact() {
        // given
        Map<String, List<Object>> facts = new HashMap<>();
        facts.put("a", asList(new MyFact("A1"), new MyFact("A2"), new MyFact("A3")));

        DefaultRulesExecutor.CrossProductFactTupleIterator iterator = new DefaultRulesExecutor.CrossProductFactTupleIterator(facts);

        List<String> result = new ArrayList<>();

        // when
        while (iterator.hasNext()) {
            Map<String, Object> item = iterator.next();
            result.add(format("%s", item.get("a")));
        }

        // then
        assertThat(result).containsExactly(
                "A1",
                "A2",
                "A3"
        );
    }

    @Test
    void shouldProperlyIterateThroughGroupOfFacts() {
        // given
        Map<String, List<Object>> facts = new HashMap<>();
        facts.put("a", asList(new MyFact("A1"), new MyFact("A2"), new MyFact("A3")));
        facts.put("b", asList(new MyFact("B1"), new MyFact("B2")));
        facts.put("c", asList(new MyFact("C1")));
        facts.put("d", asList(new MyFact("D1"), new MyFact("D2")));

        DefaultRulesExecutor.CrossProductFactTupleIterator iterator = new DefaultRulesExecutor.CrossProductFactTupleIterator(facts);

        List<String> result = new ArrayList<>();

        // when
        while (iterator.hasNext()) {
            Map<String, Object> item = iterator.next();
            result.add(format("%s:%s:%s:%s", item.get("a"), item.get("b"), item.get("c"), item.get("d")));
        }

        // then
        assertThat(result).containsExactly(
                "A1:B1:C1:D1",
                "A2:B1:C1:D1",
                "A3:B1:C1:D1",
                "A1:B2:C1:D1",
                "A2:B2:C1:D1",
                "A3:B2:C1:D1",
                "A1:B1:C1:D2",
                "A2:B1:C1:D2",
                "A3:B1:C1:D2",
                "A1:B2:C1:D2",
                "A2:B2:C1:D2",
                "A3:B2:C1:D2"
        );
    }

    private static class MyFact {
        private final String id;

        MyFact(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return id;
        }
    }
}
