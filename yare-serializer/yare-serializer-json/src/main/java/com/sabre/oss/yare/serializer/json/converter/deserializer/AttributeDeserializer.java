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

package com.sabre.oss.yare.serializer.json.converter.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabre.oss.yare.common.converter.TypeTypeConverter;
import com.sabre.oss.yare.serializer.json.converter.JsonPropertyNames;
import com.sabre.oss.yare.serializer.json.converter.utils.JsonNodeUtils;
import com.sabre.oss.yare.serializer.json.model.Attribute;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Optional;

public class AttributeDeserializer extends JsonDeserializer<Attribute> {
    private final TypeTypeConverter typeConverter = new TypeTypeConverter();

    @Override
    public Attribute deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
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
        return JsonNodeUtils.resolveChildNode(jsonNode, JsonPropertyNames.Attribute.NAME)
                .map(JsonNode::textValue)
                .orElse(null);
    }

    private String getType(JsonNode jsonNode) {
        return JsonNodeUtils.resolveChildNode(jsonNode, JsonPropertyNames.Attribute.TYPE)
                .map(JsonNode::textValue)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unable to deserialize %s, type must not be null", jsonNode.toString())));
    }

    private Object getValue(JsonNode jsonNode, String type, ObjectMapper objectMapper) throws JsonProcessingException {
        Optional<JsonNode> o = JsonNodeUtils.resolveChildNode(jsonNode, JsonPropertyNames.Attribute.VALUE);
        return o.isPresent() ? mapValueByType(o.get(), type, objectMapper) : null;
    }

    private Object mapValueByType(JsonNode jsonNode, String type, ObjectMapper objectMapper) throws JsonProcessingException {
        Class<?> resolvedType = (Class<?>) typeConverter.fromString(Type.class, type);
        return objectMapper.treeToValue(jsonNode, resolvedType);
    }
}
