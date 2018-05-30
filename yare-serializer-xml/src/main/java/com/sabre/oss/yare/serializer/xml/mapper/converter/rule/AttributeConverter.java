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
import com.sabre.oss.yare.common.converter.TypeConverter;
import com.sabre.oss.yare.common.mapper.Mapper;
import com.sabre.oss.yare.core.model.Attribute;
import com.sabre.oss.yare.core.model.Expression;
import com.sabre.oss.yare.serializer.model.AttributeSer;
import com.sabre.oss.yare.serializer.model.CustomValueSer;
import com.sabre.oss.yare.serializer.model.ValueSer;

import java.lang.reflect.Type;

class AttributeConverter implements Mapper<AttributeSer, Attribute> {
    private final TypeConverter typeConverter;

    AttributeConverter(TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
    }

    @Override
    public Attribute map(AttributeSer attribute) {
        String name = attribute.getName();
        ValueSer value = attribute.getValue();

        if (value != null) {
            return convertValue(name, value);
        }

        CustomValueSer customValue = attribute.getCustomValue();
        if (customValue != null) {
            return convertCustomValue(name, customValue);
        }

        throw new IllegalStateException("Attribute value or customValue must be set");
    }

    private Attribute convertValue(String name, ValueSer value) {
        Type type = getType(value.getType());
        String v = value.getValue() == null ? StringTypeConverter.NULL_LITERAL : value.getValue();

        Object attributeValue = null;
        if (!isNullLiteral(v)) {
            attributeValue = type == Expression.Undefined.class ? v : typeConverter.fromString(type, v);
        }

        return new Attribute(name, type, attributeValue);
    }

    private Attribute convertCustomValue(String name, CustomValueSer customValue) {
        Type type = getType(customValue.getType());
        return new Attribute(name, type, customValue.getAny());
    }

    private Type getType(String type) {
        return type == null ? Expression.Undefined.class : typeConverter.fromString(Type.class, type);
    }

    private boolean isNullLiteral(String value) {
        return StringTypeConverter.NULL_LITERAL.equals(value);
    }
}
