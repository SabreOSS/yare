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

import com.sabre.oss.yare.core.call.*;
import com.sabre.oss.yare.core.invocation.Invocation;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sabre.oss.yare.core.call.Argument.UNKNOWN;
import static java.util.Objects.requireNonNull;

public class MethodCallInvocationFactory implements InvocationFactory {
    private static final String INCOMPATIBLE_ARGUMENTS = "Incompatible arguments for call: %s(%s), arguments: [%s]";

    private final MethodCallMetadata callMetadata;

    MethodCallInvocationFactory(MethodCallMetadata callMetadata) {
        this.callMetadata = callMetadata;
    }

    @Override
    public <R> Invocation<ProcessingContext, R> create(Supplier<ArgumentValueResolver> resolver, Argument.Invocation invocation) {
        Object instance = callMetadata.getInstance();
        Method method = callMetadata.getMethod();
        if (Objects.nonNull(method)) {
            validateArgumentsCompatibility(invocation.getCall(), method.getParameterTypes(), invocation.getArguments(), false);
            return new MethodBasedInvocation<>(invocation, resolver.get(), instance, method);
        } else {
            @SuppressWarnings("unchecked")
            MethodCallMetadata.Routine<Object, R> routine = (MethodCallMetadata.Routine<Object, R>) callMetadata.getRoutine();
            return new RoutineBasedInvocation<>(invocation, resolver.get(), instance, routine);
        }
    }

    private void validateArgumentsCompatibility(String call, Class<?>[] parameterTypes, List<Argument> arguments, boolean strictCheck) {
        if (parameterTypes.length != arguments.size()) {
            throw new IllegalStateException(String.format(INCOMPATIBLE_ARGUMENTS, call, toString(parameterTypes), toString(arguments)));
        }

        for (int i = 0, len = parameterTypes.length; i < len; i++) {
            Class<?> type = parameterTypes[i];
            Argument argument = arguments.get(i);
            if (!type.isAssignableFrom(extractClass(argument.getType()))
                    && (strictCheck || !UNKNOWN.equals(argument.getType()))) {
                throw new IllegalStateException(String.format(INCOMPATIBLE_ARGUMENTS, call, toString(parameterTypes), toString(arguments)));
            }
        }
    }

    private Class<?> extractClass(Type type) {
        return type instanceof ParameterizedType ? (Class<?>) ((ParameterizedType) type).getRawType() : (Class<?>) type;
    }

    private String toString(List<Argument> arguments) {
        return arguments.stream().map(a -> a.getType().getTypeName()).collect(Collectors.joining(", "));
    }

    private String toString(Class<?>[] parameterTypes) {
        return Stream.of(parameterTypes).map(Class::getTypeName).collect(Collectors.joining(", "));
    }

    static final class MethodBasedInvocation<R> extends ProcessingInvocation<R> {
        private final MethodHandle methodHandle;

        private MethodBasedInvocation(Argument.Invocation invocation, ArgumentValueResolver argumentValueResolver, Object target, Method method) {
            super(invocation, argumentValueResolver);
            try {
                MethodHandle methodHandle = MethodHandles.lookup().unreflect(method);
                this.methodHandle = target != null ? methodHandle.bindTo(target) : methodHandle;
            } catch (RuntimeException | Error e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public R call(ProcessingContext processingContext, Object[] args) {
            try {
                return (R) methodHandle.invokeWithArguments(args);
            } catch (RuntimeException | Error e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    static final class RoutineBasedInvocation<R> extends ProcessingInvocation<R> {
        private final Object target;
        private final MethodCallMetadata.Routine<Object, R> routine;

        private RoutineBasedInvocation(Argument.Invocation invocation, ArgumentValueResolver argumentValueResolver, Object target, MethodCallMetadata.Routine<Object, R> routine) {
            super(invocation, argumentValueResolver);
            this.target = target;
            this.routine = requireNonNull(routine);
        }

        @Override
        public R call(ProcessingContext processingContext, Object[] args) {
            return routine.call(target, processingContext, Arrays.asList(args));
        }
    }
}

