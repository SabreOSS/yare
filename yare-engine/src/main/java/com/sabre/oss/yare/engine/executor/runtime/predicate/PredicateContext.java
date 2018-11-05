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
import com.google.common.collect.Maps;
import com.sabre.oss.yare.core.EngineController;
import com.sabre.oss.yare.core.call.ProcessingContext;

import java.util.Arrays;
import java.util.EnumMap;
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
    private final EnumMap<ReservedIdentifier, Object> reservedIdentifiers;

    // Do not pass merged maps due to performance implications.
    public PredicateContext(String ruleId, Object result, Map<String, Object> facts, Map<String, Object> attributes, EngineController engineController) {
        this.ruleId = ruleId;
        this.result = result;
        this.facts = facts;
        this.attributes = attributes;
        this.engineController = engineController;
        this.reservedIdentifiers = createResolveIdentifiers();
    }

    private EnumMap<ReservedIdentifier, Object> createResolveIdentifiers() {
        EnumMap<ReservedIdentifier, Object> reservedIdentifiers = new EnumMap<>(ReservedIdentifier.class);

        for (ReservedIdentifier identifier : ReservedIdentifier.values()) {
            switch (identifier) {
                case CTX:
                    reservedIdentifiers.put(identifier, result);
                    break;
                case RULE_NAME:
                    reservedIdentifiers.put(identifier, ruleId);
                    break;
                case ENGINE_CONTROLLER:
                    reservedIdentifiers.put(identifier, engineController);
                    break;
                default:
                    throw new IllegalStateException(String.format("Unknown case: %s", identifier));
            }
        }

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
        ReservedIdentifier reservedIdentifier = ReservedIdentifier.fromString(identifier);
        return reservedIdentifiers.getOrDefault(reservedIdentifier,
                attributes.getOrDefault(identifier, facts.get(identifier)));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T resolve(String identifier, T defaultValue) {
        return (T) attributes.getOrDefault(identifier, facts.getOrDefault(identifier, defaultValue));
    }

    private enum ReservedIdentifier {
        CTX("ctx"), ENGINE_CONTROLLER("engineController"), RULE_NAME("ruleName");

        private static ImmutableMap<String, ReservedIdentifier> reverseLookup =
                Maps.uniqueIndex(Arrays.asList(ReservedIdentifier.values()), ReservedIdentifier::getIdentifier);

        private final String identifier;

        ReservedIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getIdentifier() {
            return identifier;
        }

        public static ReservedIdentifier fromString(String identifier) {
            return reverseLookup.getOrDefault(identifier, null);
        }

        @Override
        public String toString() {
            return "Identifier='" + identifier + '\'';
        }
    }

    public PredicateContext copy(String ruleId) {
        return new PredicateContext(ruleId, result, facts, attributes, engineController);
    }
}
