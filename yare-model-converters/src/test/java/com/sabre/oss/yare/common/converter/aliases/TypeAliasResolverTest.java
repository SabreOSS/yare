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
    void shouldResolveTypeAliasExistenceForGivenName() {
        //given
        String name = "String";

        //when
        Boolean hasAlias = resolver.hasAliasFor(name);

        //then
        assertThat(hasAlias).isTrue();
    }

    @Test
    void shouldResolveTypeAliasForGivenType() {
        //given
        Class<?> type = String.class;

        //when
        TypeAlias alias = resolver.getAliasFor(type);

        //then
        assertThat(alias).isEqualTo(TypeAlias.STRING);
    }

    @Test
    void shouldResolveTypeAliasExistenceForGivenType() {
        //given
        Class<?> type = String.class;

        //when
        Boolean hasAlias = resolver.hasAliasFor(type);

        //then
        assertThat(hasAlias).isTrue();
    }


    @Test
    void shouldThrowExceptionForUnknownName() {
        //given
        String unknownName = "UNKNOWN_NAME";

        //when
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> resolver.getAliasFor(unknownName));


        //then
        String expectedMessage = String.format("Could not find type alias for given name: %s", unknownName);
        assertThat(e.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    void shouldThrowExceptionForUnknownType() {
        //given
        Class<?> unknownType = TypeAlias.class;

        //when
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> resolver.getAliasFor(unknownType));

        //then
        String expectedMessage = String.format("Could not find type alias for given type: %s", unknownType);
        assertThat(e.getMessage()).isEqualTo(expectedMessage);
    }

}