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

package com.sabre.oss.yare.engine;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sabre.oss.yare.engine.executor.ExecutorConfiguration;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class DefaultCallInvocationResultCache implements CallInvocationResultCache {
    private final ConcurrentMap<String, LoadingCache<ObjectsWrapper, Optional<Object>>> cachesPerFunction = new ConcurrentHashMap<>();
    private final ExecutorConfiguration configuration;

    public DefaultCallInvocationResultCache(ExecutorConfiguration configuration) {
        this.configuration = configuration;
    }

    public <T> T get(String callName, Object[] args, Function<Object[], T> compute) {
        LoadingCache<ObjectsWrapper, Optional<Object>> functionResultCache = cachesPerFunction.get(callName);
        if (functionResultCache == null) {
            functionResultCache = CacheBuilder.<ObjectsWrapper, Optional<Object>>newBuilder()
                    .expireAfterAccess(configuration.getFunctionCacheExpirationTime(callName).toMillis(), TimeUnit.MILLISECONDS)
                    .build(new CacheLoader<ObjectsWrapper, Optional<Object>>() {
                        @Override
                        public Optional<Object> load(ObjectsWrapper arguments) {
                            return Optional.ofNullable((compute.apply(arguments.getObjects())));
                        }
                    });
            cachesPerFunction.putIfAbsent(callName, functionResultCache);
        }
        return (T) functionResultCache.getUnchecked(new ObjectsWrapper(args)).orElse(null);
    }

    private static final class ObjectsWrapper {
        private final Object[] objects;

        private ObjectsWrapper(Object[] objects) {
            this.objects = objects;
        }

        private Object[] getObjects() {
            return objects;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ObjectsWrapper that = (ObjectsWrapper) o;
            return Arrays.equals(objects, that.objects);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(objects);
        }
    }
}
