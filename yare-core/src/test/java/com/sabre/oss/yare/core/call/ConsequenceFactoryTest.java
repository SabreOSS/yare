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

package com.sabre.oss.yare.core.call;

import com.sabre.oss.yare.core.invocation.Invocation;
import com.sabre.oss.yare.core.model.Attribute;
import com.sabre.oss.yare.core.model.Expression;
import com.sabre.oss.yare.core.model.ExpressionFactory;
import com.sabre.oss.yare.core.model.Rule;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class ConsequenceFactoryTest {
    private ConsequenceFactory consequenceFactory;

    @Test
    void shouldProperlyConvertActionParametersAndCallActionInvocations() {
        // given
        Map<String, Argument.Invocation> invocations = new LinkedHashMap<>();
        Map<String, Argument.Invocation> proceedInvocations = new LinkedHashMap<>();
        consequenceFactory = new ConsequenceFactory((invocation) -> {
            invocations.put(invocation.getCall(), invocation);
            return context -> {
                proceedInvocations.put(invocation.getCall(), invocation);
                return null;
            };
        });
        Map<String, Object> bindings = new HashMap<>();
        bindings.put(ProcessingContext.CONTEXT, new Object());
        bindings.put(ProcessingContext.CURRENT_RULE_NAME, "ruleOne");

        // when
        Invocation<ProcessingContext, Void> consequence = consequenceFactory.createConsequence(ruleWithName("ruleOne"), getActions());
        consequence.proceed(new StaticProcessingContext(bindings, new Object[0]));

        // then
        assertThat(consequence).isNotNull();
        assertThat(invocations.size()).isEqualTo(2);

        assertThat(invocations).containsKey("actionOne");
        Argument.Invocation actionInvocation = invocations.get("actionOne");
        assertThat(actionInvocation.getName()).isEqualTo("First Action");
        assertThat(actionInvocation.getCall()).isEqualTo("actionOne");
        List<Argument> arguments = actionInvocation.getArguments();
        assertThat(arguments).containsExactly(
                Argument.valueOf("arg1", Boolean.FALSE),
                Argument.valueOf("arg2", Boolean.TRUE),
                Argument.valueOf("arg3", 1234L),
                Argument.valueOf("arg4", BigDecimal.valueOf(-123, 456)),
                Argument.valueOf("arg5", "Just a string"),
                Argument.valueOf("arg6", new InternalParameterizedType(null, List.class, Long.class), asList(1L, 2L)),
                Argument.valueOf("arg7", new InternalParameterizedType(null, List.class, String.class), asList("one", "two")));
        assertThat(invocations).containsKey("actionTwo");
        actionInvocation = invocations.get("actionTwo");
        assertThat(actionInvocation.getName()).isEqualTo("Second action");
        assertThat(actionInvocation.getCall()).isEqualTo("actionTwo");
        arguments = actionInvocation.getArguments();
        assertThat(arguments.size()).isEqualTo(0);

        assertThat(proceedInvocations).containsKeys("actionOne", "actionTwo");
    }

    private Rule ruleWithName(String ruleName) {
        return new Rule(
                Collections.singleton(new Attribute("ruleName", String.class, ruleName)),
                Collections.emptyList(),
                null,
                Collections.emptyList());
    }

    private List<Expression.Invocation> getActions() {
        return asList(
                ExpressionFactory.actionOf("First Action", "actionOne", asList(
                        ExpressionFactory.valueOf("arg1", false),
                        ExpressionFactory.valueOf("arg2", true),
                        ExpressionFactory.valueOf("arg3", 1234L),
                        ExpressionFactory.valueOf("arg4", BigDecimal.valueOf(-123, 456)),
                        ExpressionFactory.valueOf("arg5", "Just a string"),
                        ExpressionFactory.valueOf("arg6", asList(1L, 2L)),
                        ExpressionFactory.valueOf("arg7", asList("one", "two")))),

                ExpressionFactory.actionOf("Second action", "actionTwo", emptyList())
        );
    }
}
