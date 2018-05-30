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

package com.sabre.oss.yare.serializer.json.mapper.rule;

import com.sabre.oss.yare.common.converter.DefaultTypeConverters;
import com.sabre.oss.yare.core.model.Expression;
import com.sabre.oss.yare.serializer.json.model.Function;
import com.sabre.oss.yare.serializer.json.model.Operator;
import com.sabre.oss.yare.serializer.json.model.*;
import com.sabre.oss.yare.serializer.json.model.Value;
import com.sabre.oss.yare.serializer.json.model.Values;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.sabre.oss.yare.core.model.ExpressionFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PredicateConverterTest {
    private PredicateConverter predicateConverter;

    @BeforeEach
    void setUp() {
        predicateConverter = new PredicateConverter(new NodeConverter(DefaultTypeConverters.getDefaultTypeConverter()));
    }

    @Nested
    class ValueConverterTest {
        @Test
        void shouldConvertValue() {
            Value toConvert = new Value()
                    .withValue(100L)
                    .withType("Long");

            Expression expression = predicateConverter.convert(toConvert);

            Expression.Value expected = valueOf(null, Long.class, 100L);
            assertThat(expression).isEqualTo(expected);
        }

        @Test
        void shouldConvertValueWhenTypeIsNull() {
            Value toConvert = new Value()
                    .withValue(100)
                    .withType(null);

            Expression expression = predicateConverter.convert(toConvert);

            Expression.Value expected = valueOf(null, Expression.Undefined.class, 100);
            assertThat(expression).isEqualTo(expected);
        }

        @Test
        void shouldThrowExceptionWhenUnknownTypeUsed() {
            Value toConvert = new Value()
                    .withValue(100)
                    .withType("unknown");

            assertThatThrownBy(() -> predicateConverter.convert(toConvert))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class ValuesConverterTest {
        @Test
        void shouldConvertValues() {
            Values toConvert = new Values()
                    .withType("List")
                    .withValues(
                            new Value()
                                    .withValue(100L)
                                    .withType("Long"),
                            new Values()
                                    .withType("List")
                                    .withValues(new Value()
                                            .withValue("value-value")
                                            .withType("String")),
                            new Function()
                                    .withName("function-name")
                                    .withReturnType("Boolean")
                                    .withParameters(new Parameter()
                                            .withName("parameter-name")
                                            .withExpression(new Value()
                                                    .withValue(100)
                                                    .withType("Integer")))
                    );

            Expression expression = predicateConverter.convert(toConvert);

            Expression.Values expected = valuesOf(null, List.class, Arrays.asList(
                    valueOf(null, Long.class, 100L),
                    valuesOf(null, List.class, Collections.singletonList(
                            valueOf(null, String.class, "value-value")
                    )),
                    functionOf("function-name", Boolean.class, "function-name", Collections.singletonList(
                            valueOf("parameter-name", Integer.class, 100)
                    ))
            ));
            assertThat(expression).isEqualTo(expected);
        }

        @Test
        void shouldConvertValuesWhenOneExpressionIsNull() {
            Values toConvert = new Values()
                    .withType("List")
                    .withValues(
                            null,
                            new Value()
                                    .withValue(100L)
                                    .withType("Long")
                    );

            Expression expression = predicateConverter.convert(toConvert);

            Expression.Values expected = valuesOf(null, List.class, Arrays.asList(
                    null,
                    valueOf(null, Long.class, 100L)
            ));
            assertThat(expression).isEqualTo(expected);
        }

        @Test
        void shouldConvertValuesWhenTypeIsNull() {
            Values toConvert = new Values()
                    .withType(null)
                    .withValues();

            Expression expression = predicateConverter.convert(toConvert);

            Expression.Values expected = valuesOf(null, Expression.Undefined.class, Collections.emptyList());
            assertThat(expression).isEqualTo(expected);
        }

        @Test
        void shouldThrowExceptionWhenUnknownTypeUsed() {
            Values toConvert = new Values()
                    .withType("unknown")
                    .withValues(Collections.emptyList());

            assertThatThrownBy(() -> predicateConverter.convert(toConvert))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class FunctionConverterTest {
        @Test
        void shouldConvertFunction() {
            Function toConvert = new Function()
                    .withName("function-name-1")
                    .withReturnType("Boolean")
                    .withParameters(
                            new Parameter()
                                    .withName("parameter-name-1")
                                    .withExpression(new Value()
                                            .withValue(100)
                                            .withType("Integer")),
                            new Parameter()
                                    .withName("parameter-name-2")
                                    .withExpression(new Values()
                                            .withType("Set")
                                            .withValues(new Value()
                                                    .withValue("value-value")
                                                    .withType("String"))),
                            new Parameter()
                                    .withName("parameter-name-3")
                                    .withExpression(new Function()
                                            .withName("function-name-2")
                                            .withReturnType("String")
                                            .withParameters(Collections.emptyList()))
                    );

            Expression expression = predicateConverter.convert(toConvert);

            Expression.Function expected = functionOf("function-name-1", Boolean.class, "function-name-1", Arrays.asList(
                    valueOf("parameter-name-1", Integer.class, 100),
                    valuesOf("parameter-name-2", Set.class, Collections.singletonList(
                            valueOf(null, String.class, "value-value")
                    )),
                    functionOf("parameter-name-3", String.class, "function-name-2", Collections.emptyList())
            ));
            assertThat(expression).isEqualTo(expected);
        }

        @Test
        void shouldConvertFunctionWhenOneParameterIsNull() {
            Function toConvert = new Function()
                    .withName("function-name")
                    .withReturnType("Boolean")
                    .withParameters(
                            new Parameter()
                                    .withName("parameter-name")
                                    .withExpression(new Value()
                                            .withValue(100)
                                            .withType("Integer")),
                            null
                    );

            Expression expression = predicateConverter.convert(toConvert);

            Expression.Function expected = functionOf("function-name", Boolean.class, "function-name", Arrays.asList(
                    valueOf("parameter-name", Integer.class, 100),
                    null
            ));
            assertThat(expression).isEqualTo(expected);
        }

        @Test
        void shouldConvertValuesWhenTypeIsNull() {
            Function toConvert = new Function()
                    .withName("function-name")
                    .withReturnType(null)
                    .withParameters(Collections.emptyList());

            Expression expression = predicateConverter.convert(toConvert);

            Expression.Function expected = functionOf("function-name", Expression.Undefined.class, "function-name", Collections.emptyList());
            assertThat(expression).isEqualTo(expected);
        }

        @Test
        void shouldThrowExceptionWhenUnknownTypeUsed() {
            Function toConvert = new Function()
                    .withName("function-name")
                    .withReturnType("unknown")
                    .withParameters(Collections.emptyList());

            assertThatThrownBy(() -> predicateConverter.convert(toConvert))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class OperatorConverterTest {
        @Test
        void shouldConvertOperator() {
            Operator toConvert = new Operator()
                    .withType("operator-type-1")
                    .withOperands(
                            new Value()
                                    .withValue("value-value")
                                    .withType("String"),
                            new Values()
                                    .withType("List")
                                    .withValues(
                                            new Value()
                                                    .withValue(null)
                                                    .withType("Character")
                                    ),
                            new Function()
                                    .withName("function-name")
                                    .withReturnType("Boolean")
                                    .withParameters(
                                            new Parameter()
                                                    .withName("parameter-name")
                                                    .withExpression(
                                                            new Values()
                                                                    .withType("String")
                                                    )
                                    ),
                            new Operator()
                                    .withType("operator-type-2")
                    );

            Expression expression = predicateConverter.convert(toConvert);

            Expression.Operator expected = operatorOf(null, Boolean.class, "operator-type-1", Arrays.asList(
                    valueOf(null, String.class, "value-value"),
                    valuesOf(null, List.class, Collections.singletonList(
                            valueOf(null, Character.class, null)
                    )),
                    functionOf("function-name", Boolean.class, "function-name", Collections.singletonList(
                            valuesOf("parameter-name", String.class, null)
                    )),
                    operatorOf(null, Boolean.class, "operator-type-2", (List<Expression>) null)
            ));
            assertThat(expression).isEqualTo(expected);
        }
    }
}
