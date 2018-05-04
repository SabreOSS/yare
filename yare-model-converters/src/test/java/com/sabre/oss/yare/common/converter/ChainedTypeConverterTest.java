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

package com.sabre.oss.yare.common.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChainedTypeConverterTest {
    private ChainedTypeConverter typeConverter;

    @BeforeEach
    void setUp() {
        typeConverter = new ChainedTypeConverter(Arrays.asList(
                new StringTypeConverter(),
                new BooleanTypeConverter(),
                new IntegerTypeConverter()
        ));
    }

    @Test
    void shouldThrowExceptionWhenIsApplicableAndTypeIsNull() {
        assertThatThrownBy(() -> typeConverter.isApplicable(null))
                .isExactlyInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest
    @MethodSource("applicableParameters")
    void shouldProperlyCheckIfApplicable(Type type, boolean expected) {
        boolean isApplicable = typeConverter.isApplicable(type);

        assertThat(isApplicable).isEqualTo(expected);
    }

    private static Stream<Arguments> applicableParameters() {
        return Stream.of(
                Arguments.of(String.class, true),
                Arguments.of(Boolean.class, true),
                Arguments.of(boolean.class, true),
                Arguments.of(Integer.class, true),
                Arguments.of(int.class, true),
                Arguments.of(Long.class, false)
        );
    }

    @Test
    void shouldThrowExceptionWhenFromStringAndTypeIsNull() {
        assertThatThrownBy(() -> typeConverter.fromString(null, "123"))
                .isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldThrowExceptionWhenConvertingFromStringAndUnsupportedType() {
        assertThatThrownBy(() -> typeConverter.fromString(Long.class, "123"))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionWhenToStringAndTypeIsNull() {
        assertThatThrownBy(() -> typeConverter.toString(null, 123L))
                .isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldThrowExceptionWhenConvertingToStringAndUnsupportedType() {
        assertThatThrownBy(() -> typeConverter.toString(Long.class, 123L))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @MethodSource("conversionParameters")
    void shouldProperlyConvertFromString(Type type, String toConvert, Object expected) {
        Object converted = typeConverter.fromString(type, toConvert);

        assertThat(converted).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("conversionParameters")
    void shouldProperlyConvertToString(Type type, String expected, Object toConvert) {
        Object converted = typeConverter.toString(type, toConvert);

        assertThat(converted).isEqualTo(expected);
    }

    private static Stream<Arguments> conversionParameters() {
        return Stream.of(
                Arguments.of(String.class, "test", "test"),
                Arguments.of(Boolean.class, "true", true),
                Arguments.of(Integer.class, "123", 123)
        );
    }
}
