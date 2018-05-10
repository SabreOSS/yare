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

package com.sabre.oss.yare.engine.integration;

import com.sabre.oss.yare.core.RuleSession;
import com.sabre.oss.yare.core.RulesEngine;
import com.sabre.oss.yare.core.RulesEngineBuilder;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.dsl.Expression;
import com.sabre.oss.yare.dsl.RuleDsl;
import com.sabre.oss.yare.engine.executor.DefaultRulesExecutorBuilder;
import com.sabre.oss.yare.engine.integration.fact.InnerChainingFact;
import com.sabre.oss.yare.engine.integration.fact.MidChainingFact;
import com.sabre.oss.yare.engine.integration.fact.OuterChainingFact;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.engine.MethodCallMetadata.method;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

class ChainingOperatorTest {
    private static final String ACTION_NAME = "testAction";
    private static final String IS_NULL = "isNull";
    private static final String FLATTEN_AND_CONTAINS = "flattenAndContains";

    private RulesEngine createRuleEngine(RulesExecutionConfig config) {
        TestFunction testFunction = new TestFunction();
        return new RulesEngineBuilder()
                .withRulesRepository(i -> config.getRules())
                .withActionMapping(ACTION_NAME, method(new TestAction(), a -> a.execute(null, null)))
                .withFunctionMapping(IS_NULL, method(testFunction, f -> f.isNull(null)))
                .withFunctionMapping(FLATTEN_AND_CONTAINS, method(testFunction, f -> f.flattenAndContains(null, null)))
                .withRulesExecutorBuilder(new DefaultRulesExecutorBuilder()
                        .withSequentialMode(config.isSequenceMode()))
                .build();
    }

    @Test
    void shouldCollectWhenCollectionsInChainEndingWithInstance() {
        // given
        OuterChainingFact invalidFact = getInvalidFact();
        InnerChainingFact innerChainingFact = new InnerChainingFact("test");
        MidChainingFact midChainingFact = new MidChainingFact();
        midChainingFact.put("instance", innerChainingFact);
        OuterChainingFact validFact = new OuterChainingFact(Arrays.asList(midChainingFact, midChainingFact));
        List<Object> facts = Arrays.asList(invalidFact, validFact);

        List<Rule> rule = getRule(
                "Should match when all from outerChainingFact.collection.instance.string are test",
                contains(
                        castToCollection(field("outerChainingFact.collection.instance.string", List.class), String.class),
                        values(String.class, value("test"), value("test")))
        );

        RuleSession ruleSession = createRuleSession(rule);

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), facts);

        // then
        assertThat(matchingFacts).containsExactly(validFact);
    }

    @Test
    void shouldCollectWhenInstancesInChainEndingWithInstance() {
        // given
        OuterChainingFact invalidFact = getInvalidFact();
        InnerChainingFact innerChainingFact = new InnerChainingFact("test");
        MidChainingFact midChainingFact = new MidChainingFact();
        midChainingFact.put("instance", innerChainingFact);
        OuterChainingFact validFact = new OuterChainingFact(midChainingFact);
        List<Object> facts = Arrays.asList(invalidFact, validFact);

        List<Rule> rule = getRule(
                "Should match when outerChainingFact.instance.instance.string is test",
                equal(
                        value("${outerChainingFact.instance.instance.string}"),
                        value("test")
                )
        );

        RuleSession ruleSession = createRuleSession(rule);

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), facts);

        // then
        assertThat(matchingFacts).containsExactly(validFact);
    }

    @Test
    void shouldCollectWhenCollectionsInChainEndingWithCollectionAndGroupingOperator() {
        // given
        OuterChainingFact invalidFact = getInvalidFact();
        InnerChainingFact innerChainingFact = new InnerChainingFact(Arrays.asList("test", "test"));
        MidChainingFact midChainingFact = new MidChainingFact();
        midChainingFact.put("instance", innerChainingFact);
        OuterChainingFact validFact = new OuterChainingFact(Arrays.asList(midChainingFact, midChainingFact));
        List<Object> facts = Arrays.asList(invalidFact, validFact);

        List<Rule> rule = getRule(
                "Should match when all from outerChainingFact.collection.instance.collection are test",
                contains(
                        castToCollection(field("outerChainingFact.collection.instance.collection[*]", List.class), String.class),
                        values(String.class, value("test"), value("test"), value("test"), value("test"))
                )
        );

        RuleSession ruleSession = createRuleSession(rule);

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), facts);

        // then
        assertThat(matchingFacts).containsExactly(validFact);
    }

    @Test
    void shouldCollectWhenCollectionsInChainEndingWithCollectionAndNoGroupingOperator() {
        // given
        OuterChainingFact invalidFact = getInvalidFact();
        InnerChainingFact innerChainingFact = new InnerChainingFact(Arrays.asList("test", "test"));
        MidChainingFact midChainingFact = new MidChainingFact();
        midChainingFact.put("instance", innerChainingFact);
        OuterChainingFact validFact = new OuterChainingFact(Arrays.asList(midChainingFact, midChainingFact));
        List<Object> facts = Arrays.asList(invalidFact, validFact);

        List<Rule> rule = getRule(
                "Should match when all from outerChainingFact.collection.instance.collection are test",
                equal(
                        function(FLATTEN_AND_CONTAINS, boolean.class,
                                param("collection", field("outerChainingFact.collection.instance.collection", List.class)),
                                param("value", value("test"))),
                        value(true)
                )
        );

        RuleSession ruleSession = createRuleSession(rule);

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), facts);

        // then
        assertThat(matchingFacts).containsExactly(validFact);
    }

    @Test
    void shouldCollectWhenInstancesInChainEndingWithCollectionAndGroupingOperator() {
        // given
        OuterChainingFact invalidFact = getInvalidFact();
        InnerChainingFact innerChainingFact = new InnerChainingFact(Arrays.asList("test", "test"));
        MidChainingFact midChainingFact = new MidChainingFact();
        midChainingFact.put("instance", innerChainingFact);
        OuterChainingFact validFact = new OuterChainingFact(midChainingFact);
        List<Object> facts = Arrays.asList(invalidFact, validFact);

        List<Rule> rule = getRule(
                "Should match when all from outerChainingFact.instance.instance.collection are test",
                contains(
                        castToCollection(field("outerChainingFact.instance.instance.collection[*]", List.class), String.class),
                        values(String.class, value("test"), value("test"))
                )
        );

        RuleSession ruleSession = createRuleSession(rule);

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), facts);

        // then
        assertThat(matchingFacts).containsExactly(validFact);
    }

    @Test
    void shouldCollectWhenInstancesInChainEndingWithCollectionAndNoGroupingOperator() {
        // given
        OuterChainingFact invalidFact = getInvalidFact();
        InnerChainingFact innerChainingFact = new InnerChainingFact(Arrays.asList("test", "test"));
        MidChainingFact midChainingFact = new MidChainingFact();
        midChainingFact.put("instance", innerChainingFact);
        OuterChainingFact validFact = new OuterChainingFact(midChainingFact);
        List<Object> facts = Arrays.asList(invalidFact, validFact);

        List<Rule> rule = getRule(
                "Should match when all from outerChainingFact.collection.instance.collection are test",
                contains(
                        castToCollection(field("outerChainingFact.instance.instance.collection", List.class), String.class),
                        values(String.class, value("test"))
                )
        );

        RuleSession ruleSession = createRuleSession(rule);

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), facts);

        // then
        assertThat(matchingFacts).containsExactly(validFact);
    }

    @Test
    void shouldCollectWhenCollectionsInChainAndElementOfCollectionIsNull() {
        // given
        OuterChainingFact invalidFact = getInvalidFact();
        InnerChainingFact innerChainingFact = new InnerChainingFact((String) null);
        MidChainingFact midChainingFact = new MidChainingFact();
        midChainingFact.put("instance", innerChainingFact);
        OuterChainingFact validFact = new OuterChainingFact(Arrays.asList(midChainingFact, midChainingFact));
        List<Object> facts = Arrays.asList(invalidFact, validFact);

        List<Rule> rule = getRule(
                "Should match when all of outerChainingFact.collection.instance.string are null",
                contains(
                        castToCollection(field("outerChainingFact.collection.instance.string", List.class), String.class),
                        values(String.class, value(null, String.class), value(null, String.class)))
        );

        RuleSession ruleSession = createRuleSession(rule);

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), facts);

        // then
        assertThat(matchingFacts).containsExactly(validFact);
    }

    @Test
    void shouldCollectWhenInstancesInChainAndInstanceIsNull() {
        // given
        OuterChainingFact invalidFact = getInvalidFact();
        OuterChainingFact validFact = new OuterChainingFact((MidChainingFact) null);
        List<Object> facts = Arrays.asList(invalidFact, validFact);

        List<Rule> rule = getRule(
                "Should match when outerChainingFact.instance is null",
                equal(
                        function(IS_NULL, boolean.class,
                                param("object", field("outerChainingFact.instance.instance.string", String.class))),
                        value(true)
                )
        );

        RuleSession ruleSession = createRuleSession(rule);

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), facts);

        // then
        assertThat(matchingFacts).containsExactly(validFact);
    }

    @Test
    void shouldCollectNullsWithoutGroupingOperator() {
        // given
        OuterChainingFact invalidFact = getInvalidFact();
        InnerChainingFact innerChainingFact = new InnerChainingFact(Arrays.asList(null, null));
        MidChainingFact midChainingFact = new MidChainingFact();
        midChainingFact.put("instance", innerChainingFact);
        OuterChainingFact validFact = new OuterChainingFact(Arrays.asList(midChainingFact, midChainingFact));
        List<Object> facts = Arrays.asList(invalidFact, validFact);

        List<Rule> rule = getRule(
                "Should match when all from outerChainingFact.collection.instance.collection are null",
                equal(
                        function(FLATTEN_AND_CONTAINS, boolean.class,
                                param("collection", field("outerChainingFact.collection.instance.collection", String.class)),
                                param("value", value(null, String.class))),
                        value(true)
                )
        );

        RuleSession ruleSession = createRuleSession(rule);

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), facts);

        // then
        assertThat(matchingFacts).containsExactly(validFact);
    }

    @Test
    void shouldCollectNullsWithGroupingOperator() {
        // given
        OuterChainingFact invalidFact = getInvalidFact();
        InnerChainingFact innerChainingFact = new InnerChainingFact(Arrays.asList(null, null));
        MidChainingFact midChainingFact = new MidChainingFact();
        midChainingFact.put("instance", innerChainingFact);
        OuterChainingFact validFact = new OuterChainingFact(Arrays.asList(midChainingFact, midChainingFact));
        List<Object> facts = Arrays.asList(invalidFact, validFact);

        List<Rule> rule = getRule(
                "Should match when all from outerChainingFact.collection.instance.collection are null",
                contains(
                        castToCollection(field("outerChainingFact.collection.instance.collection[*]", List.class), String.class),
                        values(String.class, value(null, String.class), value(null, String.class))
                )
        );

        RuleSession ruleSession = createRuleSession(rule);

        // when
        List<Object> matchingFacts = ruleSession.execute(new ArrayList<>(), facts);

        // then
        assertThat(matchingFacts).containsExactly(validFact);
    }

    @Test
    void shouldThrowExceptionIfTypeInChainIsObject() {
        // given /when /then
        assertThatThrownBy(() -> getRule(
                "Should match when all from outerChainingFact.objectString.instance.string are nulls",
                equal(
                        field("outerChainingFact.objectField.instance.instance", String.class),
                        value(null, String.class)
                )
        )).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldThrowExceptionWhenGroupingOperatorOnNotCollection() {
        // given /when /then
        assertThatThrownBy(() -> getRule(
                "Should match when all from outerChainingFact.objectString.instance.string are nulls",
                equal(
                        field("outerChainingFact.objectField.instance.string[*]", String.class),
                        value(null, String.class)
                )
        )).isExactlyInstanceOf(IllegalStateException.class);
    }

    private OuterChainingFact getInvalidFact() {
        InnerChainingFact innerChainingFact = new InnerChainingFact("instance", Arrays.asList("collection", "collection"));
        MidChainingFact midChainingFact = new MidChainingFact();
        midChainingFact.put("instance", innerChainingFact);
        return new OuterChainingFact(
                Arrays.asList(midChainingFact, midChainingFact),
                midChainingFact
        );
    }

    private List<Rule> getRule(String name, Expression<Boolean> expression) {
        return Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name(name)
                        .fact("outerChainingFact", OuterChainingFact.class)
                        .predicate(expression)
                        .action(ACTION_NAME,
                                param("context", reference("ctx")),
                                param("fact", reference("outerChainingFact")))
                        .build()
        );
    }

    private RuleSession createRuleSession(List<Rule> rule) {
        RulesExecutionConfig config = createConfig(rule);
        RulesEngine rulesEngine = createRuleEngine(config);
        return rulesEngine.createSession("testSession");
    }

    private RulesExecutionConfig createConfig(List<Rule> rule) {
        return RulesExecutionConfig.builder()
                .withFactTypes(Collections.singletonList(OuterChainingFact.class))
                .withInputType(List.class)
                .withRules(rule)
                .build();
    }

    public static class TestAction {

        public void execute(List<Object> results, Object fact) {
            results.add(fact);
        }
    }

    public static class TestFunction {

        public boolean isNull(Object object) {
            return Objects.isNull(object);
        }

        public boolean flattenAndContains(Collection<Collection<?>> collection, String value) {
            if (value == null) {
                return collection.stream().flatMap(Collection::stream).allMatch(Objects::isNull);
            } else {
                return collection.stream().flatMap(Collection::stream).allMatch(s -> s.equals(value));
            }
        }
    }
}
