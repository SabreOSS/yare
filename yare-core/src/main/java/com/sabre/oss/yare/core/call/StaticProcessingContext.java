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

package com.sabre.oss.yare.core.call;

import java.beans.Introspector;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class StaticProcessingContext implements ProcessingContext {
    private final String ruleId;
    private final Object result;
    private final Map<String, Object> bindings;
    private final Object[] factTuple;

    public StaticProcessingContext(Map<String, Object> bindings, Object[] factTuple) {
        this.bindings = requireNonNull(bindings);
        this.factTuple = factTuple;

        this.result = requireNonNull(bindings.get(CONTEXT));
        this.ruleId = requireNonNull((String) bindings.get(CURRENT_RULE_NAME));
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
        Object value = bindings.get(identifier);
        if (Objects.isNull(value)) {
            for (Object fact : factTuple) {
                if (Introspector.decapitalize(fact.getClass().getSimpleName()).equals(identifier)) {
                    return fact;
                }
            }
        }
        if (Objects.isNull(value) && !bindings.containsKey(identifier)) {
            throw new IllegalArgumentException(String.format("Can't resolve reference '%s' [rule: %s]", identifier, ruleId));
        }
        return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T resolve(String identifier, T defaultValue) {
        Object value = bindings.get(identifier);
        if (Objects.isNull(value) && !bindings.containsKey(identifier)) {
            return defaultValue;
        }
        return (T) value;
    }
}
