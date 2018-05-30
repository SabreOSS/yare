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

package com.sabre.oss.yare.serializer.xml.mapper.converter.xml;

import com.sabre.oss.yare.common.converter.StringTypeConverter;
import com.sabre.oss.yare.core.model.Attribute;
import com.sabre.oss.yare.core.model.Expression;
import com.sabre.oss.yare.serializer.model.AttributeSer;
import com.sabre.oss.yare.serializer.model.CustomValueSer;
import com.sabre.oss.yare.serializer.model.ValueSer;
import com.sabre.oss.yare.serializer.xml.mapper.converter.CustomTestValue;
import org.junit.jupiter.api.Test;

import static com.sabre.oss.yare.common.converter.DefaultTypeConverters.getDefaultTypeConverter;
import static org.assertj.core.api.Assertions.assertThat;

class AttributeConverterTest {
    private AttributeConverter attributeConverter = new AttributeConverter(getDefaultTypeConverter());

    @Test
    void shouldConvertWhenAttributeWithSimpleValue() {
        //given
        Attribute attribute = new Attribute("attribute", String.class, "Test");

        //when
        AttributeSer actual = attributeConverter.map(attribute);

        //then
        AttributeSer expected = new AttributeSer()
                .withName("attribute")
                .withValue(new ValueSer()
                        .withType("String")
                        .withValue("Test"));

        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    void shouldConvertAttributeWithCustomValue() {
        //given
        Attribute attribute = new Attribute("attribute", CustomTestValue.class, new CustomTestValue("testValue"));

        //when
        AttributeSer actual = attributeConverter.map(attribute);

        //then
        AttributeSer expected = new AttributeSer()
                .withName("attribute")
                .withCustomValue(new CustomValueSer()
                        .withType(CustomTestValue.class.getName())
                        .withAny(new CustomTestValue("testValue")));
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    void shouldConvertToNullLiteralWhenAttributeWithNullValue() {
        //given
        Attribute attribute = new Attribute("attribute", String.class, null);

        //when
        AttributeSer actual = attributeConverter.map(attribute);

        //then
        AttributeSer expected = new AttributeSer()
                .withName("attribute")
                .withValue(new ValueSer()
                        .withType("String")
                        .withValue(StringTypeConverter.NULL_LITERAL));
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    void shouldConvertToNullWhenAttributeWithoutValueAndWithoutType() {
        //given
        Attribute attribute = new Attribute("attribute", null, null);

        //when
        AttributeSer actual = attributeConverter.map(attribute);

        //then
        AttributeSer expected = new AttributeSer()
                .withName("attribute")
                .withValue(new ValueSer()
                        .withType(Expression.Undefined.class.getName())
                        .withValue(StringTypeConverter.NULL_LITERAL));
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    void shouldConvertToNullWhenAttributeWithCustomValue() {
        //given
        Attribute attribute = new Attribute("attribute", CustomTestValue.class, null);

        //when
        AttributeSer actual = attributeConverter.map(attribute);

        //then
        AttributeSer expected = new AttributeSer()
                .withName("attribute")
                .withCustomValue(new CustomValueSer()
                        .withType(CustomTestValue.class.getName())
                        .withAny(null));
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    void shouldConvertToUndefinedSimpleValueWhenAttributeWithSimpleValueAndWithoutType() {
        //given
        Attribute attribute = new Attribute("attribute", null, true);

        //when
        AttributeSer actual = attributeConverter.map(attribute);

        //then
        AttributeSer expected = new AttributeSer()
                .withName("attribute")
                .withValue(new ValueSer()
                        .withType(Expression.Undefined.class.getName())
                        .withValue("true"));
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    void shouldConvertToCustomValueWhenAttributeWithCustomValueAndWithoutType() {
        //given
        Attribute attribute = new Attribute("attribute", null, new CustomTestValue("test"));

        //when
        AttributeSer actual = attributeConverter.map(attribute);

        //then
        AttributeSer expected = new AttributeSer()
                .withName("attribute")
                .withCustomValue(new CustomValueSer()
                        .withType(Expression.Undefined.class.getName())
                        .withAny(new CustomTestValue("test")));
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }

}
