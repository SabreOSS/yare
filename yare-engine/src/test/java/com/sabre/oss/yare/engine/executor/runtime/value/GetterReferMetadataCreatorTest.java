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

class GetterReferMetadataCreatorTest {

    @Test
    void shouldCheckIfApplicableForClassWithGetter() {
        // given
        GetterReferMetadataCreator getterBased = new GetterReferMetadataCreator();

        // when
        boolean applicable = getterBased.isApplicable(SimpleGetterClass.class, "field");

        // then
        assertThat(applicable).isTrue();
    }

    @Test
    void shouldCheckIfApplicableForClassWithBooleanGetter() {
        // given
        GetterReferMetadataCreator getterBased = new GetterReferMetadataCreator();

        // when
        boolean applicable = getterBased.isApplicable(BooleanFieldGetterClass.class, "field");

        // then
        assertThat(applicable).isTrue();
    }

    @Test
    void shouldCheckIfApplicableForClassWithoutGetter() {
        // given
        GetterReferMetadataCreator getterBased = new GetterReferMetadataCreator();

        // when
        boolean applicable = getterBased.isApplicable(NoGetterClass.class, "field");

        // then
        assertThat(applicable).isFalse();
    }

    @Test
    void shouldCreateMetadataForSimpleGetterWithNoGroupingOperator() {
        // given
        GetterReferMetadataCreator getterBased = new GetterReferMetadataCreator();

        // when
        ReferMetadata referMetadata = getterBased.getReferMetadata(SimpleGetterClass.class, "field");

        // then
        ReferMetadata expected = new ReferMetadata(String.class, "getField()", "field");
        assertThat(referMetadata).isEqualTo(expected);
    }

    @Test
    void shouldCreateMetadataForSimpleGetterWithGroupingOperator() {
        // given
        GetterReferMetadataCreator getterBased = new GetterReferMetadataCreator();

        // when
        ReferMetadata referMetadata = getterBased.getReferMetadata(SimpleGetterClass.class, "field[*]");

        // then
        ReferMetadata expected = new ReferMetadata(String.class, "getField()", "field[*]");
        assertThat(referMetadata).isEqualTo(expected);
    }

    @Test
    void shouldCreateMetadataForBooleanGetterWithNoGroupingOperator() {
        // given
        GetterReferMetadataCreator getterBased = new GetterReferMetadataCreator();

        // when
        ReferMetadata referMetadata = getterBased.getReferMetadata(BooleanFieldGetterClass.class, "field");

        // then
        ReferMetadata expected = new ReferMetadata(boolean.class, "isField()", "field");
        assertThat(referMetadata).isEqualTo(expected);
    }

    @Test
    void shouldCreateMetadataForBooleanGetterWithGroupingOperator() {
        // given
        GetterReferMetadataCreator getterBased = new GetterReferMetadataCreator();

        // when
        ReferMetadata referMetadata = getterBased.getReferMetadata(BooleanFieldGetterClass.class, "field[*]");

        // then
        ReferMetadata expected = new ReferMetadata(boolean.class, "isField()", "field[*]");
        assertThat(referMetadata).isEqualTo(expected);
    }

    private static class SimpleGetterClass {
        private String field;

        public String getField() {
            return field;
        }
    }

    private static class BooleanFieldGetterClass {
        private boolean field;

        public boolean isField() {
            return field;
        }
    }

    private static class NoGetterClass {
        private boolean field;
    }
}
