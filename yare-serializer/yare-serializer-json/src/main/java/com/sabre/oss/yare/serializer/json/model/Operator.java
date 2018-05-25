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

package com.sabre.oss.yare.serializer.json.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sabre.oss.yare.serializer.json.serializer.OperatorSerializer;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@JsonSerialize(using = OperatorSerializer.class)
public class Operator implements Operand {
    private String type;
    private List<Operand> operands;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Operator withType(String type) {
        this.type = type;
        return this;
    }

    public List<Operand> getOperands() {
        return operands;
    }

    public void setOperands(List<Operand> operands) {
        this.operands = operands;
    }

    public Operator withOperands(List<Operand> operands) {
        this.operands = operands;
        return this;
    }

    public Operator withOperands(Operand... operands) {
        return withOperands(Arrays.asList(operands));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Operator operator = (Operator) o;
        return Objects.equals(type, operator.type) &&
                Objects.equals(operands, operator.operands);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, operands);
    }
}
