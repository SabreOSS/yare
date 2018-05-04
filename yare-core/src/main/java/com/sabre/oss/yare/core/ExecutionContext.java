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

import com.sabre.oss.yare.core.internal.BaseExecutionContext;

import static java.util.Objects.requireNonNull;

/**
 * Execution context.
 */
public interface ExecutionContext {

    static ExecutionContext create() {
        return new BaseExecutionContext();
    }

    /**
     * Checks if context contains value for given {@code key}
     *
     * @param key key
     * @param <T> type of value
     * @return true if context contains value for given key, false otherwise
     */
    <T> boolean contains(Key<T> key);

    /**
     * Returns value for given key
     *
     * @param key key
     * @param <T> type of value
     * @return value for given key (can be null) or {@link IllegalStateException} if there is no value assigned to the key
     */
    <T> T get(Key<T> key) throws IllegalStateException;

    /**
     * Returns value for given key or default value
     *
     * @param key          key
     * @param defaultValue default value
     * @param <T>          type of value
     * @return value for given key (can be null) or {@code defaultValue} if there is no value assigned to the key
     */
    <T> T get(Key<T> key, T defaultValue);

    /**
     * Puts value for given key into context
     *
     * @param key   key
     * @param value value
     * @param <T>   type of value
     * @return returns value previously assigned to the key
     */
    <T> T put(Key<T> key, T value);

    /**
     * Removes value for given key from context
     *
     * @param key key
     * @param <T> type of value
     * @return removed value
     */
    <T> T remove(Key<T> key);

    class Key<T> {
        private final String key;

        public Key(String key) {
            this.key = requireNonNull(key, "key must not be null");
        }

        public static <T> Key<T> create(String key) {
            return new Key<>(key);
        }

        public static <T> Key<T> create(Class<?> clazz, String suffix) {
            return create(requireNonNull(clazz, "clazz must not be null").getCanonicalName() + "." + suffix);
        }

        public String toString() {
            return key;
        }
    }
}

