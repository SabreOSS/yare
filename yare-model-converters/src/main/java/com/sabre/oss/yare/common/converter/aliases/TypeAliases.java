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

package com.sabre.oss.yare.common.converter.aliases;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

final class TypeAliases {
    private static final String ALIAS_CONFIGURATION_RESOURCE_FILE =
            "com/sabre/oss/yare/common/converter/aliases/typeAliases.properties";

    private final Map<String, TypeAlias> nameToAliasMap;
    private final Map<Type, TypeAlias> typeToAliasMap;

    TypeAliases() {
        Properties properties = loadAliasProperties();
        nameToAliasMap = Collections.unmodifiableMap(mapAliasesByName(properties));
        typeToAliasMap = Collections.unmodifiableMap(mapAliasesByType(properties));
    }

    private Properties loadAliasProperties() {
        InputStream stream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(ALIAS_CONFIGURATION_RESOURCE_FILE);
        try {
            Properties result = new Properties();
            result.load(stream);
            return result;
        } catch (Exception e) {
            throw new IllegalStateException(
                    String.format("Could not initialize type aliases form property file: %s",
                            ALIAS_CONFIGURATION_RESOURCE_FILE));
        }
    }

    private Map<String, TypeAlias> mapAliasesByName(Properties properties) {
        return properties.entrySet()
                .stream()
                .map(this::mapToAlias)
                .collect(Collectors.toMap(TypeAlias::getAlias, a -> a));
    }

    private Map<Type, TypeAlias> mapAliasesByType(Properties properties) {
        return properties.entrySet()
                .stream()
                .map(this::mapToAlias)
                .collect(Collectors.toMap(TypeAlias::getType, a -> a));
    }

    private TypeAlias mapToAlias(Map.Entry<Object, Object> property) {
        return TypeAlias.of(
                property.getKey().toString(),
                ClassUtils.forName(property.getValue().toString()));
    }

    Map<String, TypeAlias> getAliasesMappedByName() {
        return nameToAliasMap;
    }

    Map<Type, TypeAlias> getAliasesMappedByType() {
        return typeToAliasMap;
    }
}
