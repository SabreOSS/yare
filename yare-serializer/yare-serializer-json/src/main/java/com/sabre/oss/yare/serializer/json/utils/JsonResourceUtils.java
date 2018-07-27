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
 *
 */

package com.sabre.oss.yare.serializer.json.utils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static java.lang.String.format;

public final class JsonResourceUtils {
    private static final Pattern jsonCommentPattern = Pattern.compile("/\\*+[^*]*\\*+(?:[^/*][^*]*\\*+)*/");
    private static final Pattern yamlCommentPattern = Pattern.compile("(?m)^#.*");

    private JsonResourceUtils() {
    }

    public static String getJsonResourceAsString(String resource) {
        return getResourceWithoutComments(jsonCommentPattern, resource);
    }

    public static String getYamlResourceAsString(String resource) {
        return getResourceWithoutComments(yamlCommentPattern, resource);
    }

    private static String getResourceWithoutComments(Pattern commentPattern, String resource) {
        try (InputStream is = getResourceAsStream(resource)) {
            String content = IOUtils.toString(is, StandardCharsets.UTF_8);
            return commentPattern.matcher(content).replaceAll("");
        } catch (IOException e) {
            throw new IllegalArgumentException(format("Cannot read %s", resource), e);
        }
    }

    private static InputStream getResourceAsStream(String resource) {
        InputStream resourceAsStream = JsonResourceUtils.class.getResourceAsStream(resource);
        if (resourceAsStream == null) {
            throw new IllegalArgumentException(format("Cannot find %s", resource));
        }
        return resourceAsStream;
    }
}
