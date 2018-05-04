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

import static org.assertj.core.api.Assertions.assertThat;

class FieldReferMetadataCreatorTest {

    @Test
    void shouldCheckIfApplicableWhenFieldIsPublic() {
        // given
        FieldReferMetadataCreator fieldBased = new FieldReferMetadataCreator();

        // when
        boolean applicable = fieldBased.isApplicable(TestClass.class, "publicField");

        // then
        assertThat(applicable).isTrue();
    }

    @Test
    void shouldCheckIfApplicableWhenFieldIsPrivate() {
        // given
        FieldReferMetadataCreator fieldBased = new FieldReferMetadataCreator();

        // when
        boolean applicable = fieldBased.isApplicable(TestClass.class, "privateField");

        // then
        assertThat(applicable).isFalse();
    }

    @Test
    void shouldCheckIfApplicableWhenNoField() {
        // given
        FieldReferMetadataCreator fieldBased = new FieldReferMetadataCreator();

        // when
        boolean applicable = fieldBased.isApplicable(TestClass.class, "notExistingField");

        // then
        assertThat(applicable).isFalse();
    }

    @Test
    void shouldCreateMetadataWhenNoGroupingOperator() {
        // given
        FieldReferMetadataCreator fieldBased = new FieldReferMetadataCreator();

        // when
        ReferMetadata referMetadata = fieldBased.getReferMetadata(TestClass.class, "publicField");

        // then
        ReferMetadata expected = new ReferMetadata(boolean.class, "publicField", "publicField");
        assertThat(referMetadata).isEqualTo(expected);
    }

    @Test
    void shouldCreateMetadataWhenGroupingOperator() {
        // given
        FieldReferMetadataCreator fieldBased = new FieldReferMetadataCreator();

        // when
        ReferMetadata referMetadata = fieldBased.getReferMetadata(TestClass.class, "publicField[*]");

        // then
        ReferMetadata expected = new ReferMetadata(boolean.class, "publicField", "publicField[*]");
        assertThat(referMetadata).isEqualTo(expected);
    }

    private static class TestClass {
        public boolean publicField;
        private boolean privateField;
    }
}
