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

package com.sabre.oss.yare.serializer.json.deserializer.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabre.oss.yare.serializer.json.model.Operand;
import com.sabre.oss.yare.serializer.json.model.Value;

import java.util.Optional;

class ValueDeserializationHandler extends DeserializationHandler {
    private static final String VALUE_PROPERTY_NAME = "value";
    private static final String VALUE_TYPE_PROPERTY_NAME = "type";

    @Override
    protected boolean isApplicable(JsonNode jsonNode) {
        return jsonNode.has(VALUE_PROPERTY_NAME);
    }

    @Override
    protected Operand deserialize(JsonNode jsonNode, ObjectMapper objectMapper)
            throws JsonProcessingException {
        String type = getType(jsonNode);
        Object value = getValue(jsonNode, type, objectMapper);
        return new Value()
                .withValue(value)
                .withType(type);
    }

    private String getType(JsonNode jsonNode) {
        return jsonNode.has(VALUE_TYPE_PROPERTY_NAME) ?
                jsonNode.get(VALUE_TYPE_PROPERTY_NAME).textValue() : String.class.getName();
    }

    private Object getValue(JsonNode jsonNode, String type, ObjectMapper objectMapper)
            throws JsonProcessingException {
        TreeNode valueNode = jsonNode.get(VALUE_PROPERTY_NAME);
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
