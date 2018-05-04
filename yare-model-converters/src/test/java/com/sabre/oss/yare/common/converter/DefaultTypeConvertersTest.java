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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTypeConvertersTest {
    private TypeConverter typeConverter;

    @BeforeEach
    void setUp() {
        typeConverter = DefaultTypeConverters.getDefaultTypeConverter();
    }

    @ParameterizedTest
    @MethodSource("applicableParameters")
    void shouldProperlyCheckIfApplicable(Type type) {
        boolean isApplicable = typeConverter.isApplicable(type);

        assertThat(isApplicable).isTrue();
    }

    private static Collection<Class<?>> applicableParameters() {
        return Arrays.asList(
                BigDecimal.class,
                Boolean.class,
                Integer.class,
                Long.class,
                String.class,
                Type.class,
                ZonedDateTime.class
        );
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
        String converted = typeConverter.toString(type, toConvert);

        assertThat(converted).isEqualTo(expected);
    }

    private static Stream<Arguments> conversionParameters() {
        return Stream.of(
                Arguments.of(BigDecimal.class, "123.123", new BigDecimal("123.123")),
                Arguments.of(Boolean.class, "false", false),
                Arguments.of(Integer.class, "123", 123),
                Arguments.of(Long.class, "123", 123L),
                Arguments.of(String.class, "test", "test"),
                Arguments.of(Type.class, "java.lang.Object", Object.class),
                Arguments.of(ZonedDateTime.class, "2014-12-02T10:45:30+02:00", ZonedDateTime.parse("2014-12-02T10:45:30+02:00"))
        );
    }
}
