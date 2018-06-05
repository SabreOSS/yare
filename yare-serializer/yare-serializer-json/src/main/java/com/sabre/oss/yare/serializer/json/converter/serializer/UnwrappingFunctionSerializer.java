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

package com.sabre.oss.yare.serializer.json.converter.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.sabre.oss.yare.serializer.json.converter.JsonPropertyNames;
import com.sabre.oss.yare.serializer.json.model.Function;

import java.io.IOException;

class UnwrappingFunctionSerializer extends JsonSerializer<Function> {
    @Override
    public void serialize(Function value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeFieldName(JsonPropertyNames.Function.FUNCTION);
        gen.writeStartObject();
        gen.writeObjectField(JsonPropertyNames.Function.NAME, value.getName());
        gen.writeObjectField(JsonPropertyNames.Function.RETURN_TYPE, value.getReturnType());
        gen.writeObjectField(JsonPropertyNames.Function.PARAMETERS, value.getParameters());
        gen.writeEndObject();
    }

    @Override
    public boolean isUnwrappingSerializer() {
        return true;
    }
}
