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
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

class ReferMetadataProviderTest {

    @Test
    void shouldThrowExceptionWhenUnableToRefer() {
        // given
        String ref = "field";
        ReferMetadataProvider referMetadataProvider = new ReferMetadataProvider();

        // when /then
        assertThatThrownBy(() -> referMetadataProvider.createReferMetadata(InapplicableClass.class, ref)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldPreferMapWhenMapGetterFieldWhenNoGroupingOperator() {
        // given
        String ref = "field";
        ReferMetadataProvider referMetadataProvider = new ReferMetadataProvider();

        // when
        ReferMetadata referMetadata = referMetadataProvider.createReferMetadata(MapFieldGetter.class, ref);

        // then
        ReferMetadata expected = new ReferMetadata(String.class, "get(\"field\")", "field");
        assertThat(referMetadata).isEqualTo(expected);
    }

    @Test
    void shouldPreferMapWhenMapGetterWhenNoGroupingOperator() {
        // given
        String ref = "field";
        ReferMetadataProvider referMetadataProvider = new ReferMetadataProvider();

        // when
        ReferMetadata referMetadata = referMetadataProvider.createReferMetadata(MapGetter.class, ref);

        // then
        ReferMetadata expected = new ReferMetadata(String.class, "get(\"field\")", "field");
        assertThat(referMetadata).isEqualTo(expected);
    }

    @Test
    void shouldPreferMapWhenMapFieldWhenNoGroupingOperator() {
        // given
        String ref = "field";
        ReferMetadataProvider referMetadataProvider = new ReferMetadataProvider();

        // when
        ReferMetadata referMetadata = referMetadataProvider.createReferMetadata(MapField.class, ref);

        // then
        ReferMetadata expected = new ReferMetadata(String.class, "get(\"field\")", "field");
        assertThat(referMetadata).isEqualTo(expected);
    }

    @Test
    void shouldPreferFieldWhenGetterFieldWhenNoGroupingOperator() {
        // given
        String ref = "field";
        ReferMetadataProvider referMetadataProvider = new ReferMetadataProvider();

        // when
        ReferMetadata referMetadata = referMetadataProvider.createReferMetadata(FieldGetter.class, ref);

        // then
        ReferMetadata expected = new ReferMetadata(String.class, "field", "field");
        assertThat(referMetadata).isEqualTo(expected);
    }

    @Test
    void shouldPreferMapWhenMapGetterFieldWhenGroupingOperator() {
        // given
        String ref = "field[*]";
        ReferMetadataProvider referMetadataProvider = new ReferMetadataProvider();

        // when
        ReferMetadata referMetadata = referMetadataProvider.createReferMetadata(MapFieldGetter.class, ref);

        // then
        ReferMetadata expected = new ReferMetadata(String.class, "get(\"field\")", ref);
        assertThat(referMetadata).isEqualTo(expected);
    }

    @Test
    void shouldPreferMapWhenMapGetterWhenGroupingOperator() {
        // given
        String ref = "field[*]";
        ReferMetadataProvider referMetadataProvider = new ReferMetadataProvider();

        // when
        ReferMetadata referMetadata = referMetadataProvider.createReferMetadata(MapGetter.class, ref);

        // then
        ReferMetadata expected = new ReferMetadata(String.class, "get(\"field\")", ref);
        assertThat(referMetadata).isEqualTo(expected);
    }

    @Test
    void shouldPreferMapWhenMapFieldWhenGroupingOperator() {
        // given
        String ref = "field[*]";
        ReferMetadataProvider referMetadataProvider = new ReferMetadataProvider();

        // when
        ReferMetadata referMetadata = referMetadataProvider.createReferMetadata(MapField.class, ref);

        // then
        ReferMetadata expected = new ReferMetadata(String.class, "get(\"field\")", ref);
        assertThat(referMetadata).isEqualTo(expected);
    }

    @Test
    void shouldPreferFieldWhenGetterFieldWhenGroupingOperator() {
        // given
        String ref = "field[*]";
        ReferMetadataProvider referMetadataProvider = new ReferMetadataProvider();

        // when
        ReferMetadata referMetadata = referMetadataProvider.createReferMetadata(FieldGetter.class, ref);

        // then
        ReferMetadata expected = new ReferMetadata(String.class, "field", ref);
        assertThat(referMetadata).isEqualTo(expected);
    }

    private static class InapplicableClass {
    }

    private static class MapFieldGetter extends HashMap<String, String> {
        public String field;

        public String getField() {
            return "String";
        }
    }

    private static class MapGetter extends HashMap<String, String> {

        public String getField() {
            return "String";
        }
    }

    private static class MapField extends HashMap<String, String> {
        public String field;
    }

    private static class FieldGetter {
        public String field;

        public String getField() {
            return "String";
        }
    }
}
