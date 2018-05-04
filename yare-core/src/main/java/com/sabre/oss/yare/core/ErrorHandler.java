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

package com.sabre.oss.yare.core;

/**
 * {@link ErrorHandler} is general purpose interface for errors handling.
 * <p>
 * It should be noted that it is required to implement handlers in stateless manner because
 * single handler is shared between multiple Rule Engine evaluations.
 */
public interface ErrorHandler {

    /**
     * This method is called if any error occurs in rule pre-processing phase (i.e. when internal representation of rule
     * is preparing, during initialization of Rule Engine) or in run-time (i.e. rules evaluating).
     * Method should returns true if reported error was fully compensated by this handler, false otherwise (so, callee
     * should perform compensation (if possible) or propagate error).
     *
     * @param event Event describing error
     * @return true if error was fully compensated by this handler, false otherwise (so callee has to propagate and error)
     */
    boolean handleError(ErrorEvent event);

    /**
     * Interface describing an error event.
     */
    interface ErrorEvent {
        /**
         * Returns thrown Throwable (if any)
         *
         * @return thrown Throwable
         */
        Throwable getThrowable();
    }

    /**
     * This type of event is used to indicate Rules Engine configuration / initialization time errors.
     */
    interface ConfigurationErrorEvent extends ErrorEvent {
    }

    /**
     * This type of event is used to indicate rules evaluation / execution time errors.
     */
    interface ExecutionErrorEvent extends ErrorEvent {

        /**
         * Return rule name in which consequence section error occurs
         *
         * @return rule name
         */
        String getRuleName();
    }

    /**
     * This type of event is used to indicate errors during unmarshalling.
     */
    interface DeserializationErrorEvent extends ErrorEvent {

        /**
         * Return native rule which caused an error
         *
         * @return rule definition
         */
        byte[] getRule();
    }
}
