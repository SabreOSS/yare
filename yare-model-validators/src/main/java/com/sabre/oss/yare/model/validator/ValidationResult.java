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

package com.sabre.oss.yare.model.validator;

import java.util.Objects;

public class ValidationResult {
    private final Level level;
    private final String code;
    private final String message;

    ValidationResult(Level level, String code, String message) {
        this.level = Objects.requireNonNull(level);
        this.code = code;
        this.message = message;
    }

    public static ValidationResult error(String code, String message) {
        return new ValidationResult(Level.ERROR, code, message);
    }

    public static ValidationResult warning(String code, String message) {
        return new ValidationResult(Level.WARNING, code, message);
    }

    public static ValidationResult info(String code, String message) {
        return new ValidationResult(Level.INFO, code, message);
    }

    public Level getLevel() {
        return level;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ValidationResult that = (ValidationResult) o;
        return level == that.level &&
                Objects.equals(code, that.code) &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, code, message);
    }

    @Override
    public String toString() {
        return String.format("ValidationResult{level=%s, code='%s', message='%s'}", level, code, message);
    }

    public enum Level {
        ERROR,
        WARNING,
        INFO
    }
}
