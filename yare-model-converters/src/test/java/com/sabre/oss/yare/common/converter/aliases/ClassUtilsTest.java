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
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClassUtilsTest {
    @Test
    void shouldResolvePrimitiveTypeProperly() {
        //given
        String name = "int";

        //when
        Class<?> type = ClassUtils.forName(name);

        //then
        assertThat(type).isEqualTo(int.class);
    }

    @Test
    void shouldResolveObjectTypeProperly() {
        //given
        String name = "java.lang.String";

        //when
        Class<?> type = ClassUtils.forName(name);

        //then
        assertThat(type).isEqualTo(String.class);
    }

    @Test
    void shouldThrowExceptionForUnknownType() {
        //given
        String unknownTypeName = "UNKNOWN_TYPE_NAME";

        //when
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> ClassUtils.forName(unknownTypeName));

        //then
        String expectedMessage = String.format("Could not resolve type for name: %s", unknownTypeName);
        assertThat(e.getMessage()).isEqualTo(expectedMessage);
    }
}
