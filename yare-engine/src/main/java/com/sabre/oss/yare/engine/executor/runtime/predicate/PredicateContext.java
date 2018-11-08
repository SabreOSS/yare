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

import com.sabre.oss.yare.core.EngineController;
import com.sabre.oss.yare.core.call.ProcessingContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Predicate evaluation context.
 */
public class PredicateContext implements ProcessingContext {
    private final String ruleId;
    private final Object result;
    private final Map<String, Object> facts;
    private final Map<String, Object> attributes;
    private final EngineController engineController;
    private final Map<String, Object> reservedIdentifiers;

    // Do not pass merged maps due to performance implications.
    public PredicateContext(String ruleId, Object result, Map<String, Object> facts, Map<String, Object> attributes, EngineController engineController) {
        this.ruleId = ruleId;
        this.result = result;
        this.facts = facts;
        this.attributes = attributes;
        this.engineController = engineController;
        this.reservedIdentifiers = createResolveIdentifiers();
    }

    private Map<String, Object> createResolveIdentifiers() {
        Map<String, Object> reservedIdentifiers = new HashMap<>();
        reservedIdentifiers.put("ctx", result);
        reservedIdentifiers.put("ruleName", ruleId);
        reservedIdentifiers.put("engineController", engineController);

        return reservedIdentifiers;
    }

    @Override
    public String getRuleId() {
        return ruleId;
    }

    @Override
    public Object getResult() {
        return result;
    }

    @Override
    public Object resolve(String identifier) {
        return reservedIdentifiers.getOrDefault(identifier, attributes.getOrDefault(identifier, facts.get(identifier)));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T resolve(String identifier, T defaultValue) {
        return (T) attributes.getOrDefault(identifier, facts.getOrDefault(identifier, defaultValue));
    }

    public PredicateContext copy(String ruleId) {
        return new PredicateContext(ruleId, result, facts, attributes, engineController);
    }
}
