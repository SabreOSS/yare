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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

public interface CarXml {

    @SuppressWarnings("UnnecessaryInterfaceModifier")
    // tag::part-of-operand-xml-car-class[]
    @XmlRootElement(namespace = "http://example.sabre.com/example/model/v1")
    class Car {
        private String model;
        private int productionYear;

        public Car() {
        }

        public Car(String model, int productionYear) {
            this.model = model;
            this.productionYear = productionYear;
        }

        @XmlElement
        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        @XmlAttribute(name = "year")
        public int getProductionYear() {
            return productionYear;
        }

        public void setProductionYear(int productionYear) {
            this.productionYear = productionYear;
        }
        // end::part-of-operand-xml-car-class[]

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Car car = (Car) o;
            return productionYear == car.productionYear &&
                    Objects.equals(model, car.model);
        }

        @Override
        public int hashCode() {
            return Objects.hash(model, productionYear);
        }
        // tag::part-of-operand-xml-car-class[]
    }
    // end::part-of-operand-xml-car-class[]
}
