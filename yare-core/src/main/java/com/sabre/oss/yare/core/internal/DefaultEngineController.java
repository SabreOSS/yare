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

import com.sabre.oss.yare.core.EngineController;
import com.sabre.oss.yare.core.listener.Listener;
import com.sabre.oss.yare.core.listener.StopProcessingContext;
import com.sabre.oss.yare.core.listener.StopProcessingListener;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

public class DefaultEngineController implements EngineController {
    private final AtomicReference<StopProcessingListener> stopProcessingListener = new AtomicReference<>();

    @Override
    public void stopProcessing() {
        execute(stopProcessingListener, StopProcessingListener::onStopProcessing, new StopProcessingContext() {
        });
    }

    public void register(Listener listener) {
        Objects.requireNonNull(listener, "Listener can not be null");
        tryToRegister(StopProcessingListener.class, listener, stopProcessingListener);
    }

    private <T, C> void execute(AtomicReference<T> listener, BiConsumer<T, C> callback, C context) {
        T l = listener.get();
        if (l != null) {
            callback.accept(l, context);
        }
    }

    @SuppressWarnings({"SameParameterValue", "unchecked"})
    private <T> void tryToRegister(Class<T> clazz, Listener listener, AtomicReference<T> destination) {
        if (clazz.isAssignableFrom(listener.getClass())) {
            boolean wasUninitialized = destination.compareAndSet(null, (T) listener);
            if (!wasUninitialized) {
                throw new AlreadyRegisteredListenerException("Listener is already registered");
            }
        }
    }

    static class AlreadyRegisteredListenerException extends RuntimeException {

        AlreadyRegisteredListenerException(String message) {
            super(message);
        }
    }

}
