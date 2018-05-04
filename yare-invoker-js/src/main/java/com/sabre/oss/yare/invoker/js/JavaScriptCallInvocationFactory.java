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

import com.sabre.oss.yare.core.call.*;
import com.sabre.oss.yare.core.invocation.Invocation;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Objects;
import java.util.function.Supplier;

import static java.lang.String.format;

public class JavaScriptCallInvocationFactory implements InvocationFactory {
    private final JavaScriptCallMetadata callMetadata;

    JavaScriptCallInvocationFactory(JavaScriptCallMetadata callMetadata) {
        this.callMetadata = callMetadata;
    }

    @Override
    public <R> Invocation<ProcessingContext, R> create(Supplier<ArgumentValueResolver> resolver, Argument.Invocation invocation) {
        return new JavaScriptBasedInvocation<>(invocation, resolver.get(), callMetadata);
    }

    private static class JavaScriptBasedInvocation<R> extends ProcessingInvocation<R> {
        private static final NashornScriptEngineFactory scriptEngineManager = new NashornScriptEngineFactory();

        private final JavaScriptCallMetadata callMetadata;

        JavaScriptBasedInvocation(Argument.Invocation invocation, ArgumentValueResolver argumentValueResolver, JavaScriptCallMetadata callMetadata) {
            super(invocation, argumentValueResolver);
            this.callMetadata = Objects.requireNonNull(callMetadata);
        }

        @Override
        @SuppressWarnings("unchecked")
        public R call(ProcessingContext processingContext, Object[] args) {
            try {
                ScriptEngine scriptEngine = getScriptEngine();
                scriptEngine.eval(callMetadata.getScript());
                Invocable invocable = (Invocable) scriptEngine;
                return (R) invocable.invokeFunction(callMetadata.getFunctionName(), args);
            } catch (ScriptException e) {
                throw new IllegalArgumentException(format("Failed to execute script %s", callMetadata.getScript()), e);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(format("No function %s in script %s",
                        callMetadata.getFunctionName(), callMetadata.getScript()), e);
            }
        }

        private ScriptEngine getScriptEngine() {
            return scriptEngineManager.getScriptEngine();
        }
    }
}
