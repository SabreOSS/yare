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
import com.sabre.oss.yare.dsl.Operand;
import com.sabre.oss.yare.dsl.RuleDsl;
import com.sabre.oss.yare.core.model.Rule;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.sabre.oss.yare.dsl.RuleDsl.Operator.*;
import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.engine.integration.BaseRulesUtils.*;
import static com.sabre.oss.yare.engine.integration.BaseRulesUtils.Expression.expr;
import static java.lang.String.format;
import static java.util.Arrays.asList;

class LogicalOperatorsTest extends AbstractBaseRulesTest {
    private static List<Rule> generateRulesCheckSet(List<Rule> rules, Expression trueExp, Expression falseExp, Expression nullExp) {
        simpleBooleanOperatorsCheckSet(rules, trueExp, falseExp, nullExp);
        nestedBooleanOperatorsCheckSet(rules, trueExp, falseExp);
        return rules;
    }

    private static void simpleBooleanOperatorsCheckSet(List<Rule> rules, Expression trueExpression, Expression falseExpression, Expression nullExpression) {
        com.sabre.oss.yare.dsl.Expression<Boolean> trueExp = trueExpression.expression();
        com.sabre.oss.yare.dsl.Expression<Boolean> falseExp = falseExpression.expression();
        com.sabre.oss.yare.dsl.Expression<Boolean> nullExp = nullExpression.expression();
        String desc = "true=" + trueExpression + "', false=" + falseExpression + ", null=" + nullExp;

        rules.addAll(asList(
                rule(format("Rule - true; [%s]", desc), true, trueExp),
                rule(format("Rule - false; [%s]", desc), false, falseExp),
                rule(format("Rule - null; [%s]", desc), false, nullExp),

                rule(format("Rule - Not(true); [%s]", desc), false, not(trueExp)),
                rule(format("Rule - Not(false); [%s]", desc), true, not(falseExp)),
                rule(format("Rule - Not(null); [%s]", desc), false, not(nullExp)),

                rule(format("Rule - Or(true); [%s]", desc), true, or(trueExp)),
                rule(format("Rule - Or(false); [%s]", desc), false, or(falseExp)),
                rule(format("Rule - Or(true, true); [%s]", desc), true, or(trueExp, trueExp)),
                rule(format("Rule - Or(true, false); [%s]", desc), true, or(trueExp, falseExp)),
                rule(format("Rule - Or(false, true); [%s]", desc), true, or(falseExp, trueExp)),
                rule(format("Rule - Or(false, false); [%s]", desc), false, or(falseExp, falseExp)),
                rule(format("Rule - Or(null); [%s]", desc), false, or(nullExp)),
                rule(format("Rule - Or(null, true); [%s]", desc), true, or(nullExp, trueExp)),
                rule(format("Rule - Or(null, false); [%s]", desc), false, or(nullExp, falseExp)),
                rule(format("Rule - Or(null, false, true); [%s]", desc), true, or(nullExp, falseExp, trueExp)),
                rule(format("Rule - Or(null, true, false); [%s]", desc), true, or(nullExp, trueExp, falseExp)),
                rule(format("Rule - Or(true, null); [%s]", desc), true, or(trueExp, nullExp)),
                rule(format("Rule - Or(false, null); [%s]", desc), false, or(falseExp, nullExp)),
                rule(format("Rule - Or(true, false, null); [%s]", desc), true, or(trueExp, falseExp, nullExp)),

                rule(format("Rule - And(true); [%s]", desc), true, and(trueExp)),
                rule(format("Rule - And(false); [%s]", desc), false, and(falseExp)),
                rule(format("Rule - And(true, true); [%s]", desc), true, and(trueExp, trueExp)),
                rule(format("Rule - And(true, false); [%s]", desc), false, and(trueExp, falseExp)),
                rule(format("Rule - And(false, true); [%s]", desc), false, and(falseExp, trueExp)),
                rule(format("Rule - And(false, false); [%s]", desc), false, and(falseExp, falseExp)),
                rule(format("Rule - And(null); [%s]", desc), false, and(nullExp)),
                rule(format("Rule - And(null, true); [%s]", desc), false, and(nullExp, trueExp)),
                rule(format("Rule - And(null, false); [%s]", desc), false, and(nullExp, falseExp)),
                rule(format("Rule - And(null, false, true); [%s]", desc), false, and(nullExp, falseExp, trueExp)),
                rule(format("Rule - And(null, true, false); [%s]", desc), false, and(nullExp, trueExp, falseExp)),
                rule(format("Rule - And(true, null); [%s]", desc), false, and(trueExp, nullExp)),
                rule(format("Rule - And(false, null); [%s]", desc), false, and(falseExp, nullExp)),
                rule(format("Rule - And(true, false, null); [%s]", desc), false, and(trueExp, falseExp, nullExp))
        ));
    }

    private static void nestedBooleanOperatorsCheckSet(List<Rule> rules, Expression trueExpression, Expression falseExpression) {
        com.sabre.oss.yare.dsl.Expression<Boolean> trueExp = trueExpression.expression();
        com.sabre.oss.yare.dsl.Expression<Boolean> falseExp = falseExpression.expression();
        String desc = "true=" + trueExpression + "', false=" + falseExpression;

        rules.addAll(asList(
                rule(format("Rule - Or(Or(true)); [%s]", desc), true, or(or(trueExp))),
                rule(format("Rule - Or(Or(false)); [%s]", desc), false, or(or(falseExp))),
                rule(format("Rule - Or(And(true)); [%s]", desc), true, or(and(trueExp))),
                rule(format("Rule - Or(And(false)); [%s]", desc), false, or(and(falseExp))),
                rule(format("Rule - Or(true, Or(true)); [%s]", desc), true, or(trueExp, or(trueExp))),
                rule(format("Rule - Or(true, Or(false)); [%s]", desc), true, or(trueExp, or(falseExp))),
                rule(format("Rule - Or(false, Or(true)); [%s]", desc), true, or(falseExp, or(trueExp))),
                rule(format("Rule - Or(false, Or(false)); [%s]", desc), false, or(falseExp, or(falseExp))),
                rule(format("Rule - Or(true, And(true)); [%s]", desc), true, or(trueExp, and(trueExp))),
                rule(format("Rule - Or(true, And(false)); [%s]", desc), true, or(trueExp, and(falseExp))),
                rule(format("Rule - Or(false, And(true)); [%s]", desc), true, or(falseExp, and(trueExp))),
                rule(format("Rule - Or(false, And(false)); [%s]", desc), false, or(falseExp, and(falseExp))),

                rule(format("Rule - And(And(true)); [%s]", desc), true, and(and(trueExp))),
                rule(format("Rule - And(And(false)); [%s]", desc), false, and(and(falseExp))),
                rule(format("Rule - And(Or(true)); [%s]", desc), true, and(or(trueExp))),
                rule(format("Rule - And(Or(false)); [%s]", desc), false, and(or(falseExp))),
                rule(format("Rule - And(true, And(true)); [%s]", desc), true, and(trueExp, and(trueExp))),
                rule(format("Rule - And(true, And(false)); [%s]", desc), false, and(trueExp, and(falseExp))),
                rule(format("Rule - And(false, And(true)); [%s]", desc), false, and(falseExp, and(trueExp))),
                rule(format("Rule - And(false, And(false)); [%s]", desc), false, and(falseExp, and(falseExp))),
                rule(format("Rule - And(true, Or(true)); [%s]", desc), true, and(trueExp, or(trueExp))),
                rule(format("Rule - And(true, Or(false)); [%s]", desc), false, and(trueExp, or(falseExp))),
                rule(format("Rule - And(false, Or(true)); [%s]", desc), false, and(falseExp, or(trueExp))),
                rule(format("Rule - And(false, Or(false)); [%s]", desc), false, and(falseExp, or(falseExp)))
        ));
    }

    private static void topLevelIsXXXOperatorsCheckSet(List<Rule> rules, Expression trueExpression, Expression falseExpression, Expression nullExpression) {
        Operand<Boolean> trueExp = trueExpression.operand();
        Operand<Boolean> falseExp = falseExpression.operand();
        Operand<Boolean> nullExp = nullExpression.operand();
        String desc = "true=" + trueExpression + "', false=" + falseExpression + ", null=" + nullExp;

        rules.addAll(asList(
                rule(format("Rule - IsTrue(true); [%s]", desc), true, operator(IS_TRUE, trueExp)),
                rule(format("Rule - IsTrue(false); [%s]", desc), false, operator(IS_TRUE, falseExp)),
                rule(format("Rule - IsTrue(null); [%s]", desc), false, operator(IS_TRUE, nullExp)),

                rule(format("Rule - IsFalse(true); [%s]", desc), false, operator(IS_FALSE, trueExp)),
                rule(format("Rule - IsFalse(false); [%s]", desc), true, operator(IS_FALSE, falseExp)),
                rule(format("Rule - IsFalse(null); [%s]", desc), false, operator(IS_FALSE, nullExp)),

                rule(format("Rule - IsNull(true); [%s]", desc), false, operator(IS_NULL, trueExp)),
                rule(format("Rule - IsNull(false); [%s]", desc), false, operator(IS_NULL, falseExp)),
                rule(format("Rule - IsNull(null); [%s]", desc), true, operator(IS_NULL, nullExp))
        ));
    }

    private static List<Expression> trueExpressionGenerator(List<Expression> trueExps, List<Expression> falseExps) {
        List<Expression> expressions = new ArrayList<>(trueExps);

        for (Expression falseExp : falseExps) {
            expressions.add(expr(not(falseExp.expression()), "Not(" + falseExp + ")"));
        }

        for (Expression trueExp : trueExps) {
            expressions.add(expr(or(trueExp.expression()), "Or(" + trueExp + ")"));
            expressions.add(expr(and(trueExp.expression()), "And(" + trueExp + ")"));
            for (Expression falseExp : falseExps) {
                expressions.add(expr(or(trueExp.expression(), falseExp.expression()), "Or(" + trueExp + ", " + falseExp + ")"));
                expressions.add(expr(or(falseExp.expression(), trueExp.expression()), "Or(" + falseExp + ", " + trueExp + ")"));
            }
        }
        return expressions;
    }

    private static List<Expression> falseExpressionGenerator(List<Expression> trueExps, List<Expression> falseExps) {
        List<Expression> expressions = new ArrayList<>(falseExps);

        for (Expression trueExp : trueExps) {
            expressions.add(expr(not(trueExp.expression()), "Not(" + trueExp + ")"));
        }

        for (Expression falseExp : falseExps) {
            expressions.add(expr(or(falseExp.expression()), "Or(" + falseExp + ")"));
            expressions.add(expr(and(falseExp.expression()), "And(" + falseExp + ")"));
            for (Expression trueExp : trueExps) {
                expressions.add(expr(and(trueExp.expression(), falseExp.expression()), "And(" + trueExp + ", " + falseExp + ")"));
                expressions.add(expr(and(falseExp.expression(), trueExp.expression()), "And(" + falseExp + ", " + trueExp + ")"));
            }
        }
        return expressions;
    }

    public static Rule rule(String name, boolean match, com.sabre.oss.yare.dsl.Expression<Boolean> predicate) {
        return RuleDsl.ruleBuilder()
                .name(name)
                .fact("factOne", FactOne.class)
                .fact("factTwo", FactTwo.class)
                .attribute(SHOULD_MATCH_ATTRIBUTE, match)
                .predicate(predicate)
                .action("collect",
                        param("context", reference("ctx")),
                        param("ruleName", reference("ruleName")),
                        param("factOne", reference(FACT_ONE)),
                        param("factTwo", reference(FACT_TWO)))
                .build();
    }

    @Test
    void shouldProperlyEvaluateTopLevelIsXXXOperatorsForFunction() {
        // given
        BaseRulesUtils.Expression trueExp = expr(function(RETURN_ARGUMENT, Boolean.class, param("param", value(true))), "trueFunction()");
        BaseRulesUtils.Expression falseExp = expr(function(RETURN_ARGUMENT, Boolean.class, param("param", value(false))), "falseFunction()");
        BaseRulesUtils.Expression nullExp = expr(function(RETURN_ARGUMENT, Boolean.class, param("param", value((Boolean) null, Boolean.class))), "nullFunction()");

        List<Rule> rules = new ArrayList<>();
        topLevelIsXXXOperatorsCheckSet(rules, trueExp, falseExp, nullExp);

        // when / then
        evaluateAndAssert(rules);
    }

    @Test
    void shouldProperlyEvaluateTopLevelIsXXXOperatorsForProperties() {
        // given
        BaseRulesUtils.Expression trueExp = expr(field(FACT_ONE, PRIMITIVE_TRUE_PROPERTY, Boolean.class), "trueProperty");
        BaseRulesUtils.Expression falseExp = expr(field(FACT_ONE, PRIMITIVE_FALSE_PROPERTY, Boolean.class), "falseProperty");
        Expression nullExp = expr(field(FACT_ONE, NULL_PROPERTY, Boolean.class), "nullProperty");

        List<Rule> rules = new ArrayList<>();
        topLevelIsXXXOperatorsCheckSet(rules, trueExp, falseExp, nullExp);

        // when / then
        evaluateAndAssert(rules);
    }

    @Test
    void shouldProperlyEvaluateTopLevelValuePredicate() {
        // given
        List<Rule> rules = Arrays.asList(
                rule("Rule - value(true)", true, value(true)),
                rule("Rule - value(false)", false, value(false)),
                rule("Rule - value(null)", false, value((Boolean) null, Boolean.class)));

        // when / then
        evaluateAndAssert(rules);
    }

    @Test
    void shouldProperlyEvaluateTopLevelFunctionPredicate() {
        // given
        List<Rule> rules = Arrays.asList(
                rule("Rule - functionReturningPrimitiveTrue()", true, function(RETURN_PRIMITIVE_TRUE, Boolean.class)),
                rule("Rule - functionReturningPrimitiveFalse()", false, function(RETURN_PRIMITIVE_FALSE, Boolean.class)),
                rule("Rule - functionReturningWrappedTrue()", true, function(RETURN_WRAPPED_TRUE, Boolean.class)),
                rule("Rule - functionReturningWrappedFalse()", false, function(RETURN_WRAPPED_FALSE, Boolean.class)),
                rule("Rule - functionReturningNull()", false, function(RETURN_WRAPPED_FALSE, Boolean.class)));

        // when / then
        evaluateAndAssert(rules);
    }

    @Test
    void shouldProperlyEvaluateOperatorsWithConstants() {
        // given
        List<Rule> rules = generateRulesCheckSet(new ArrayList<>(), TRUE_VALUE, FALSE_VALUE, NULL_VALUE);

        // when / then
        evaluateAndAssert(rules);
    }

    @Test
    void shouldProperlyEvaluateOperatorsWithConstntsAndFunctions() {
        // given
        List<Rule> rules = new ArrayList<>();
        generateRulesCheckSet(rules,
                expr(function(RETURN_WRAPPED_TRUE, Boolean.class), "function()"),
                FALSE_VALUE,
                NULL_VALUE);
        generateRulesCheckSet(rules,
                TRUE_VALUE,
                expr(function(RETURN_ARGUMENT, Boolean.class, param("result", value(false))), "function(constant)"),
                NULL_VALUE);
        generateRulesCheckSet(rules,
                expr(function(RETURN_ARGUMENT, Boolean.class, param("result", value(true))), "function(constant)"),
                expr(function(RETURN_ARGUMENT, Boolean.class, param("result", value(false))), "function(constant)"),
                NULL_VALUE);
        generateRulesCheckSet(rules,
                expr(function(RETURN_ARGUMENT, Boolean.class, param("trueFromFact", field("factOne", "primitiveTrueProperty"))), "function(factOne)"),
                expr(function(RETURN_ARGUMENT, Boolean.class, param("falseFromFact", field("factOne", "primitiveFalseProperty"))), "function(factOne)"),
                NULL_VALUE);

        // when / then
        evaluateAndAssert(rules);
    }

    @Test
    void shouldProperlyEvaluateMixedLogicalOperatorsWithSingleFactType() {
        // given
        List<Expression> trueExps = asList(
                TRUE_VALUE,
                expr(function(RETURN_WRAPPED_TRUE, Boolean.class), "trueFunction()"),
                expr(function(RETURN_ARGUMENT, Boolean.class, param("constant", value(true))), "function(true)"),
                expr(function(RETURN_ARGUMENT, Boolean.class, param("factOne", field("factOne", "primitiveTrueProperty"))), "function(factOne.true)"));

        List<Expression> falseExps = asList(
                FALSE_VALUE,
                expr(function(RETURN_WRAPPED_FALSE, Boolean.class), "falseFunction()"),
                expr(function(RETURN_ARGUMENT, Boolean.class, param("constant", value(false))), "function(false)"),
                expr(function(RETURN_ARGUMENT, Boolean.class, param("factOne", field("factOne", "primitiveFalseProperty"))), "function(factOne.false)"));

        for (int i = 0; i < 2; i++) {
            List<Expression> trueExpressions = trueExpressionGenerator(trueExps, falseExps);
            List<Expression> falseExpressions = falseExpressionGenerator(trueExps, falseExps);
            trueExps = trueExpressions;
            falseExps = falseExpressions;
        }

        List<Rule> rules = generateRules(trueExps, falseExps);

        // when / then
        evaluateAndAssert(rules);
    }

    private List<Rule> generateRules(List<Expression> trueExps, List<Expression> falseExps) {
        Map<String, Rule> rules = new LinkedHashMap<>(trueExps.size() + falseExps.size());
        for (Expression exp : trueExps) {
            String ruleName = format("Rule - %s", exp);
            rules.put(ruleName, rule(ruleName, true, exp.expression()));
        }
        for (Expression exp : falseExps) {
            String ruleName = format("Rule - %s", exp);
            rules.put(ruleName, rule(ruleName, false, exp.expression()));
        }
        return new ArrayList<>(rules.values());
    }

    private void evaluateAndAssert(List<Rule> rules) {
        List<Object> facts = asList(new FactOne(), new FactTwo());
        RulesEngine rulesEngine = createRuleEngine(createConfig(rules));

        // when
        Map<String, List<Object>> result = rulesEngine.createSession("test").execute(new HashMap<>(), facts);

        // then
        assertRulesMatching(result, rules, facts);
    }
}
