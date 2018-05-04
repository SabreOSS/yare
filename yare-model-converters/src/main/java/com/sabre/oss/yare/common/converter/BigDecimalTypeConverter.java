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

package com.sabre.oss.yare.common.converter;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Objects;

public class BigDecimalTypeConverter implements TypeConverter {

    @Override
    public boolean isApplicable(Type type) {
        return type instanceof Class<?> && BigDecimal.class.isAssignableFrom((Class<?>) type);
    }

    @Override
    public BigDecimal fromString(Type ignored, String value) {
        try {
            return StringTypeConverter.NULL_LITERAL.equals(value) || value == null ? null : new BigDecimal(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Unable convert to a BigDecimal: " + value, e);
        }
    }

    @Override
    public String toString(Type ignored, Object value) {
        return Objects.isNull(value)
                ? StringTypeConverter.NULL_LITERAL
                : value.toString();
    }
}
