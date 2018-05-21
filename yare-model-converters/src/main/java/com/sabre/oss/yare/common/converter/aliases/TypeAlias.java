package com.sabre.oss.yare.common.converter.aliases;

import java.lang.reflect.Type;

public enum TypeAlias {
    STRING("String", String.class),
    INTEGER("Integer", Integer.class),
    INT("int", int.class);
    //TODO!!!

    private final String name;

    private final Type type;

    TypeAlias(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

}
