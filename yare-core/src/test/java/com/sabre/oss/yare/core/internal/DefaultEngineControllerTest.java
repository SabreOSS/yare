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

package com.sabre.oss.yare.core.internal;

import com.sabre.oss.yare.core.listener.Listener;
import com.sabre.oss.yare.core.listener.StopProcessingContext;
import com.sabre.oss.yare.core.listener.StopProcessingListener;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultEngineControllerTest {

    @Test
    void shouldRegisterListener() {
        //given
        DefaultEngineController engineController = new DefaultEngineController();
        TestListener testListener = new TestListener();
        engineController.register(testListener);

        //when
        engineController.stopProcessing();

        //then
        assertThat(testListener.isEvaluationTerminated()).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenListenerAlreadyRegistered() {
        //given
        DefaultEngineController engineController = new DefaultEngineController();

        //when/then
        engineController.register(new TestListener());
        assertThatThrownBy(() -> engineController.register(new TestListener()))
                .isInstanceOf(DefaultEngineController.AlreadyRegisteredListenerException.class)
                .hasMessage("Listener is already registered");
    }

    @Test
    void shouldThrowExceptionWhenNullListener() {
        //given
        DefaultEngineController engineController = new DefaultEngineController();

        //when/then
        assertThatThrownBy(() -> engineController.register(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Listener can not be null");

    }

    @Test
    void shouldRegisterOnlyStopProcessingListener() {
        //given
        DefaultEngineController engineController = new DefaultEngineController();
        TestListener testListener = new TestListener();

        //when
        engineController.register(new UnknownListener());
        engineController.register(testListener);
        engineController.stopProcessing();

        //then
        assertThat(testListener.isEvaluationTerminated()).isTrue();
    }

    private static class TestListener implements StopProcessingListener {
        private boolean evaluationTerminated;

        TestListener() {
            evaluationTerminated = false;
        }

        boolean isEvaluationTerminated() {
            return evaluationTerminated;
        }

        @Override
        public void onStopProcessing(StopProcessingContext context) {
            evaluationTerminated = true;
        }
    }

    private static class UnknownListener implements Listener {
    }
}
