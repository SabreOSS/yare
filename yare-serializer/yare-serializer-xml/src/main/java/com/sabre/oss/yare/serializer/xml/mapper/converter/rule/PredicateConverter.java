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

package com.sabre.oss.yare.serializer.xml.mapper.converter.rule;

import com.sabre.oss.yare.core.model.Expression;
import com.sabre.oss.yare.serializer.xml.model.PredicateSer;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static java.util.Arrays.asList;

class PredicateConverter {
    private static final List<Function<PredicateSer, ?>> getters = asList(
            PredicateSer::getValue,
            PredicateSer::getFunction,
            PredicateSer::getOperator,
            PredicateSer::getAnd,
            PredicateSer::getOr,
            PredicateSer::getNot
    );
    private final NodeConverter nodeConverter;

    PredicateConverter(NodeConverter nodeConverter) {
        this.nodeConverter = nodeConverter;
    }

    Expression convert(PredicateSer predicate) {
        if (predicate != null) {
            Object toConvert = getters.stream()
                    .map(f -> f.apply(predicate))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Predicate seems to be not defined properly"));

            return nodeConverter.convert(null, toConvert);
        }

        return null;
    }
}
