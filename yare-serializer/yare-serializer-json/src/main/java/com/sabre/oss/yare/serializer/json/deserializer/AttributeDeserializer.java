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

package com.sabre.oss.yare.serializer.json.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabre.oss.yare.serializer.json.model.Attribute;

import java.io.IOException;

public class AttributeDeserializer extends JsonDeserializer<Attribute> {
    @Override
    public Attribute deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException, JsonProcessingException {
        JsonNode attributeNode = jsonParser.getCodec().readTree(jsonParser);
        ObjectMapper objectMapper = (ObjectMapper) jsonParser.getCodec();
        String name = attributeNode.get("name").textValue();
        String type = attributeNode.get("type").textValue();
        Object value = getValue(attributeNode, type, objectMapper);
        return new Attribute().withName(name).withType(type).withValue(value);
    }

    private Object getValue(JsonNode attributeNode, String type, ObjectMapper objectMapper) throws JsonProcessingException {
        JsonNode valueNode = attributeNode.get("value");
        try {
            Class<?> resolvedType = Thread.currentThread().getContextClassLoader().loadClass(type);
            return objectMapper.treeToValue(valueNode, resolvedType);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(String.format("Unable to deserialize %s, cannot find %s class", valueNode.toString(), type), e);
        }
    }
}
