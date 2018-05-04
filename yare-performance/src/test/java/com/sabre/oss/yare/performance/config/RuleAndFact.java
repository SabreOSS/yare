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

package com.sabre.oss.yare.performance.config;

public class RuleAndFact {
    private final Object fact;
    private final String ruleName;

    public RuleAndFact(String ruleName, Object fact) {
        this.ruleName = ruleName;
        this.fact = fact;
    }

    public String getRuleName() {
        return ruleName;
    }

    public Object getFact() {
        return fact;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RuleAndFact that = (RuleAndFact) o;

        if (fact != null ? !fact.equals(that.fact) : that.fact != null) {
            return false;
        }
        return ruleName != null ? ruleName.equals(that.ruleName) : that.ruleName == null;

    }

    @Override
    public int hashCode() {
        int result = fact != null ? fact.hashCode() : 0;
        result = 31 * result + (ruleName != null ? ruleName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "fact=" + fact +
                ", ruleName='" + ruleName;
    }
}
