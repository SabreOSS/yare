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

package com.sabre.oss.yare.serializer.json.mapper.json;

import com.sabre.oss.yare.common.converter.TypeConverter;
import com.sabre.oss.yare.serializer.json.model.Attribute;
import com.sabre.oss.yare.serializer.json.model.Fact;
import com.sabre.oss.yare.serializer.json.model.Rule;

import java.util.List;
import java.util.stream.Collectors;

public class ToJsonConverter {
    private final AttributeConverter attributeConverter;
    private final FactConverter factConverter;
    private final PredicateConverter predicateConverter;
    private final ActionConverter actionConverter;

    public ToJsonConverter(TypeConverter typeConverter) {
        NodeConverter nodeConverter = new NodeConverter(typeConverter);
        this.attributeConverter = new AttributeConverter(typeConverter);
        this.factConverter = new FactConverter(typeConverter);
        this.predicateConverter = new PredicateConverter(nodeConverter);
        this.actionConverter = new ActionConverter(nodeConverter);
    }

    public Rule convert(com.sabre.oss.yare.core.model.Rule rule) {
        List<Attribute> attributes = prepareAttributes(rule);
        List<Fact> facts = prepareFacts(rule);
        return new Rule()
                .withAttributes(attributes)
                .withFacts(facts)
                .withPredicate(predicateConverter.convert(rule.getPredicate()))
                .withActions(actionConverter.convert(rule.getActions()));
    }

    private List<Attribute> prepareAttributes(com.sabre.oss.yare.core.model.Rule rule) {
        return rule.getAttributes().stream()
                .map(attributeConverter::convert)
                .collect(Collectors.toList());
    }

    private List<Fact> prepareFacts(com.sabre.oss.yare.core.model.Rule rule) {
        return rule.getFacts().stream()
                .map(factConverter::convert)
                .collect(Collectors.toList());
    }
}
