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

package com.sabre.oss.yare.core.model;

import com.sabre.oss.yare.core.model.Expression.Action;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class Rule {
    private final Set<Attribute> attributes;
    private final List<Fact> facts;
    private final Expression predicate;
    private final List<Action> actions;

    public Rule(Set<Attribute> attributes, List<Fact> facts, Expression predicate, List<Expression.Action> actions) {
        this.attributes = Collections.unmodifiableSet(attributes);
        this.facts = Collections.unmodifiableList(facts);
        this.predicate = predicate;
        this.actions = Collections.unmodifiableList(actions);
    }

    public Set<Attribute> getAttributes() {
        return attributes;
    }

    public boolean containsAttribute(String name) {
        return attributes.stream()
                .anyMatch(attr -> attr.getName().equals(name));
    }

    public Attribute getAttribute(String name) {
        return attributes.stream()
                .filter(attr -> attr.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public List<Fact> getFacts() {
        return facts;
    }

    public boolean containsFact(String identifier) {
        return facts.stream()
                .anyMatch(attr -> attr.getIdentifier().equals(identifier));
    }

    public Fact getFact(String identifier) {
        return facts.stream()
                .filter(attr -> attr.getIdentifier().equals(identifier))
                .findFirst()
                .orElse(null);
    }

    public Expression getPredicate() {
        return predicate;
    }

    public List<Expression.Action> getActions() {
        return actions;
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
        return Objects.equals(attributes, rule.attributes) &&
                Objects.equals(facts, rule.facts) &&
                Objects.equals(predicate, rule.predicate) &&
                Objects.equals(actions, rule.actions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributes, facts, predicate, actions);
    }
}
