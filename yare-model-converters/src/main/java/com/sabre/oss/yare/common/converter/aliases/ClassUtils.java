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

package com.sabre.oss.yare.common.converter.aliases;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

final class ClassUtils {
    private static final Map<String, Class<?>> primitiveTypes =
            new HashMap<String, Class<?>>() {
                {
                    put("int", Integer.TYPE);
                    put("long", Long.TYPE);
                    put("double", Double.TYPE);
                    put("float", Float.TYPE);
                    put("boolean", Boolean.TYPE);
                    put("char", Character.TYPE);
                    put("byte", Byte.TYPE);
                    put("void", Void.TYPE);
                    put("short", Short.TYPE);
                }
            };

    private ClassUtils() {
    }

    static Class<?> forName(String name) {
        return Stream.of(
                tryResolveTypeFor(name),
                tryResolvePrimitiveTypeFor(name))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Could not resolve type for name: %s", name)));

    }

    private static Optional<Class<?>> tryResolveTypeFor(String name) {
        try {
            return Optional.of(Class.forName(name));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    private static Optional<Class<?>> tryResolvePrimitiveTypeFor(String name) {
        return Optional.ofNullable(primitiveTypes.get(name));
    }
}
