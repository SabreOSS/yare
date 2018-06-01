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

import com.sabre.oss.yare.engine.Collector;

import java.util.List;

public class PublicCollector implements Collector {

    public PublicCollector() {
    }

    @Override
    public void collect(List<Object> context, Object object) {

    }

    public static class StaticPublicInnerInPublicOuter implements Collector {

        @Override
        public void collect(List<Object> context, Object object) {

        }

        public static class StaticPublicTooDeeplyNested implements Collector {

            @Override
            public void collect(List<Object> context, Object object) {

            }
        }
    }

    public static class StaticPublicInnerInPublicOuterWithDefaultPublicConstructor implements Collector {

        public StaticPublicInnerInPublicOuterWithDefaultPublicConstructor() {
        }

        @Override
        public void collect(List<Object> context, Object object) {

        }
    }

    public static class StaticPublicInnerInPublicOuterWithDefaultPackageConstructor implements Collector {

        StaticPublicInnerInPublicOuterWithDefaultPackageConstructor() {
        }

        @Override
        public void collect(List<Object> context, Object object) {

        }
    }

    public static class StaticPublicInnerInPublicOuterWithNoDefaultConstructor implements Collector {

        public StaticPublicInnerInPublicOuterWithNoDefaultConstructor(int i) {
        }

        @Override
        public void collect(List<Object> context, Object object) {

        }
    }

    public class PublicInnerInPublicOuter implements Collector {

        public PublicInnerInPublicOuter() {
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

    static class StaticPackageInnerInPublicOuter implements Collector {

        @Override
        public void collect(List<Object> context, Object object) {

        }
    }

    class PackageInnerInPublicOuter implements Collector {

        @Override
        public void collect(List<Object> context, Object object) {

        }
    }

}
