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

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TypeAliasResolver {

    public boolean hasAliasFor(Type type) {
        return resolveAliasFor(type)
                .isPresent();
    }

    public boolean hasAliasFor(String name) {
        return resolveAliasFor(name)
                .isPresent();
    }

    public TypeAlias getAliasFor(Type type) {
        return resolveAliasFor(type)
                .orElseThrow(() -> {
                    String message = String.format("Could not find type alias for given type: %s", type);
                    return new IllegalArgumentException(message);
                });
    }

    public TypeAlias getAliasFor(String name) {
        return resolveAliasFor(name)
                .orElseThrow(() -> {
                    String message = String.format("Could not find type alias for given name: %s", name);
                    return new IllegalArgumentException(message);
                });
    }

    private Optional<TypeAlias> resolveAliasFor(Type type) {
        return resolveAliasThat(a -> a.getType().equals(type));
    }

    private Optional<TypeAlias> resolveAliasFor(String name) {
        return resolveAliasThat(a -> a.getAlias().equals(name));
    }

    private Optional<TypeAlias> resolveAliasThat(Predicate<TypeAlias> predicate) {
        return Stream.of(TypeAlias.values())
                .filter(predicate)
                .findAny();
    }

}
