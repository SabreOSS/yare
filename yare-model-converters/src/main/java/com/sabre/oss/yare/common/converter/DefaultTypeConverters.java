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

import static java.util.Arrays.asList;

/**
 * A utility class which configures {@link TypeConverter} with the default set of type converters.
 */
public abstract class DefaultTypeConverters {
    private static final TypeConverter DEFAULT_AGGREGATE_TYPE_CONVERTER = prepareDefaultTypeConverter();

    /**
     * Returns pre-configured aggregate {@link TypeConverter}.
     *
     * @return {@link TypeConverter}
     */
    public static TypeConverter getDefaultTypeConverter() {
        return DEFAULT_AGGREGATE_TYPE_CONVERTER;
    }

    /**
     * Prepares default aggregate type converter that is able to handle simple types.
     *
     * @return aggregated {@link TypeConverter}
     */
    private static TypeConverter prepareDefaultTypeConverter() {
        return new ChainedTypeConverter(asList(
                new TypeTypeConverter(),
                new StringTypeConverter(),
                new BooleanTypeConverter(),
                new IntegerTypeConverter(),
                new LongTypeConverter(),
                new BigDecimalTypeConverter(),
                new ZonedDateTimeConverter()
        ));
    }
}
