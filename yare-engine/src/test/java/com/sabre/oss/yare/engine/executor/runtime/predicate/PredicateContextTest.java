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

package com.sabre.oss.yare.engine.executor.runtime.predicate;

import com.google.common.collect.ImmutableMap;
import com.sabre.oss.yare.core.EngineController;
import com.sabre.oss.yare.core.internal.DefaultEngineController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PredicateContextTest {

    @Test
    void shouldResolveCtxToResult() {
        // given
        Object expected = new Object();
        PredicateContext predicateContext = new PredicateContext(null, expected, Collections.emptyMap(), Collections.emptyMap(), null);
        String identifier = "ctx";

        // when
        Object resolved = predicateContext.resolve(identifier);

        // then
        assertThat(resolved).isEqualTo(expected);
    }

    @Test
    void shouldResolveRuleNameToRuleId() {
        // given
        String ruleId = "testRuleId";
        PredicateContext predicateContext = new PredicateContext(ruleId, null, Collections.emptyMap(), Collections.emptyMap(), null);
        String identifier = "ruleName";

        // when
        Object resolved = predicateContext.resolve(identifier);

        // then
        assertThat(resolved).isEqualTo(ruleId);
    }

    @Test
    void shouldResolveEngineControllerToEngineController() {
        // given
        EngineController engineController = new DefaultEngineController();
        PredicateContext predicateContext = new PredicateContext(null, null, Collections.emptyMap(), Collections.emptyMap(), engineController);
        String identifier = "engineController";

        // when
        Object resolved = predicateContext.resolve(identifier);

        // then
        assertThat(resolved).isEqualTo(engineController);
    }

    @Test
    void shouldResolveToAttributeValue() {
        // given
        Map<String, Object> attributes = Collections.singletonMap("key", "attributeValue");
        Map<String, Object> facts = Collections.singletonMap("key", "factValue");
        PredicateContext predicateContext = new PredicateContext(null, null, facts, attributes, null);
        String identifier = "key";

        // when
        Object resolved = predicateContext.resolve(identifier);

        // then
        assertThat(resolved).isEqualTo("attributeValue");
    }

    @Test
    void shouldResolveToFactValue() {
        // given
        Map<String, Object> attributes = Collections.singletonMap("attributeKey", "attributeValue");
        Map<String, Object> facts = Collections.singletonMap("factKey", "factValue");
        PredicateContext predicateContext = new PredicateContext(null, null, facts, attributes, null);
        String identifier = "factKey";

        // when
        Object resolved = predicateContext.resolve(identifier);

        // then
        assertThat(resolved).isEqualTo("factValue");
    }

    @Test
    void shouldResolveToNullWhenUnknownIdentifier() {
        // given
        Map<String, Object> attributes = Collections.singletonMap("attributeKey", "attributeValue");
        Map<String, Object> facts = Collections.singletonMap("factKey", "factValue");
        PredicateContext predicateContext = new PredicateContext("ruleId", "result", facts, attributes, new DefaultEngineController());
        String identifier = "unknownIdentifier";

        // when
        Object resolved = predicateContext.resolve(identifier);

        // then
        assertThat(resolved).isNull();
    }

    @ParameterizedTest(name = "{index} => identifier={0}, expectedValue={1}")
    @CsvSource({
            "key, attributeValue",
            "factKey, factValue",
            "unknownIdentifier, defaultValue"
    })
    void shouldResolveAttributeOrFactOrDefaultValue(String identifier, String expectedValue) {
        // given
        Map<String, Object> attributes = Collections.singletonMap("key", "attributeValue");
        Map<String, Object> facts = ImmutableMap.of("key", "factValue", "factKey", "factValue");
        PredicateContext predicateContext = new PredicateContext("ruleId", "result", facts, attributes, new DefaultEngineController());

        // when
        String resolved = predicateContext.resolve(identifier, "defaultValue");

        // then
        assertThat(resolved).isEqualTo(expectedValue);
    }

}
