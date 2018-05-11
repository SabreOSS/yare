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

package com.sabre.oss.yare.serializer;

import com.google.common.collect.ImmutableMap;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.serializer.model.*;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

import static java.time.ZoneId.ofOffset;
import static java.time.ZonedDateTime.of;
import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;
import static java.util.Arrays.asList;

abstract class ConverterTest {
    static Rule constructInvalidRule() {
        return new Rule(Collections.emptySet(), Collections.emptyList(), null, Collections.emptyList());
    }

    static RuleSer constructValidRuleWithBuildInObjectTypes() {
        return new RuleSer()
                .withFact(new FactSer().withName("fact").withType("com.sabre.oss.yare.serializer.SimpleFact"))
                .withFact(new FactSer().withName("otherFact").withType("com.sabre.oss.yare.serializer.OtherFact"))
                .withAttribute(new AttributeSer()
                        .withName("ruleName")
                        .withValue("RuleName")
                        .withType(String.class.getName()))
                .withAttribute(new AttributeSer()
                        .withName("ruleType")
                        .withType(String.class.getName())
                        .withValue("RuleType"))
                .withAttribute(new AttributeSer()
                        .withName("startDate")
                        .withType(ZonedDateTime.class.getName())
                        .withValue(of(2016, 6, 30, 0, 0, 0, 0, ZoneId.of("Europe/London")).format(ISO_ZONED_DATE_TIME)))
                .withAttribute(new AttributeSer()
                        .withName("expireDate")
                        .withType(ZonedDateTime.class.getName())
                        .withValue(of(2017, 1, 2, 0, 0, 0, 0, ofOffset("", ZoneOffset.of("+00:00"))).format(ISO_ZONED_DATE_TIME)))
                .withAttribute(new AttributeSer()
                        .withName("someVariable")
                        .withType(Boolean.class.getName())
                        .withValue("true"))
                .withPredicate(new PredicateSer()
                        .withAnd(new AndSer()
                                .withBooleanExpression(new AndSer()
                                        .withBooleanExpression(new ValueSer().withType(Boolean.class.getName()).withValue("false"))
                                        .withBooleanExpression(new OperatorSer()
                                                .withType("EQUALS")
                                                .withOperand(new ReferenceSer().withRef("someVariable"))
                                                .withOperand(new ValueSer().withType(Boolean.class.getName()).withValue("false")))
                                        .withBooleanExpression(new OperatorSer()
                                                .withType("EQUALS")
                                                .withOperand(new FieldSer()
                                                        .withRef("fact")
                                                        .withPath("boolField")
                                                        .withType("java.lang.Boolean"))
                                                .withOperand(new FieldSer()
                                                        .withRef("otherFact")
                                                        .withPath("otherField"))))
                                .withBooleanExpression(new OrSer()
                                        .withBooleanExpression(new ValueSer().withType(Boolean.class.getName()).withValue("false"))
                                        .withBooleanExpression(new OperatorSer()
                                                .withType("EQUALS")
                                                .withOperand(new ReferenceSer().withRef("someVariable"))
                                                .withOperand(new ValueSer().withType(Boolean.class.getName()).withValue("false"))))
                                .withBooleanExpression(new NotSer()
                                        .withValue(new ValueSer().withType(Boolean.class.getName()).withValue("false")))
                                .withBooleanExpression(new OperatorSer()
                                        .withType("EQUALS")
                                        .withOperand(new ReferenceSer().withRef("someVariable"))
                                        .withOperand(new ValueSer().withType(Boolean.class.getName()).withValue("false")))
                                .withBooleanExpression(new FunctionSer()
                                        .withName("someFunction")
                                        .withReturnType("java.lang.Boolean")
                                        .withParameter(new ParameterSer()
                                                .withName("funcArg1")
                                                .withValue(new ValueSer().withValue("value").withType(String.class.getName())))
                                        .withParameter(new ParameterSer()
                                                .withName("funcArg1")
                                                .withReference(new ReferenceSer().withRef("fact")))
                                        .withParameter(new ParameterSer()
                                                .withName("funcArg3")
                                                .withFunction(new FunctionSer()
                                                        .withName("otherFunction")
                                                        .withReturnType("java.lang.Long")
                                                        .withParameter(new ParameterSer()
                                                                .withName("param")
                                                                .withField(new FieldSer()
                                                                        .withRef("fact")
                                                                        .withPath("stringField")
                                                                        .withType("java.lang.String"))))))
                                .withBooleanExpression(new ValueSer().withType(Boolean.class.getName()).withValue("false"))))
                .withAction(new ActionSer()
                        .withName("ActionName1")
                        .withParameter(new ParameterSer()
                                .withName("ParameterName1")
                                .withValues(new ValuesSer()
                                        .withType("java.lang.String")
                                        .withValue(new ValueSer().withValue("anyValueA"))))
                        .withParameter(new ParameterSer()
                                .withName("ParameterName2")
                                .withValues(new ValuesSer()
                                        .withType("java.lang.String")
                                        .withValue(new ValueSer().withValue("anyValueB")))))
                .withAction(new ActionSer()
                        .withName("ActionName1")
                        .withParameter(new ParameterSer()
                                .withName("ParameterName1")
                                .withValue(new ValueSer().withType(String.class.getName()).withValue("anyValueA")))
                        .withParameter(new ParameterSer()
                                .withName("ParameterName2")
                                .withValue(new ValueSer().withType(String.class.getName()).withValue("anyValueB")))
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
                .withFact(new FactSer().withName("fact").withType("java.lang.Object"))
                .withAttribute(new AttributeSer().withName("ruleName").withType(String.class.getName()).withValue("ruleOperatingOnCustomObjectTypes"))
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
