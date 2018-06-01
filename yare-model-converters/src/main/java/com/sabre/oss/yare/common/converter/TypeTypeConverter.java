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

import com.sabre.oss.yare.common.converter.aliases.TypeAliasResolver;
import com.sabre.oss.yare.core.model.Expression;
import com.sabre.oss.yare.core.model.type.InternalParameterizedType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static final Pattern typePattern =
            Pattern.compile("([a-zA-Z][a-zA-Z0-9_$\\.]*)(?:<((?:,{0,1}[a-zA-Z][a-zA-Z0-9_$\\.]*)*)>){0,1}");

    private final Map<String, Type> stringToTypeCache = new ConcurrentHashMap<>();
    private final Map<Type, String> typeToStringCache = new ConcurrentHashMap<>();

    private final TypeAliasResolver typeAliasResolver = new TypeAliasResolver();

    @Override
    public boolean isApplicable(Type type) {
        return Type.class.equals(type);
    }

    @Override
    public Object fromString(Type ignored, String value) {
        if (value == null) {
            return Expression.Undefined.class;
        }
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
        if (value == null) {
            return Expression.Undefined.class.getName();
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
        return typeAliasResolver.hasAliasFor(value) ?
                typeAliasResolver.getAliasFor(value).getType() :
                convertRawTypeString(value);
    }

    private Type convertRawTypeString(String value) {
        Matcher matcher = typePattern.matcher(value);
        if (matcher.matches()) {
            String rawTypeName = matcher.group(1);
            String parameters = matcher.group(2);
            return isParametrizedType(parameters) ?
                    convertParametrizedTypeStrings(rawTypeName, parameters) :
                    convertSimpleTypeString(rawTypeName);
        }
        throw new IllegalArgumentException(String.format("Can't convert '%s' into Java type", value));
    }

    private Boolean isParametrizedType(String parameters) {
        return parameters != null && !parameters.isEmpty();
    }

    private Type convertParametrizedTypeStrings(String rawTypeName, String parameters) {
        List<Type> parameterTypes = new ArrayList<>();
        for (String paramTypeName : parameters.split(",")) {
            parameterTypes.add(fromString(Type.class, paramTypeName));
        }
        Class<?> rawType = (Class<?>) fromString(Type.class, rawTypeName);
        return new InternalParameterizedType(null, rawType, parameterTypes.toArray(new Type[0]));
    }

    private Type convertSimpleTypeString(String typeName) {
        return tryToLoadClass(typeName)
                .orElseGet(() -> loadAsInnerClass(typeName));
    }

    private Type loadAsInnerClass(String typeName) {
        return tryToResolveInnerClassName(typeName)
                .flatMap(this::tryToLoadClass)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Can't convert '%s' into Java type", typeName)));
    }

    private Optional<String> tryToResolveInnerClassName(String typeName) {
        int lastDot = typeName.lastIndexOf('.');
        if (lastDot != -1) {
            String innerClassName = typeName.substring(0, lastDot) + '$' + typeName.substring(lastDot + 1);
            return Optional.of(innerClassName);
        }
        return Optional.empty();
    }

    private Optional<Type> tryToLoadClass(String typeName) {
        try {
            Type type = Thread.currentThread().getContextClassLoader().loadClass(typeName);
            return Optional.of(type);
        } catch (ClassNotFoundException ex) {
            return Optional.empty();
        }
    }

    private String convertType(Object value) {
        if (value instanceof Class) {
            return convertType((Class<?>) value);
        }
        if (value instanceof ParameterizedType) {
            return convertType((ParameterizedType) value);
        }
        throw new IllegalArgumentException("Unsupported type");
    }

    private String convertType(Class<?> type) {
        return typeAliasResolver.hasAliasFor(type) ?
                typeAliasResolver.getAliasFor(type).getAlias() :
                type.getCanonicalName();
    }

    private String convertType(ParameterizedType type) {
        String prefix = toString(null, type.getRawType()) + '<';
        StringJoiner stringJoiner = new StringJoiner(",", prefix, ">");
        for (Type paramType : type.getActualTypeArguments()) {
            stringJoiner.add(toString(null, paramType));
        }
        return stringJoiner.toString();
    }
}
