package com.sabre.oss.yare.common.converter.aliases;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class TypeAliases {
    private final static Map<String, TypeAlias> nameToAliasMap;
    private final static Map<Type, TypeAlias> typeToAliasMap;

    static {
        nameToAliasMap = new HashMap<>();
        typeToAliasMap = new HashMap<>();

        registerAlias("Object", Object.class);
        registerAlias("String", String.class);
        registerAlias("Integer", Integer.class);
        registerAlias("Long", Long.class);
        registerAlias("Double", Double.class);
        registerAlias("Boolean", Boolean.class);
        registerAlias("Byte", Byte.class);
        registerAlias("Short", Short.class);
        registerAlias("Character", Character.class);
        registerAlias("Float", Float.class);

        registerAlias("int", int.class);
        registerAlias("long", long.class);
        registerAlias("double", double.class);
        registerAlias("boolean", boolean.class);
        registerAlias("byte", byte.class);
        registerAlias("short", short.class);
        registerAlias("char", char.class);
        registerAlias("float", float.class);

        registerAlias("ZonedDateTime", ZonedDateTime.class);

        registerAlias("List", List.class);
        registerAlias("Map", Map.class);
        registerAlias("Set", Set.class);
    }

    private static void registerAlias(String name, Type type) {
        TypeAlias alias = TypeAlias.of(name, type);
        nameToAliasMap.put(name, alias);
        typeToAliasMap.put(type, alias);
    }

    static Map<String, TypeAlias> mapAliasesByName() {
        return nameToAliasMap;
    }

    static Map<Type, TypeAlias> mapAliasesByType() {
        return typeToAliasMap;
    }
}
