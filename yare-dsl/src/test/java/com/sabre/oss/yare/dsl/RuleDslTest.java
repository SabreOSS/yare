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

package com.sabre.oss.yare.dsl;

import com.sabre.oss.yare.common.converter.DefaultTypeConverters;
import com.sabre.oss.yare.common.converter.TypeConverter;
import com.sabre.oss.yare.core.model.*;
import com.sabre.oss.yare.core.model.Expression;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sabre.oss.yare.core.model.ExpressionFactory.*;
import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RuleDslTest {
    private TypeConverter converter = DefaultTypeConverters.getDefaultTypeConverter();

    @Test
    void shouldProperlyBuildRule() {
        Rule rule = ruleBuilder()
                .name("this.is.MyRuleName")
                .fact("exampleFact", ExampleFact.class)
                .fact("otherFact", OtherFact.class)
                .attribute("stringValue", "string")
                .attribute("doubleValue", 1.0)
                .predicate(
                        and(
                                or(
                                        lessOrEqual(
                                                value(123L),
                                                value("${otherFact.number}")
                                        ),
                                        match(
                                                value("${stringValue}"),
                                                value("10")
                                        ),
                                        operator("asd",
                                                value("${otherFact.enabled}"),
                                                value(true)
                                        ),
                                        and(
                                                equal(value("${otherFact.enabled}"),
                                                        value(true)
                                                ),
                                                not(
                                                        value("true", Boolean.class)
                                                ),
                                                value(null),
                                                value((Boolean) null)
                                        )
                                ),
                                less(
                                        value("${exampleFact.startDate}"),
                                        value("${exampleFact.stopDate}")
                                ),
                                operator("contains", values(String.class, value("${stringValue}"), value("b"), value("c")), value("string")),
                                function("function", Boolean.class,
                                        param("param1", value("${ruleName}")),
                                        param("param2", value("my value"))
                                )
                        )
                )
                .action("exampleAction", param("param1", value("${ctx}")))
                .build();

        assertThat(rule.getAttributes()).containsExactly(
                new Attribute("ruleName", String.class, "this.is.MyRuleName"),
                new Attribute("stringValue", String.class, "string"),
                new Attribute("doubleValue", Double.class, 1.0)
        );
        assertThat(rule.getFacts()).containsExactly(
                new Fact("exampleFact", ExampleFact.class),
                new Fact("otherFact", OtherFact.class)
        );
        assertThat(rule.getPredicate()).isEqualTo(expectedValidPredicateModel());
        assertThat(rule.getActions()).containsExactly(
                actionOf("exampleAction", "exampleAction",
                        valueOf("param1", String.class, "${ctx}")
                )
        );
    }

    @Test
    void shouldProperlyValidateNullPredicate() {
        assertThatThrownBy(() -> ruleBuilder()
                .name("this.is.MyRuleName")
                .fact("exampleFact", ExampleFact.class)
                .predicate(null)
                .action("exampleAction", param("param1", value("${ctx}")))
                .build())
                .hasMessage("Rule validation error(s):\n" +
                        "[ERROR] Predicate Error: predicate was not specified")
                .hasSameClassAs(new IllegalStateException());
    }

    @Test
    void shouldProperlyInitializeEmptyRule() {
        Rule rule = ruleBuilder()
                .build(false);
        assertThat(rule.getActions()).isEmpty();
        assertThat(rule.getAttributes()).isEmpty();
        assertThat(rule.getFacts()).isEmpty();
        assertThat(rule.getPredicate()).isNull();
    }

    @ParameterizedTest
    @MethodSource("testValue")
    void shouldProperlyCreateValue(Class<Object> type, Object object) {
        // when
        ExpressionOperand<Object> operand = value(object, type);

        // then
        Expression expression = operand.getExpression(null, null);
        assertThat(expression).isInstanceOf(Expression.Value.class);

        assertThat(((Expression.Value) expression).getValue()).isEqualTo(object);
        assertThat(expression.getType()).isEqualTo(type);
    }

    private static Stream<Arguments> testValue() {
        return Stream.of(
                Arguments.of(Boolean.class, true),
                Arguments.of(Boolean.class, null),
                Arguments.of(Boolean.class, false),
                Arguments.of(Integer.class, 10),
                Arguments.of(Long.class, 100L),
                Arguments.of(String.class, "string"),
                Arguments.of(BigDecimal.class, new BigDecimal("100.123")),
                Arguments.of(ZonedDateTime.class, ZonedDateTime.now()),
                Arguments.of(Bean.class, new Bean()),
                Arguments.of(Bean.class, null),
                Arguments.of(List.class, new ArrayList<>())
        );
    }

    @ParameterizedTest
    @MethodSource("testValues")
    void assertBuildInTypeCollectionValues(Class<Object> type, Object[] objects) {
        // when
        CollectionOperand<Object> operand = values(type, objects);

        // then
        Expression expression = operand.getExpression(null, null);
        assertThat(expression).isInstanceOfSatisfying(Expression.Values.class, v -> {
            List<Object> values = v.getValues().stream()
                    .map(Expression.Value.class::cast)
                    .map(Expression.Value::getValue)
                    .collect(Collectors.toList());
            assertThat(values).containsExactly(objects);
        });

        String parametrizedType = String.format("java.util.List<%s>", converter.toString(Type.class, type));
        Type fullType = DefaultTypeConverters.getDefaultTypeConverter().fromString(Type.class, parametrizedType);
        assertThat(expression.getType()).isEqualTo(fullType);
    }

    private static Stream<Arguments> testValues() {
        return Stream.of(
                Arguments.of(Boolean.class, new Boolean[]{true, false, null}),
                Arguments.of(Integer.class, new Integer[]{10, 20}),
                Arguments.of(Long.class, new Long[]{10L, 20L}),
                Arguments.of(Integer.class, new Integer[]{10}),
                Arguments.of(String.class, new String[]{"1", "2"}),
                Arguments.of(Bean.class, new Bean[]{new Bean(), new Bean(), null})
        );
    }

    @Test
    void shouldEscapePlaceholdersInValuesMethodWithConstants() {
        // given
        CollectionOperand<String> values = values(String.class,
                "${placeholder}",
                "\\${placeholder}"
        );

        // when
        Expression expression = values.getExpression(null, null);

        // then
        assertThat(expression).isInstanceOfSatisfying(Expression.Values.class, v ->
                assertThat(v.getValues()).containsExactly(
                        ExpressionFactory.valueOf(null, "\\${placeholder}"),
                        ExpressionFactory.valueOf(null, "\\\\${placeholder}")
                ));
    }

    @ParameterizedTest
    @MethodSource("deprecatedReferenceMethods")
    void shouldSupportDeprecatedMethods(Operand<Object> operand, String value) {
        Expression expression = operand.getExpression(null, null);

        assertThat(expression)
                .isInstanceOfSatisfying(Expression.Value.class,
                        e -> assertThat(e.getValue()).isEqualTo(value));
    }

    private static Stream<Arguments> deprecatedReferenceMethods() {
        return Stream.of(
                Arguments.of(field("field"), "${field}"),
                Arguments.of(field("stringField", String.class), "${stringField}"),
                Arguments.of(field("reference", "path"), "${reference.path}"),
                Arguments.of(field("reference", "path.longValue", Long.class), "${reference.path.longValue}"),
                Arguments.of(reference("reference"), "${reference}"),
                Arguments.of(reference("integerReference", Integer.class), "${integerReference}")
        );
    }

    private Expression.Operator expectedValidPredicateModel() {
        return operatorOf(null, Boolean.class, "and",
                operatorOf(null, Boolean.class, "or",
                        operatorOf(null, Boolean.class, "less-or-equal",
                                valueOf(null, Long.class, 123L),
                                valueOf(null, String.class, "${otherFact.number}")
                        ),
                        operatorOf(null, Boolean.class, "match",
                                valueOf(null, String.class, "${stringValue}"),
                                valueOf(null, String.class, "10")
                        ),
                        operatorOf(null, Boolean.class, "asd",
                                valueOf(null, String.class, "${otherFact.enabled}"),
                                valueOf(null, Boolean.class, true)
                        ),
                        operatorOf(null, Boolean.class, "and",
                                operatorOf(null, Boolean.class, "equal",
                                        valueOf(null, String.class, "${otherFact.enabled}"),
                                        valueOf(null, Boolean.class, true)
                                ),
                                operatorOf(null, Boolean.class, "not",
                                        valueOf(null, Boolean.class, true)
                                ),
                                valueOf(null, Expression.UNDEFINED, null),
                                valueOf(null, Expression.UNDEFINED, null)
                        )
                ),
                operatorOf(null, Boolean.class, "less",
                        valueOf(null, String.class, "${exampleFact.startDate}"),
                        valueOf(null, String.class, "${exampleFact.stopDate}")
                ),
                operatorOf(null, Boolean.class, "contains",
                        valuesOf(null, converter.fromString(Type.class, "java.util.List<java.lang.String>"), Arrays.asList(
                                valueOf(null, "${stringValue}"),
                                valueOf(null, "b"),
                                valueOf(null, "c")
                        )),
                        valueOf(null, String.class, "string")
                ),
                functionOf("function", Boolean.class, "function",
                        valueOf("param1", String.class, "${ruleName}"),
                        valueOf("param2", String.class, "my value")
                )
        );
    }

    private static class Bean {
    }

    private static class ExampleFact {
        public Date startDate;
        public Date stopDate;
    }

    public static class OtherFact {
        public boolean enabled;
        public Long number;
    }
}
