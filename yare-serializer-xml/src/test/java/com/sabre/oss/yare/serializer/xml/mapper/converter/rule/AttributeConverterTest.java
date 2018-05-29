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

package com.sabre.oss.yare.serializer.xml.mapper.converter.rule;

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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AttributeConverterTest {
    private AttributeConverter attributeConverter = new AttributeConverter(getDefaultTypeConverter());

    @Test
    void shouldConvertAttributeWithSimpleValue() {
        // given
        AttributeSer attributeSer = new AttributeSer()
                .withName("attribute")
                .withValue(new ValueSer()
                        .withType("String")
                        .withValue("Test"));

        // when
        Attribute actual = attributeConverter.map(attributeSer);

        // then
        Attribute expected = new Attribute("attribute", String.class, "Test");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldThrowExceptionWhenAttributeWithUnsupportedType() {
        // given
        AttributeSer attributeSer = new AttributeSer()
                .withName("attribute")
                .withValue(new ValueSer()
                        .withType("Time")
                        .withValue("Test"));

        // when / then
        assertThatThrownBy(() -> attributeConverter.map(attributeSer))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldConvertAttributeWithUndefinedType() {
        // given
        AttributeSer attributeSer = new AttributeSer()
                .withName("attribute")
                .withValue(new ValueSer()
                        .withType(Expression.Undefined.class.getName())
                        .withValue("Test"));

        // when
        Attribute actual = attributeConverter.map(attributeSer);

        // then
        Attribute expected = new Attribute("attribute", Expression.Undefined.class, "Test");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldConvertAttributeWithCustomValue() {
        // given
        AttributeSer attributeSer = new AttributeSer()
                .withName("attribute")
                .withCustomValue(new CustomValueSer()
                        .withType(CustomTestValue.class.getName())
                        .withAny(new CustomTestValue("testValue")));

        // when
        Attribute actual = attributeConverter.map(attributeSer);

        // then
        Attribute expected = new Attribute("attribute", CustomTestValue.class, new CustomTestValue("testValue"));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldConvertToSimpleValueWhenAttributeWithValueAndCustomValue() {
        // given
        AttributeSer attributeSer = new AttributeSer()
                .withName("attribute")
                .withValue(new ValueSer()
                        .withType("String")
                        .withValue("Test"))
                .withCustomValue(new CustomValueSer()
                        .withType(CustomTestValue.class.getName())
                        .withAny(new CustomTestValue("testValue")));

        // when
        Attribute actual = attributeConverter.map(attributeSer);

        // then
        Attribute expected = new Attribute("attribute", String.class, "Test");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldConvertToNullWhenAttributeWithValueWithLiteralNull() {
        // given
        AttributeSer attributeSer = new AttributeSer()
                .withName("attribute")
                .withValue(new ValueSer()
                        .withType("String")
                        .withValue(StringTypeConverter.NULL_LITERAL));

        // when
        Attribute actual = attributeConverter.map(attributeSer);

        // then
        Attribute expected = new Attribute("attribute", String.class, null);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldConvertAttributeWithSimpleValueWithoutType() {
        // given
        AttributeSer attributeSer = new AttributeSer()
                .withName("attribute")
                .withValue(new ValueSer()
                        .withValue("testValue"));

        // when
        Attribute actual = attributeConverter.map(attributeSer);

        // then
        Attribute expected = new Attribute("attribute", Expression.Undefined.class, "testValue");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldConvertAttributeWithCustomValueWithoutType() {
        // given
        AttributeSer attributeSer = new AttributeSer()
                .withName("attribute")
                .withCustomValue(new CustomValueSer()
                        .withAny(new CustomTestValue("testValue")));

        // when
        Attribute actual = attributeConverter.map(attributeSer);

        // then
        Attribute expected = new Attribute("attribute", Expression.Undefined.class, new CustomTestValue("testValue"));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldThrowExceptionWhenValueInValueIsNull() {
        // given
        AttributeSer attributeSer = new AttributeSer()
                .withName("attribute")
                .withValue(new ValueSer()
                        .withType("String")
                        .withValue(null));

        // when / then
        assertThatThrownBy(() -> attributeConverter.map(attributeSer))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldThrowExceptionWhenValueInCustomValueIsNull() {
        // given
        AttributeSer attributeSer = new AttributeSer()
                .withName("attribute")
                .withCustomValue(new CustomValueSer()
                        .withType(CustomTestValue.class.getName())
                        .withAny(null));

        // when / then
        assertThatThrownBy(() -> attributeConverter.map(attributeSer))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldThrowExceptionWhenValueAndCustomValueIsNotSet() {
        // given
        AttributeSer attributeSer = new AttributeSer()
                .withName("attribute");

        // when / then
        assertThatThrownBy(() -> attributeConverter.map(attributeSer))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

}
