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

import java.util.Objects;

// tag::part-of-changing-example-fact[]
public class Airline {
    private final String name;
    private boolean rejected = false;

    public Airline(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean getRejected() {
        return rejected;
    }

    public Airline withRejected(boolean rejected) {
        this.rejected = rejected;
        return this;
    }
    // end::part-of-changing-example-fact[]

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Airline airline = (Airline) o;
        return rejected == airline.rejected &&
                Objects.equals(name, airline.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, rejected);
    }
    // tag::part-of-changing-example-fact[]
}
// end::part-of-changing-example-fact[]
