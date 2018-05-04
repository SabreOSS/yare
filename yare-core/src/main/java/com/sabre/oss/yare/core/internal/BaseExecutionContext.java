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

package com.sabre.oss.yare.core.internal;

import com.sabre.oss.yare.core.ExecutionContext;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class BaseExecutionContext implements ExecutionContext {
    private Map<String, Object> context = new LinkedHashMap<>();

    @Override
    public <T> boolean contains(Key<T> key) {
        return context.containsKey(key.toString());
    }

    @Override
    public <T> T get(Key<T> key) throws IllegalStateException {
        if (context.containsKey(key.toString())) {
            return (T) context.get(key.toString());
        }
        throw new IllegalStateException("No value for key " + key.toString());
    }

    @Override
    public <T> T get(Key<T> key, T defaultValue) {
        return context.containsKey(key.toString())
                ? (T) context.get(key.toString())
                : defaultValue;
    }

    @Override
    public <T> T put(Key<T> key, T value) {
        return (T) context.put(key.toString(), value);
    }

    @Override
    public <T> T remove(Key<T> key) {
        return (T) context.remove(key.toString());
    }
}
