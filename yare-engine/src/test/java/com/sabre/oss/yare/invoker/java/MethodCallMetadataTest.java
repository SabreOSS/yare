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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.sabre.oss.yare.invoker.java.MethodCallMetadata.method;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class MethodCallMetadataTest {

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

    @ParameterizedTest
    @MethodSource("validImplementations")
    void shouldCreateProxyForGivenMethodCall(Collector collector) {
        method(collector, a -> a.collect(null, null));
    }

    private static Stream<Collector> validImplementations() {
        return Stream.of(
                new PublicCollector(),
                new PublicCollector.StaticPublicInner(),
                new PublicCollector.StaticPublicInnerWithDefaultPublicConstructor()
        );
    }

    @ParameterizedTest
    @MethodSource("invalidImplementations")
    void shouldNotCreateProxyForGivenMethodCall(Collector collector, String message) {
        assertThatThrownBy(() -> method(collector, a -> a.collect(null, null)))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    private static Stream<Arguments> invalidImplementations() {
        return Stream.of(
                Arguments.of(
                        new PublicCollector().new PublicInner(),
                        "Class com.sabre.oss.yare.invoker.java.PublicCollector.PublicInner has to be static!"
                ),
                Arguments.of(
                        new PublicCollector().new PackageInner(),
                        "Class com.sabre.oss.yare.invoker.java.PublicCollector.PackageInner has to be static!"
                ),
                Arguments.of(
                        new PublicCollector.StaticPackageInner(),
                        "Class com.sabre.oss.yare.invoker.java.PublicCollector.StaticPackageInner has to be public!"
                ),
                Arguments.of(
                        new PublicCollector.StaticPublicInner.StaticPublicTooDeeplyNested(),
                        "Class com.sabre.oss.yare.invoker.java.PublicCollector.StaticPublicInner.StaticPublicTooDeeplyNested is too deeply nested!"
                ),
                Arguments.of(
                        new PublicCollector().new PublicInner().new PublicTooDeeplyNested(),
                        "Class com.sabre.oss.yare.invoker.java.PublicCollector.PublicInner.PublicTooDeeplyNested is too deeply nested!"
                ),
                Arguments.of(
                        new PublicCollector.StaticPublicInnerWithNoDefaultConstructor(0),
                        "Class com.sabre.oss.yare.invoker.java.PublicCollector.StaticPublicInnerWithNoDefaultConstructor needs to have a public default constructor!"
                ),
                Arguments.of(
                        new PublicCollector.StaticPublicInnerWithDefaultPackageConstructor(),
                        "Class com.sabre.oss.yare.invoker.java.PublicCollector.StaticPublicInnerWithDefaultPackageConstructor needs to have a public default constructor!"
                ),
                Arguments.of(
                        new PackageCollector(),
                        "Class com.sabre.oss.yare.invoker.java.PackageCollector has to be public!"
                ),
                Arguments.of(
                        new PackageCollector().new PublicInner(),
                        "Class com.sabre.oss.yare.invoker.java.PackageCollector has to be public!"
                ),
                Arguments.of(
                        new PackageCollector().new PackageInner(),
                        "Class com.sabre.oss.yare.invoker.java.PackageCollector has to be public!"
                ),
                Arguments.of(
                        new PackageCollector.StaticPublicInner(),
                        "Class com.sabre.oss.yare.invoker.java.PackageCollector has to be public!"
                ),
                Arguments.of(
                        new PackageCollector.StaticPackageInner(),
                        "Class com.sabre.oss.yare.invoker.java.PackageCollector has to be public!"
                )
        );
    }

    @Test
    void shouldMapPublicMethodCorrectly() {
        method(this, a -> a.publicCollect(null, null));
    }

    @Test
    void shouldFailWhenMappingPackageAccessMethod() {
        assertThatThrownBy(() -> method(this, a -> a.packageCollect(null, null)))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("" +
                        "Method void com.sabre.oss.yare.invoker.java.MethodCallMetadataTest.packageCollect(java.util.List,java.lang.Object)" +
                        " in class com.sabre.oss.yare.invoker.java.MethodCallMetadataTest must be public!");
    }

    @Test
    void shouldFailWhenMappingPrivateAccessMethod() {
        assertThatThrownBy(() -> method(this, a -> a.privateCollect(null, null)))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldFailWhenMappingPublicStaticMethod() {
        assertThatThrownBy(() -> method(this, a -> a.publicStaticCollect(null, null)))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldMapPublicMethodInDifferentPublicAccessClassInstanceCorrectly() {
        SimplePublicCollector simplePublicCollector = new SimplePublicCollector();
        method(simplePublicCollector, a -> a.publicCollect(null, null));
    }

    @Test
    void shouldFailWhenMappingPackageAccessMethodInDifferentPublicAccessClassInstance() {
        SimplePublicCollector simplePublicCollector = new SimplePublicCollector();
        assertThatThrownBy(() -> method(simplePublicCollector, a -> a.packageCollect(null, null)))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("" +
                        "Method void com.sabre.oss.yare.invoker.java.SimplePublicCollector.packageCollect(java.util.List,java.lang.Object)" +
                        " in class com.sabre.oss.yare.invoker.java.SimplePublicCollector must be public!");
    }

    @Test
    void shouldFailWhenMappingPublicAccessMethodInDifferentPackageAccessClassInstance() {
        SimplePackageCollector simplePackageCollector = new SimplePackageCollector();
        assertThatThrownBy(() -> method(simplePackageCollector, a -> a.publicCollect(null, null)))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldFailWhenMappingPackageAccessMethodInDifferentPackageAccessClassInstance() {
        SimplePackageCollector simplePackageCollector = new SimplePackageCollector();
        assertThatThrownBy(() -> method(simplePackageCollector, a -> a.packageCollect(null, null)))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    public void publicCollect(List<Object> context, Object object) {
    }

    void packageCollect(List<Object> context, Object object) {
    }

    private void privateCollect(List<Object> context, Object object) {
    }

    public static void publicStaticCollect(List<Object> context, Object object) {
    }

    public static class AnAction {

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
