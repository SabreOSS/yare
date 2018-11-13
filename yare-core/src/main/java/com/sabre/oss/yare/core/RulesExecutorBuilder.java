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
import com.sabre.oss.yare.core.feature.FeaturedObject;

import java.util.Map;

/**
 * Configures and creates {@link RulesExecutor} instance.
 */
public interface RulesExecutorBuilder {
    /**
     * Specify {@link RulesRepository} used by the rules executor.
     *
     * @param rulesRepository rules repository
     * @return this rulesExecutorBuilder instance
     */
    RulesExecutorBuilder withRulesRepository(RulesRepository rulesRepository);

    /**
     * Specify mappings for actions.
     *
     * @param actionMappings mappings for actions
     * @return this rulesExecutorBuilder instance
     */
    RulesExecutorBuilder withActionMappings(Map<String, CallMetadata> actionMappings);

    /**
     * Specify function mappings.
     *
     * @param functionsMapping mappings for functions
     * @return this rulesExecutorBuilder instance
     */
    RulesExecutorBuilder withFunctionMappings(Map<String, FeaturedObject<CallMetadata>> functionsMapping);

    /**
     * Specify {@link ErrorHandler} used in the rules executor.
     *
     * @param errorHandler error handler
     * @return this rulesExecutorBuilder instance
     */
    RulesExecutorBuilder withErrorHandler(ErrorHandler errorHandler);

    /**
     * Builds previously configured {@link RulesExecutor} instance.
     *
     * @return rules executor instance
     */
    RulesExecutor build();
}
