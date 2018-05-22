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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TypeAliasResolverTest {
    private TypeAliasResolver resolver = new TypeAliasResolver();

    @Test
    void shouldResolveTypeAliasForGivenName() {
        //given
        String name = "String";

        //when
        TypeAlias alias = resolver.getAliasFor(name);

        //then
        assertThat(alias.getAlias()).isEqualTo("String");
        assertThat(alias.getType()).isEqualTo(String.class);
    }

    @Test
    void shouldResolveTypeAliasExistenceForGivenName() {
        //given
        String name = "String";

        //when
        Boolean hasAlias = resolver.hasAliasFor(name);

        //then
        assertThat(hasAlias).isTrue();
    }

    @Test
    void shouldResolveTypeAliasForGivenType() {
        //given
        Class<?> type = String.class;

        //when
        TypeAlias alias = resolver.getAliasFor(type);

        //then
        assertThat(alias.getAlias()).isEqualTo("String");
        assertThat(alias.getType()).isEqualTo(String.class);
    }

    @Test
    void shouldResolveTypeAliasExistenceForGivenType() {
        //given
        Class<?> type = String.class;

        //when
        Boolean hasAlias = resolver.hasAliasFor(type);

        //then
        assertThat(hasAlias).isTrue();
    }


    @Test
    void shouldThrowExceptionForUnknownName() {
        //given
        String unknownName = "UNKNOWN_NAME";

        //when /then
        String expectedMessage = String.format("Could not find type alias for given name: %s", unknownName);
        assertThatThrownBy(() -> resolver.getAliasFor(unknownName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    void shouldThrowExceptionForUnknownType() {
        //given
        Class<?> unknownType = TypeAlias.class;

        //when /then
        String expectedMessage = String.format("Could not find type alias for given type: %s", unknownType);
        assertThatThrownBy(() -> resolver.getAliasFor(unknownType))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }
}
