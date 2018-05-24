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

import com.sabre.oss.yare.core.call.ProcessingContext;
import com.sabre.oss.yare.core.invocation.Invocation;

import java.lang.reflect.Type;
import java.util.List;

public final class ValueProviderFactory {

    private ValueProviderFactory() {
    }

    public static ValueProvider constantTrue() {
        return createFromConstant(Boolean.TRUE);
    }

    public static ValueProvider constantFalse() {
        return createFromConstant(Boolean.FALSE);
    }

    public static ValueProvider constantNull() {
        return createFromConstant(null);
    }

    public static ValueProvider createFromConstant(Object value) {
        return new ConstantValueProvider(value);
    }

    public static ValueProvider createFromPath(Class<?> referenceType, String reference, Class<?> type, String path) {
        return FieldReferringClassFactory.create(referenceType, reference, path);
    }

    public static ValueProvider createFromMapKey(String reference, String key) {
        return new MapValueProvider(reference, key);
    }

    public static ValueProvider createFromInvocation(Invocation<ProcessingContext, Object> invocation) {
        return new InvocationBasedValueProvider(invocation);
    }

    public static ValueProvider createFromValues(Type type, List<ValueProvider> values) {
        return new ValuesValueProvider(type, values);
    }
}
