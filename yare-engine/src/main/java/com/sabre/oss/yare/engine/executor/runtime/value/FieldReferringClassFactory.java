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

import com.sabre.oss.yare.engine.executor.runtime.predicate.PredicateContext;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.sabre.oss.yare.engine.executor.runtime.value.TypeUtils.getRawType;
import static com.sabre.oss.yare.engine.executor.runtime.value.TypeUtils.isCollection;

public abstract class FieldReferringClassFactory {
    private static final Logger log = LoggerFactory.getLogger(FieldReferringClassFactory.class);
    private static final Map<String, ValueProvider> valueProviders = new ConcurrentHashMap<>();
    private static final ClassPool classPool = createClassPool();

    private FieldReferringClassFactory() {
    }

    public static ValueProvider create(Class<?> targetClass, String identifier, String propertyName) {
        String key = nameForType(targetClass, propertyName) + '$' + identifier;
        return valueProviders.computeIfAbsent(key, k -> createFieldReferringInstance(targetClass, identifier, propertyName));
    }

    private static ValueProvider createFieldReferringInstance(Class<?> targetClass, String identifier, String propertyName) {
        String className = nameForType(targetClass, propertyName);
        Class<?> fieldReferringClass = createClass(targetClass, className, propertyName);
        return createInstance(fieldReferringClass, identifier);
    }

    private static String nameForType(Class<?> clazz, String propertyName) {
        return clazz.getName() + "$impl_" + propertyName.replace("[*]", "Grouped");
    }

    private static Class<?> createClass(Class<?> targetClass, String className, String path) {
        Class<?> fieldReferringClass;
        try {
            fieldReferringClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            CtClass ctClass = classPool.makeClass(className);
            setSuperClass(ctClass);
            createMethods(targetClass, path.replaceAll("!", ""), ctClass);
            fieldReferringClass = getFieldReferringClass(ctClass);
            log.debug("Created ValueProvider implementation: {}\n", className);
        }
        return fieldReferringClass;
    }

    private static void setSuperClass(CtClass fieldReferringClass) {
        try {
            fieldReferringClass.setSuperclass(classPool.get(AbstractFieldReferringValueProvider.class.getName()));
        } catch (CannotCompileException | NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createMethods(Class<?> targetClass, String path, CtClass ctClass) {
        List<ReferMetadata> referMetadata = resolveReferMetadata(targetClass, path);
        try {
            String getValueMethodBody = createGetValueMethodBody(targetClass, referMetadata);
            CtMethod getValueMethod = CtMethod.make(getValueMethodBody, ctClass);
            ctClass.addMethod(getValueMethod);

            String getTypeMethodBody = createGetTypeMethodBody(targetClass, referMetadata);
            CtMethod getTypeMethod = CtMethod.make(getTypeMethodBody, ctClass);
            ctClass.addMethod(getTypeMethod);
        } catch (CannotCompileException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<ReferMetadata> resolveReferMetadata(Class<?> targetClass, String path) {
        List<ReferMetadata> referMetadataPath = new LinkedList<>();
        ReferMetadataProvider provider = new ReferMetadataProvider();
        Type currentType = targetClass;
        List<String> pathSeparated = new LinkedList<>(Arrays.asList(path.split("\\.")));
        for (String pathPart : pathSeparated) {
            ReferMetadata refMetadata = provider.createReferMetadata(currentType, pathPart);
            referMetadataPath.add(refMetadata);
            currentType = refMetadata.getRefType();
        }
        return referMetadataPath;
    }

    private static String createGetValueMethodBody(Class<?> targetClass, List<ReferMetadata> referMetadata) {
        return String.format(
                "public Object get(%s ctx) { \n" +
                        "%s v0 = (%s) ctx.resolve(identifier); \n" +
                        "if (v0 == null) return null; \n" +
                        "java.util.List result = new java.util.LinkedList(); \n" +
                        "%s \n" +
                        "return result; \n" +
                        "} \n",
                PredicateContext.class.getCanonicalName(),
                targetClass.getCanonicalName(),
                targetClass.getCanonicalName(),
                createChainingStatements(referMetadata, 0, new ReferringCodeGenerator()));
    }

    private static String createGetTypeMethodBody(Class<?> targetClass, List<ReferMetadata> referMetadata) {
        Type type = isAnyReferTypeCollection(referMetadata.subList(0, referMetadata.size() - 1))
                ? List.class
                : referMetadata.isEmpty() ? targetClass : referMetadata.get(referMetadata.size() - 1).getRefType();
        return String.format(
                "public final %s getType() { \n" +
                        "return %s.class; \n" +
                        "} \n",
                Type.class.getCanonicalName(),
                getRawType(type).getCanonicalName());
    }

    private static String createChainingStatements(List<ReferMetadata> referMetadata, int currentElement, ReferringCodeGenerator referringCodeGenerator) {
        if (referMetadata.isEmpty()) {
            return "result.add(v0);";
        }
        return isLastElement(referMetadata, currentElement)
                ? createEndingReferringBody(referMetadata, currentElement, referringCodeGenerator)
                : createReferringBody(referMetadata, currentElement, referringCodeGenerator);
    }

    private static String createEndingReferringBody(List<ReferMetadata> referMetadata, int currentElement, ReferringCodeGenerator referringCodeGenerator) {
        return isAnyReferTypeCollection(referMetadata.subList(0, referMetadata.size() - 1))
                ? referringCodeGenerator.generateEndingReferringBodyWhenChaining(referMetadata.get(currentElement))
                : referringCodeGenerator.generateEndingReferringBodyWhenNotChaining(referMetadata.get(currentElement));
    }

    private static String createReferringBody(List<ReferMetadata> referMetadata, int currentElement, ReferringCodeGenerator referringCodeGenerator) {
        return String.format(referringCodeGenerator.generateReferringBody(referMetadata.get(currentElement)),
                createChainingStatements(referMetadata, currentElement + 1, referringCodeGenerator));
    }

    private static boolean isAnyReferTypeCollection(List<ReferMetadata> referMetadata) {
        for (ReferMetadata metadata : referMetadata) {
            if (isCollection(metadata.getRefType())) {
                return true;
            }
        }
        return false;
    }

    private static Class<?> getFieldReferringClass(CtClass ctClass) {
        Class<?> fieldReferringClass;
        try {
            fieldReferringClass = ctClass.toClass();
        } catch (CannotCompileException e) {
            throw new RuntimeException(e);
        }
        ctClass.detach();
        return fieldReferringClass;
    }

    private static ValueProvider createInstance(Class<?> fieldReferringClass, String identifier) {
        try {
            return (ValueProvider) fieldReferringClass.getConstructor(String.class).newInstance(identifier);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static ClassPool createClassPool() {
        ClassPool classPool = new ClassPool(ClassPool.getDefault());
        classPool.appendClassPath(new LoaderClassPath(FieldReferringClassFactory.class.getClassLoader()));
        return classPool;
    }

    private static boolean isLastElement(Collection<?> list, int index) {
        return index == list.size() - 1;
    }

    public abstract static class AbstractFieldReferringValueProvider extends ValueProvider {

        protected final String identifier;

        public AbstractFieldReferringValueProvider(String identifier) {
            this.identifier = identifier;
        }
    }
}
