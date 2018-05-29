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
import com.sabre.oss.yare.engine.executor.runtime.predicate.PredicateContext;
import com.sabre.oss.yare.engine.executor.runtime.value.FieldReferringClassFactory;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class DefaultArgumentValueResolver implements ArgumentValueResolver {

    private final ProcessingInvocationFactory<Object> processingInvocationFactory;

    public DefaultArgumentValueResolver(ProcessingInvocationFactory<Object> processingInvocationFactory) {
        this.processingInvocationFactory = requireNonNull(processingInvocationFactory);
    }

    @Override
    public Object resolve(VariableResolver variableResolver, Argument argument) {
        if (argument instanceof Argument.Value) {
            return ((Argument.Value) argument).getValue();
        }
        if (argument instanceof Argument.Values) {
            return ((Argument.Values) argument).getArguments().stream()
                    .map(a -> resolve(variableResolver, a))
                    .collect(Collectors.toList());
        }
        if (argument instanceof Argument.Reference) {
            if (!(variableResolver instanceof PredicateContext)) {
                throw new IllegalArgumentException("Expected PredicateContext as VariableResolver");
            }
            Argument.Reference reference = (Argument.Reference) argument;
            String path = reference.getReference();
            int dotIndex = path.indexOf(".");
            if (dotIndex == -1) {
                return variableResolver.resolve(path);
            }
            String identifier = path.substring(0, dotIndex);
            path = path.substring(dotIndex + 1);
            Object resolvedValue = variableResolver.resolve(identifier);
            Class referenceType = TypeUtils.getRawType(reference.getReferenceType(), null);
            referenceType = resolvedValue != null && Object.class.equals(referenceType)
                    ? resolvedValue.getClass()
                    : referenceType;
            return FieldReferringClassFactory.create(referenceType, identifier, path).get((PredicateContext) variableResolver);
        }
        if (argument instanceof Argument.Invocation) {
            if (!(variableResolver instanceof ProcessingContext)) {
                throw new IllegalArgumentException("Expected ProcessingContext as VariableResolver");
            }
            ProcessingContext processingContext = (ProcessingContext) variableResolver;
            Invocation<ProcessingContext, Object> value = processingInvocationFactory.create((Argument.Invocation) argument);
            return value.proceed(processingContext);
        }

        throw new IllegalArgumentException(String.format("Unsupported argument type %s", argument.getClass()));
    }
}
