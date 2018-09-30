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

package com.sabre.oss.yare.serializer.validator;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public final class SchemaValidationResults {
    private static final SchemaValidationResults okResult = new SchemaValidationResults(Collections.emptySet());

    private final Set<SchemaValidationError> results;

    private SchemaValidationResults(Set<SchemaValidationError> results) {
        this.results = Collections.unmodifiableSet(results);
    }

    public static SchemaValidationResults success() {
        return okResult;
    }

    public static SchemaValidationResults ofErrors(Set<SchemaValidationError> results) {
        return new SchemaValidationResults(results);
    }

    public static SchemaValidationResults ofError(SchemaValidationError result) {
        return new SchemaValidationResults(Collections.singleton(result));
    }

    public Set<SchemaValidationError> getResults() {
        return results;
    }

    public boolean hasErrors() {
        return !results.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SchemaValidationResults that = (SchemaValidationResults) o;
        return Objects.equals(results, that.results);
    }

    @Override
    public int hashCode() {
        return Objects.hash(results);
    }
}
