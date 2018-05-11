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

package com.sabre.oss.yare.serializer.xml.mapper.converter.xml;

import com.sabre.oss.yare.common.converter.TypeConverter;
import com.sabre.oss.yare.common.mapper.Mapper;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.serializer.xml.model.AttributeSer;
import com.sabre.oss.yare.serializer.xml.model.FactSer;
import com.sabre.oss.yare.serializer.xml.model.RuleSer;

import java.util.List;
import java.util.stream.Collectors;

public class ToXmlConverter implements Mapper<Rule, RuleSer> {
    private final AttributeConverter attributeConverter;
    private final FactConverter factConverter;
    private final ExpressionConverter expressionConverter;
    private final ActionConverter actionConverter;

    public ToXmlConverter(TypeConverter typeConverter) {
        this.attributeConverter = new AttributeConverter(typeConverter);
        this.factConverter = new FactConverter(typeConverter);
        this.expressionConverter = new ExpressionConverter(typeConverter);
        this.actionConverter = new ActionConverter(expressionConverter);
    }

    @Override
    public RuleSer map(Rule rule) {
        List<AttributeSer> attributes = prepareAttributes(rule);
        List<FactSer> facts = prepareFacts(rule);
        return new RuleSer()
                .withAttribute(attributes)
                .withFact(facts)
                .withPredicate(expressionConverter.map(rule.getPredicate()))
                .withAction(actionConverter.map(rule.getActions()));
    }

    private List<FactSer> prepareFacts(Rule rule) {
        return rule.getFacts().stream()
                .map(factConverter::map)
                .collect(Collectors.toList());
    }

    private List<AttributeSer> prepareAttributes(Rule rule) {
        return rule.getAttributes().stream()
                .map(attributeConverter::map)
                .collect(Collectors.toList());
    }
}
