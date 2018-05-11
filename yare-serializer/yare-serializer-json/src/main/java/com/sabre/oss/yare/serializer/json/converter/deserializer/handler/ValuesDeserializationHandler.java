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
import com.sabre.oss.yare.serializer.json.converter.JsonPropertyNames;
import com.sabre.oss.yare.serializer.json.model.Expression;
import com.sabre.oss.yare.serializer.json.model.Operand;
import com.sabre.oss.yare.serializer.json.model.Values;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class ValuesDeserializationHandler extends DeserializationHandler {
    @Override
    protected boolean isApplicable(JsonNode jsonNode) {
        return jsonNode.has(JsonPropertyNames.Values.VALUES)
                && jsonNode.get(JsonPropertyNames.Values.VALUES).isArray()
                && jsonNode.has(JsonPropertyNames.Values.TYPE);
    }

    @Override
    protected Operand deserialize(JsonNode jsonNode, ObjectMapper objectMapper) throws JsonProcessingException {
        String type = jsonNode.get(JsonPropertyNames.Values.TYPE).textValue();
        List<Expression> values = getValues(jsonNode, objectMapper);
        return new Values()
                .withType(type)
                .withValues(values);
    }

    private List<Expression> getValues(JsonNode jsonNode, ObjectMapper objectMapper) throws JsonProcessingException {
        Iterator<JsonNode> valueNodes = jsonNode.get(JsonPropertyNames.Values.VALUES).iterator();
        List<Expression> expressions = new ArrayList<>();
        while (valueNodes.hasNext()) {
            JsonNode node = valueNodes.next();
            Expression e = objectMapper.treeToValue(node, Expression.class);
            expressions.add(e);
        }
        return expressions;
    }
}
