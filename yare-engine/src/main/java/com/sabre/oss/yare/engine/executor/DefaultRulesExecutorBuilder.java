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

import com.sabre.oss.yare.core.*;
import com.sabre.oss.yare.core.call.CallMetadata;
import com.sabre.oss.yare.core.call.ConsequenceFactory;
import com.sabre.oss.yare.core.call.FunctionFactory;
import com.sabre.oss.yare.core.call.ProcessingInvocationFactory;
import com.sabre.oss.yare.core.feature.FeaturedObject;
import com.sabre.oss.yare.engine.*;
import com.sabre.oss.yare.engine.feature.DefaultEngineFeature;
import org.apache.commons.lang3.ArrayUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultRulesExecutorBuilder implements RulesExecutorBuilder {
    private ExecutorConfiguration.Builder configurationBuilder = ExecutorConfiguration.builder();
    private Map<String, CallMetadata> actionMappings = new HashMap<>();
    private Map<String, CallMetadata> functionMappings = new HashMap<>();
    private RulesRepository rulesRepository;
    private ErrorHandler errorHandler;
    private EngineController engineController;
    private CallInvocationResultCache invocationCache;

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultRulesExecutorBuilder withRulesRepository(RulesRepository rulesRepository) {
        this.rulesRepository = rulesRepository;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultRulesExecutorBuilder withActionMappings(Map<String, CallMetadata> actionMappings) {
        this.actionMappings.putAll(actionMappings);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultRulesExecutorBuilder withFunctionMappings(Map<String, FeaturedObject<CallMetadata>> functionMappings) {
        for (Map.Entry<String, FeaturedObject<CallMetadata>> entry : functionMappings.entrySet()) {
            String name = entry.getKey();
            FeaturedObject<CallMetadata> object = entry.getValue();
            boolean shouldCache = !ArrayUtils.contains(object.getFeatures(), DefaultEngineFeature.DISABLE_CACHE_FUNCTION_RESULT);
            this.functionMappings.put(name, object.getObject());
            withFunctionCacheable(name, shouldCache);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultRulesExecutorBuilder withErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RulesExecutorBuilder withEngineController(EngineController engineController) {
        this.engineController = engineController;
        return this;
    }

    /**
     * Specify {@link CallInvocationResultCache} used for caching results of functions.
     *
     * @param invocationCache invocation cache
     * @return this defaultRulesExecutorBuilder instance
     */
    public DefaultRulesExecutorBuilder withFunctionResultCache(CallInvocationResultCache invocationCache) {
        this.invocationCache = invocationCache;
        return this;
    }

    /**
     * Specify whether results of the function identified by {@code functionName} should be cached.
     *
     * @param functionName name of the function
     * @param shouldCache  whether result should be cached
     * @return this defaultRulesExecutorBuilder instance
     */
    public DefaultRulesExecutorBuilder withFunctionCacheable(String functionName, boolean shouldCache) {
        this.configurationBuilder.withFunctionCacheable(functionName, shouldCache);
        return this;
    }

    /**
     * Specify time to expire cache for function identified by {@code functionName}.
     *
     * @param functionName   name of the function
     * @param expirationTime expiration time
     * @return this defaultRulesExecutorBuilder instance
     */
    public DefaultRulesExecutorBuilder withFunctionCacheExpirationTime(String functionName, Duration expirationTime) {
        this.configurationBuilder.withFunctionCacheExpirationTime(functionName, expirationTime);
        return this;
    }

    /**
     * Indicate whether results of functions should be cached by default.
     *
     * @param shouldCache whether functions' results should be cached
     * @return this defaultRulesExecutorBuilder instance
     */
    public DefaultRulesExecutorBuilder withDefaultFunctionCacheable(boolean shouldCache) {
        this.configurationBuilder.withDefaultFunctionCacheable(shouldCache);
        return this;
    }

    /**
     * Specify default expiration time for functions' results cache.
     *
     * @param expirationTime default expiration time
     * @return this defaultRulesExecutorBuilder instance
     */
    public DefaultRulesExecutorBuilder withDefaultFunctionCacheExpirationTime(Duration expirationTime) {
        this.configurationBuilder.withDefaultFunctionCacheExpirationTime(expirationTime);
        return this;
    }

    /**
     * Specify refresh time for functions' results cache.
     *
     * @param rulesCacheRefreshTime default refresh time
     * @return this defaultRulesExecutorBuilder instance
     */
    public DefaultRulesExecutorBuilder withRulesCacheRefreshTime(Duration rulesCacheRefreshTime) {
        this.configurationBuilder.withRulesCacheRefreshTime(rulesCacheRefreshTime);
        return this;
    }

    /**
     * Specify whether {@link DefaultRulesExecutor} should evaluate rules sequentially.
     *
     * @param sequentialMode should work in sequential mode
     * @return this defaultRulesExecutorBuilder
     */
    public DefaultRulesExecutorBuilder withSequentialMode(boolean sequentialMode) {
        this.configurationBuilder.withSequentialMode(sequentialMode);
        return this;
    }

    /**
     * Specify whether {@link DefaultRulesExecutor} should create cross product when multiple types of facts is used
     * as an input.
     *
     * @param crossProductMode should work in cross product mode
     * @return this defaultRulesExecutorBuilder
     */
    public DefaultRulesExecutorBuilder withCrossProductMode(boolean crossProductMode) {
        this.configurationBuilder.withCrossProductMode(crossProductMode);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RulesExecutor build() {
        ExecutorConfiguration configuration = configurationBuilder.build();
        invocationCache = invocationCache != null ? invocationCache : new DefaultCallInvocationResultCache(configuration);
        AtomicReference<DefaultArgumentValueResolver> resolverReference = new AtomicReference<>();
        ProcessingInvocationFactory<Object> functionInvocationFactory = new CachingDelegatingProcessingInvocationFactory<>(
                new DefaultProcessingInvocationFactory<>(resolverReference::get, functionMappings),
                resolverReference::get,
                invocationCache,
                configuration
        );
        ProcessingInvocationFactory<Void> actionInvocationFactory = new DefaultProcessingInvocationFactory<>(resolverReference::get, actionMappings);
        resolverReference.set(new DefaultArgumentValueResolver(functionInvocationFactory));
        FunctionFactory functionFactory = new FunctionFactory(functionInvocationFactory);
        ConsequenceFactory consequenceFactory = new ConsequenceFactory(actionInvocationFactory, errorHandler);
        RuntimeRulesBuilder runtimeRulesBuilder = new RuntimeRulesBuilder(new DefaultPredicateFactory(), functionFactory, consequenceFactory);

        return new DefaultRulesExecutor(rulesRepository, runtimeRulesBuilder, configuration, engineController);
    }
}
