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

package com.sabre.oss.yare.core;

import com.sabre.oss.yare.core.call.CallMetadata;
import com.sabre.oss.yare.core.feature.Feature;
import com.sabre.oss.yare.core.feature.FeaturedObject;
import com.sabre.oss.yare.core.interceptor.ErrorHandlerSetUpInterceptor;
import com.sabre.oss.yare.core.internal.DefaultEngineController;
import com.sabre.oss.yare.core.internal.DefaultRulesEngine;
import com.sabre.oss.yare.core.invocation.Interceptor;

import java.util.*;

/**
 * Configures and creates {@link RulesEngine} instance.
 */
public class RulesEngineBuilder {
    private static final String DEFAULT_RULES_EXECUTOR_BUILDER_CLASS = "com.sabre.oss.yare.engine.executor.DefaultRulesExecutorBuilder";

    private List<Interceptor<ExecutionContext, ExecutionContext>> interceptors = new ArrayList<>();
    private Map<String, CallMetadata> actionMappings = new HashMap<>();
    private Map<String, FeaturedObject<CallMetadata>> functionMappings = new HashMap<>();
    private RulesRepository rulesRepository;
    private RulesExecutorBuilder rulesExecutorBuilder;
    private ErrorHandler errorHandler;
    private EngineControllerObservable engineController;

    /**
     * Specify {@link RulesRepository} used by the rules engine.
     *
     * @param rulesRepository rules repository
     * @return this rulesEngineBuilder instance
     */
    public RulesEngineBuilder withRulesRepository(RulesRepository rulesRepository) {
        this.rulesRepository = rulesRepository;
        return this;
    }

    /**
     * Specify mapping for an action. {@code callMetadata} will be identified by {@code actionName} across rules engine.
     *
     * @param actionName   name of the action
     * @param callMetadata metadata of the action
     * @return this rulesEngineBuilder instance
     */
    public RulesEngineBuilder withActionMapping(String actionName, CallMetadata callMetadata) {
        this.actionMappings.put(actionName, callMetadata);
        return this;
    }

    /**
     * Specify mappings for actions.
     *
     * @param actionMappings mappings for actions
     * @return this rulesEngineBuilder instance
     */
    public RulesEngineBuilder withActionMappings(Map<String, CallMetadata> actionMappings) {
        this.actionMappings.putAll(actionMappings);
        return this;
    }

    /**
     * Specify mapping for a function. {@code callMetadata} will be identified by {@code functionName} across rules engine.
     *
     * @param functionName name of the function
     * @param callMetadata metadata of the function
     * @return this rulesEngineBuilder instance
     */
    public RulesEngineBuilder withFunctionMapping(String functionName, CallMetadata callMetadata, Feature... features) {
        this.functionMappings.put(functionName, new FeaturedObject<>(callMetadata, features));
        return this;
    }

    /**
     * Specify function mappings. Optionally you can pass {@link Feature}s.
     *
     * @param functionMappings mappings for functions
     * @param features         function mapping features
     * @return this rulesEngineBuilder instance
     */
    public RulesEngineBuilder withFunctionMappings(Map<String, CallMetadata> functionMappings, Feature... features) {
        for (Map.Entry<String, CallMetadata> entry : functionMappings.entrySet()) {
            this.functionMappings.put(entry.getKey(), new FeaturedObject<>(entry.getValue(), features));
        }
        return this;
    }

    /**
     * Specify {@link Interceptor} used in the rules engine i.e. {@link com.sabre.oss.yare.core.interceptor.InputOutputLogger}.
     *
     * @param interceptor interceptor
     * @return this rulesEngineBuilder instance
     */
    public RulesEngineBuilder withInterceptor(Interceptor<ExecutionContext, ExecutionContext> interceptor) {
        interceptors.add(interceptor);
        return this;
    }

    /**
     * Specify multiple {@link Interceptor}s used in the rules engine.
     *
     * @param interceptors interceptors list
     * @return this rulesEngineBuilder instance
     */
    public RulesEngineBuilder withInterceptors(List<Interceptor<ExecutionContext, ExecutionContext>> interceptors) {
        this.interceptors = interceptors;
        return this;
    }

    /**
     * Specify {@link RulesExecutorBuilder} used for creating {@link RulesExecutor}.
     *
     * @param rulesExecutorBuilder rules executor builder.
     * @return this rulesEngineBuilder instance
     */
    public RulesEngineBuilder withRulesExecutorBuilder(RulesExecutorBuilder rulesExecutorBuilder) {
        this.rulesExecutorBuilder = rulesExecutorBuilder;
        return this;
    }

    /**
     * Specify {@link ErrorHandler} used in the rules engine.
     *
     * @param errorHandler error handler
     * @return this rulesEngineBuilder instance
     */
    public RulesEngineBuilder withErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    /**
     * Specify {@link EngineControllerObservable} used by the rules engine.
     *
     * @param engineController engine controller
     * @return this rulesEngineBuilder instance
     */
    public RulesEngineBuilder withEngineControllerObservable(EngineControllerObservable engineController) {
        this.engineController = engineController;
        return this;
    }

    /**
     * Builds previously configured {@link RulesEngine} instance.
     *
     * @return rules engine instance
     */
    public RulesEngine build() {
        Objects.requireNonNull(rulesRepository, "rulesRepository cannot be bull");
        Objects.requireNonNull(interceptors, "interceptors cannot be bull");

        if (rulesExecutorBuilder == null) {
            rulesExecutorBuilder = createDefaultRulesExecutorBuilder();
        }

        if ( engineController == null ) {
            engineController = new DefaultEngineController();
        }

        List<Interceptor<ExecutionContext, ExecutionContext>> fixedInterceptors = new ArrayList<>(this.interceptors);
        fixedInterceptors.add(0, new ErrorHandlerSetUpInterceptor(errorHandler));

        RulesExecutor rulesExecutor = rulesExecutorBuilder
                .withRulesRepository(rulesRepository)
                .withActionMappings(actionMappings)
                .withFunctionMappings(functionMappings)
                .withErrorHandler(errorHandler)
                .withEngineControllerObservable(engineController)
                .build();
        return new DefaultRulesEngine(rulesExecutor, fixedInterceptors);
    }

    private RulesExecutorBuilder createDefaultRulesExecutorBuilder() {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Class<RulesExecutorBuilder> aClass = (Class<RulesExecutorBuilder>) classLoader.loadClass(DEFAULT_RULES_EXECUTOR_BUILDER_CLASS);
            return aClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("DefaultRulesExecutorBuilder is not on classpath. Please include com.sabre.oss.yare:yare-engine dependency.", e);
        }
    }
}
