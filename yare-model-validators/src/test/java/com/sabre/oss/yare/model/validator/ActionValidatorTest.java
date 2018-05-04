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

import com.sabre.oss.yare.core.model.Expression;
import com.sabre.oss.yare.core.model.ExpressionFactory;
import com.sabre.oss.yare.core.model.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ActionValidatorTest {
    private ActionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ActionValidator(false);
    }

    @Test
    void shouldFailIfNoActionHasBeenSpecified() {
        // given
        Rule rule = new Rule(Collections.emptySet(), Collections.emptyList(), null, Collections.emptyList());

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.action.not-defined", "Action Error: no actions were specified")
        );
    }

    @Test
    void shouldPassIfActionHasBeenSpecified() {
        // given
        Rule rule = new Rule(Collections.emptySet(), Collections.emptyList(), null,
                Collections.singletonList(
                        ExpressionFactory.actionOf("actionName", "actionName"))
        );

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).isEmpty();
    }

    @Test
    void shouldFailWhenActionsAreNull() {
        // given
        Rule rule = new Rule(Collections.emptySet(), Collections.emptyList(), null, Arrays.asList(
                null,
                null
        ));

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.action.action-null", "Action Error: action cannot be null")
        );
    }

    @Test
    void shouldFailWhenActionsWithNullNames() {
        // given
        Rule rule = new Rule(Collections.emptySet(), Collections.emptyList(), null, Arrays.asList(
                ExpressionFactory.actionOf(null, "actionCall1", Collections.emptyList()),
                ExpressionFactory.actionOf(null, "actionCall2", Collections.emptyList())
        ));

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.action.action-name-null", "Action Error: name cannot be null")
        );
    }

    @Test
    void shouldFailWhenActionsWithNullCalls() {
        // given
        Rule rule = new Rule(Collections.emptySet(), Collections.emptyList(), null, Arrays.asList(
                ExpressionFactory.actionOf("actionName1", null, Collections.emptyList()),
                ExpressionFactory.actionOf("actionName2", null, Collections.emptyList())
        ));

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.action.action-call-null", "Action Error: call cannot be null")
        );
    }

    @Test
    void shouldFailWhenActionsWithNullArguments() {
        // given
        Rule rule = new Rule(Collections.emptySet(), Collections.emptyList(), null, Arrays.asList(
                ExpressionFactory.actionOf("actionName1", "actionCall1", (List<Expression>) null),
                ExpressionFactory.actionOf("actionName2", "actionCall2", (List<Expression>) null)
        ));

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.action.action-arguments-null", "Action Error: arguments cannot be null")
        );
    }

    @Test
    void shouldFailWhenActionsWithArgumentsContainNullElements() {
        // given
        Rule rule = new Rule(Collections.emptySet(), Collections.emptyList(), null, Arrays.asList(
                ExpressionFactory.actionOf("actionName1", "actionCall1", Collections.singletonList(null)),
                ExpressionFactory.actionOf("actionName2", "actionCall2", Arrays.asList(null, null))
        ));

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.action.action-arguments-contain-null", "Action Error: arguments cannot contain null elements")
        );
    }

    @Test
    void shouldFailWhenActionsWithNullNamesAndNullCallsAndNullArguments() {
        // given
        Rule rule = new Rule(Collections.emptySet(), Collections.emptyList(), null, Arrays.asList(
                ExpressionFactory.actionOf(null, null, (List<Expression>) null),
                ExpressionFactory.actionOf(null, null, (List<Expression>) null)
        ));

        // when
        ValidationResults results = validator.validate(rule);

        // then
        assertThat(results.getResults()).containsExactly(
                ValidationResult.error("rule.action.action-name-null", "Action Error: name cannot be null"),
                ValidationResult.error("rule.action.action-call-null", "Action Error: call cannot be null"),
                ValidationResult.error("rule.action.action-arguments-null", "Action Error: arguments cannot be null")
        );
    }
}
