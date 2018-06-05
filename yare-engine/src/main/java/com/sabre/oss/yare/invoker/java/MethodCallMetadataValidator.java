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

package com.sabre.oss.yare.invoker.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

final class MethodCallMetadataValidator {
    private static final String METHOD_NONPUBLIC = "Method %s in %s must be public!";
    private static final String METHOD_STATIC = "Method %s in %s cannot be static!";
    private static final String CLASS_TOO_DEEPLY_NESTED = "Class %s is too deeply nested!";
    private static final String CLASS_NO_DEF_CONSTRUCTOR = "Class %s needs to have a public default constructor!";
    private static final String CLASS_NONSTATIC = "Class %s has to be static!";
    private static final String CLASS_NON_PUBLIC = "Class %s has to be public!";

    private MethodCallMetadataValidator() {
    }

    static void validate(Class<?> clazz) throws IllegalArgumentException {
        Class<?> outerClazz = clazz.getDeclaringClass();
        if (outerClazz != null) {
            checkTooDeeplyNested(clazz, outerClazz);
            checkPublic(outerClazz);
            checkStatic(clazz);
            checkDefaultConstructor(outerClazz);
        }
        checkPublic(clazz);
        checkDefaultConstructor(clazz);
    }

    static void validate(Method method) {
        if (!Modifier.isPublic(method.getModifiers())) {
            throw new IllegalArgumentException(String.format(METHOD_NONPUBLIC, method, method.getDeclaringClass()));
        }
        if (Modifier.isStatic(method.getModifiers())) {
            throw new IllegalArgumentException(String.format(METHOD_STATIC, method, method.getDeclaringClass()));
        }
    }

    private static void checkTooDeeplyNested(Class<?> clazz, Class<?> outerClazz) {
        if (outerClazz.getDeclaringClass() != null) {
            throw new IllegalArgumentException(String.format(CLASS_TOO_DEEPLY_NESTED, clazz.getCanonicalName()));
        }
    }

    private static void checkDefaultConstructor(Class<?> outerClazz) {
        if (!hasDefaultPublicConstructor(outerClazz)) {
            throw new IllegalArgumentException(String.format(CLASS_NO_DEF_CONSTRUCTOR, outerClazz.getCanonicalName()));
        }
    }

    private static void checkStatic(Class<?> clazz) {
        if (!Modifier.isStatic(clazz.getModifiers())) {
            throw new IllegalArgumentException(String.format(CLASS_NONSTATIC, clazz.getCanonicalName()));
        }
    }

    private static void checkPublic(Class<?> outerClazz) {
        if (!Modifier.isPublic(outerClazz.getModifiers())) {
            throw new IllegalArgumentException(String.format(CLASS_NON_PUBLIC, outerClazz.getCanonicalName()));
        }
    }

    private static boolean hasDefaultPublicConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == 0) {
                return true;
            }
        }
        return false;
    }
}
