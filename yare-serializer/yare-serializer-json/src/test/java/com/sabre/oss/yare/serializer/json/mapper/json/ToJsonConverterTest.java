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

package com.sabre.oss.yare.serializer.json.mapper.json;

import com.sabre.oss.yare.common.converter.DefaultTypeConverters;
import com.sabre.oss.yare.serializer.json.model.Action;
import com.sabre.oss.yare.serializer.json.model.*;
import com.sabre.oss.yare.serializer.json.model.Function;
import com.sabre.oss.yare.serializer.json.model.Operator;
import com.sabre.oss.yare.serializer.json.model.Value;
import com.sabre.oss.yare.serializer.json.model.Values;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import static com.sabre.oss.yare.core.model.ExpressionFactory.*;
import static org.assertj.core.api.Assertions.assertThat;

class ToJsonConverterTest {
    private ToJsonConverter toJsonConverter;

    @BeforeEach
    void setUp() {
        toJsonConverter = new ToJsonConverter(DefaultTypeConverters.getDefaultTypeConverter());
    }

    @Test
    void shouldConvertRule() {
        com.sabre.oss.yare.core.model.Rule toConvert = new com.sabre.oss.yare.core.model.Rule(
                new LinkedHashSet<>(Arrays.asList(
                        new com.sabre.oss.yare.core.model.Attribute("attribute-name-1", String.class, "attribute-value-1"),
                        new com.sabre.oss.yare.core.model.Attribute("attribute-name-2", Integer.class, 100)
                )),
                Arrays.asList(
                        new com.sabre.oss.yare.core.model.Fact("fact-name-1", Character.class),
                        new com.sabre.oss.yare.core.model.Fact("fact-name-2", BigDecimal.class)
                ),
                operatorOf(null, Boolean.class, "operator-type-1", Arrays.asList(
                        operatorOf(null, Boolean.class, "operator-type-2", Collections.singletonList(
                                valueOf(null, BigInteger.class, 100)
                        )),
                        null,
                        functionOf("function-name-1", Boolean.class, "function-name-1", Arrays.asList(
                                valuesOf("parameter-name-1", List.class, Collections.emptyList()),
                                null
                        ))
                )),
                Arrays.asList(
                        null,
                        actionOf("action-name", "action-name", Collections.singletonList(
                                functionOf("parameter-name-2", Boolean.class, "function-name-2", Collections.emptyList())
                        ))
                )
        );

        Rule rule = toJsonConverter.convert(toConvert);

        Rule expected = new Rule()
                .withAttributes(
                        new Attribute()
                                .withName("attribute-name-1")
                                .withValue("attribute-value-1")
                                .withType("String"),
                        new Attribute()
                                .withName("attribute-name-2")
                                .withValue(100)
                                .withType("Integer")
                )
                .withFacts(
                        new Fact()
                                .withName("fact-name-1")
                                .withType("Character"),
                        new Fact()
                                .withName("fact-name-2")
                                .withType(BigDecimal.class.getName())
                )
                .withPredicate(
                        new Operator()
                                .withType("operator-type-1")
                                .withOperands(
                                        new Operator()
                                                .withType("operator-type-2")
                                                .withOperands(
                                                        new Value()
                                                                .withValue(100)
                                                                .withType(BigInteger.class.getName())
                                                ),
                                        null,
                                        new Function()
                                                .withName("function-name-1")
                                                .withReturnType("Boolean")
                                                .withParameters(
                                                        new Parameter()
                                                                .withName("parameter-name-1")
                                                                .withExpression(new Values()
                                                                        .withType("List")
                                                                        .withValues(Collections.emptyList())),
                                                        null
                                                )
                                )
                )
                .withActions(
                        null,
                        new Action()
                                .withName("action-name")
                                .withParameters(
                                        new Parameter()
                                                .withName("parameter-name-2")
                                                .withExpression(
                                                        new Function()
                                                                .withName("function-name-2")
                                                                .withReturnType("Boolean")
                                                                .withParameters(Collections.emptyList())
                                                )
                                )
                );
        assertThat(rule).isEqualTo(expected);
    }
}
