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

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class MapReferMetadataCreatorTest {

    @Test
    void shouldCheckIfApplicableIfClassImplementsMap() {
        // given
        MapReferMetadataCreator mapBased = new MapReferMetadataCreator();

        // when
        boolean applicable = mapBased.isApplicable(TestMapClass.class, "sampleField");

        // then
        assertThat(applicable).isTrue();
    }

    @Test
    void shouldCheckIfApplicableIfClassDoesntImplementMap() {
        // given
        MapReferMetadataCreator mapBased = new MapReferMetadataCreator();

        // when
        boolean applicable = mapBased.isApplicable(TestClass.class, "sampleField");

        // then
        assertThat(applicable).isFalse();
    }

    @Test
    void shouldCreateMetadataWhenNoGroupingOperator() {
        // given
        MapReferMetadataCreator mapBased = new MapReferMetadataCreator();

        // when
        ReferMetadata referMetadata = mapBased.getReferMetadata(TestMapClass.class, "key");

        // then
        ReferMetadata expected = new ReferMetadata(String.class, "get(\"key\")", "key");
        assertThat(referMetadata).isEqualTo(expected);
    }

    @Test
    void shouldCreateMetadataWhenGroupingOperator() {
        // given
        MapReferMetadataCreator mapBased = new MapReferMetadataCreator();

        // when
        ReferMetadata referMetadata = mapBased.getReferMetadata(TestMapClass.class, "key[*]");

        // then
        ReferMetadata expected = new ReferMetadata(String.class, "get(\"key\")", "key[*]");
        assertThat(referMetadata).isEqualTo(expected);
    }

    private static class TestMapClass extends HashMap<String, String> {
    }

    private static class TestClass {
    }
}
