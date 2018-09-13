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

import java.util.Set;
import java.util.stream.Collectors;

public class SchemaValidationException extends RuntimeException {
    private final Set<SchemaValidationError> schemaValidationErrors;

    public SchemaValidationException(String message, Set<SchemaValidationError> schemaValidationErrors) {
        super(message);
        this.schemaValidationErrors = schemaValidationErrors;
    }

    @Override
    public String getMessage() {
        String errorsMessage = schemaValidationErrors.stream()
                .map(e -> String.format("Line: %d. Column: %d. Error: %s", e.getLine(), e.getColumn(), e.getReason()))
                .collect(Collectors.joining("\n"));
        return String.format("%s. Errors:\n%s", super.getMessage(), errorsMessage);
    }
}
