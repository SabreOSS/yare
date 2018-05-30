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
import com.sabre.oss.yare.core.model.Fact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FactConverterTest {
    private FactConverter factConverter;

    @BeforeEach
    void setUp() {
        factConverter = new FactConverter(DefaultTypeConverters.getDefaultTypeConverter());
    }

    @Test
    void shouldConvertToFact() {
        com.sabre.oss.yare.serializer.json.model.Fact toConvert = new com.sabre.oss.yare.serializer.json.model.Fact()
                .withName("fact-name")
                .withType("String");

        Fact fact = factConverter.convert(toConvert);

        Fact expected = new Fact("fact-name", String.class);
        assertThat(fact).isEqualTo(expected);
    }

    @Test
    void shouldConvertNullFact() {
        Fact fact = factConverter.convert(null);

        assertThat(fact).isNull();
    }

    @Test
    void shouldThrowExceptionWhenUnknownTypeUsed() {
        com.sabre.oss.yare.serializer.json.model.Fact toConvert = new com.sabre.oss.yare.serializer.json.model.Fact()
                .withName("fact-name")
                .withType("unknown");

        assertThatThrownBy(() -> factConverter.convert(toConvert))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
