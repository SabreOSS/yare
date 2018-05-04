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

import static com.sabre.oss.yare.engine.executor.runtime.value.TypeUtils.*;

public class ReferringCodeGenerator {
    private static final String BOXING_UTILS_BOX = BoxingUtils.class.getCanonicalName() + ".box";
    private Integer referenceCounter = 0;
    private boolean afterCollection = false;

    String generateReferringBody(ReferMetadata referMetadata) {
        Type referType = referMetadata.getRefType();
        String ref = referMetadata.getRef();
        return isCollection(referType)
                ? generateLoop(getCollectionGeneric(referType).getTypeName(), ref)
                : generateAssignment(getRawType(referType).getTypeName(), ref);
    }

    String generateEndingReferringBodyWhenChaining(ReferMetadata referMetadata) {
        Type referType = referMetadata.getRefType();
        String ref = referMetadata.getRef();
        return isCollection(referType) && referMetadata.getPathPart().contains("[*]")
                ? generateEndingLoop(getCollectionGeneric(referType).getTypeName(), ref)
                : generateEndingAssignment(getRawType(referType).getTypeName(), ref, "result.add(%s(%s));");
    }

    String generateEndingReferringBodyWhenNotChaining(ReferMetadata referMetadata) {
        Type referType = referMetadata.getRefType();
        String ref = referMetadata.getRef();
        return generateEndingAssignment(getRawType(referType).getTypeName(), ref, "return %s(%s);");
    }

    private String generateLoop(String type, String propertyRef) {
        String template = generateLoopTemplate(type, propertyRef);
        afterCollection = true;
        String ref = getRefName();
        return String.format(template, generateNullCheck(ref) + "%s");
    }

    private String generateEndingLoop(String type, String propertyRef) {
        String propertyRefWithoutOperator = propertyRef.replace("[*]", "");
        String template = generateLoopTemplate(type, propertyRefWithoutOperator);
        String listAdding = String.format("result.add(%s(%s));", BOXING_UTILS_BOX, getRefName());
        return String.format(template, listAdding);
    }

    private String generateAssignment(String type, String propertyRef) {
        String referenceToField = String.format("%s.%s", getRefName(), propertyRef);
        String template = generateAssignmentTemplate(type, propertyRef);
        String ref = getRefName();
        return generateNullCheck(referenceToField) + String.format(template,
                generateNullCheck(ref) + "%s");
    }

    private String generateEndingAssignment(String type, String propertyRef, String action) {
        return String.format(generateAssignmentTemplate(type, propertyRef),
                String.format(action, BOXING_UTILS_BOX, getRefName()));
    }

    private String generateLoopTemplate(String type, String propertyRef) {
        String outerRef = getRefName();
        String iterator = getIteratorName();
        referenceCounter++;
        String ref = getRefName();
        String referenceToField = String.format("%s.%s", outerRef, propertyRef);
        return String.format("" +
                        "%s" +
                        "java.util.Iterator %s = %s.iterator(); \n" +
                        "while(%s.hasNext()) { \n" +
                        "   %s %s = (%s) %s.next(); \n" +
                        "   %%s \n" +
                        "} \n",
                generateNullCheck(referenceToField),
                iterator, referenceToField, iterator, type, ref, type, iterator);
    }

    private String generateAssignmentTemplate(String type, String propertyRef) {
        String outerRef = getRefName();
        referenceCounter++;
        String ref = getRefName();
        String referenceToField = String.format("%s.%s", outerRef, propertyRef);
        return String.format("" +
                        "%s %s = (%s) %s; \n" +
                        "%%s",
                type, ref, type, referenceToField);
    }

    private String generateNullCheck(String toCheck) {
        return afterCollection
                ? generateNullCheckingForLoop(toCheck)
                : generateNullCheckingForAssignment(toCheck);
    }

    private String generateNullCheckingForLoop(String ref) {
        String action = "continue;";
        return String.format(generateNullCheckingIf(ref), action);
    }

    private String generateNullCheckingForAssignment(String ref) {
        String action = "return null;";
        return String.format(generateNullCheckingIf(ref), action);
    }

    private String generateNullCheckingIf(String ref) {
        return String.format("" +
                        "if(%s(%s) == null) { \n" +
                        "   %%s \n" +
                        "} \n",
                BOXING_UTILS_BOX, ref);
    }

    private String getRefName() {
        return "v" + referenceCounter;
    }

    private String getIteratorName() {
        return "i" + referenceCounter;
    }
}
