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
import com.sabre.oss.yare.serializer.json.model.Action;
import com.sabre.oss.yare.serializer.json.model.Function;
import com.sabre.oss.yare.serializer.json.model.*;
import com.sabre.oss.yare.serializer.json.model.Value;
import com.sabre.oss.yare.serializer.json.model.Values;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.sabre.oss.yare.core.model.ExpressionFactory.*;
import static org.assertj.core.api.Assertions.assertThat;

class ActionConverterTest {
    private ActionConverter actionConverter;

    @BeforeEach
    void setUp() {
        actionConverter = new ActionConverter(new NodeConverter(DefaultTypeConverters.getDefaultTypeConverter()));
    }

    @Test
    void shouldConvertAction() {
        Action toConvert = new Action()
                .withName("action-name")
                .withParameters(
                        new Parameter()
                                .withName("parameter-name-1")
                                .withExpression(new Value()
                                        .withValue("value-value")
                                        .withType(String.class.getName())),
                        new Parameter()
                                .withName("parameter-name-2")
                                .withExpression(new Values()
                                        .withType(Set.class.getName())
                                        .withValues(
                                                new Value()
                                                        .withValue(100L)
                                                        .withType(Long.class.getName())
                                        )),
                        new Parameter()
                                .withName("parameter-name-3")
                                .withExpression(new Function()
                                        .withName("function-name")
                                        .withReturnType(Boolean.class.getName())
                                        .withParameters(Collections.emptyList()))
                );

        List<Expression.Action> actions = actionConverter.convert(Collections.singletonList(toConvert));

        Expression.Action expected = actionOf("action-name", "action-name", Arrays.asList(
                valueOf("parameter-name-1", String.class, "value-value"),
                valuesOf("parameter-name-2", Set.class, Collections.singletonList(
                        valueOf(null, Long.class, 100L)
                )),
                functionOf("parameter-name-3", Boolean.class, "function-name", Collections.emptyList())
        ));

        assertThat(actions).containsExactly(expected);
    }
}
