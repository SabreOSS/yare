package com.sabre.oss.yare.common.converter.aliases;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TypeAliasResolver {

    public boolean hasAliasFor(Type type) {
        return resolveAliasFor(type)
                .isPresent();
    }

    public boolean hasAliasFor(String name) {
        return resolveAliasFor(name)
                .isPresent();
    }

    public TypeAlias getAliasFor(Type type) {
        return resolveAliasFor(type)
                .orElseThrow(() -> {
                    String message = String.format("Could not find type alias for given type: %s", type);
                    return new IllegalArgumentException(message);
                });
    }

    public TypeAlias getAliasFor(String name) {
        return resolveAliasFor(name)
                .orElseThrow(() -> {
                    String message = String.format("Could not find type alias for given name: %s", name);
                    return new IllegalArgumentException(message);
                });
    }

    private Optional<TypeAlias> resolveAliasFor(Type type) {
        return resolveAliasThat(a -> a.getType().equals(type));
    }

    private Optional<TypeAlias> resolveAliasFor(String name) {
        return resolveAliasThat(a -> a.getName().equals(name));
    }

    private Optional<TypeAlias> resolveAliasThat(Predicate<TypeAlias> predicate) {
        return Stream.of(TypeAlias.values())
                .filter(predicate)
                .findAny();
    }

}
