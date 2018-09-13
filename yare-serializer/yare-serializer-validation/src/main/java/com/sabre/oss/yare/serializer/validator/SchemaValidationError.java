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

import java.util.Objects;

public final class SchemaValidationError {
    public static final int UNKNOWN = -1;

    private final int line;
    private final int column;
    private final String reason;

    private SchemaValidationError(int line, int column, String reason) {
        this.line = line;
        this.column = column;
        this.reason = reason;
    }

    public static SchemaValidationError of(int line, int column, String reason) {
        int lineValue = line > 0 ? line : UNKNOWN;
        int columnValue = column > 0 ? column : UNKNOWN;
        return new SchemaValidationError(lineValue, columnValue, reason);
    }

    public static SchemaValidationError of(String reason) {
        return new SchemaValidationError(UNKNOWN, UNKNOWN, reason);
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SchemaValidationError that = (SchemaValidationError) o;
        return line == that.line &&
                column == that.column &&
                Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, column, reason);
    }
}
