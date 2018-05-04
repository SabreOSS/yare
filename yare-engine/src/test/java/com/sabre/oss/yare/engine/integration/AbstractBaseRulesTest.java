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

import com.sabre.oss.yare.core.RulesEngine;
import com.sabre.oss.yare.core.RulesEngineBuilder;
import com.sabre.oss.yare.engine.executor.DefaultRulesExecutorBuilder;

import static com.sabre.oss.yare.engine.MethodCallMetadata.method;
import static com.sabre.oss.yare.engine.integration.BaseRulesUtils.*;

abstract class AbstractBaseRulesTest {
    RulesEngine createRuleEngine(RulesExecutionConfig config) {
        Actions actions = new Actions();
        Functions functions = new Functions();
        return new RulesEngineBuilder()
                .withRulesRepository(i -> config.getRules())
                .withActionMapping("collect", method(actions, (a) -> a.collect(null, null, null, null)))
                .withActionMapping("collectMore", method(actions, (a) -> a.collect(null, null, null, null, null)))
                .withFunctionMapping(RETURN_PRIMITIVE_TRUE, method(functions, RETURN_PRIMITIVE_TRUE))
                .withFunctionMapping(RETURN_PRIMITIVE_FALSE, method(functions, RETURN_PRIMITIVE_FALSE))
                .withFunctionMapping(RETURN_WRAPPED_TRUE, method(functions, RETURN_WRAPPED_TRUE))
                .withFunctionMapping(RETURN_WRAPPED_FALSE, method(functions, RETURN_WRAPPED_FALSE))
                .withFunctionMapping(RETURN_ARGUMENT, method(functions, RETURN_ARGUMENT, Object.class))
                .withRulesExecutorBuilder(new DefaultRulesExecutorBuilder()
                        .withSequentialMode(config.isSequenceMode()))
                .build();
    }
}
