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

import com.sabre.oss.yare.core.call.Argument;
import com.sabre.oss.yare.core.call.Argument.Reference;
import com.sabre.oss.yare.core.call.Argument.Value;
import com.sabre.oss.yare.core.call.ProcessingInvocationFactory;
import com.sabre.oss.yare.core.call.VariableResolver;
import com.sabre.oss.yare.engine.executor.runtime.predicate.PredicateContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.sabre.oss.yare.core.call.Argument.UNKNOWN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultArgumentValueResolverTest {
    private ProcessingInvocationFactory<Object> functionProcessingInvocationFactory;
    private DefaultArgumentValueResolver defaultArgumentValueResolver;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        functionProcessingInvocationFactory = mock(ProcessingInvocationFactory.class);
        defaultArgumentValueResolver = new DefaultArgumentValueResolver(functionProcessingInvocationFactory);
    }

    @Test
    void shouldProperlyResolveValueArgument() {
        // given
        Object expectedValue = new Object();
        Value valueArgument = Argument.valueOf("myValue", expectedValue);

        // when
        Object resolvedValue = defaultArgumentValueResolver.resolve(null, valueArgument);

        // then
        assertThat(resolvedValue).isEqualTo(expectedValue);
    }

    @Test
    void shouldProperlyResolveReferenceArgument() {
        // given
        Reference expectedDirectValueRef = Argument.referenceOf("myReference", MyProperty.class, UNKNOWN, "myProperty.value");
        Reference expectedValueARef = Argument.referenceOf("myProperty", MyProperty.class, UNKNOWN, "myProperty.nested.value");
        Reference expectedValueBRef = Argument.referenceOf("myProperty", MyProperty.class, UNKNOWN, "myProperty.nested.nested.value");

        VariableResolver variableResolver = mock(PredicateContext.class);
        when(variableResolver.resolve("myProperty")).thenReturn(
                new MyProperty("directValue",
                        new MyProperty("valueA",
                                new MyProperty("valueB"))));

        // when
        Object resultDirectValueRef = defaultArgumentValueResolver.resolve(variableResolver, expectedDirectValueRef);
        Object resultValueARef = defaultArgumentValueResolver.resolve(variableResolver, expectedValueARef);
        Object resultValueBRef = defaultArgumentValueResolver.resolve(variableResolver, expectedValueBRef);

        // then
        assertThat(resultDirectValueRef).isEqualTo("directValue");
        assertThat(resultValueARef).isEqualTo("valueA");
        assertThat(resultValueBRef).isEqualTo("valueB");
    }

    public static class MyProperty {
        private String value;
        private MyProperty nested;

        public MyProperty(String value) {
            this.value = value;
        }

        public MyProperty(String value, MyProperty nested) {
            this.value = value;
            this.nested = nested;
        }

        public MyProperty getNested() {
            return nested;
        }

        public String getValue() {
            return value;
        }
    }
}
