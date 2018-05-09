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

package com.sabre.oss.yare.core.call;

import com.sabre.oss.yare.core.model.Expression;
import com.sabre.oss.yare.core.model.ExpressionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class CallConverterTest {
    private CallConverter callConverter;

    @BeforeEach
    void setUp() {
        callConverter = new CallConverter();
    }

    @ParameterizedTest
    @MethodSource("mappings")
    void shouldConvertExpressionToArgument(Expression expression, Argument argument) {
        Argument.Invocation invocation = callConverter.convert(null, ExpressionFactory.functionOf("name", Object.class, "call", expression));

        assertThat(invocation).isEqualTo(ArgumentFactory.invocationOf("name", Argument.UNKNOWN, "call", argument));
    }

    private static Stream<Arguments> mappings() {
        return Stream.of(
                Arguments.of(
                        ExpressionFactory.valueOf("booleanFalse", false),
                        Argument.valueOf("booleanFalse", Boolean.FALSE)
                ),
                Arguments.of(
                        ExpressionFactory.valueOf("booleanTrue", true),
                        Argument.valueOf("booleanTrue", Boolean.TRUE)
                ),
                Arguments.of(
                        ExpressionFactory.valueOf("number", 1234L),
                        Argument.valueOf("number", Long.parseLong("1234"))
                ),
                Arguments.of(
                        ExpressionFactory.valueOf("decimalNumber", BigDecimal.valueOf(-123, 456)),
                        Argument.valueOf("decimalNumber", BigDecimal.valueOf(-123, 456))
                ),
                Arguments.of(
                        ExpressionFactory.valueOf("ordinaryString", "Just a string"),
                        Argument.valueOf("ordinaryString", "Just a string")
                ),
                Arguments.of(
                        ExpressionFactory.valueOf("listOfNumbers", asList(1L, 2L)),
                        Argument.valueOf("listOfNumbers", new InternalParameterizedType(null, List.class, Long.class), asList(Long.parseLong("1"), Long.parseLong("2")))
                ),
                Arguments.of(
                        ExpressionFactory.valueOf("listOfStrings", asList("one", "two")),
                        Argument.valueOf("listOfStrings", new InternalParameterizedType(null, List.class, String.class), asList("one", "two"))
                ),
                Arguments.of(
                        ExpressionFactory.referenceOf("simpleReference", Object.class, "person"),
                        Argument.referenceOf("simpleReference", Object.class, Argument.UNKNOWN, "person")
                ),
                Arguments.of(
                        ExpressionFactory.referenceOf("propertyReference", Object.class, "person", String.class, "name"),
                        Argument.referenceOf("propertyReference", Object.class, Argument.UNKNOWN, "person.name")
                )
        );
    }

    @Test
    void shouldConvertMultipleExpressions() {
        Argument.Invocation invocation = callConverter.convert(
                null, ExpressionFactory.functionOf("functionName", Object.class, "functionCall",
                        ExpressionFactory.valueOf("string", "value"),
                        ExpressionFactory.valueOf("boolean", false)
                ));

        assertThat(invocation).isEqualTo(
                ArgumentFactory.invocationOf("functionName", Argument.UNKNOWN, "functionCall",
                        Argument.valueOf("string", "value"),
                        Argument.valueOf("boolean", Boolean.FALSE)
                ));
    }
}
