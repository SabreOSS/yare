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

import com.sabre.oss.yare.core.RulesEngine;
import com.sabre.oss.yare.dsl.Expression;
import com.sabre.oss.yare.dsl.ExpressionOperand;
import com.sabre.oss.yare.dsl.Operand;
import com.sabre.oss.yare.dsl.RuleDsl;
import com.sabre.oss.yare.core.model.Rule;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Supplier;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.engine.integration.BaseRulesUtils.*;
import static java.lang.String.format;
import static java.util.Arrays.asList;

class YareRelationOperatorsTest extends AbstractBaseRulesTest {
    static final Map<Class, List<Operand>> typeOperands = new LinkedHashMap<>();
    static final Map<Class, Object> typeInstances = new HashMap<>();

    static <T> List<Rule> addRulesWithSupportedRelationOperatorConfigurations(List<Rule> rules, String operator, Class<T> type,
                                                                              Operand<T> current, Operand<T> lt, Operand<T> gt,
                                                                              boolean currentAgainstCurrent, boolean currentAgainstLess, boolean currentAgainstGreater) {
        String typeName = type.getSimpleName();
        ExpressionOperand<T> nullValue = value((T) null, type);

        addRule(rules, () -> rule(format("Rule - operator(%s, function(current), value(current); [%s]", operator, typeName),
                currentAgainstCurrent, operator(operator, function(RETURN_ARGUMENT, type, param("result", current)), current)), current);
        addRule(rules, () -> rule(format("Rule - operator(%s, function(current), value(lessThen) [%s]", operator, typeName),
                currentAgainstLess, operator(operator, function(RETURN_ARGUMENT, type, param("result", current)), lt)), current, lt);
        addRule(rules, () -> rule(format("Rule - operator(%s, function(current), value(greaterThen) [%s]", operator, typeName),
                currentAgainstGreater, operator(operator, function(RETURN_ARGUMENT, type, param("result", current)), gt)), current, gt);

        addRule(rules, () -> rule(format("Rule - operator(%s, field(current), value(current) [%s]", operator, typeName),
                currentAgainstCurrent, operator(operator, value(format("${%s.%s}", FACT_ONE, CURRENT_PROPERTY)), current)), current);
        addRule(rules, () -> rule(format("Rule - operator(%s, field(current), value(lessThen) [%s]", operator, typeName),
                currentAgainstLess, operator(operator, value(format("${%s.%s}", FACT_ONE, CURRENT_PROPERTY)), lt)), current, lt);
        addRule(rules, () -> rule(format("Rule - operator(%s, field(current), value(greaterThen) [%s]", operator, typeName),
                currentAgainstGreater, operator(operator, value(format("${%s.%s}", FACT_ONE, CURRENT_PROPERTY)), gt)), current, gt);

        rules.addAll(asList(
                rule(format("Rule - operator(%s, function(current), value(null) [%s]", operator, typeName),
                        false, operator(operator, function(RETURN_ARGUMENT, type, param("result", current)), nullValue)),
                rule(format("Rule - operator(%s, function(null), value(current) [%s]", operator, typeName),
                        false, operator(operator, function(RETURN_ARGUMENT, type, param("result", nullValue)), current)),
                rule(format("Rule - operator(%s, function(null), value(null) [%s]", operator, typeName),
                        false, operator(operator, function(RETURN_ARGUMENT, type, param("result", nullValue)), nullValue)),

                rule(format("Rule - operator(%s, field(current), value(null) [%s]", operator, typeName),
                        false, operator(operator, value(format("${%s.%s}", FACT_ONE, CURRENT_PROPERTY)), nullValue)),
                rule(format("Rule - operator(%s, field(null), value(current) [%s]", operator, typeName),
                        false, operator(operator, value(format("${%s.%s}", FACT_ONE, NULL_PROPERTY)), current)),
                rule(format("Rule - operator(%s, field(null), value(null) [%s]", operator, typeName),
                        false, operator(operator, value(format("${%s.%s}", FACT_ONE, NULL_PROPERTY)), nullValue))
        ));

        return rules;
    }

    private static void addRule(List<Rule> rules, Supplier<Rule> ruleProvider, Operand<?>... operands) {
        if (Arrays.stream(operands).allMatch(Objects::nonNull)) {
            rules.add(ruleProvider.get());
        }
    }

    private static Rule rule(String name, boolean match, Expression<Boolean> predicate) {
        return RuleDsl.ruleBuilder()
                .name(name)
                .fact("factOne", FactOne.class)
                .attribute(SHOULD_MATCH_ATTRIBUTE, match)
                .predicate(predicate)
                .action("collect",
                        param("context", value("${ctx}")),
                        param("ruleName", value("${ruleName}")),
                        param("factOne", value("${factOne}")),
                        param("factTwo", value("${factTwo}")))
                .build();
    }

    @Test
    void shouldSupportEqOperator() {
        executeSupportedConfigurations(RuleDsl.Operator.EQUAL, true, false, false);
    }

    @Test
    void shouldSupportLtOperator() {
        executeSupportedConfigurations(RuleDsl.Operator.LESS, false, false, true);
    }

    @Test
    void shouldSupportLeOperator() {
        executeSupportedConfigurations(RuleDsl.Operator.LESS_EQUAL, true, false, true);
    }

    @Test
    void shouldSupportGtOperator() {
        executeSupportedConfigurations(RuleDsl.Operator.GREATER, false, true, false);
    }

    @Test
    void shouldSupportGeOperator() {
        executeSupportedConfigurations(RuleDsl.Operator.GREATER_EQUAL, true, true, false);
    }

    private void executeSupportedConfigurations(String operator, boolean currentAgainstCurrent, boolean currentAgainstLess, boolean currentAgainstGreater) {
        for (Map.Entry<Class, List<Operand>> entry : typeOperands.entrySet()) {

            // given
            Class type = entry.getKey();
            List<Operand> operands = entry.getValue();
            Object instance = typeInstances.get(type);

            @SuppressWarnings("unchecked")
            List<Rule> rules = addRulesWithSupportedRelationOperatorConfigurations(new ArrayList<>(),
                    operator, type, operands.get(0), operands.get(1), operands.get(2), currentAgainstCurrent,
                    currentAgainstLess, currentAgainstGreater);
            RulesEngine rulesEngine = createRuleEngine(createConfig(rules));
            List<Object> facts = Arrays.asList(new BaseRulesUtils.FactOne(instance), new BaseRulesUtils.FactTwo());

            // when
            Map<String, List<Object>> result = rulesEngine.createSession("test").execute(new HashMap<>(), facts);

            // then
            assertRulesMatching(result, rules, facts);
        }
    }
}
