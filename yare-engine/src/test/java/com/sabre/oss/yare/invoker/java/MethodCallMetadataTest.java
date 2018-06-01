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

import com.sabre.oss.yare.engine.Collector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.sabre.oss.yare.invoker.java.MethodCallMetadata.method;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    public void shouldCreateProxyForGivenMethodCall(Collector collector) {
        method(collector, a -> a.collect(null, null));
    }

    private static Stream<Collector> validImplementations() {
        PublicCollector collector1 = new PublicCollector();
        PublicCollector.StaticPublicInnerInPublicOuter collector2 = new PublicCollector.StaticPublicInnerInPublicOuter();
        PublicCollector.StaticPublicInnerInPublicOuterWithDefaultPublicConstructor collector3 = new PublicCollector.StaticPublicInnerInPublicOuterWithDefaultPublicConstructor();
        return Stream.of(
                collector1,
                collector2,
                collector3
        );
    }

    @ParameterizedTest
    @MethodSource("invalidImplementations")
    public void shouldNotCreateProxyForGivenMethodCall(AbstractMap.SimpleEntry<Collector, String> entry) {
        //given
        Collector collector = entry.getKey();
        String message = entry.getValue();
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> method(collector, a -> a.collect(null, null)));
        //then
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    private static Stream<AbstractMap.SimpleEntry<Collector, String>> invalidImplementations() {
        PublicCollector collector1 = new PublicCollector();
        PublicCollector.PublicInnerInPublicOuter collector2 = collector1.new PublicInnerInPublicOuter();
        PublicCollector.PackageInnerInPublicOuter collector3 = collector1.new PackageInnerInPublicOuter();
        PublicCollector.StaticPackageInnerInPublicOuter collector4 = new PublicCollector.StaticPackageInnerInPublicOuter();
        PublicCollector.StaticPublicInnerInPublicOuter.StaticPublicTooDeeplyNested collector5 = new PublicCollector.StaticPublicInnerInPublicOuter.StaticPublicTooDeeplyNested();
        PublicCollector.PublicInnerInPublicOuter.PublicTooDeeplyNested collector6 = collector2.new PublicTooDeeplyNested();
        PublicCollector.StaticPublicInnerInPublicOuterWithNoDefaultConstructor collector7 = new PublicCollector.StaticPublicInnerInPublicOuterWithNoDefaultConstructor(0);
        PublicCollector.StaticPublicInnerInPublicOuterWithDefaultPackageConstructor collector8 = new PublicCollector.StaticPublicInnerInPublicOuterWithDefaultPackageConstructor();

        PackageCollector collector9 = new PackageCollector();
        PackageCollector.PublicInnerInPackageOuter collector10 = collector9.new PublicInnerInPackageOuter();
        PackageCollector.PackageInnerInPackageOuter collector11 = collector9.new PackageInnerInPackageOuter();
        PackageCollector.StaticPublicInnerInPackageOuter collector12 = new PackageCollector.StaticPublicInnerInPackageOuter();
        PackageCollector.StaticPackageInnerInPackageOuter collector13 = new PackageCollector.StaticPackageInnerInPackageOuter();

        return Stream.of(
                new AbstractMap.SimpleEntry<>(collector2, String.format(MethodCallMetadataValidator.CLASS_NONSTATIC, collector2.getClass())),
                new AbstractMap.SimpleEntry<>(collector3, String.format(MethodCallMetadataValidator.CLASS_NONSTATIC, collector3.getClass())),
                new AbstractMap.SimpleEntry<>(collector4, String.format(MethodCallMetadataValidator.CLASS_NON_PUBLIC, collector4.getClass())),
                new AbstractMap.SimpleEntry<>(collector5, String.format(MethodCallMetadataValidator.CLASS_TOO_DEEPLY_NESTED, collector5.getClass())),
                new AbstractMap.SimpleEntry<>(collector6, String.format(MethodCallMetadataValidator.CLASS_TOO_DEEPLY_NESTED, collector6.getClass())),
                new AbstractMap.SimpleEntry<>(collector7, String.format(MethodCallMetadataValidator.CLASS_NO_DEF_CONSTRUCTOR, collector7.getClass())),
                new AbstractMap.SimpleEntry<>(collector8, String.format(MethodCallMetadataValidator.CLASS_NO_DEF_CONSTRUCTOR, collector8.getClass())),
                new AbstractMap.SimpleEntry<>(collector9, String.format(MethodCallMetadataValidator.CLASS_NON_PUBLIC, collector9.getClass())),
                new AbstractMap.SimpleEntry<>(collector10, String.format(MethodCallMetadataValidator.CLASS_NON_PUBLIC, collector10.getClass().getDeclaringClass())),
                new AbstractMap.SimpleEntry<>(collector11, String.format(MethodCallMetadataValidator.CLASS_NON_PUBLIC, collector11.getClass().getDeclaringClass())),
                new AbstractMap.SimpleEntry<>(collector12, String.format(MethodCallMetadataValidator.CLASS_NON_PUBLIC, collector12.getClass().getDeclaringClass())),
                new AbstractMap.SimpleEntry<>(collector13, String.format(MethodCallMetadataValidator.CLASS_NON_PUBLIC, collector13.getClass().getDeclaringClass()))
        );
    }

    @Test
    void shouldMapPublicMethodCorrectly() {
        method(this, a -> a.publicCollect(null, null));
    }

    @Test
    void shouldFailWhenMappingPackageAccessMethod() {
        assertThatThrownBy(() -> method(this, a -> a.packageCollect(null, null)))
                .isExactlyInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldFailWhenMappingPrivateAccessMethod() {
        assertThatThrownBy(() -> method(this, a -> a.privateCollect(null, null)))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldFailWhenMappingPublicStaticMethod() {
        assertThatThrownBy(() -> method(this, a -> a.publicStaticCollect(null, null)))
                .isExactlyInstanceOf(IllegalStateException.class);
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
                .isExactlyInstanceOf(RuntimeException.class);
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
