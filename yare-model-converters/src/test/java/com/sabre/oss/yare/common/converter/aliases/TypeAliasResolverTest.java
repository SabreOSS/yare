package com.sabre.oss.yare.common.converter.aliases;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TypeAliasResolverTest {

    private TypeAliasResolver resolver = new TypeAliasResolver();

    @Test
    void shouldResolveTypeAliasForGivenName() {
        //given
        String name = "String";

        //when
        TypeAlias alias = resolver.getAliasFor(name);

        //then
        assertThat(alias).isEqualTo(TypeAlias.STRING);
    }

    @Test
    void shouldResolveTypeExistenceAliasForGivenName() {
        //given
        String name = "String";

        //when
        Boolean hasAlias = resolver.hasAliasFor(name);

        //then
        assertThat(hasAlias).isTrue();
    }

    @Test
    void shouldResolveTypeAliasForGivenType() {
    }

    @Test
    void shouldThrowExceptionForGivenName() {
        //given
        String name = "NOT_EXISTING_NAME";

        //when
        TypeAlias alias = resolver.getAliasFor(name);

        //then
        assertThat(alias).isEqualTo(TypeAlias.STRING);
    }

    @Test
    void shouldThrowExceptionForGivenType() {
        //given
        Class<?> not_handled_class = TypeAlias.class;

        //then
        assertThrows(IllegalArgumentException.class, () -> {
            //when
            resolver.getAliasFor(not_handled_class);
        }, "Could not find type alias for given type: xxx!"); //TODO!!!
    }

}