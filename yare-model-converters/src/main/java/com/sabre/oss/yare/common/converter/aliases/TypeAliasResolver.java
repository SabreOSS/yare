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

public class TypeAliasResolver {
    public boolean hasAliasFor(Type type) {
        return TypeAliases.mapAliasesByType().containsKey(type);
    }

    public boolean hasAliasFor(String name) {
        return TypeAliases.mapAliasesByName().containsKey(name);
    }

    public TypeAlias getAliasFor(Type type) {
        TypeAlias alias = TypeAliases.mapAliasesByType().get(type);
        if (alias == null) {
            String message = String.format("Could not find type alias for given type: %s", type);
            throw new IllegalArgumentException(message);
        }
        return alias;
    }

    public TypeAlias getAliasFor(String name) {
        TypeAlias alias = TypeAliases.mapAliasesByName().get(name);
        if (alias == null) {
            String message = String.format("Could not find type alias for given name: %s", name);
            throw new IllegalArgumentException(message);
        }
        return alias;
    }
}
