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

package com.sabre.oss.yare.engine.executor.runtime.value;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.sabre.oss.yare.engine.executor.runtime.value.TypeUtils.getRawType;

public class GetterReferMetadataCreator implements ReferMetadataCreator {

    @Override
    public boolean isApplicable(Type type, String ref) {
        Class<?> classType = getRawType(type);
        List<String> gettersNames = createGettersNames(classType, ref);
        return gettersNames.stream()
                .flatMap(getter -> Arrays.stream(classType.getMethods())
                        .filter(method -> method.getParameterCount() == 0)
                        .filter(method -> method.getName().equals(getter)))
                .findFirst()
                .isPresent();
    }

    @Override
    public ReferMetadata getReferMetadata(Type type, String path) {
        Class<?> classType = getRawType(type);
        List<String> gettersNames = createGettersNames(classType, path.replace("[*]", ""));
        Method getterMethod = gettersNames.stream()
                .flatMap(getter -> Arrays.stream(classType.getMethods())
                        .filter(method -> method.getParameterCount() == 0)
                        .filter(method -> method.getName().equals(getter)))
                .findFirst()
                .get();
        String getterName = getterMethod.getName() + "()";
        Type refType = getterMethod.getGenericReturnType();
        return new ReferMetadata(refType, getterName, path);
    }

    private List<String> createGettersNames(Class<?> clazz, String fieldName) {
        List<String> gettersNames = new LinkedList<>();
        gettersNames.add(createSimpleGetterName(fieldName));
        gettersNames.addAll(createBooleanGetterName(clazz, fieldName));
        return gettersNames;
    }

    private List<String> createBooleanGetterName(Class<?> baseClass, String fieldName) {
        return Arrays.stream(baseClass.getDeclaredFields())
                .filter(field -> field.getName().equals(fieldName))
                .filter(this::isBoolean)
                .map(field -> "is" + StringUtils.capitalize(fieldName))
                .collect(Collectors.toList());
    }

    private String createSimpleGetterName(String fieldName) {
        return "get" + StringUtils.capitalize(fieldName);
    }

    private boolean isBoolean(Field field) {
        Class<?> typeOfField = field.getType();
        return typeOfField.equals(boolean.class) || typeOfField.equals(Boolean.class);
    }
}
