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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class CallConverterTest {
    private CallConverter callConverter;

    @BeforeEach
    void setUp() {
        callConverter = new CallConverter();
    }

    @Test
    void shouldProperlyConvertAction() {
        // given
        Expression.Action action = getAction();

        // when
        Argument.Invocation invocation = callConverter.apply(action);

        // then
        assertThat(invocation).isNotNull();
        assertThat(invocation.getName()).isEqualTo(action.getName());
        assertThat(invocation.getCall()).isEqualTo(action.getName());
        assertThat(invocation.getArguments().stream()
                .map(Argument::getName)
                .collect(Collectors.toSet()))
                .isEqualTo(action.getArguments().stream()
                        .map(Expression::getName)
                        .collect(Collectors.toSet()));
    }

    @Test
    void shouldProperlyConvertActionParametersToArguments() {
        // given
        List<? extends Expression> actionParameters = getActionParameters();

        // when
        Map<String, Argument> arguments = actionParameters.stream()
                .map(parameter -> new Object[]{parameter.getName(), callConverter.convert(parameter)})
                .collect(Collectors.toMap((Object[] k) -> k[0].toString(), (Object[] v) -> (Argument) v[1]));

        // then
        assertThat(arguments).containsOnly(
                entry("booleanFalse", Argument.valueOf("booleanFalse", Boolean.FALSE)),
                entry("booleanTrue", Argument.valueOf("booleanTrue", Boolean.TRUE)),
                entry("number", Argument.valueOf("number", Long.parseLong("1234"))),
                entry("decimalNumber", Argument.valueOf("decimalNumber", BigDecimal.valueOf(-123, 456))),
                entry("ordinaryString", Argument.valueOf("ordinaryString", "Just a string")),
                entry("listOfNumbers", Argument.valueOf("listOfNumbers", new CallConverter.InternalParameterizedType(null, List.class, Long.class), asList(Long.parseLong("1"), Long.parseLong("2")))),
                entry("listOfStrings", Argument.valueOf("listOfStrings", new CallConverter.InternalParameterizedType(null, List.class, String.class), asList("one", "two"))),
                entry("simpleReference", Argument.referenceOf("simpleReference", Object.class, Argument.UNKNOWN, "person")),
                entry("propertyReference", Argument.referenceOf("propertyReference", Object.class, Argument.UNKNOWN, "person.name"))
        );
    }

    Expression.Action getAction() {
        return ExpressionFactory.actionOf("actionOne", "actionOne", getActionParameters());
    }

    List<Expression> getActionParameters() {
        return asList(
                ExpressionFactory.valueOf("booleanFalse", false),
                ExpressionFactory.valueOf("booleanTrue", true),
                ExpressionFactory.valueOf("number", 1234L),
                ExpressionFactory.valueOf("decimalNumber", BigDecimal.valueOf(-123, 456)),
                ExpressionFactory.valueOf("ordinaryString", "Just a string"),
                ExpressionFactory.valueOf("listOfNumbers", asList(1L, 2L)),
                ExpressionFactory.valueOf("listOfStrings", asList("one", "two")),
                ExpressionFactory.referenceOf("simpleReference", Object.class, "person"),
                ExpressionFactory.referenceOf("propertyReference", Object.class, "person", String.class, "name"));
    }
}
