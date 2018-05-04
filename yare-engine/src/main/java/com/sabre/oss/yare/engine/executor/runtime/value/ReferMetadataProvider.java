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

package com.sabre.oss.yare.engine.executor.runtime.value;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static com.sabre.oss.yare.engine.executor.runtime.value.TypeUtils.getCollectionGeneric;
import static com.sabre.oss.yare.engine.executor.runtime.value.TypeUtils.isCollection;

public class ReferMetadataProvider {
    private List<ReferMetadataCreator> creators = Arrays.asList(new MapReferMetadataCreator(), new FieldReferMetadataCreator(), new GetterReferMetadataCreator());

    public ReferMetadata createReferMetadata(Type type, String ref) {
        Type actualType = type;
        if (isCollection(type)) {
            actualType = getCollectionGeneric(type);
        }
        for (ReferMetadataCreator creator : creators) {
            if (creator.isApplicable(actualType, ref.replace("[*]", ""))) {
                return creator.getReferMetadata(actualType, ref);
            }
        }
        throw new IllegalArgumentException(String.format("Unable to refer to field %s " +
                "Class doesn't implement map interface and " +
                "class doesn't have public field and " +
                "class doesn't have appropriate getter.", ref));
    }
}
