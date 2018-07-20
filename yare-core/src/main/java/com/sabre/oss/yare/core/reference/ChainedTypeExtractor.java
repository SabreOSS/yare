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

package com.sabre.oss.yare.core.reference;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ChainedTypeExtractor {
    private static final ConcurrentMap<Type, ConcurrentMap<String, Type>> typeCache = new ConcurrentHashMap<>();

    public Type findPathType(Type type, String path) {
        String[] pathParts = path.split("\\.", -1);
        Type currentType = type;
        Iterator<String> iterator = Arrays.asList(pathParts).iterator();
        while (iterator.hasNext()) {
            String pathPart = iterator.next();
            Type finalCurrentType = currentType;
            currentType = typeCache
                    .computeIfAbsent(currentType, k -> new ConcurrentHashMap<>())
                    .computeIfAbsent(pathPart, s -> computeTypeFromProperty(finalCurrentType, s));
            if (iterator.hasNext() && isCollection(currentType)) {
                return List.class;
            }
        }
        return currentType;
    }

    private Type computeTypeFromProperty(Type type, String pathPart) {
        return computeTypeOfReference(unwrapCollectionType(type), pathPart.replace("[*]", ""));
    }

    private Type unwrapCollectionType(Type type) {
        return isCollection(type) ? getTypeOfElementInList(type) : type;
    }

    private boolean isCollection(Type type) {
        return type instanceof ParameterizedType &&
                Collection.class.isAssignableFrom((Class<?>) ((ParameterizedType) type).getRawType());
    }

    private Type getTypeOfElementInList(Type listType) {
        return ((ParameterizedType) listType).getActualTypeArguments()[0];
    }

    private Type computeTypeOfReference(Type type, String pathPart) {
        Optional<Type> map = extractMapType(type);
        if (map.isPresent()) {
            return map.get();
        }
        Optional<Type> field = extractFieldType((Class<?>) type, pathPart);
        if (field.isPresent()) {
            return field.get();
        }
        Optional<Type> method = extractMethodType((Class<?>) type, pathPart);
        if (method.isPresent()) {
            return method.get();
        }
        throw new InvalidPathException();
    }

    private Optional<Type> extractMapType(Type type) {
        return Optional.of(type)
                .map(Class.class::cast)
                .filter(Map.class::isAssignableFrom)
                .map(Class::getGenericSuperclass)
                .filter(ParameterizedType.class::isInstance)
                .map(ParameterizedType.class::cast)
                .map(p -> p.getActualTypeArguments()[1]);
    }

    private Optional<Type> extractFieldType(Class<?> type, String fieldName) {
        return Arrays.stream(type.getFields())
                .filter(f -> Modifier.isPublic(f.getModifiers()))
                .filter(f -> f.getName().equals(fieldName))
                .map(Field::getGenericType)
                .findFirst();
    }

    private Optional<Type> extractMethodType(Class<?> type, String fieldName) {
        return Arrays.stream(type.getMethods())
                .filter(m -> m.getParameterCount() == 0)
                .filter(m -> (m.getName().equals("get" + capitalize(fieldName)) ||
                        m.getName().equals("is" + capitalize(fieldName))))
                .map(Method::getGenericReturnType)
                .findFirst();
    }

    private String capitalize(String fieldName) {
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    public static class InvalidPathException extends RuntimeException {
    }
}
