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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.sabre.oss.yare.serializer.json.model.Function;
import com.sabre.oss.yare.serializer.json.model.Operand;
import com.sabre.oss.yare.serializer.json.model.Parameter;

import java.util.ArrayList;
import java.util.List;

class FunctionDeserializationHandler extends DeserializationHandler {
    @Override
    protected boolean isApplicable(JsonNode jsonNode) {
        return jsonNode.has("function");
    }

    @Override
    protected Operand deserialize(JsonNode jsonNode, ObjectMapper objectMapper) throws JsonProcessingException {
        JsonNode functionNode = jsonNode.get("function");
        String name = functionNode.get("name").textValue();
        List<Parameter> parameters = getParameters(functionNode, objectMapper);
        return new Function().withName(name).withParameters(parameters);
    }

    private List<Parameter> getParameters(JsonNode jsonNode, ObjectMapper objectMapper) throws JsonProcessingException {
        ArrayNode parametersNode = (ArrayNode) jsonNode.get("parameters");
        List<Parameter> parameters = new ArrayList<>();
        for (JsonNode parameterNode : (Iterable<JsonNode>) parametersNode::elements) {
            Parameter parameter = objectMapper.treeToValue(parameterNode, Parameter.class);
            parameters.add(parameter);
        }
        return parameters;
    }
}
