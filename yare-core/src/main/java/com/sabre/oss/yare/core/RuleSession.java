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

import java.util.Collection;

/**
 * {@link RuleSession} is an interface exposing method(s) for rules execution.
 * <p>
 * Implementation should allow thread-safe execution of rule set.
 */
public interface RuleSession {

    /**
     * Return rule execution set uri.
     *
     * @return string representing rule execution set uri
     */
    String getUri();

    /**
     * Executes rules against provided {@code facts}. Pre-initialized {@code result} can be then
     * updated by rules' actions.
     * <p>
     * Subsequent calls on the same {@link RuleSession} instance evaluate the same rules, however
     * the time of preparing/loading the rules is implementation specific and can occur during/before
     * creation of the session or just before the first call to this method.
     *
     * @param result result (can be partially initialized)
     * @param facts  facts based on which rules will be applied
     * @param <T>    type of result
     * @return {@code result} (in general this not must be the same instance as passed to the method - but type have to be preserved)
     */
    <T> T execute(T result, Collection<?> facts);
}
