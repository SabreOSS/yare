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

package com.sabre.oss.yare.core.reference;

import com.sabre.oss.yare.core.model.Expression;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderExtractor {
    private static final Pattern placeholderPattern = Pattern.compile("^\\$\\{(.*)}$");
    private static final Pattern escapedPlaceholderPattern = Pattern.compile("^(\\\\)+\\$\\{.*}$");

    public Optional<String> extractPlaceholder(Expression.Value value) {
        if (isReferenceCandidate(value)) {
            Matcher matcher = placeholderPattern.matcher(value.getValue().toString());
            if (matcher.find()) {
                return Optional.of(matcher.group(1));
            }
        }
        return Optional.empty();
    }

    public Optional<Object> unescapePlaceholder(Expression.Value value) {
        if (isReferenceCandidate(value)) {
            Matcher matcher = escapedPlaceholderPattern.matcher(value.getValue().toString());
            if (matcher.find()) {
                return Optional.of(value.getValue().toString().substring(1));
            }
        }
        return Optional.empty();
    }

    private boolean isReferenceCandidate(Expression.Value value) {
        return value != null && String.class.equals(value.getType()) && value.getValue() != null;
    }
}
