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
import com.sabre.oss.yare.serializer.json.converter.utils.JsonNodeUtils;
import com.sabre.oss.yare.serializer.json.model.Function;
import com.sabre.oss.yare.serializer.json.model.Operand;
import com.sabre.oss.yare.serializer.json.model.Parameter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

class FunctionDeserializationHandler extends DeserializationHandler {
    @Override
    protected boolean isApplicable(JsonNode jsonNode) {
        return jsonNode.has(JsonPropertyNames.Function.FUNCTION);
    }

    @Override
    protected Operand deserialize(JsonNode jsonNode, ObjectMapper objectMapper) throws JsonProcessingException {
        Optional<JsonNode> o = JsonNodeUtils.resolveChildNode(jsonNode, JsonPropertyNames.Function.FUNCTION);
        return o.isPresent() ? getFunction(o.get(), objectMapper) : null;
    }

    private Operand getFunction(JsonNode jsonNode, ObjectMapper objectMapper) throws JsonProcessingException {
        String functionName = getName(jsonNode);
        String returnType = getType(jsonNode);
        List<Parameter> functionParameters = getParameters(jsonNode, objectMapper);
        return new Function()
                .withName(functionName)
                .withReturnType(returnType)
                .withParameters(functionParameters);
    }

    private String getName(JsonNode jsonNode) {
        return JsonNodeUtils.resolveChildNode(jsonNode, JsonPropertyNames.Function.NAME)
                .map(JsonNode::textValue)
                .orElse(null);
    }

    private String getType(JsonNode jsonNode) {
        return JsonNodeUtils.resolveChildNode(jsonNode, JsonPropertyNames.Function.RETURN_TYPE)
                .map(JsonNode::textValue)
                .orElse(null);
    }

    private List<Parameter> getParameters(JsonNode jsonNode, ObjectMapper objectMapper) throws JsonProcessingException {
        Optional<JsonNode> o = JsonNodeUtils.resolveChildNode(jsonNode, JsonPropertyNames.Function.PARAMETERS);
        return o.isPresent() ? mapNodeAsListOfParameters(o.get(), objectMapper) : null;
    }

    private List<Parameter> mapNodeAsListOfParameters(JsonNode jsonNode, ObjectMapper objectMapper) throws JsonProcessingException {
        Iterator<JsonNode> parametersNodes = jsonNode.iterator();
        List<Parameter> parameters = new ArrayList<>();
        while (parametersNodes.hasNext()) {
            JsonNode node = parametersNodes.next();
            Parameter p = objectMapper.treeToValue(node, Parameter.class);
            parameters.add(p);
        }
        return parameters;
    }
}
