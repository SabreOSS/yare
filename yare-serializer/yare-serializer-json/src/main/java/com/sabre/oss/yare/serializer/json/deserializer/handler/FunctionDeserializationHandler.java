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
import com.sabre.oss.yare.serializer.json.model.Function;
import com.sabre.oss.yare.serializer.json.model.Operand;
import com.sabre.oss.yare.serializer.json.model.Parameter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class FunctionDeserializationHandler extends DeserializationHandler {
    private static final String FUNCTION_PROPERTY_NAME = "function";
    private static final String NAME_PROPERTY_NAME = "name";
    private static final String PARAMETERS_PROPERTY_NAME = "parameters";

    @Override
    protected boolean isApplicable(JsonNode jsonNode) {
        return jsonNode.has(FUNCTION_PROPERTY_NAME);
    }

    @Override
    protected Operand deserialize(JsonNode jsonNode, ObjectMapper objectMapper) throws JsonProcessingException {
        JsonNode functionNode = jsonNode.get(FUNCTION_PROPERTY_NAME);
        String functionName = functionNode.get(NAME_PROPERTY_NAME).textValue();
        List<Parameter> functionParameters = getParameters(functionNode, objectMapper);
        return new Function()
                .withName(functionName)
                .withParameters(functionParameters);
    }

    private List<Parameter> getParameters(JsonNode jsonNode, ObjectMapper objectMapper) throws JsonProcessingException {
        Iterator<JsonNode> parametersNodes = jsonNode.get(PARAMETERS_PROPERTY_NAME).iterator();
        List<Parameter> parameters = new ArrayList<>();
        while (parametersNodes.hasNext()) {
            JsonNode node = parametersNodes.next();
            Parameter p = objectMapper.treeToValue(node, Parameter.class);
            parameters.add(p);
        }
        return parameters;
    }
}
