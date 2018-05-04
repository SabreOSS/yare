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

import com.sabre.oss.yare.engine.executor.ExecutorConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultCallInvocationResultCacheTest {
    private DefaultCallInvocationResultCache defaultFunctionResultCache;
    private int howManyExecutions = 0;

    @BeforeEach
    void setUp() {
        defaultFunctionResultCache = new DefaultCallInvocationResultCache(ExecutorConfiguration.builder().build());
        howManyExecutions = 0;
    }

    @Test
    void shouldCacheResultForNewFunction() {
        //given
        Object[] arguments = {1, 2, 3};

        //when
        int result = defaultFunctionResultCache.get("testFunction", arguments, this::testFunction);

        //then
        assertThat(howManyExecutions).isEqualTo(1);
        assertThat(result).isEqualTo(6);
    }

    @Test
    void shouldNotCacheResultForExistingFunction() {
        //given
        Object[] arguments = {1, 2, 3};
        defaultFunctionResultCache.get("testFunction", arguments, this::testFunction);

        //when
        Object[] newArguments = {1, 2, 3};
        int result = defaultFunctionResultCache.get("testFunction", newArguments, this::testFunction);

        //then
        assertThat(howManyExecutions).isEqualTo(1);
        assertThat(result).isEqualTo(6);
    }

    @Test
    void shouldCacheForDifferentArguments() {
        //given
        Object[] arguments = {1, 2, 3};
        int result = defaultFunctionResultCache.get("testFunction", arguments, this::testFunction);

        //when
        Object[] newArguments = {4, 5, 6};
        int newResult = defaultFunctionResultCache.get("testFunction", newArguments, this::testFunction);

        //then
        assertThat(howManyExecutions).isEqualTo(2);
        assertThat(result).isEqualTo(6);
        assertThat(newResult).isEqualTo(15);
    }

    @Test
    void shouldCacheForDifferentFunctions() {
        //given
        Object[] arguments = {1, 2, 3};
        defaultFunctionResultCache.get("testFunction", arguments, this::testFunction);

        //when
        int result = defaultFunctionResultCache.get("otherTestFunction", arguments, this::testFunction);

        //then
        assertThat(howManyExecutions).isEqualTo(2);
        assertThat(result).isEqualTo(6);
    }

    private Integer testFunction(Object[] args) {
        howManyExecutions++;
        return (Integer) args[0] + (Integer) args[1] + (Integer) args[2];
    }
}
