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
import java.util.List;
import java.util.Map;
import java.util.Set;

public enum TypeAlias {
    OBJECT("Object", Object.class),
    STRING("String", String.class),
    INTEGER("Integer", Integer.class),
    LONG("Long", Long.class),
    DOUBLE("Double", Double.class),
    BOOLEAN("Boolean", Boolean.class),
    BYTE("Byte", Byte.class),
    SHORT("Short", Short.class),
    CHARACTER("Character", Character.class),
    FLOAT("Float", Float.class),

    P_INT("int", int.class),
    P_LONG("long", long.class),
    P_DOUBLE("double", double.class),
    P_BOOLEAN("boolean", boolean.class),
    P_BYTE("byte", byte.class),
    P_SHORT("short", short.class),
    P_CHAR("char", char.class),
    P_FLOAT("float", float.class),

    ZONED_DATE_TIME("ZonedDateTime", ZonedDateTime.class),

    LIST("List", List.class),
    MAP("Map", Map.class),
    SET("Set", Set.class);

    private final String name;

    private final Type type;

    TypeAlias(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getAlias() {
        return name;
    }

    public Type getType() {
        return type;
    }

}
