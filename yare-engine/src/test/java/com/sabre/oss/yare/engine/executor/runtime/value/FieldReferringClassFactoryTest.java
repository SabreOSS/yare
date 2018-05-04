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

import com.google.common.collect.ImmutableMap;
import com.sabre.oss.yare.engine.executor.runtime.predicate.PredicateContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FieldReferringClassFactoryTest {

    @Test
    void shouldProperlyGenerateFieldBasedValueProvider() {
        // given
        TestClass testClass = new TestClass("justDoIt");
        ValueProvider valueProvider = FieldReferringClassFactory.create(TestClass.class, "testClass", "aString");

        // when
        Object result = valueProvider.get(new PredicateContext("any", new Object(), ImmutableMap.of("testClass", testClass)));

        // then
        assertThat(result).isEqualTo("justDoIt");
    }

    @Test
    void shouldCallSimpleGetterForBooleanField() {
        // given
        SimpleGetterClass simpleGetterClass = new SimpleGetterClass(true);
        ValueProvider valueProvider = FieldReferringClassFactory.create(SimpleGetterClass.class, "simpleGetterClass", "flag");

        // when
        Object result = valueProvider.get(new PredicateContext("any", new Object(), ImmutableMap.of("simpleGetterClass", simpleGetterClass)));

        // then
        assertThat(result).isEqualTo(true);
        assertThat(simpleGetterClass.getterExecutionCounter).isEqualTo(1);
    }

    @Test
    void shouldCallBooleanFieldGetterForBooleanField() {
        // given
        BooleanFieldGetterClass booleanFieldGetterClass = new BooleanFieldGetterClass(true);
        ValueProvider valueProvider = FieldReferringClassFactory.create(BooleanFieldGetterClass.class, "booleanFieldGetterClass", "flag");

        // when
        Object result = valueProvider.get(new PredicateContext("any", new Object(), ImmutableMap.of("booleanFieldGetterClass", booleanFieldGetterClass)));

        // then
        assertThat(result).isEqualTo(true);
        assertThat(booleanFieldGetterClass.getterExecutionCounter).isEqualTo(1);
    }

    @Test
    void shouldCallSimpleFieldGetterForBooleanFieldWhenTwoGettersAvailable() {
        // given
        BothBooleanGettersClass bothBooleanGettersClass = new BothBooleanGettersClass(true);
        ValueProvider valueProvider = FieldReferringClassFactory.create(BothBooleanGettersClass.class, "bothBooleanGettersClass", "flag");

        // when
        Object result = valueProvider.get(new PredicateContext("any", new Object(), ImmutableMap.of("bothBooleanGettersClass", bothBooleanGettersClass)));

        // then
        assertThat(result).isEqualTo(true);
        assertThat(bothBooleanGettersClass.simpleGetterExecutionCounter).isEqualTo(1);
        assertThat(bothBooleanGettersClass.booleanFieldGetterExecutionCounter).isEqualTo(0);
    }

    @Test
    void shouldCallSimpleFieldGetterForBooleanWrapperFieldWhenTwoGettersAvailable() {
        // given
        BooleanWrapperBothBooleanGettersClass booleanWrapperBothBooleanGettersClass = new BooleanWrapperBothBooleanGettersClass(true);
        ValueProvider valueProvider = FieldReferringClassFactory.create(BooleanWrapperBothBooleanGettersClass.class, "booleanWrapperBothBooleanGettersClass", "flag");

        // when
        Object result = valueProvider.get(new PredicateContext("any", new Object(), ImmutableMap.of("booleanWrapperBothBooleanGettersClass", booleanWrapperBothBooleanGettersClass)));

        // then
        assertThat(result).isEqualTo(true);
        assertThat(booleanWrapperBothBooleanGettersClass.simpleGetterExecutionCounter).isEqualTo(1);
        assertThat(booleanWrapperBothBooleanGettersClass.booleanFieldGetterExecutionCounter).isEqualTo(0);
    }

    private static class TestClass {
        public final String aString;

        TestClass(String aString) {
            this.aString = aString;
        }
    }

    private static class SimpleGetterClass {
        private final boolean flag;
        private int getterExecutionCounter;

        SimpleGetterClass(boolean flag) {
            this.flag = flag;
        }

        public boolean getFlag() {
            getterExecutionCounter++;
            return flag;
        }
    }

    private static class BooleanFieldGetterClass {
        private final boolean flag;
        private int getterExecutionCounter;

        BooleanFieldGetterClass(boolean flag) {
            this.flag = flag;
        }

        public boolean isFlag() {
            getterExecutionCounter++;
            return flag;
        }
    }

    private static class BothBooleanGettersClass {
        private final boolean flag;
        private int simpleGetterExecutionCounter;
        private int booleanFieldGetterExecutionCounter;

        BothBooleanGettersClass(boolean flag) {
            this.flag = flag;
        }

        public boolean getFlag() {
            simpleGetterExecutionCounter++;
            return flag;
        }

        public boolean isFlag() {
            booleanFieldGetterExecutionCounter++;
            return flag;
        }
    }

    private static class BooleanWrapperBothBooleanGettersClass {
        private final Boolean flag;
        private int simpleGetterExecutionCounter;
        private int booleanFieldGetterExecutionCounter;

        BooleanWrapperBothBooleanGettersClass(Boolean flag) {
            this.flag = flag;
        }

        public Boolean getFlag() {
            simpleGetterExecutionCounter++;
            return flag;
        }

        public Boolean isFlag() {
            booleanFieldGetterExecutionCounter++;
            return flag;
        }
    }
}
