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

package com.sabre.oss.yare.examples.facts;

import java.time.LocalDateTime;
import java.util.Objects;

public class Flight {

    private String classOfService;
    private LocalDateTime dateOfDeparture;

    public String getClassOfService() {
        return this.classOfService;
    }

    public LocalDateTime getDateOfDeparture() {
        return dateOfDeparture;
    }

    public Flight withClassOfService(final String classOfService) {
        this.classOfService = classOfService;
        return this;
    }

    public Flight withDateOfDeparture(final LocalDateTime dateOfDeparture) {
        this.dateOfDeparture = dateOfDeparture;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Flight that = (Flight) o;
        return Objects.equals(classOfService, that.classOfService) &&
                Objects.equals(dateOfDeparture, that.dateOfDeparture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classOfService, dateOfDeparture);
    }
}
