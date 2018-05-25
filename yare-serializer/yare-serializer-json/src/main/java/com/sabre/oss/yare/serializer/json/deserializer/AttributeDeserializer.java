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
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabre.oss.yare.serializer.json.model.Attribute;

import java.io.IOException;
import java.util.Optional;

public class AttributeDeserializer extends JsonDeserializer<Attribute> {
    private static final String ATTRIBUTE_NAME_PROPERTY_NAME = "name";
    private static final String ATTRIBUTE_TYPE_PROPERTY_NAME = "type";
    private static final String ATTRIBUTE_VALUE_PROPERTY_NAME = "value";

    @Override
    public Attribute deserialize(JsonParser jsonParser, DeserializationContext ctx)
            throws IOException {
        ObjectMapper objectMapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode attributeNode = objectMapper.readTree(jsonParser);
        String name = getName(attributeNode);
        String type = getType(attributeNode);
        Object value = getValue(attributeNode, type, objectMapper);
        return new Attribute()
                .withName(name)
                .withType(type)
                .withValue(value);
    }

    private String getName(JsonNode jsonNode) {
        return jsonNode.get(ATTRIBUTE_NAME_PROPERTY_NAME).textValue();
    }

    private String getType(JsonNode jsonNode) {
        return jsonNode.get(ATTRIBUTE_TYPE_PROPERTY_NAME).textValue();
    }

    private Object getValue(JsonNode jsonNode, String type, ObjectMapper objectMapper)
            throws JsonProcessingException {
        TreeNode valueNode = jsonNode.get(ATTRIBUTE_VALUE_PROPERTY_NAME);
        Class<?> resolvedType = resolveType(type)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Unable to deserialize %s, cannot find %s class", valueNode.toString(), type)));
        return objectMapper.treeToValue(valueNode, resolvedType);
    }

    private Optional<Class<?>> resolveType(String type) {
        try {
            Class<?> c = Thread.currentThread().getContextClassLoader().loadClass(type);
            return Optional.of(c);
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }
}
