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
import com.sabre.oss.yare.core.model.Attribute;
import com.sabre.oss.yare.core.model.Expression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AttributeConverterTest {
    private AttributeConverter attributeConverter;

    @BeforeEach
    void setUp() {
        attributeConverter = new AttributeConverter(DefaultTypeConverters.getDefaultTypeConverter());
    }

    @Test
    void shouldConvertToAttribute() {
        com.sabre.oss.yare.serializer.json.model.Attribute toConvert = new com.sabre.oss.yare.serializer.json.model.Attribute()
                .withName("attribute-name")
                .withType("java.lang.String")
                .withValue("attribute-value");

        Attribute attribute = attributeConverter.convert(toConvert);

        Attribute expected = new Attribute("attribute-name", String.class, "attribute-value");
        assertThat(attribute).isEqualTo(expected);
    }

    @Test
    void shouldConvertToAttributeWhenTypeIsNull() {
        com.sabre.oss.yare.serializer.json.model.Attribute toConvert = new com.sabre.oss.yare.serializer.json.model.Attribute()
                .withName("attribute-name")
                .withType(null)
                .withValue("attribute-value");

        Attribute attribute = attributeConverter.convert(toConvert);

        Attribute expected = new Attribute("attribute-name", Expression.Undefined.class, "attribute-value");
        assertThat(attribute).isEqualTo(expected);
    }

    @Test
    void shouldThrowExceptionWhenUnknownTypeUsed() {
        com.sabre.oss.yare.serializer.json.model.Attribute toConvert = new com.sabre.oss.yare.serializer.json.model.Attribute()
                .withName("attribute-name")
                .withType("unknown")
                .withValue("attribute-value");

        assertThatThrownBy(() -> attributeConverter.convert(toConvert))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
