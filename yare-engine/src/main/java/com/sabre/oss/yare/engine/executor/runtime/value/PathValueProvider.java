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

package com.sabre.oss.yare.engine.executor.runtime.value;

import com.sabre.oss.yare.engine.executor.runtime.predicate.PredicateContext;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.Arrays.stream;
import static org.apache.commons.lang3.Validate.notEmpty;

public class PathValueProvider extends ValueProvider {
    private final Pattern splitPattern = Pattern.compile("\\.");

    private final String reference;
    private final String path;
    private final MethodHandle[] handles;

    public PathValueProvider(Class<?> type, String reference, String path) {
        this.reference = notEmpty(reference);
        this.path = notEmpty(path);
        this.handles = resolveMethodHandles(type, path);
    }

    @Override
    public Object get(PredicateContext context) {
        Object result = context.resolve(reference);
        try {
            for (MethodHandle handle : handles) {
                result = handle.bindTo(result).invoke();
            }
        } catch (Throwable throwable) {
            throw new IllegalStateException(String.format("Can't evaluate path '%s.%s'", reference, path), throwable);
        }
        return result;
    }

    private MethodHandle[] resolveMethodHandles(Class<?> type, String path) {
        String[] parts = splitPattern.split(path);
        List<MethodHandle> handles = new ArrayList<>(parts.length);
        Type currType = type;
        for (String part : parts) {
            handles.add(resolveMethod(currType, part));
            currType = resolveType(currType, part);
        }
        return handles.toArray(new MethodHandle[handles.size()]);
    }

    private MethodHandle resolveMethod(Type type, String part) {
        try {
            return MethodHandles.publicLookup().unreflect(findGetter(type, part));
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Can't build path based value provider", e);
        }
    }

    private Type resolveType(Type currType, String part) {
        return findGetter(currType, part).getReturnType();
    }

    private Method findGetter(Type type, String propertyName) {
        Class<?> clazz = (Class<?>) type;
        String getterName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        return stream(clazz.getMethods())
                .filter(m -> m.getName().equals(getterName) && m.getParameterCount() == 0)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unable to find getter on %s for property '%s' [expected getter name: '%s']", clazz, propertyName, getterName)));
    }
}
