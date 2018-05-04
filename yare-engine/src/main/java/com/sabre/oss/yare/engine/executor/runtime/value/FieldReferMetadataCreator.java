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

import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

public class FieldReferMetadataCreator implements ReferMetadataCreator {

    @Override
    public boolean isApplicable(Type type, String ref) {
        Field field;
        try {
            field = TypeUtils.getRawType(type, null).getField(ref);
        } catch (NoSuchFieldException e) {
            return false;
        }
        return Modifier.isPublic(field.getModifiers());
    }

    @Override
    public ReferMetadata getReferMetadata(Type type, String path) {
        String pathWithoutOperator = path.replace("[*]", "");
        Type refType;
        try {
            refType = TypeUtils.getRawType(type, null).getField(pathWithoutOperator).getGenericType();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return new ReferMetadata(refType, pathWithoutOperator, path);
    }
}
