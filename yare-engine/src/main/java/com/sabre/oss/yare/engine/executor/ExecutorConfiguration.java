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

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ExecutorConfiguration {
    private final Map<String, Boolean> functionToCacheable;
    private final Map<String, Duration> functionToCacheExpirationTime;
    private final boolean defaultFunctionCacheable;
    private final Duration defaultFunctionCacheExpirationTime;
    private final Duration rulesCacheRefreshTime;
    private final boolean sequentialMode;
    private final boolean crossProductMode;

    public ExecutorConfiguration(Builder builder) {
        this.functionToCacheable = Collections.unmodifiableMap(builder.functionToCacheable);
        this.functionToCacheExpirationTime = Collections.unmodifiableMap(builder.functionToCacheExpirationTime);
        this.defaultFunctionCacheable = builder.defaultFunctionCacheable;
        this.defaultFunctionCacheExpirationTime = builder.defaultFunctionCacheExpirationTime;
        this.rulesCacheRefreshTime = builder.rulesCacheRefreshTime;
        this.sequentialMode = builder.sequentialMode;
        this.crossProductMode = builder.crossProductMode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isFunctionCacheable(String functionName) {
        return functionToCacheable.getOrDefault(functionName, defaultFunctionCacheable);
    }

    public Duration getFunctionCacheExpirationTime(String functionName) {
        return functionToCacheExpirationTime.getOrDefault(functionName, defaultFunctionCacheExpirationTime);
    }

    public Duration getRulesCacheRefreshTime() {
        return rulesCacheRefreshTime;
    }

    public boolean isSequentialMode() {
        return sequentialMode;
    }

    public boolean isCrossProductMode() {
        return crossProductMode;
    }

    public static final class Builder {
        private Map<String, Boolean> functionToCacheable = new HashMap<>();
        private Map<String, Duration> functionToCacheExpirationTime = new HashMap<>();
        private boolean defaultFunctionCacheable = false;
        private Duration defaultFunctionCacheExpirationTime = Duration.ofMinutes(5);
        private Duration rulesCacheRefreshTime = Duration.ofMinutes(5);
        private boolean sequentialMode = false;
        private boolean crossProductMode = false;

        private Builder() {
        }

        public Builder withFunctionCacheable(String functionName, boolean shouldCache) {
            functionToCacheable.put(functionName, shouldCache);
            return this;
        }

        public Builder withFunctionCacheExpirationTime(String functionName, Duration expirationTime) {
            functionToCacheExpirationTime.put(functionName, expirationTime);
            return this;
        }

        public Builder withDefaultFunctionCacheable(boolean functionResultCacheable) {
            this.defaultFunctionCacheable = functionResultCacheable;
            return this;
        }

        public Builder withDefaultFunctionCacheExpirationTime(Duration defaultFunctionCacheExpirationTime) {
            this.defaultFunctionCacheExpirationTime = defaultFunctionCacheExpirationTime;
            return this;
        }

        public Builder withRulesCacheRefreshTime(Duration rulesCacheRefreshTime) {
            this.rulesCacheRefreshTime = rulesCacheRefreshTime;
            return this;
        }

        public Builder withSequentialMode(boolean sequentialMode) {
            this.sequentialMode = sequentialMode;
            return this;
        }

        public Builder withCrossProductMode(boolean crossProductMode) {
            this.crossProductMode = crossProductMode;
            return this;
        }

        public ExecutorConfiguration build() {
            return new ExecutorConfiguration(this);
        }
    }
}
