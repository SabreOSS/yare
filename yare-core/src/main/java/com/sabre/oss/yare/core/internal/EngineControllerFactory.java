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
import com.sabre.oss.yare.core.listener.CloseSessionListener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class EngineControllerFactory {
    private EngineControllerFactory() {
    }

    public static EngineController createDefaultFrom(Listener... listeners) {
        Map<Class, Listener> registeredListeners = new ConcurrentHashMap<>();
        Arrays.stream(listeners).forEach(listener -> tryToRegister(registeredListeners, CloseSessionListener.class, listener));
        return new DefaultEngineController(registeredListeners);
    }

    private static <T> void tryToRegister(Map<Class, Listener> registeredListeners, Class<T> clazz, Listener listener) {
        if (clazz.isAssignableFrom(listener.getClass())) {
            if (registeredListeners.containsKey(clazz)) {
                throw new AlreadyRegisteredListenerException("Listener is already registered");
            }
            registeredListeners.put(clazz, listener);
        }
    }
}
