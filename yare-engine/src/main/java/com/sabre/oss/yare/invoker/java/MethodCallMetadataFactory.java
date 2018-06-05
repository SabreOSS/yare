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

import com.google.common.base.Defaults;
import com.sabre.oss.yare.core.call.ProcessingContext;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.lang.String.format;

abstract class MethodCallMetadataFactory {
    public static final Type ANY = AnyType.class;
    private static final Map<Class<?>, Class<?>> proxies = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    static <T> MethodCallMetadata method(T instance, CatchingInvocation<T> invocation) {
        Class<?> targetType = instance.getClass();
        MethodCallMetadataValidator.validate(targetType);
        Class<?> proxyClass = proxies.computeIfAbsent(targetType, aClass -> {
            ProxyFactory proxy = new ProxyFactory();
            proxy.setSuperclass(targetType);
            return proxy.createClass();
        });

        try {
            AtomicReference<Method> calledMethod = new AtomicReference<>();
            Proxy proxy = (Proxy) proxyClass.<Proxy>newInstance();
            proxy.setHandler((self, method, proceed, args) -> {
                MethodCallMetadataValidator.validate(method);
                calledMethod.set(method);
                return Defaults.defaultValue(method.getReturnType());
            });
            invocation.prepare((T) proxy, null, null);
            if (Objects.nonNull(calledMethod.get())) {
                return MethodCallMetadata.method(instance, calledMethod.get());
            }
            throw new IllegalArgumentException("No method call on instance could be caught!");
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static MethodCallMetadata method(Object instance, String methodName, Type... parameterTypes) {
        Method method = extractMethod(instance.getClass(), methodName, Arrays.asList(parameterTypes));
        return MethodCallMetadata.method(instance, method);
    }

    static MethodCallMetadata method(Class<?> targetType, String methodName, Type... parameterTypes) {
        Method method = extractMethod(targetType, methodName, Arrays.asList(parameterTypes));
        return MethodCallMetadata.method(method);
    }

    private static Method extractMethod(Class<?> targetType, String methodName, List<Type> parameterTypes) {
        List<Method> candidates = Arrays.stream(targetType.getMethods())
                .filter(m -> m.getName().equals(methodName))
                .filter(m -> Modifier.isPublic(m.getModifiers()))
                .filter(m -> !Modifier.isAbstract(m.getModifiers()))
                .filter(m -> checkParametersCompatibility(m.getParameterTypes(), parameterTypes, false))
                .collect(Collectors.toList());

        List<Method> methods = candidates.stream()
                .filter(m -> checkParametersCompatibility(m.getParameterTypes(), parameterTypes, true))
                .collect(Collectors.toList());

        if (candidates.size() == 1) {
            return candidates.get(0);
        }
        if (methods.size() == 1) {
            return methods.get(0);
        }
        if (candidates.size() == 0) {
            throw new IllegalArgumentException(format("Call can't be mapped to method - method not found %s.%s(%s)",
                    targetType, methodName, parameterTypes.stream().map(Type::toString).collect(Collectors.joining(","))));
        }
        throw new IllegalArgumentException(format("Call '%s' can't be mapped to method - too many candidates found %s(%s)", targetType, methodName, parameterTypes));
    }

    private static boolean checkParametersCompatibility(Class<?>[] parameterTypes, List<Type> expectedParameterTypes, boolean strict) {
        if (expectedParameterTypes.size() == 1 && expectedParameterTypes.get(0) == ANY) {
            return true;
        }
        for (int i = 0, len = Math.min(parameterTypes.length, expectedParameterTypes.size()); i < len; i++) {
            Class<?> type = parameterTypes[i];
            Type expectedType = expectedParameterTypes.get(i);
            if (ANY.equals(expectedType)) {
                return true;
            }
            Class<?> expectedClass = expectedType instanceof ParameterizedType ? (Class<?>) ((ParameterizedType) expectedType).getRawType() : (Class<?>) expectedType;
            if (!(strict && type.equals(expectedClass) || !strict && type.isAssignableFrom(expectedClass))) {
                return false;
            }
        }
        return parameterTypes.length == expectedParameterTypes.size();
    }

    public interface CatchingInvocation<T> {
        void prepare(T target, ProcessingContext ctx, List<Object> args);
    }

    private static final class AnyType {
    }
}
