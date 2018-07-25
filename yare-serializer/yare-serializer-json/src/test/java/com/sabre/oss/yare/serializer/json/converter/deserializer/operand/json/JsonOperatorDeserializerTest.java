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

package com.sabre.oss.yare.serializer.json.converter.deserializer.operand.json;

import com.sabre.oss.yare.serializer.json.RuleToJsonConverter;
import com.sabre.oss.yare.serializer.json.converter.deserializer.operand.OperatorDeserializer;
import com.sabre.oss.yare.serializer.json.converter.deserializer.operand.OperatorDeserializerTestCase;
import com.sabre.oss.yare.serializer.json.utils.ResourceUtils;
import org.junit.jupiter.api.BeforeEach;

class JsonOperatorDeserializerTest extends OperatorDeserializerTestCase {
    @Override
    protected String getTestResource(String fileName) {
        return ResourceUtils.getJsonResourceAsString(String.format("/json/converter/deserializer/handler/operator/%s.json", fileName));
    }

    @BeforeEach
    void setUp() {
        mapper = RuleToJsonConverter.createObjectMapper();
        deserializer = new OperatorDeserializer();
    }
}
