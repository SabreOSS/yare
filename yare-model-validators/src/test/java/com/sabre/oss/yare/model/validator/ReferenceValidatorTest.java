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

package com.sabre.oss.yare.model.validator;

import com.sabre.oss.yare.core.model.Attribute;
import com.sabre.oss.yare.core.model.Expression;
import com.sabre.oss.yare.core.model.Fact;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.core.reference.ChainedTypeExtractor;
import com.sabre.oss.yare.core.reference.PlaceholderExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import static com.sabre.oss.yare.core.model.ExpressionFactory.*;
import static org.assertj.core.api.Assertions.assertThat;

class ReferenceValidatorTest {
    private ReferenceValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ReferenceValidator(false, new ChainedTypeExtractor(), new PlaceholderExtractor());
    }

    @Test
    void shouldFailOnMissingPredicate() {
        // given
        Rule rule = ruleWithPredicate(null);

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.predicate.not-defined", "Predicate Error: predicate was not specified")
        );
    }

    @Test
    void shouldPassOnSimplePredicate() {
        // given
        Rule rule = ruleWithPredicate(
                valueOf(null, Boolean.class, true)
        );

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).isEmpty();
    }

    @Test
    void shouldFailOnEmptyReference() {
        // given
        Rule rule = ruleWithPredicate(
                operatorOf(null, Boolean.class, "equal",
                        valueOf(null, String.class, "${}"),
                        valueOf(null, String.class, "")
                )
        );

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.ref.empty-reference", "Reference Error: empty reference used")
        );
    }

    @Test
    void shouldFailOnUnknownReference() {
        // given
        Rule rule = ruleWithPredicate(
                operatorOf(null, Boolean.class, "equal",
                        valueOf(null, String.class, "${unknown}"),
                        valueOf(null, String.class, "")
                )
        );

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.ref.unknown-reference", "Reference Error: unknown reference used -> unknown")
        );
    }

    @Test
    void shouldPassOnPredefinedContextReference() {
        // given
        Rule rule = ruleWithPredicate(
                operatorOf(null, Boolean.class, "equal",
                        valueOf(null, String.class, "${ctx}"),
                        valueOf(null, String.class, "")
                )
        );

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).isEmpty();
    }

    @Test
    void shouldPassOnExistingAttribute() {
        // given
        Rule rule = new Rule(
                new LinkedHashSet<>(Collections.singletonList(
                        new Attribute("existing", String.class, "")
                )),
                Collections.emptyList(),
                operatorOf(null, Boolean.class, "equal",
                        valueOf(null, String.class, "${existing}"),
                        valueOf(null, String.class, "")
                ),
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).isEmpty();
    }

    @Test
    void shouldPassOnExistingFact() {
        // given
        Rule rule = new Rule(
                Collections.emptySet(),
                Collections.singletonList(
                        new Fact("fact", InnerFact.class)
                ),
                operatorOf(null, Boolean.class, "equal",
                        valueOf(null, String.class, "${fact}"),
                        valueOf(null, String.class, "")
                ),
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).isEmpty();
    }

    @Test
    void shouldFailOnNonExistingFact() {
        // given
        Rule rule = ruleWithPredicate(
                operatorOf(null, Boolean.class, "equal",
                        valueOf(null, String.class, "${fact}"),
                        valueOf(null, String.class, "")
                )
        );

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.ref.unknown-reference", "Reference Error: unknown reference used -> fact")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "${fact.}",
            "${fact..}",
            "${fact..empty}",
            "${fact.empty..inside}",
            "${fact.ending..}"
    })
    void shouldFailOnEmptyPathSegment(String path) {
        // given
        Rule rule = new Rule(
                Collections.emptySet(),
                Collections.singletonList(
                        new Fact("fact", InnerFact.class)
                ),
                operatorOf(null, Boolean.class, "equal",
                        valueOf(null, String.class, path),
                        valueOf(null, String.class, "")
                ),
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.ref.empty-field", "Reference Error: field cannot have empty segments")
        );
    }

    @Test
    void shouldFailOnNonExistingField() {
        // given
        Rule rule = new Rule(
                Collections.emptySet(),
                Collections.singletonList(
                        new Fact("fact", InnerFact.class)
                ),
                operatorOf(null, Boolean.class, "equal",
                        valueOf(null, String.class, "${fact.missing}"),
                        valueOf(null, String.class, "")
                ),
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.ref.unknown-field", "Reference Error: unknown field used -> fact.missing")
        );
    }

    @Test
    void shouldFailOnPrivateField() {
        // given
        Rule rule = new Rule(
                Collections.emptySet(),
                Collections.singletonList(
                        new Fact("fact", InnerFact.class)
                ),
                operatorOf(null, Boolean.class, "equal",
                        valueOf(null, String.class, "${fact.privateField}"),
                        valueOf(null, String.class, "")
                ),
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.ref.unknown-field", "Reference Error: unknown field used -> fact.privateField")
        );
    }

    @Test
    void shouldPassOnPublicField() {
        // given
        Rule rule = new Rule(
                Collections.emptySet(),
                Collections.singletonList(
                        new Fact("fact", InnerFact.class)
                ),
                operatorOf(null, Boolean.class, "equal",
                        valueOf(null, String.class, "${fact.publicField}"),
                        valueOf(null, String.class, "")
                ),
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).isEmpty();
    }

    @Test
    void shouldPassOnGetterAccessibleField() {
        // given
        Rule rule = new Rule(
                Collections.emptySet(),
                Collections.singletonList(
                        new Fact("fact", InnerFact.class)
                ),
                operatorOf(null, Boolean.class, "equal",
                        valueOf(null, String.class, "${fact.getterField}"),
                        valueOf(null, String.class, "")
                ),
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).isEmpty();
    }

    @Test
    void shouldPassOnBooleanGetterAccessibleField() {
        // given
        Rule rule = new Rule(
                Collections.emptySet(),
                Collections.singletonList(
                        new Fact("fact", InnerFact.class)
                ),
                operatorOf(null, Boolean.class, "equal",
                        valueOf(null, String.class, "${fact.accessible}"),
                        valueOf(null, Boolean.class, true)
                ),
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).isEmpty();
    }

    @Test
    void shouldPassOnMultiSegmentPath() {
        // given
        Rule rule = new Rule(
                Collections.emptySet(),
                Collections.singletonList(
                        new Fact("fact", InnerFact.class)
                ),
                operatorOf(null, Boolean.class, "equal",
                        valueOf(null, String.class, "${fact.nested.nested.publicField}"),
                        valueOf(null, String.class, "")
                ),
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).isEmpty();
    }

    @Test
    void shouldFailOnNonCollectionWithMarker() {
        // given
        Rule rule = new Rule(
                Collections.emptySet(),
                Collections.singletonList(
                        new Fact("fact", InnerFact.class)
                ),
                operatorOf(null, Boolean.class, "equal",
                        valueOf(null, String.class, "${fact.getterField[*]}"),
                        valueOf(null, Collection.class, Collections.emptyList())
                ),
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.ref.non-collection-field", "Reference Error: field is not collection type -> fact.getterField[*]")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "${fact.collection[*]}",
            "${fact.[*]collection}",
            "${fact.collection[*].collection[*]}",
            "${fact.collection.collection[*]}",
            "${fact.[*]collection.collection[*]}",
            "${fact.[*]collection.[*]collection}"
    })
    void shouldPassOnCollectionWithMarker(String path) {
        // given
        Rule rule = new Rule(
                Collections.emptySet(),
                Collections.singletonList(
                        new Fact("fact", InnerFact.class)
                ),
                operatorOf(null, Boolean.class, "equal",
                        valueOf(null, String.class, path),
                        valueOf(null, Collection.class, Collections.emptyList())
                ),
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "fact.collection[*][*]",
            "fact.[*]collection[*]",
            "fact.[*]collection[*][*]",
            "fact.collection.collection[*][*]",
            "fact.collection.[*]collection[*]"
    })
    void shouldWarnAboutMultipleCollectionMarkers(String path) {
        // given
        Rule rule = new Rule(
                Collections.emptySet(),
                Collections.singletonList(
                        new Fact("fact", InnerFact.class)
                ),
                operatorOf(null, Boolean.class, "equal",
                        valueOf(null, String.class, "${" + path + "}"),
                        valueOf(null, Collection.class, Collections.emptyList())
                ),
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.warning("rule.ref.multiple-collection-markers", "Reference Error: field has more than one collection marker -> " + path)
        );
    }

    @Test
    void shouldPassOnMapFact() {
        // given
        Rule rule = new Rule(
                Collections.emptySet(),
                Collections.singletonList(
                        new Fact("fact", MapFact.class)
                ),
                operatorOf(null, Boolean.class, "equal",
                        valueOf(null, String.class, "${fact.dynamicValue}"),
                        valueOf(null, String.class, "")
                ),
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).isEmpty();
    }

    @Test
    void shouldPassAllOperatorsInPredicate() {
        // given
        Rule rule = new Rule(
                Collections.emptySet(),
                Arrays.asList(
                        new Fact("mapFact", MapFact.class),
                        new Fact("staticFact", InnerFact.class)
                ),
                operatorOf(null, Boolean.class, "or",
                        operatorOf(null, Boolean.class, "equal",
                                valueOf(null, String.class, "${staticFact.publicField}"),
                                valueOf(null, String.class, "")
                        ),
                        operatorOf(null, Boolean.class, "and",
                                functionOf(null, Boolean.class, "function",
                                        valueOf("param1", String.class, "${staticFact}"),
                                        valueOf("param2", String.class, "${mapFact.dynamic}"),
                                        functionOf("param3", Boolean.class, "function",
                                                valueOf("param1", String.class, "${staticFact}"),
                                                valueOf("param2", String.class, "${staticFact.nested.collection[*].getterField}")
                                        )
                                ),
                                operatorOf(null, Boolean.class, "not",
                                        operatorOf(null, Boolean.class, "equal",
                                                valueOf(null, String.class, "${mapFact.dynamicValue}"),
                                                valueOf(null, String.class, "")
                                        )
                                )
                        )
                ),
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).isEmpty();
    }

    @Test
    void shouldFailAllOperatorsInPredicate() {
        // given
        Rule rule = new Rule(
                Collections.emptySet(),
                Collections.singletonList(
                        new Fact("staticFact", InnerFact.class)
                ),
                operatorOf(null, Boolean.class, "or",
                        operatorOf(null, Boolean.class, "equal",
                                valueOf(null, String.class, "${staticFact.privateField}"),
                                valueOf(null, String.class, "")
                        ),
                        operatorOf(null, Boolean.class, "and",
                                functionOf(null, Boolean.class, "function",
                                        valueOf("param1", String.class, "${missingFact}"),
                                        valueOf("param2", String.class, "${missingFact.dynamic}"),
                                        functionOf("param3", Boolean.class, "function",
                                                valueOf("param", String.class, "${staticFact.nested.collection.getterField[*]}")
                                        )
                                ),
                                operatorOf(null, Boolean.class, "not",
                                        operatorOf(null, Boolean.class, "equal",
                                                valueOf(null, String.class, "${staticFact.missingValue}"),
                                                valueOf(null, String.class, "")
                                        )
                                )
                        )
                ),
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.ref.unknown-field", "Reference Error: unknown field used -> staticFact.privateField"),
                ValidationResult.error("rule.ref.unknown-reference", "Reference Error: unknown reference used -> missingFact"),
                ValidationResult.error("rule.ref.unknown-reference", "Reference Error: unknown reference used -> missingFact"),
                ValidationResult.error("rule.ref.non-collection-field", "Reference Error: field is not collection type -> staticFact.nested.collection.getterField[*]"),
                ValidationResult.error("rule.ref.unknown-field", "Reference Error: unknown field used -> staticFact.missingValue")
        );
    }

    @Test
    void shouldPassAllOperatorsInAction() {
        // given
        Rule rule = new Rule(
                Collections.emptySet(),
                Arrays.asList(
                        new Fact("mapFact", MapFact.class),
                        new Fact("staticFact", InnerFact.class)
                ),
                valueOf(null, Boolean.class, true),
                Arrays.asList(
                        actionOf("first", "first",
                                valueOf("param1", String.class, "${mapFact}"),
                                valueOf("param2", String.class, "${mapFact.dynamic}")
                        ),
                        actionOf("second", "second",
                                functionOf("function", Boolean.class, "function",
                                        valueOf("param1", String.class, "${staticFact}"),
                                        valueOf("param2", String.class, "${staticFact.nested.collection[*].getterField}")
                                )
                        )
                ));

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).isEmpty();
    }

    @Test
    void shouldFailAllOperatorsInAction() {
        // given
        Rule rule = new Rule(
                Collections.emptySet(),
                Collections.singletonList(
                        new Fact("staticFact", InnerFact.class)
                ),
                valueOf(null, Boolean.class, true),
                Arrays.asList(
                        actionOf("first", "first",
                                valueOf("param1", String.class, "${missingFact}"),
                                valueOf("param2", String.class, "${staticFact.privateField}")
                        ),
                        actionOf("second", "second",
                                functionOf("function", Boolean.class, "function",
                                        valueOf("param1", String.class, "${staticFact.missing}"),
                                        valueOf("param2", String.class, "${staticFact.nested.collection.getterField[*]}")
                                )
                        )
                ));

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.ref.unknown-reference", "Reference Error: unknown reference used -> missingFact"),
                ValidationResult.error("rule.ref.unknown-field", "Reference Error: unknown field used -> staticFact.privateField"),
                ValidationResult.error("rule.ref.unknown-field", "Reference Error: unknown field used -> staticFact.missing"),
                ValidationResult.error("rule.ref.non-collection-field", "Reference Error: field is not collection type -> staticFact.nested.collection.getterField[*]")
        );
    }

    @Test
    void shouldFailOnDuplicatedNames() {
        // given
        Rule rule = new Rule(new LinkedHashSet<>(
                        Arrays.asList(
                                new Attribute("duplicatedName1", null, null),
                                new Attribute("duplicatedName2", null, null))),
                        Arrays.asList(
                                new Fact("duplicatedName1", null),
                                new Fact("duplicatedName2", null)
                        ),
                        valueOf(null, Boolean.class, true),
                        Collections.emptyList()
                );

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.ref.duplicated-names", "Naming Error: There are duplicated names -> [duplicatedName1, duplicatedName2]")
        );
    }

    @Test
    void shouldFailOnDuplicatedFactNames() {
        // given
        Rule rule = new Rule(Collections.emptySet(),
                Arrays.asList(
                        new Fact("duplicatedFactName", String.class),
                        new Fact("duplicatedFactName", Integer.class)
                ),
                valueOf(null, Boolean.class, true),
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.ref.duplicated-names", "Naming Error: There are duplicated names -> [duplicatedFactName]")
        );
    }

    @Test
    void shouldFailOnDuplicatedAttributeNames() {
        // given
        Rule rule = new Rule(
                new LinkedHashSet<>(
                        Arrays.asList(
                                new Attribute("duplicatedAttributeName", String.class, null),
                                new Attribute("duplicatedAttributeName", Integer.class, null)
                        )
                ),
                Collections.emptyList(),
                valueOf(null, Boolean.class, true),
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.ref.duplicated-names", "Naming Error: There are duplicated names -> [duplicatedAttributeName]")
        );
    }

    @Test
    void shouldFailOnCtxName() {
        // given
        Rule rule = new Rule(Collections.emptySet(),
                Collections.singletonList(new Fact("ctx", null)),
                valueOf(null, Boolean.class, true),
                Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.ref.reserved-names", "Naming Error: Reserved names are used -> [ctx]")
        );
    }

    private Rule ruleWithPredicate(Expression expression) {
        return new Rule(Collections.emptySet(), Collections.emptyList(),
                expression,
                Collections.emptyList());
    }

    public static class InnerFact {
        public String publicField = "";
        private String getterField;
        private boolean accessible;
        private String privateField;
        private InnerFact nested;
        public Collection<InnerFact> collection;

        public String getGetterField() {
            return getterField;
        }

        public boolean isAccessible() {
            return accessible;
        }

        public InnerFact getNested() {
            return nested;
        }
    }
}

