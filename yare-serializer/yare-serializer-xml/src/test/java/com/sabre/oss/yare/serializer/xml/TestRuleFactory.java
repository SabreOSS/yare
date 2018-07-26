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

package com.sabre.oss.yare.serializer.xml;

import com.google.common.collect.ImmutableMap;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.serializer.model.*;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

import static java.time.ZoneId.ofOffset;
import static java.time.ZonedDateTime.of;
import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;
import static java.util.Arrays.asList;

final class TestRuleFactory {
    private TestRuleFactory() {
    }

    static Rule constructInvalidRule() {
        return new Rule(Collections.emptySet(), Collections.emptyList(), null, Collections.emptyList());
    }

    static RuleSer constructValidRuleWithBuildInObjectTypes() {
        return new RuleSer()
                .withFact(new FactSer().withName("fact").withType("com.sabre.oss.yare.serializer.xml.fact.SimpleFact"))
                .withFact(new FactSer().withName("otherFact").withType("com.sabre.oss.yare.serializer.xml.fact.OtherFact"))
                .withAttribute(new AttributeSer()
                        .withName("ruleName")
                        .withValue(new ValueSer()
                                .withType("String")
                                .withValue("RuleName")))
                .withAttribute(new AttributeSer()
                        .withName("ruleType")
                        .withValue(new ValueSer()
                                .withType("String")
                                .withValue("RuleType")))
                .withAttribute(new AttributeSer()
                        .withName("startDate")
                        .withValue(new ValueSer()
                                .withType("ZonedDateTime")
                                .withValue(of(2016, 6, 30, 0, 0, 0, 0, ZoneId.of("Europe/London")).format(ISO_ZONED_DATE_TIME))))
                .withAttribute(new AttributeSer()
                        .withName("expireDate")
                        .withValue(new ValueSer()
                                .withType("ZonedDateTime")
                                .withValue(of(2017, 1, 2, 0, 0, 0, 0, ofOffset("", ZoneOffset.of("+00:00"))).format(ISO_ZONED_DATE_TIME))))
                .withAttribute(new AttributeSer()
                        .withName("someVariable")
                        .withValue(new ValueSer()
                                .withType("Boolean")
                                .withValue("true")))
                .withPredicate(new PredicateSer()
                        .withAnd(new AndSer()
                                .withBooleanExpression(new AndSer()
                                        .withBooleanExpression(new ValueSer().withType("Boolean").withValue("false"))
                                        .withBooleanExpression(new OperatorSer()
                                                .withType("EQUALS")
                                                .withOperand(new ValueSer().withValue("${someVariable}"))
                                                .withOperand(new ValueSer().withType("Boolean").withValue("false")))
                                        .withBooleanExpression(new OperatorSer()
                                                .withType("EQUALS")
                                                .withOperand(new ValueSer().withValue("${fact.boolField}"))
                                                .withOperand(new ValueSer().withValue("${otherFact.otherField}"))))
                                .withBooleanExpression(new OrSer()
                                        .withBooleanExpression(new ValueSer().withType("Boolean").withValue("false"))
                                        .withBooleanExpression(new OperatorSer()
                                                .withType("EQUALS")
                                                .withOperand(new ValueSer().withValue("${someVariable}"))
                                                .withOperand(new ValueSer().withType("Boolean").withValue("false"))))
                                .withBooleanExpression(new NotSer()
                                        .withValue(new ValueSer().withType("Boolean").withValue("false")))
                                .withBooleanExpression(new OperatorSer()
                                        .withType("EQUALS")
                                        .withOperand(new ValueSer().withValue("${someVariable}"))
                                        .withOperand(new ValueSer().withType("Boolean").withValue("false")))
                                .withBooleanExpression(new FunctionSer()
                                        .withName("someFunction")
                                        .withReturnType("Boolean")
                                        .withParameter(new ParameterSer()
                                                .withName("funcArg1")
                                                .withValue(new ValueSer().withValue("value").withType("String")))
                                        .withParameter(new ParameterSer()
                                                .withName("funcArg1")
                                                .withValue(new ValueSer().withValue("${fact}")))
                                        .withParameter(new ParameterSer()
                                                .withName("funcArg3")
                                                .withFunction(new FunctionSer()
                                                        .withName("otherFunction")
                                                        .withReturnType("Long")
                                                        .withParameter(new ParameterSer()
                                                                .withName("param")
                                                                .withValue(new ValueSer().withValue("${fact.stringField}"))))))
                                .withBooleanExpression(new ValueSer().withType("Boolean").withValue("false"))))
                .withAction(new ActionSer()
                        .withName("ActionName1")
                        .withParameter(new ParameterSer()
                                .withName("ParameterName1")
                                .withValues(new ValuesSer()
                                        .withType("String")
                                        .withOperand(
                                                new ValueSer().withValue("anyValueA"),
                                                new ValueSer().withValue("${fact.stringField}"),
                                                new FunctionSer()
                                                        .withName("function")
                                                        .withReturnType("String"))))
                        .withParameter(new ParameterSer()
                                .withName("ParameterName2")
                                .withValues(new ValuesSer()
                                        .withType("List")
                                        .withOperand(
                                                new ValuesSer()
                                                        .withType("String")
                                                        .withOperand(new ValueSer().withValue("anyValueB")),
                                                new ValuesSer()
                                                        .withType("String")
                                                        .withOperand(new ValueSer().withValue("anyValueC"))))))
                .withAction(new ActionSer()
                        .withName("ActionName1")
                        .withParameter(new ParameterSer()
                                .withName("ParameterName1")
                                .withValue(new ValueSer().withType("String").withValue("anyValueA")))
                        .withParameter(new ParameterSer()
                                .withName("ParameterName2")
                                .withValue(new ValueSer().withType("String").withValue("anyValueB")))
                );
    }

    static RuleSer constructValidRuleWithCustomObjectTypes() {
        ParameterSer[] operands = new ParameterSer[]{
                new ParameterSer()
                        .withName("object")
                        .withCustomValue(new CustomValueSer()
                        .withType(Isbn.class.getCanonicalName())
                        .withAny(new Isbn("000000000001"))),

                new ParameterSer()
                        .withName("wrappedList")
                        .withCustomValue(new CustomValueSer()
                        .withType(MyList.class.getCanonicalName())
                        .withAny(new MyList(asList(
                                new Isbn("000000000021"),
                                new Isbn("000000000022"))))),

                new ParameterSer()
                        .withName("wrappedMap")
                        .withCustomValue(new CustomValueSer()
                        .withType(MyMap.class.getCanonicalName())
                        .withAny(new MyMap(
                                ImmutableMap.of(
                                        "key1", new Isbn("000000000031"),
                                        "key2", new Isbn("000000000032")))))
        };

        return new RuleSer()
                .withFact(new FactSer().withName("fact").withType("Object"))
                .withAttribute(new AttributeSer().withName("ruleName").withValue(new ValueSer().withType("String").withValue("ruleOperatingOnCustomObjectTypes")))
                .withAttribute(new AttributeSer()
                        .withName("customObject")
                        .withCustomValue(new CustomValueSer()
                                .withType(Isbn.class.getCanonicalName())
                                .withAny(new Isbn("000000000001"))))
                .withAttribute(new AttributeSer()
                        .withName("null")
                        .withValue(new ValueSer().withType("String").withValue("@null")))
                .withPredicate(new PredicateSer()
                        .withOr(new OrSer()
                                .withBooleanExpression(new FunctionSer()
                                        .withName("functionTakingCustomObjectsAsArguments")
                                        .withParameter(operands))
                                .withBooleanExpression(new OperatorSer()
                                        .withType("contains")
                                        .withOperand(operands[1].getCustomValue())
                                        .withOperand(operands[0].getCustomValue()))
                                .withBooleanExpression(new OperatorSer()
                                        .withType("anyOtherOperator")
                                        .withOperand(operands[1].getCustomValue())
                                        .withOperand(operands[2].getCustomValue()))))
                .withAction(new ActionSer()
                        .withName("actionTakingCustomObjectsAsArguments")
                        .withParameter(operands));
    }

    @XmlRootElement(namespace = "http://example.sabre.com/custom/schema", name = "Isbn")
    static class Isbn {
        private String code;

        Isbn() {
            this("000000000000");
        }

        Isbn(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Isbn isbn = (Isbn) o;
            return Objects.equals(code, isbn.code);
        }

        @Override
        public int hashCode() {
            return Objects.hash(code);
        }
    }

    @XmlRootElement(namespace = "http://example.sabre.com/custom/schema", name = "MyList")
    static class MyList {
        @XmlElement(name = "Item")
        private List<Isbn> items = new ArrayList<>();

        MyList() {
        }

        MyList(List<Isbn> items) {
            this.items = items;
        }

        public List<Isbn> getItems() {
            return items;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MyList myList = (MyList) o;
            return Objects.equals(items, myList.items);
        }

        @Override
        public int hashCode() {
            return Objects.hash(items);
        }
    }

    @XmlRootElement(namespace = "http://example.sabre.com/custom/schema", name = "MyMap")
    static class MyMap {
        @XmlElement
        private Map<String, Isbn> map = new HashMap<>();

        MyMap() {
        }

        MyMap(Map<String, Isbn> map) {
            this.map = map;
        }

        public Map<String, Isbn> getMap() {
            return map;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MyMap myMap = (MyMap) o;
            return Objects.equals(map, myMap.map);
        }

        @Override
        public int hashCode() {
            return Objects.hash(map);
        }
    }
}
