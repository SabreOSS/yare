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

import com.sabre.oss.yare.common.converter.TypeConverter;
import com.sabre.oss.yare.common.mapper.Mapper;
import com.sabre.oss.yare.core.model.Attribute;
import com.sabre.oss.yare.serializer.model.AttributeSer;

import java.lang.reflect.Type;

import static java.util.Objects.requireNonNull;

class AttributeConverter implements Mapper<Attribute, AttributeSer> {
    private final TypeConverter typeConverter;

    AttributeConverter(TypeConverter typeConverter) {
        this.typeConverter = requireNonNull(typeConverter, "typeConverter cannot be null");
    }

    @Override
    public AttributeSer map(Attribute attribute) {
        return new AttributeSer()
                .withName(attribute.getName())
                .withType(typeConverter.toString(Type.class, attribute.getType()))
                .withValue(typeConverter.toString(attribute.getType(), attribute.getValue()));
    }
}
