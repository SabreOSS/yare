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

import com.sabre.oss.yare.core.model.type.InternalParameterizedType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sabre.oss.yare.common.converter.StringTypeConverter.NULL_LITERAL;

/**
 * {@link TypeTypeConverter} is able to fromString string to {@link Type}.
 * <p>
 * The format is given below:
 * <pre>
 *     type        := &lt;identifier&gt; | &lt;identifier&gt; '&lt;' &lt;identifiers&gt; '&gt;'
 *     identifiers := &lt;identifier&gt; | &lt;identifier&gt; ',' &lt;identifiers&gt;
 *     identifier  := qualified Java class name
 * </pre>
 * <p>
 * Examples:
 * <pre>
 *     java.lang.String
 *     java.util.Map&lt;java.lang.String,java.lang.Object&gt;
 * </pre>
 */
public class TypeTypeConverter implements TypeConverter {
    private static final Pattern typePattern = Pattern.compile("([a-zA-Z][a-zA-Z0-9_$\\.]*)(?:<((?:,{0,1}[a-zA-Z][a-zA-Z0-9_$\\.]*)*)>){0,1}");

    private final Map<String, Type> stringToTypeCache = new ConcurrentHashMap<>();
    private final Map<Type, String> typeToStringCache = new ConcurrentHashMap<>();

    @Override
    public boolean isApplicable(Type type) {
        return Type.class.equals(type);
    }

    @Override
    public Object fromString(Type ignored, String value) {
        Type result = stringToTypeCache.get(value);
        if (result == null) {
            result = convertString(value);
            Type prev = stringToTypeCache.putIfAbsent(value, result);
            result = prev != null ? prev : result;
        }
        return result;
    }

    @Override
    public String toString(Type ignored, Object value) {
        if (Objects.isNull(value)) {
            return NULL_LITERAL;
        }
        String result = typeToStringCache.get(value);
        if (result == null) {
            result = convertType(value);
            String prev = typeToStringCache.putIfAbsent((Type) value, result);
            result = prev != null ? prev : result;
        }
        return result;
    }

    private Type convertString(String value) {
        Matcher matcher = typePattern.matcher(value);
        if (matcher.matches()) {
            String rawTypeName = matcher.group(1);
            String parameters = matcher.group(2);

            if (parameters == null || parameters.isEmpty()) {
                try {
                    return Thread.currentThread().getContextClassLoader().loadClass(rawTypeName);
                } catch (ClassNotFoundException e) {
                    int lastDot = rawTypeName.lastIndexOf('.');
                    if (lastDot != -1) {
                        String innerClassName = rawTypeName.substring(0, lastDot) + '$' + rawTypeName.substring(lastDot + 1);
                        try {
                            return Thread.currentThread().getContextClassLoader().loadClass(innerClassName);
                        } catch (ClassNotFoundException ex) {
                            throw new IllegalArgumentException(String.format("Can't convert '%s' into Java type", innerClassName));
                        }
                    }
                    throw new IllegalArgumentException(String.format("Can't convert '%s' into Java type", rawTypeName));
                }
            } else {
                Type rawType = fromString(Type.class, rawTypeName);
                List<Type> parameterTypes = new ArrayList<>();
                for (String paramTypeName : parameters.split(",")) {
                    parameterTypes.add(fromString(Type.class, paramTypeName));
                }
                return new InternalParameterizedType(null, (Class<?>) rawType, parameterTypes.toArray(new Type[0]));
            }
        }
        throw new IllegalArgumentException(String.format("Can't convert '%s' into Java type", value));
    }

    private String convertType(Object value) {
        if (value instanceof Class) {
            return ((Class<?>) value).getCanonicalName();
        }
        if (value instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) value;
            String prefix = toString(null, parameterizedType.getRawType()) + '<';
            StringJoiner stringJoiner = new StringJoiner(",", prefix, ">");
            for (Type paramType : parameterizedType.getActualTypeArguments()) {
                stringJoiner.add(toString(null, paramType));
            }
            return stringJoiner.toString();
        }
        throw new IllegalArgumentException("Unsupported type");
    }
}
