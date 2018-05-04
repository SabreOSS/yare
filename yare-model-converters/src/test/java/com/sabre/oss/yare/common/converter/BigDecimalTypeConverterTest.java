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
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BigDecimalTypeConverterTest {
    private BigDecimalTypeConverter typeConverter;

    @BeforeEach
    void setUp() {
        typeConverter = new BigDecimalTypeConverter();
    }

    @ParameterizedTest
    @MethodSource("applicableParameters")
    void shouldProperlyCheckIfApplicable(Type type, boolean expected) {
        boolean isApplicable = typeConverter.isApplicable(type);

        assertThat(isApplicable).isEqualTo(expected);
    }

    private static Stream<Arguments> applicableParameters() {
        return Stream.of(
                Arguments.of(BigDecimal.class, true),
                Arguments.of(Integer.class, false),
                Arguments.of(null, false)
        );
    }

    @Test
    void shouldThrowExceptionWhenConvertingFromMalformedString() {
        String toConvert = "malformed string";

        assertThatThrownBy(() -> typeConverter.fromString(null, toConvert))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @MethodSource("conversionParameters")
    void shouldProperlyConvertFromString(String toConvert) {
        BigDecimal expected = new BigDecimal(toConvert);

        BigDecimal converted = typeConverter.fromString(null, toConvert);

        assertThat(converted).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("conversionParameters")
    void shouldProperlyConvertToString(String toConvert) {
        String converted = typeConverter.toString(null, new BigDecimal(toConvert));

        assertThat(converted).isEqualTo(toConvert);
    }

    private static Collection<String> conversionParameters() {
        return Arrays.asList(
                "100",
                "100.23"
        );
    }
}
