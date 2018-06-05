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

package com.sabre.oss.yare.invoker.java;

import java.util.List;

public class PublicCollector implements Collector {
    public PublicCollector() {
    }

    @Override
    public void collect(List<Object> context, Object object) {
    }

    public static class StaticPublicInner implements Collector {

        @Override
        public void collect(List<Object> context, Object object) {
        }

        public static class StaticPublicTooDeeplyNested implements Collector {

            @Override
            public void collect(List<Object> context, Object object) {
            }
        }
    }

    public static class StaticPublicInnerWithDefaultPublicConstructor implements Collector {
        public StaticPublicInnerWithDefaultPublicConstructor() {
        }

        @Override
        public void collect(List<Object> context, Object object) {
        }
    }

    public static class StaticPublicInnerWithDefaultPackageConstructor implements Collector {
        StaticPublicInnerWithDefaultPackageConstructor() {
        }

        @Override
        public void collect(List<Object> context, Object object) {
        }
    }

    public static class StaticPublicInnerWithNoDefaultConstructor implements Collector {
        public StaticPublicInnerWithNoDefaultConstructor(int i) {
        }

        @Override
        public void collect(List<Object> context, Object object) {
        }
    }

    public class PublicInner implements Collector {
        public PublicInner() {
        }

        @Override
        public void collect(List<Object> context, Object object) {
        }

        public class PublicTooDeeplyNested implements Collector {

            @Override
            public void collect(List<Object> context, Object object) {
            }
        }
    }

    static class StaticPackageInner implements Collector {

        @Override
        public void collect(List<Object> context, Object object) {
        }
    }

    class PackageInner implements Collector {

        @Override
        public void collect(List<Object> context, Object object) {
        }
    }
}
