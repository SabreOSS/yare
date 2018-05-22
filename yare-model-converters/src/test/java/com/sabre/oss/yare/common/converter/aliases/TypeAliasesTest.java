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

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TypeAliasesTest {
    @Test
    void shouldInitializeTypeAliasesFromPropertiesFile() {
        //when
        TypeAliases typeAliases = new TypeAliases();

        //then
        assertThat(typeAliases.getAliasesMappedByName()).isNotEmpty();
        assertThat(typeAliases.getAliasesMappedByName())
                .containsOnlyKeys(
                        "Integer", "Long", "Double", "Boolean", "Byte",
                        "Short", "Character", "Float", "Void",
                        "int", "long", "double", "boolean", "byte",
                        "short", "char", "float", "void",
                        "Object", "String", "ZonedDateTime", "List", "Map", "Set");
        assertThat(typeAliases.getAliasesMappedByName().get("String"))
                .isEqualTo(TypeAlias.of("String", String.class));

        assertThat(typeAliases.getAliasesMappedByType()).isNotEmpty();
        assertThat(typeAliases.getAliasesMappedByType())
                .containsOnlyKeys(
                        Integer.class, Long.class, Double.class, Boolean.class, Byte.class,
                        Short.class, Character.class, Float.class, Void.class,
                        int.class, long.class, double.class, boolean.class, byte.class,
                        short.class, char.class, float.class, void.class,
                        Object.class, String.class, ZonedDateTime.class, List.class, Map.class, Set.class);
        assertThat(typeAliases.getAliasesMappedByType().get(String.class))
                .isEqualTo(TypeAlias.of("String", String.class));
    }
}
