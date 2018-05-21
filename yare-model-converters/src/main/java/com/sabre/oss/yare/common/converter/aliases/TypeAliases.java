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
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class TypeAliases {
    private static final Map<String, TypeAlias> nameToAliasMap;
    private static final Map<Type, TypeAlias> typeToAliasMap;

    static {
        nameToAliasMap = new HashMap<>();
        typeToAliasMap = new HashMap<>();

        registerAlias("Object", Object.class);
        registerAlias("String", String.class);
        registerAlias("Integer", Integer.class);
        registerAlias("Long", Long.class);
        registerAlias("Double", Double.class);
        registerAlias("Boolean", Boolean.class);
        registerAlias("Byte", Byte.class);
        registerAlias("Short", Short.class);
        registerAlias("Character", Character.class);
        registerAlias("Float", Float.class);

        registerAlias("int", int.class);
        registerAlias("long", long.class);
        registerAlias("double", double.class);
        registerAlias("boolean", boolean.class);
        registerAlias("byte", byte.class);
        registerAlias("short", short.class);
        registerAlias("char", char.class);
        registerAlias("float", float.class);

        registerAlias("ZonedDateTime", ZonedDateTime.class);

        registerAlias("List", List.class);
        registerAlias("Map", Map.class);
        registerAlias("Set", Set.class);
    }

    private static void registerAlias(String name, Type type) {
        TypeAlias alias = TypeAlias.of(name, type);
        nameToAliasMap.put(name, alias);
        typeToAliasMap.put(type, alias);
    }

    private TypeAliases() {

    }

    static Map<String, TypeAlias> mapAliasesByName() {
        return nameToAliasMap;
    }

    static Map<Type, TypeAlias> mapAliasesByType() {
        return typeToAliasMap;
    }
}
