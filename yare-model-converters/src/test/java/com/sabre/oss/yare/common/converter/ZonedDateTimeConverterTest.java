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

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ZonedDateTimeConverterTest {
    private ZonedDateTimeConverter typeConverter;

    @BeforeEach
    void setUp() {
        typeConverter = new ZonedDateTimeConverter();
    }

    @ParameterizedTest
    @MethodSource("applicableParameters")
    void shouldBeApplicableWhenZonedDateTime() {
        boolean isApplicable = typeConverter.isApplicable(ZonedDateTime.class);

        assertThat(isApplicable).isTrue();
    }

    private static Stream<Arguments> applicableParameters() {
        return Stream.of(
                Arguments.of(ZonedDateTimeConverter.class, true),
                Arguments.of(Integer.class, false),
                Arguments.of(null, false)
        );
    }

    @Test
    void shouldProperlyConvertFromStringNull() {
        ZonedDateTime converted = typeConverter.fromString(ZonedDateTime.class, "@null");

        assertThat(converted).isNull();
    }

    @Test
    void shouldProperlyConvertToStringNull() {
        String converted = typeConverter.toString(ZonedDateTime.class, null);

        assertThat(converted).isEqualTo("@null");
    }

    @ParameterizedTest
    @MethodSource("conversionParameters")
    void shouldProperlyConvertFromString(String toConvert) {
        ZonedDateTime expected = ZonedDateTime.parse(toConvert);

        ZonedDateTime converted = typeConverter.fromString(ZonedDateTime.class, toConvert);

        assertThat(converted).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("conversionParameters")
    void shouldProperlyConvertToString(String toConvert) {
        ZonedDateTime dateToConvert = ZonedDateTime.parse(toConvert);

        String converted = typeConverter.toString(ZonedDateTime.class, dateToConvert);

        assertThat(converted).isEqualTo(toConvert);
    }

    private static Collection<String> conversionParameters() {
        return Arrays.asList(
                "2014-12-02T10:45:30+02:00",
                "2016-10-16T22:45:30+02:00[Europe/Warsaw]"
        );
    }
}
