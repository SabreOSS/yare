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

import com.sabre.oss.yare.core.model.Expression;
import org.assertj.core.data.MapEntry;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class TypeAliasesTest {
    private TypeAliases typeAliases = new TypeAliases();

    @Test
    void shouldInitializeMapOfAliasesByNameProperly() {
        //given /when /then
        assertThat(typeAliases.getAliasesMappedByName().entrySet())
                .containsExactlyInAnyOrder(
                        getEntryWithNameKey("Object", Object.class),
                        getEntryWithNameKey("String", String.class),
                        getEntryWithNameKey("Integer", Integer.class),
                        getEntryWithNameKey("Long", Long.class),
                        getEntryWithNameKey("Double", Double.class),
                        getEntryWithNameKey("Boolean", Boolean.class),
                        getEntryWithNameKey("Byte", Byte.class),
                        getEntryWithNameKey("Short", Short.class),
                        getEntryWithNameKey("Character", Character.class),
                        getEntryWithNameKey("Float", Float.class),
                        getEntryWithNameKey("Void", Void.class),
                        getEntryWithNameKey("int", int.class),
                        getEntryWithNameKey("long", long.class),
                        getEntryWithNameKey("double", double.class),
                        getEntryWithNameKey("boolean", boolean.class),
                        getEntryWithNameKey("byte", byte.class),
                        getEntryWithNameKey("short", short.class),
                        getEntryWithNameKey("char", char.class),
                        getEntryWithNameKey("float", float.class),
                        getEntryWithNameKey("void", void.class),
                        getEntryWithNameKey("ZonedDateTime", ZonedDateTime.class),
                        getEntryWithNameKey("BigDecimal", BigDecimal.class),
                        getEntryWithNameKey("List", List.class),
                        getEntryWithNameKey("Map", Map.class),
                        getEntryWithNameKey("Set", Set.class),
                        getEntryWithNameKey("Expression.Undefined", Expression.UNDEFINED));
    }

    @Test
    void shouldInitializeMapOfAliasesByTypeProperly() {
        //given /when /then
        assertThat(typeAliases.getAliasesMappedByType().entrySet())
                .containsExactlyInAnyOrder(
                        getEntryWithTypeKey("Object", Object.class),
                        getEntryWithTypeKey("String", String.class),
                        getEntryWithTypeKey("Integer", Integer.class),
                        getEntryWithTypeKey("Long", Long.class),
                        getEntryWithTypeKey("Double", Double.class),
                        getEntryWithTypeKey("Boolean", Boolean.class),
                        getEntryWithTypeKey("Byte", Byte.class),
                        getEntryWithTypeKey("Short", Short.class),
                        getEntryWithTypeKey("Character", Character.class),
                        getEntryWithTypeKey("Float", Float.class),
                        getEntryWithTypeKey("Void", Void.class),
                        getEntryWithTypeKey("int", int.class),
                        getEntryWithTypeKey("long", long.class),
                        getEntryWithTypeKey("double", double.class),
                        getEntryWithTypeKey("boolean", boolean.class),
                        getEntryWithTypeKey("byte", byte.class),
                        getEntryWithTypeKey("short", short.class),
                        getEntryWithTypeKey("char", char.class),
                        getEntryWithTypeKey("float", float.class),
                        getEntryWithTypeKey("void", void.class),
                        getEntryWithTypeKey("ZonedDateTime", ZonedDateTime.class),
                        getEntryWithTypeKey("BigDecimal", BigDecimal.class),
                        getEntryWithTypeKey("List", List.class),
                        getEntryWithTypeKey("Map", Map.class),
                        getEntryWithTypeKey("Set", Set.class),
                        getEntryWithTypeKey("Expression.Undefined", Expression.UNDEFINED));
    }

    private MapEntry<String, TypeAlias> getEntryWithNameKey(String typeName, Class<?> type) {
        return entry(typeName, TypeAlias.of(typeName, type));
    }

    private MapEntry<Type, TypeAlias> getEntryWithTypeKey(String typeName, Class<?> type) {
        return entry(type, TypeAlias.of(typeName, type));
    }
}
