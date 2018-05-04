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

package com.sabre.oss.yare.engine.integration.fact;

import java.util.Collection;
import java.util.Objects;

public class InnerChainingFact {

    private final String string;
    private final Collection<String> collection;

    public InnerChainingFact(String string) {
        this(string, null);
    }

    public InnerChainingFact(Collection<String> collection) {
        this(null, collection);
    }

    public InnerChainingFact(String string, Collection<String> collection) {
        this.string = string;
        this.collection = collection;
    }

    public String getString() {
        return string;
    }

    public Collection<String> getCollection() {
        return collection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InnerChainingFact that = (InnerChainingFact) o;
        return Objects.equals(string, that.string) && Objects.equals(collection, that.collection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(string, collection);
    }
}
