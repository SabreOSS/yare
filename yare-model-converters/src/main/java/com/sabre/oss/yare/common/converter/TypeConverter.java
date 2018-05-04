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

public interface TypeConverter {
    /**
     * Check if this converter support specified {@code type}.
     *
     * @param type to check if supported
     * @return true if this converter is able to fromString specified {@code type}, false otherwise
     */
    boolean isApplicable(Type type);

    /**
     * Converts string representation of value into object of specified type.
     *
     * @param type  expected type
     * @param value string representation
     * @return string converted to expected type
     */
    Object fromString(Type type, String value);

    /**
     * Converts object into string representation of the object.
     *
     * @param type  expected type
     * @param value string representation
     * @return string converted to expected type
     */
    String toString(Type type, Object value);

    /**
     * Converts string representation of value into object of specified type.
     *
     * @param type  expected type
     * @param value string representation
     * @param <T>   expected type
     * @return string converted to expected type
     */
    @SuppressWarnings("unchecked")
    default <T> T fromString(Class<T> type, String value) {
        return (T) fromString((Type) type, value);
    }
}
