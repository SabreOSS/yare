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
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

public final class ResourceUtils {
    private static final Pattern YAML_COMMENT_REGEX = Pattern.compile("(?m)^#.*");

    private ResourceUtils() {
    }

    public static String getJsonResourceAsString(String resource) {
        try (InputStream is = getResourceAsStream(resource)) {
            String content = IOUtils.toString(is, UTF_8);
            return content.substring(content.indexOf("*/") + 2);
        } catch (IOException e) {
            throw new IllegalArgumentException(format("Cannot read %s", resource), e);
        }
    }

    public static String getYamlResourceAsString(String resource) {
        try (InputStream is = getResourceAsStream(resource)) {
            String content = IOUtils.toString(is, UTF_8);
            return YAML_COMMENT_REGEX.matcher(content).replaceAll("");
        } catch (IOException e) {
            throw new IllegalArgumentException(format("Cannot read %s", resource), e);
        }
    }

    private static InputStream getResourceAsStream(String resource) {
        InputStream resourceAsStream = ResourceUtils.class.getResourceAsStream(resource);
        if (resourceAsStream == null) {
            throw new IllegalArgumentException(format("Cannot find %s", resource));
        }
        return resourceAsStream;
    }
}
