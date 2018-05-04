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

import com.sabre.oss.yare.core.ExecutionContext.Key;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

public interface DefaultContextKey {

    /**
     * Key for preserving facts passed to rules engine
     */
    Key<Collection<?>> FACTS = Key.create(DefaultContextKey.class, "facts");

    /**
     * Key for preserving result
     */
    Key<Object> RESULT = Key.create(DefaultContextKey.class, "result");

    /**
     * Key for preserving rule execution set uri
     */
    Key<String> RULE_EXECUTION_SET_URI = Key.create(DefaultContextKey.class, "rule-execution-set-uri");

    /**
     * Execution set for given session. Because different YARE Providers can differently preserve rule sets,
     * object reference is stored.
     */
    Key<AtomicReference<Object>> RULE_EXECUTION_SET = Key.create(DefaultContextKey.class, "rule-execution-set");

}
