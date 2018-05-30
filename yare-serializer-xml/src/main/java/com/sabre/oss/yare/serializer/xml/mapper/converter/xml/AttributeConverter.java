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
import com.sabre.oss.yare.common.converter.TypeConverter;
import com.sabre.oss.yare.common.mapper.Mapper;
import com.sabre.oss.yare.core.model.Attribute;
import com.sabre.oss.yare.core.model.Expression;
import com.sabre.oss.yare.serializer.model.AttributeSer;
import com.sabre.oss.yare.serializer.model.CustomValueSer;
import com.sabre.oss.yare.serializer.model.ValueSer;

import java.lang.reflect.Type;

class AttributeConverter implements Mapper<Attribute, AttributeSer> {
    private final TypeConverter typeConverter;

    AttributeConverter(TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
    }

    @Override
    public AttributeSer map(Attribute attribute) {
        AttributeSer attributeSer = new AttributeSer()
                .withName(attribute.getName());

        Object value = attribute.getValue();
        Type type = attribute.getType();

        if (type == null) {
            return convertUndefinedType(attributeSer, value);
        }

        if (isSimpleValue(type)) {
            return convertSimpleValue(attributeSer, type, value);
        }

        return convertCustomValue(attributeSer, type, value);
    }

    private AttributeSer convertUndefinedType(AttributeSer attributeSer, Object value) {
        String type = Expression.Undefined.class.getName();
        if (value == null) {
            attributeSer.withValue(new ValueSer()
                    .withType(type)
                    .withValue(StringTypeConverter.NULL_LITERAL));
        } else if (isSimpleValue(value.getClass())) {
            attributeSer.withValue(new ValueSer()
                    .withType(type)
                    .withValue(typeConverter.toString(value.getClass(), value)));
        } else {
            attributeSer.withCustomValue(new CustomValueSer()
                    .withType(type)
                    .withAny(value));
        }
        return attributeSer;
    }

    private boolean isSimpleValue(Type type) {
        return typeConverter.isApplicable(type);
    }

    private AttributeSer convertSimpleValue(AttributeSer attributeSer, Type type, Object value) {
        return attributeSer
                .withValue(new ValueSer()
                        .withType(typeConverter.toString(Type.class, type))
                        .withValue(typeConverter.toString(type, value)));
    }

    private AttributeSer convertCustomValue(AttributeSer attributeSer, Type type, Object value) {
        return attributeSer
                .withCustomValue(new CustomValueSer()
                        .withType(typeConverter.toString(Type.class, type))
                        .withAny(value));
    }
}
