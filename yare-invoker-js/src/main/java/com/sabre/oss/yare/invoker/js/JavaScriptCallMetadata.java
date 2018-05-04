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

package com.sabre.oss.yare.invoker.js;

import com.sabre.oss.yare.core.call.CallMetadata;
import com.sabre.oss.yare.core.call.InvocationFactory;

import java.util.Objects;

public final class JavaScriptCallMetadata implements CallMetadata {
    private final String functionName;
    private final String script;

    private JavaScriptCallMetadata(String functionName, String script) {
        this.functionName = Objects.requireNonNull(functionName);
        this.script = Objects.requireNonNull(script);
    }

    /**
     * Creates JavaScrip-based {@link CallMetadata} based on JavaScript source code.
     *
     * @param functionName name of the function to be invoked
     * @param script       JavaScript source
     * @return {@link JavaScriptCallMetadata} instance
     */
    public static JavaScriptCallMetadata js(String functionName, String script) {
        return new JavaScriptCallMetadata(functionName, script);
    }

    @Override
    public InvocationFactory getInvocationFactory() {
        return new JavaScriptCallInvocationFactory(this);
    }

    public String getFunctionName() {
        return functionName;
    }

    public String getScript() {
        return script;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JavaScriptCallMetadata that = (JavaScriptCallMetadata) o;
        return Objects.equals(functionName, that.functionName) &&
                Objects.equals(script, that.script);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functionName, script);
    }
}
