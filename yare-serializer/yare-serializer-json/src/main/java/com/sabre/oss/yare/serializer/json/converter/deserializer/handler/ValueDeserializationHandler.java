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

package com.sabre.oss.yare.serializer.json.converter.deserializer.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabre.oss.yare.common.converter.TypeTypeConverter;
import com.sabre.oss.yare.serializer.json.converter.JsonPropertyNames;
import com.sabre.oss.yare.serializer.json.converter.utils.JsonNodeUtils;
import com.sabre.oss.yare.serializer.json.model.Operand;
import com.sabre.oss.yare.serializer.json.model.Value;

import java.lang.reflect.Type;
import java.util.Optional;

class ValueDeserializationHandler extends DeserializationHandler {
    private final TypeTypeConverter typeConverter = new TypeTypeConverter();

    @Override
    protected boolean isApplicable(JsonNode jsonNode) {
        return jsonNode.has(JsonPropertyNames.Value.VALUE);
    }

    @Override
    protected Operand deserialize(JsonNode jsonNode, ObjectMapper objectMapper) throws JsonProcessingException {
        String type = getType(jsonNode);
        Object value = getValue(jsonNode, type, objectMapper);
        return new Value()
                .withValue(value)
                .withType(type);
    }

    private String getType(JsonNode jsonNode) {
        return JsonNodeUtils.resolveChildNode(jsonNode, JsonPropertyNames.Value.TYPE)
                .map(JsonNode::textValue)
                .orElse("String");
    }

    private Object getValue(JsonNode jsonNode, String type, ObjectMapper objectMapper) throws JsonProcessingException {
        Optional<JsonNode> o = JsonNodeUtils.resolveChildNode(jsonNode, JsonPropertyNames.Value.VALUE);
        return o.isPresent() ? mapValueByType(o.get(), type, objectMapper) : null;
    }

    private Object mapValueByType(JsonNode jsonNode, String type, ObjectMapper objectMapper) throws JsonProcessingException {
        Class<?> resolvedType = (Class<?>) typeConverter.fromString(Type.class, type);
        return objectMapper.treeToValue(jsonNode, resolvedType);
    }
}
