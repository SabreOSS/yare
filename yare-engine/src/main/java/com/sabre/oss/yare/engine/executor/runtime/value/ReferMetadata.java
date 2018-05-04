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

import java.lang.reflect.Type;
import java.util.Objects;

public class ReferMetadata {
    private final Type refType;
    private final String ref;
    private final String pathPart;

    public ReferMetadata(Type referType, String ref, String partPart) {
        this.refType = referType;
        this.ref = ref;
        this.pathPart = partPart;
    }

    public Type getRefType() {
        return refType;
    }

    public String getRef() {
        return ref;
    }

    public String getPathPart() {
        return pathPart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReferMetadata that = (ReferMetadata) o;
        return Objects.equals(refType, that.refType) &&
                Objects.equals(ref, that.ref) &&
                Objects.equals(pathPart, that.pathPart);
    }

    @Override
    public int hashCode() {
        return Objects.hash(refType, ref, pathPart);
    }
}
