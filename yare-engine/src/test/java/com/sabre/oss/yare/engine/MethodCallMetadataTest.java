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

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class MethodCallMetadataTest {

    @Test
    void shouldProperlyResolvePublicMethodViaInvocationCatching() {
        // given / when
        AnAction instance = new AnAction();
        MethodCallMetadata metadata = MethodCallMetadata.method(instance, (action) -> action.publicExecuteWith2Args((Map<?, ?>) null, null));

        // then
        assertThat(metadata.getInstance()).isSameAs(instance);
        assertThat(metadata.getMethod().getName()).isEqualTo("publicExecuteWith2Args");
        assertThat(metadata.getMethod().getParameterTypes()).isEqualTo(new Class<?>[]{Map.class, Object.class});
    }

    @Test
    void shouldProperlyResolvePublicOverloadedMethodBasedOnMethodNameAndParametersTypes() {
        // given / when
        AnAction instance1 = new AnAction();
        AnAction instance2 = new AnAction();
        MethodCallMetadata metadata = MethodCallMetadata.method(instance1, "publicExecuteWith2Args", List.class, Object.class);
        MethodCallMetadata metadata2 = MethodCallMetadata.method(instance2, "publicExecuteWith2Args", Map.class, Object.class);

        // then
        assertThat(metadata.getInstance()).isSameAs(instance1);
        assertThat(metadata.getMethod().getName()).isEqualTo("publicExecuteWith2Args");
        assertThat(metadata.getMethod().getParameterTypes()).isEqualTo(new Class<?>[]{List.class, Object.class});

        assertThat(metadata2.getInstance()).isSameAs(instance2);
        assertThat(metadata2.getMethod().getName()).isEqualTo("publicExecuteWith2Args");
        assertThat(metadata2.getMethod().getParameterTypes()).isEqualTo(new Class<?>[]{Map.class, Object.class});
    }

    @Test
    void shouldNotResolveProtectedMethod() {
        assertThatThrownBy(() -> MethodCallMetadata.method(new AnAction(), "protectedExecute"))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldNotResolveDefaultScopedMethod() {
        assertThatThrownBy(() -> MethodCallMetadata.method(new AnAction(), "defaultExecute"))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    static class AnAction {

        public void publicExecute() {
        }

        public void publicExecuteWith2Args(Map<?, ?> map, Object object) {
        }

        public void publicExecuteWith2Args(List<?> list, Object object) {
        }

        protected void protectedExecute() {
        }

        void defaultExecute() {
        }
    }
}
