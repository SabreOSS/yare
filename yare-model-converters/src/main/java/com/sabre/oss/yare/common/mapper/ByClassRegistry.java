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
 *
 */

package com.sabre.oss.yare.common.mapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ByClassRegistry<C extends Class<?>, E> {
    private final Map<C, E> registry = new ConcurrentHashMap<>();

    public void add(C clazz, E element) {
        boolean isSuperclassRegistered = registry.entrySet().stream()
                .map(Map.Entry::getKey)
                .anyMatch(e -> e.isAssignableFrom(clazz));
        if (isSuperclassRegistered) {
            throw new IllegalArgumentException(String.format("Superclass of %s is already registered", clazz.getName()));
        }

        registry.put(clazz, element);
    }

    public E get(C clazz) {
        E element = registry.get(clazz);
        if (element != null) {
            return element;
        }
        return registry.entrySet().stream()
                .filter(entry -> entry.getKey().isAssignableFrom(clazz))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }
}
