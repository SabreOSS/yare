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
import com.sabre.oss.yare.serializer.json.model.Operand;
import com.sabre.oss.yare.serializer.json.model.Operator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class OperatorDeserializationHandler extends DeserializationHandler {
    @Override
    protected boolean isApplicable(JsonNode jsonNode) {
        String fieldName = jsonNode.fieldNames().next();
        JsonNode operatorNode = jsonNode.get(fieldName);
        return operatorNode.isArray();
    }

    @Override
    protected Operand deserialize(JsonNode jsonNode, ObjectMapper objectMapper)
            throws JsonProcessingException {
        String operatorType = getOperatorType(jsonNode);
        JsonNode operandsNode = jsonNode.get(operatorType);
        List<Operand> operands = getOperands(operandsNode, objectMapper);
        return new Operator()
                .withType(operatorType)
                .withOperands(operands);
    }

    private String getOperatorType(JsonNode jsonNode) {
        return jsonNode.fieldNames().next();
    }

    private List<Operand> getOperands(JsonNode jsonNode, ObjectMapper objectMapper)
            throws JsonProcessingException {
        Iterator<JsonNode> operandNodes = jsonNode.iterator();
        List<Operand> operands = new ArrayList<>();
        while (operandNodes.hasNext()) {
            JsonNode node = operandNodes.next();
            Operand o = objectMapper.treeToValue(node, Operand.class);
            operands.add(o);
        }
        return operands;
    }
}
