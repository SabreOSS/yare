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

package com.sabre.oss.yare.documentation.userguide;

import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.dsl.CollectionOperand;
import com.sabre.oss.yare.dsl.Operand;
import com.sabre.oss.yare.dsl.RuleDsl;
import com.sabre.oss.yare.model.converter.RuleConverter;
import com.sabre.oss.yare.serializer.xml.RuleToXmlConverter;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OperandsTest {

    @Test
    void buildInTypesOperandsCreation() {
        // tag::part-of-operand-build-in-operands-creation[]
        // build-in type operands
        Operand<Boolean> booleanArg = RuleDsl.value(true);
        Operand<String> stringArg = RuleDsl.value("any string");
        Operand<Integer> integerArg = RuleDsl.value(10);
        Operand<ZonedDateTime> zonedDataTimeArgFromInstance = RuleDsl.value(ZonedDateTime.now(), ZonedDateTime.class);
        Operand<ZonedDateTime> zonedDataTimeArgFromString = RuleDsl.value("2012-12-02T11:15:00+00:00", ZonedDateTime.class);
        Operand<String> nullStringArg = RuleDsl.value(null, String.class);
        Operand<Long> nullLongArg = RuleDsl.value((Long) null, Long.class);

        // collections of build-in type operands
        CollectionOperand<String> stringsCollectionArg = RuleDsl.values(String.class, "Stella", "Aurelia");
        CollectionOperand<BigDecimal> bigDecimalsCollectionArg = RuleDsl.values(BigDecimal.class, BigDecimal.valueOf(10.1), null);

        // end::part-of-operand-build-in-operands-creation[]
    }

    @Test
    void customTypeOperandsCreation() {
        // tag::part-of-operand-custom-type-operand-creation[]
        Operand<Car> car = RuleDsl.value(new Car("Audi", 2017));
        CollectionOperand<Car> cars = RuleDsl.values(Car.class, new Car("Jeep", 2015), new Car("Mitsubishi", 2010));
        // end::part-of-operand-custom-type-operand-creation[]
    }

    @Test
    void shouldMarshalAndUnmarshalCarToXml() {
        Operand<CarXml.Car> car = RuleDsl.value(new CarXml.Car("Audi", 2017));
        Operand<CarXml.Car> car2 = RuleDsl.value(new CarXml.Car("Mitsubishi", 2017));
        Rule originalRule = RuleDsl.ruleBuilder().predicate(RuleDsl.equal(car, car2)).build(false);

        RuleConverter converter = RuleToXmlConverter.getInstance(CarXml.Car.class);
        String ruleAsString = converter.marshal(originalRule);

        Rule rule = converter.unmarshal(ruleAsString);

        assertThat(rule).isEqualTo(originalRule);
    }
}
