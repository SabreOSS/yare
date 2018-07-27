/*
 * MIT License
 *
 * Copyright 2018 Sabre GLBL c.
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

package com.sabre.oss.yare.serializer.json.model.yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabre.oss.yare.serializer.json.RuleToYamlConverter;
import com.sabre.oss.yare.serializer.json.model.FunctionSerializationTestCase;
import com.sabre.oss.yare.serializer.json.utils.JsonResourceUtils;

class YamlFunctionSerializationTest extends FunctionSerializationTestCase {
    @Override
    protected ObjectMapper createObjectMapper() {
        return RuleToYamlConverter.createObjectMapper();
    }

    @Override
    protected String getTestResource(String fileName) {
        return JsonResourceUtils.getYamlResourceAsString(String.format("/yaml/model/function/%s.yml", fileName));
    }
}
