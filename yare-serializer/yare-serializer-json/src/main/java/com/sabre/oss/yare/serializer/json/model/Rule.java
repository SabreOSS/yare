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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Rule {
    private List<Attribute> attributes;
    private List<Fact> facts;
    private Operand predicate;
    private List<Action> actions;

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public Rule withAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
        return this;
    }

    public Rule withAttributes(Attribute... attributes) {
        return withAttributes(Arrays.asList(attributes));
    }

    public List<Fact> getFacts() {
        return facts;
    }

    public void setFacts(List<Fact> facts) {
        this.facts = facts;
    }

    public Rule withFacts(List<Fact> facts) {
        this.facts = facts;
        return this;
    }

    public Rule withFacts(Fact... facts) {
        return withFacts(Arrays.asList(facts));
    }

    public Operand getPredicate() {
        return predicate;
    }

    public void setPredicate(Operand predicate) {
        this.predicate = predicate;
    }

    public Rule withPredicate(Operand predicate) {
        this.predicate = predicate;
        return this;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public Rule withActions(List<Action> actions) {
        this.actions = actions;
        return this;
    }

    public Rule withActions(Action... actions) {
        return withActions(Arrays.asList(actions));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Rule rule = (Rule) o;
        boolean result = Objects.equals(attributes, rule.attributes) &&
                Objects.equals(facts, rule.facts) &&
                Objects.equals(predicate, rule.predicate) &&
                Objects.equals(actions, rule.actions);
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributes, facts, predicate, actions);
    }
}
