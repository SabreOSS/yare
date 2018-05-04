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

import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TypeUtilsTest {

    @Test
    void shouldCheckIfTypeIsCollectionWhenCollectionPassed() {
        // given
        Type type = org.apache.commons.lang3.reflect.TypeUtils.parameterize(List.class, String.class);

        // when
        boolean isCollection = TypeUtils.isCollection(type);

        // then
        assertThat(isCollection).isTrue();
    }

    @Test
    void shouldCheckIfTypeIsCollectionWhenNotCollectionPassed() {
        // given
        Type type = String.class;

        // when
        boolean isCollection = TypeUtils.isCollection(type);

        // then
        assertThat(isCollection).isFalse();
    }

    @Test
    void shouldCheckIfTypeIsMapWhenMapPassed() {
        // given
        Type type = org.apache.commons.lang3.reflect.TypeUtils.parameterize(Map.class, String.class, String.class);

        // when
        boolean isMap = TypeUtils.isMap(type);

        // then
        assertThat(isMap).isTrue();
    }

    @Test
    void shouldCheckIfTypeIsMapWhenNotMapPassed() {
        // given
        Type type = String.class;

        // when
        boolean isMap = TypeUtils.isMap(type);

        // then
        assertThat(isMap).isFalse();
    }

    @Test
    void shouldReturnCollectionGeneric() {
        // given
        Type type = org.apache.commons.lang3.reflect.TypeUtils.parameterize(List.class, String.class);

        // when
        Type collectionGeneric = TypeUtils.getCollectionGeneric(type);

        // then
        assertThat(collectionGeneric).isEqualTo(String.class);
    }

    @Test
    void shouldReturnMapValueGeneric() {
        // given
        Type type = org.apache.commons.lang3.reflect.TypeUtils.parameterize(Map.class, String.class, Object.class);

        // when
        Type mapGeneric = TypeUtils.getMapValueGeneric(type);

        // then
        assertThat(mapGeneric).isEqualTo(Object.class);
    }

    @Test
    void shouldGetProperRawTypeForCollection() {
        // given
        Type type = org.apache.commons.lang3.reflect.TypeUtils.parameterize(List.class, String.class);

        // when
        Class<?> rawType = TypeUtils.getRawType(type);

        // then
        assertThat(rawType).isEqualTo(List.class);
    }

    @Test
    void shouldGetProperRawTypeForSimpleClass() {
        // given
        Type type = String.class;

        // when
        Class<?> rawType = TypeUtils.getRawType(type);

        // then
        assertThat(rawType).isEqualTo(String.class);
    }

    private static class TestClass {
        public String field;
    }
}
