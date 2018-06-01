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

import com.sabre.oss.yare.core.model.Expression;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TypeTypeConverterTest {
    private TypeTypeConverter typeConverter;

    @BeforeEach
    void setUp() {
        typeConverter = new TypeTypeConverter();
    }

    @ParameterizedTest
    @MethodSource("applicableParameters")
    void shouldProperlyCheckIfApplicable(Type type, boolean expected) {
        boolean isApplicable = typeConverter.isApplicable(type);

        assertThat(isApplicable).isEqualTo(expected);
    }

    private static Stream<Arguments> applicableParameters() {
        return Stream.of(
                Arguments.of(Type.class, true),
                Arguments.of(Integer.class, false),
                Arguments.of(null, false)
        );
    }

    @Test
    void shouldThrowExceptionWhenClassToConvertDoesNotExist() {
        String toConvert = "UnknownClass";

        assertThatThrownBy(() -> typeConverter.fromString(null, toConvert))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionWhenNestedClassToConvertDoesNotExist() {
        String toConvert = "UnknownClass.UnknownNestedClass";

        assertThatThrownBy(() -> typeConverter.fromString(null, toConvert))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionWhenWrongTypeNameFormat() {
        String toConvert = "%&^";

        assertThatThrownBy(() -> typeConverter.fromString(null, toConvert))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionWhenUnsupportedTypeToConvert() {
        Type unknownType = new Type() {
            @Override
            public String getTypeName() {
                return "UnknownType";
            }
        };

        assertThatThrownBy(() -> typeConverter.toString(null, unknownType))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldProperlyConvertFromNullLiteralString() {
        String converted = typeConverter.toString(null, null);

        assertThat(converted).isEqualTo(Expression.Undefined.class.getName());
    }

    @Test
    void shouldProperlyConvertAliasTypeFromFullyQualifiedString() {
        Type converted = typeConverter.fromString(null, "java.lang.String");

        assertThat(converted).isEqualTo(String.class);
    }

    @Test
    void shouldProperlyConvertNestedGenericToString() {
        String converted = typeConverter.toString(null,
                TypeUtils.parameterize(Map.class, String.class, TypeUtils.parameterize(List.class, Object.class)));

        assertThat(converted).isEqualTo("Map<String,List<Object>>");
    }

    @ParameterizedTest
    @MethodSource("conversionParameters")
    void shouldProperlyConvertFromString(String toConvert, Type expected) {
        Type converted = typeConverter.fromString(null, toConvert);

        assertThat(converted).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("conversionParameters")
    void shouldProperlyConvertToString(String expected, Type toConvert) {
        String converted = typeConverter.toString(null, toConvert);

        assertThat(converted).isEqualTo(expected);
    }

    private static Stream<Arguments> conversionParameters() {
        return Stream.of(
                Arguments.of("String", String.class),
                Arguments.of("List<String>", TypeUtils.parameterize(List.class, String.class)),
                Arguments.of("Object", Object.class),
                Arguments.of("List<String>", TypeUtils.parameterize(List.class, String.class)),
                Arguments.of("Map<String,Object>", TypeUtils.parameterize(Map.class, String.class, Object.class)),
                Arguments.of(NestedClass.class.getCanonicalName(), NestedClass.class)
        );
    }

    private static class NestedClass {
    }
}
