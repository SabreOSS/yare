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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReferringCodeGeneratorTest {

    @Test
    void shouldCreateLoop() {
        // given
        String ref = "ref";
        Type type = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{String.class};
            }

            @Override
            public Type getRawType() {
                return List.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
        ReferMetadata referMetadata = new ReferMetadata(type, ref, ref);

        // when
        String body = new ReferringCodeGenerator().generateReferringBody(referMetadata);

        // then
        assertThat(body).isEqualToIgnoringWhitespace("" +
                "if(" + BoxingUtils.class.getName() + ".box(v0.ref) == null) { \n" +
                "   return null; \n" +
                "} \n" +
                "java.util.Iterator i0 = v0.ref.iterator(); \n" +
                "while(i0.hasNext()) { \n" +
                "   java.lang.String v1 = (java.lang.String) i0.next(); \n" +
                "   if(" + BoxingUtils.class.getName() + ".box(v1) == null) { \n" +
                "   continue; \n" +
                "} \n" +
                "%s \n" +
                "} \n");

    }

    @Test
    void shouldCreateAssignment() {
        // given
        String ref = "ref";
        Type type = String.class;
        ReferMetadata referMetadata = new ReferMetadata(type, ref, ref);

        // when
        String body = new ReferringCodeGenerator().generateReferringBody(referMetadata);

        // then
        assertThat(body).isEqualToIgnoringWhitespace("" +
                "if(" + BoxingUtils.class.getName() + ".box(v0.ref) == null) { \n" +
                "   return null; \n" +
                "} \n" +
                "java.lang.String v1 = (java.lang.String) v0.ref; \n" +
                "if(" + BoxingUtils.class.getName() + ".box(v1) == null) { \n" +
                "   return null; \n" +
                "} \n" +
                "%s");

    }

    @Test
    void shouldCreateEndingLoopWhenNotChaining() {
        // given
        String ref = "ref[*]";
        Type type = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{String.class};
            }

            @Override
            public Type getRawType() {
                return List.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
        ReferMetadata referMetadata = new ReferMetadata(type, "ref", ref);

        // when
        String body = new ReferringCodeGenerator().generateEndingReferringBodyWhenNotChaining(referMetadata);

        // then
        assertThat(body).isEqualToNormalizingWhitespace("" +
                "java.util.List v1 = (java.util.List) v0.ref; \n" +
                "return " + BoxingUtils.class.getName() + ".box(v1);");

    }

    @Test
    void shouldCreateEndingLoopWhenChainingAndGroupingOperator() {
        // given
        String ref = "ref[*]";
        Type type = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{String.class};
            }

            @Override
            public Type getRawType() {
                return List.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
        ReferMetadata referMetadata = new ReferMetadata(type, "ref", ref);

        // when
        String body = new ReferringCodeGenerator().generateEndingReferringBodyWhenChaining(referMetadata);

        // then
        assertThat(body).isEqualToNormalizingWhitespace("" +
                "if(" + BoxingUtils.class.getName() + ".box(v0.ref) == null) { \n" +
                "   return null; \n" +
                "} \n" +
                "java.util.Iterator i0 = v0.ref.iterator(); \n" +
                "while(i0.hasNext()) { \n" +
                "   java.lang.String v1 = (java.lang.String) i0.next(); \n" +
                "   result.add(" + BoxingUtils.class.getName() + ".box(v1)); \n" +
                "} \n");

    }

    @Test
    void shouldCreateEndingLoopWhenChainingAndNoGroupingOperator() {
        // given
        String ref = "ref";
        Type type = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{String.class};
            }

            @Override
            public Type getRawType() {
                return List.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
        ReferMetadata referMetadata = new ReferMetadata(type, ref, ref);

        // when
        String body = new ReferringCodeGenerator().generateEndingReferringBodyWhenChaining(referMetadata);

        // then
        assertThat(body).isEqualToIgnoringWhitespace("" +
                "java.util.List v1 = (java.util.List) v0.ref; \n" +
                "result.add(" + BoxingUtils.class.getName() + ".box(v1));");

    }

    @Test
    void shouldCreateEndingAssignmentWhenNotChaining() {
        // given
        String ref = "ref";
        Type type = String.class;
        ReferMetadata referMetadata = new ReferMetadata(type, ref, ref);

        // when
        String body = new ReferringCodeGenerator().generateEndingReferringBodyWhenNotChaining(referMetadata);

        // then
        assertThat(body).isEqualToIgnoringWhitespace("" +
                "java.lang.String v1 = (java.lang.String) v0.ref; \n" +
                "return " + BoxingUtils.class.getName() + ".box(v1);");

    }

    @Test
    void shouldCreateEndingAssignmentWhenChaining() {
        // given
        String ref = "ref";
        Type type = String.class;
        ReferMetadata referMetadata = new ReferMetadata(type, ref, ref);

        // when
        String body = new ReferringCodeGenerator().generateEndingReferringBodyWhenChaining(referMetadata);

        // then
        assertThat(body).isEqualToIgnoringWhitespace("" +
                "java.lang.String v1 = (java.lang.String) v0.ref; \n" +
                "result.add(" + BoxingUtils.class.getName() + ".box(v1));");

    }
}
