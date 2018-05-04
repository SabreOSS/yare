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

package com.sabre.oss.yare.engine;

import com.sabre.oss.yare.core.call.CallMetadata;
import com.sabre.oss.yare.core.call.InvocationFactory;
import com.sabre.oss.yare.core.call.ProcessingContext;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class MethodCallMetadata implements CallMetadata {
    private final Object instance;
    private final Method method;
    private final Routine<?, ?> routine;

    public MethodCallMetadata(Object instance, Method method, Routine<?, ?> routine) {
        this.instance = instance;
        this.method = method;
        this.routine = routine;
    }

    /**
     * Creates {@link MethodCallMetadata} based on target instance and {@link Method}.
     *
     * @param instance instance on which method is executed
     * @param method   method to call
     * @return {@link MethodCallMetadata}
     */
    public static MethodCallMetadata method(Object instance, Method method) {
        return new MethodCallMetadata(
                Objects.requireNonNull(instance),
                Objects.requireNonNull(method),
                null);
    }

    /**
     * Creates {@link MethodCallMetadata} based on static {@link Method}.
     *
     * @param method method to call
     * @return {@link MethodCallMetadata}
     */
    public static MethodCallMetadata method(Method method) {
        return new MethodCallMetadata(
                null,
                Objects.requireNonNull(method),
                null);
    }

    /**
     * Creates {@link MethodCallMetadata} based on target instance and {@link Routine}.
     *
     * @param instance instance on which method is executed
     * @param routine  call to execute
     * @param <T>      type method target
     * @return {@link MethodCallMetadata}
     */
    public static <T> MethodCallMetadata method(Object instance, Routine<T, Object> routine) {
        return new MethodCallMetadata(
                Objects.requireNonNull(instance),
                null,
                Objects.requireNonNull(routine));
    }

    /**
     * Creates {@link MethodCallMetadata} based on method described by {@code instance} {@code methodName}.
     * and {@code argumentsTypes}.
     *
     * @param instance       instance on which method is executed
     * @param methodName     method name
     * @param argumentsTypes types method method's arguments
     * @return {@link MethodCallMetadata}
     */
    public static MethodCallMetadata method(Object instance, String methodName, Type... argumentsTypes) {
        return MethodCallMetadataFactory.method(instance, methodName, argumentsTypes);
    }

    /**
     * Creates {@link MethodCallMetadata} based on method described by {@code targetType} {@code methodName}
     * and {@code argumentsTypes}.
     *
     * @param targetType     targetType in which {@code Method} exists
     * @param methodName     method name
     * @param argumentsTypes types method method's arguments
     * @return {@link MethodCallMetadata}
     */
    public static MethodCallMetadata method(Class<?> targetType, String methodName, Type... argumentsTypes) {
        return MethodCallMetadataFactory.method(targetType, methodName, argumentsTypes);
    }

    /**
     * Creates {@link MethodCallMetadata} based on method invocation described by {@code invocation}.
     * <p>
     * It should be noted that invocation implementation is used to determine called method. Arguments are ignored,
     * so {@code null} values passing is highly recommended.
     * <p>
     * Example:
     * <pre>
     * MethodCallMetadata call = MethodCallMetadata.method(instance, (i) -&gt; i.equals(null));
     * </pre>
     * <p>
     * will prepare {@link MethodCallMetadata} with 'equals" method assigned.
     *
     * @param instance           instance on which method is executed
     * @param catchingInvocation invocation implementation that performs method invocation on providing target instance
     * @param <T>                type method target
     * @return {@link MethodCallMetadata}
     */
    public static <T> MethodCallMetadata method(T instance, Consumer<T> catchingInvocation) {
        return MethodCallMetadataFactory.method(instance, (target, ctx, args) -> catchingInvocation.accept(target));
    }

    @Override
    public InvocationFactory getInvocationFactory() {
        return new MethodCallInvocationFactory(this);
    }

    public Object getInstance() {
        return instance;
    }

    public Method getMethod() {
        return method;
    }

    public Routine<?, ?> getRoutine() {
        return routine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MethodCallMetadata)) {
            return false;
        }
        MethodCallMetadata that = (MethodCallMetadata) o;
        return Objects.equals(instance, that.instance) &&
                Objects.equals(method, that.method) &&
                Objects.equals(routine, that.routine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instance, method, routine);
    }

    public interface Routine<T, R> {
        R call(T target, ProcessingContext ctx, List<Object> args);
    }
}
