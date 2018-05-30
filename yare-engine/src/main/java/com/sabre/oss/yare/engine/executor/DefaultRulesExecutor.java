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

import com.google.common.base.Stopwatch;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sabre.oss.yare.core.*;
import com.sabre.oss.yare.core.call.ProcessingContext;
import com.sabre.oss.yare.core.invocation.Invocation;
import com.sabre.oss.yare.core.management.EvictableCache;
import com.sabre.oss.yare.core.model.Attribute;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.engine.executor.runtime.predicate.PredicateContext;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class DefaultRulesExecutor implements RulesExecutor, Wrapper, EvictableCache {
    private static final Logger log = LoggerFactory.getLogger(DefaultRulesExecutor.class);

    private final Map<Class<?>, String> typeNames = new ConcurrentHashMap<>();
    private final LoadingCache<String, RuntimeRules> runtimeRulesCache;
    private final ExecutorConfiguration configuration;

    public DefaultRulesExecutor(RulesRepository rulesRepository, RuntimeRulesBuilder runtimeRulesBuilder, ExecutorConfiguration configuration) {
        this.configuration = configuration;
        this.runtimeRulesCache = buildCachingContext(rulesRepository, runtimeRulesBuilder);
    }

    @Override
    public ExecutionContext proceed(ExecutionContext context) {
        Object result = context.get(DefaultContextKey.RESULT);
        Collection<?> inFacts = context.get(DefaultContextKey.FACTS);

        String uri = context.get(DefaultContextKey.RULE_EXECUTION_SET_URI);
        AtomicReference<Object> evaluationContextHolder = context.get(DefaultContextKey.RULE_EXECUTION_SET);
        RuntimeRules runtimeRules = (RuntimeRules) evaluationContextHolder.get();
        if (runtimeRules == null) {
            runtimeRules = runtimeRulesCache.getUnchecked(uri);
            if (!evaluationContextHolder.compareAndSet(null, runtimeRules)) {
                runtimeRules = (RuntimeRules) evaluationContextHolder.get();
            }
        }

        if (runtimeRules == null) {
            throw new IllegalStateException(String.format("PlainJava RE could not load rules execution set %s", uri));
        }

        Map<String, List<Object>> groupedFact = groupFacts(inFacts, runtimeRules.getFactNames());
        Iterator<Map<String, Object>> iterator = groupedFact.size() == 1
                ? new SingleTypeFactTupleIterator(groupedFact)
                : configuration.isCrossProductMode() ? new CrossProductFactTupleIterator(groupedFact) : new SingleInstanceFactTupleIterator(groupedFact);

        if (configuration.isSequentialMode()) {
            while (iterator.hasNext()) {
                evaluateSequentially(runtimeRules, result, iterator.next());
            }
        } else {
            while (iterator.hasNext()) {
                evaluate(runtimeRules, result, iterator.next());
            }
        }
        return context;
    }

    @Override
    public <T> T unwrap(Class<T> expected) {
        return expected.isAssignableFrom(getClass())
                ? expected.cast(this)
                : null;
    }

    @Override
    public boolean evict(Object key) {
        runtimeRulesCache.invalidate(key);
        return true;
    }

    @Override
    public boolean clear() {
        runtimeRulesCache.invalidateAll();
        return true;
    }

    private Map<String, List<Object>> groupFacts(Collection<?> inFacts, Map<Type, String> factNames) {
        Map<Class<?>, List<Object>> facts = new HashMap<>();
        for (Object fact : inFacts) {
            List<Object> grouped = facts.computeIfAbsent(fact.getClass(), (k) -> new ArrayList<>());
            grouped.add(fact);
        }
        Map<String, List<Object>> result = new HashMap<>(facts.size());
        for (Entry<Class<?>, List<Object>> entry : facts.entrySet()) {
            result.put(typeNames.computeIfAbsent(entry.getKey(), factNames::get), entry.getValue());
        }
        return result;
    }

    private void evaluateSequentially(RuntimeRules runtimeRules, Object result, Map<String, Object> factMap) {
        for (RuntimeRules.ExecutableRule executableRule : runtimeRules.getExecutableRules()) {
            PredicateContext context = new PredicateContext(executableRule.getRuleId(), result, mergeReferenceMaps(factMap, executableRule.getAttributes()));
            Boolean evaluationResult = executableRule.getPredicate().evaluate(context);
            if (Boolean.TRUE.equals(evaluationResult)) {
                executableRule.getConsequence().proceed(context);
            }
        }
    }

    private void evaluate(RuntimeRules runtimeRules, Object result, Map<String, Object> factMap) {
        List<Pair<Invocation<ProcessingContext, Void>, PredicateContext>> consequences = new LinkedList<>();
        for (RuntimeRules.ExecutableRule executableRule : runtimeRules.getExecutableRules()) {
            PredicateContext context = new PredicateContext(executableRule.getRuleId(), result, mergeReferenceMaps(factMap, executableRule.getAttributes()));
            Boolean evaluationResult = executableRule.getPredicate().evaluate(context);
            if (Boolean.TRUE.equals(evaluationResult)) {
                consequences.add(Pair.of(executableRule.getConsequence(), context));
            }
        }

        for (Pair<Invocation<ProcessingContext, Void>, PredicateContext> consequence : consequences) {
            consequence.getKey().proceed(consequence.getValue());
        }
    }

    private Map<String, Object> mergeReferenceMaps(Map<String, Object> factMap, Map<String, Object> attributeMap) {
        Map<String, Object> referencesMap = new HashMap<>();
        referencesMap.putAll(factMap);
        referencesMap.putAll(attributeMap);
        return referencesMap;
    }

    private LoadingCache<String, RuntimeRules> buildCachingContext(RulesRepository rulesRepository, RuntimeRulesBuilder runtimeRulesBuilder) {
        return CacheBuilder.newBuilder()
                .refreshAfterWrite(configuration.getRulesCacheRefreshTime().toMillis(), TimeUnit.MILLISECONDS)
                .build(new CacheLoader<String, RuntimeRules>() {
                    @Override
                    public RuntimeRules load(String key) {
                        log.info("Loading rules base for {} ...", key);
                        Stopwatch stopwatch = Stopwatch.createStarted();

                        List<Rule> rules = rulesRepository.get(key).stream()
                                .filter(rule -> rule.getAttribute("ignored") == null || Boolean.FALSE.equals(rule.getAttribute("ignored").getValue()))
                                .sorted((a, b) -> {
                                    long priorityA = getPriority(a);
                                    long priorityB = getPriority(b);
                                    return Long.compare(priorityB, priorityA);
                                })
                                .collect(Collectors.toList());

                        RuntimeRules runtimeRules = runtimeRulesBuilder.build(rules);

                        log.info("Loaded rules '{}' in {} seconds", key, stopwatch.elapsed(TimeUnit.SECONDS));
                        return runtimeRules;
                    }
                });
    }

    private long getPriority(Rule a) {
        Attribute attribute = a.getAttribute("priority");
        return attribute != null ? (Long) attribute.getValue() : 0L;
    }

    static class SingleInstanceFactTupleIterator implements Iterator<Map<String, Object>> {
        private final Map<String, Object> factMap;
        private boolean end;

        SingleInstanceFactTupleIterator(Map<String, List<Object>> groupedFacts) {
            if (groupedFacts.values().stream().anyMatch(l -> l.size() > 1)) {
                log.warn("Multiple instances of {} fact type(s) found. First one will be used",
                        groupedFacts.keySet().stream()
                                .filter(e -> groupedFacts.get(e).size() > 1)
                                .collect(Collectors.joining(", ")));
            }
            for (Entry<String, List<Object>> entry : groupedFacts.entrySet()) {
                String key = entry.getKey();
                List<?> value = entry.getValue();

                if (value == null || value.isEmpty()) {
                    throw new IllegalArgumentException(String.format("No fact instances for identifier '%s'", key));
                }
            }
            factMap = groupedFacts.entrySet().stream()
                    .collect(Collectors.toMap(Entry::getKey, e -> e.getValue().get(0)));
            end = groupedFacts.isEmpty();
        }

        @Override
        public boolean hasNext() {
            return !end;
        }

        @Override
        public Map<String, Object> next() {
            end = true;
            return factMap;
        }
    }

    static class SingleTypeFactTupleIterator implements Iterator<Map<String, Object>> {
        private final String identifier;
        private final List<Object> instances;
        private final int size;
        private final Map<String, Object> factTuple = new HashMap<>(2);
        private int index = 0;
        private boolean end;

        SingleTypeFactTupleIterator(Map<String, List<Object>> groupedFacts) {
            Validate.isTrue(groupedFacts.size() == 1, "SingleTypeFactTupleIterator purpose is to support fact instances with the same type");
            Entry<String, List<Object>> entry = groupedFacts.entrySet().iterator().next();
            this.identifier = entry.getKey();
            this.instances = entry.getValue();
            if (instances == null || instances.isEmpty()) {
                throw new IllegalArgumentException(String.format("No fact instances for identifier '%s'", identifier));
            }
            this.size = this.instances.size();
            end = groupedFacts.isEmpty();
        }

        @Override
        public boolean hasNext() {
            return !end;
        }

        @Override
        public Map<String, Object> next() {
            int current = index;
            index = (index + 1) % size;
            if (index == 0) {
                end = true;
            }
            factTuple.put(identifier, instances.get(current));
            return factTuple;
        }
    }

    static class CrossProductFactTupleIterator implements Iterator<Map<String, Object>> {
        private final Map<String, List<Object>> groupedFact;
        private final int[] sizes;
        private final int[] values;
        private final String[] names;
        private int valuesSize;
        private int valuesIdx;

        private Map<String, Object> next;

        private boolean end;

        @SuppressWarnings("unchecked")
        CrossProductFactTupleIterator(Map<String, List<Object>> groupedFact) {

            this.groupedFact = groupedFact;

            this.valuesSize = groupedFact.keySet().size();
            this.valuesIdx = 0;

            this.sizes = new int[valuesSize];
            this.values = new int[valuesSize];
            this.names = new String[valuesSize];

            next = new HashMap<>(this.valuesSize);

            int idx = 0;
            for (Entry<String, List<Object>> entry : groupedFact.entrySet()) {
                String key = entry.getKey();
                List<?> value = entry.getValue();

                if (value == null || value.isEmpty()) {
                    throw new IllegalArgumentException(String.format("No fact instances for identifier '%s'", key));
                }

                names[idx] = key;
                sizes[idx] = value.size();
                next.put(key, value.get(0));
                idx++;
            }
            this.end = this.valuesSize == 0;
        }

        @Override
        public boolean hasNext() {
            return !end;
        }

        @Override
        public Map<String, Object> next() {
            if (!hasNext()) {
                throw new IllegalStateException("No next value!");
            }

            Map<String, Object> current = new HashMap<>(next);

            values[valuesIdx] = (values[valuesIdx] + 1) % sizes[valuesIdx];
            next.put(names[valuesIdx], groupedFact.get(names[valuesIdx]).get(values[valuesIdx]));

            while (values[valuesIdx] == 0) {
                valuesIdx = (valuesIdx + 1) % valuesSize;
                if (valuesIdx == 0) {
                    end = true;
                    break;
                }
                values[valuesIdx] = (values[valuesIdx] + 1) % sizes[valuesIdx];
                next.put(names[valuesIdx], groupedFact.get(names[valuesIdx]).get(values[valuesIdx]));
            }
            valuesIdx = 0;

            return current;
        }
    }
}
