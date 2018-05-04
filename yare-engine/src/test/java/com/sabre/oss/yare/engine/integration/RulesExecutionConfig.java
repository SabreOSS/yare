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

import com.sabre.oss.yare.core.model.Rule;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

public final class RulesExecutionConfig {
    private final Class<?> inputType;
    private final List<Rule> rules;
    private final List<Class<?>> factTypes;
    private final List<Class<?>> types;
    private final boolean sequenceMode;

    private RulesExecutionConfig(Class<?> inputType, List<Rule> rules,
                                 List<Class<?>> factTypes,
                                 List<Class<?>> types, boolean sequenceMode) {
        this.inputType = inputType;
        this.rules = rules;
        this.factTypes = factTypes;
        this.types = types;
        this.sequenceMode = sequenceMode;
    }

    static RulesExecutionConfigBuilder builder() {
        return new RulesExecutionConfigBuilder();
    }

    public List<Rule> getRules() {
        return rules;
    }

    public boolean isSequenceMode() {
        return sequenceMode;
    }

    public static class RulesExecutionConfigBuilder {
        private Class<?> inputType;
        private List<Rule> rules = new ArrayList<>();
        private List<Class<?>> factTypes = new ArrayList<>();
        private List<Class<?>> types = new ArrayList<>();
        private boolean sequenceMode;

        public RulesExecutionConfigBuilder withInputType(Class<?> inputType) {
            this.inputType = requireNonNull(inputType);
            return this;
        }

        public RulesExecutionConfigBuilder withRules(List<Rule> rules) {
            this.rules.addAll(rules);
            return this;
        }

        public RulesExecutionConfigBuilder withFactTypes(List<Class<?>> factTypes) {
            this.factTypes.addAll(factTypes);
            return this;
        }

        public RulesExecutionConfigBuilder withAdditionalTypes(List<Class<?>> types) {
            this.types.addAll(types);
            return this;
        }

        public RulesExecutionConfigBuilder withSequenceMode(boolean sequenceMode) {
            this.sequenceMode = sequenceMode;
            return this;
        }

        public RulesExecutionConfig build() {
            return new RulesExecutionConfig(inputType,
                    unmodifiableList(rules),
                    unmodifiableList(factTypes),
                    unmodifiableList(types),
                    sequenceMode);
        }

    }

}
