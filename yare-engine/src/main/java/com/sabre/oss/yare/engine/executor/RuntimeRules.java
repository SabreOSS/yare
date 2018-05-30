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

import com.sabre.oss.yare.core.call.ProcessingContext;
import com.sabre.oss.yare.core.invocation.Invocation;
import com.sabre.oss.yare.engine.executor.runtime.predicate.Predicate;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class RuntimeRules {
    private final List<RuntimeRules.ExecutableRule> executableRules;
    private final Map<Type, String> factNames;

    public RuntimeRules(List<ExecutableRule> executableRules, Map<Type, String> factNames) {
        this.executableRules = executableRules;
        this.factNames = factNames;
    }

    public List<ExecutableRule> getExecutableRules() {
        return executableRules;
    }

    public Map<Type, String> getFactNames() {
        return factNames;
    }

    public static final class ExecutableRule {
        private final String ruleId;
        private final Predicate predicate;
        private final Invocation<ProcessingContext, Void> consequence;
        private final Map<String, Object> attributes;
        private final long order;

        private ExecutableRule(String ruleId, Map<String, Object> attributes, Predicate predicate, Invocation<ProcessingContext, Void> consequence, long order) {
            this.ruleId = ruleId;
            this.attributes = attributes;
            this.predicate = predicate;
            this.consequence = consequence;
            this.order = order;
        }

        public static ExecutableRule of(String ruleId, Map<String, Object> attributeMap, Predicate predicate, Invocation<ProcessingContext, Void> consequence, long order) {
            return new ExecutableRule(ruleId, attributeMap, predicate, consequence, order);
        }

        public String getRuleId() {
            return ruleId;
        }

        public Predicate getPredicate() {
            return predicate;
        }

        public Map<String, Object> getAttributes() {
            return attributes;
        }

        public Invocation<ProcessingContext, Void> getConsequence() {
            return consequence;
        }

        public long getOrder() {
            return order;
        }
    }
}
