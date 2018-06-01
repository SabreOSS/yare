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

import com.sabre.oss.yare.core.model.Attribute;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.dsl.ExpressionOperand;
import com.sabre.oss.yare.dsl.Operand;

import java.beans.Introspector;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sabre.oss.yare.dsl.RuleDsl.value;
import static com.sabre.oss.yare.engine.integration.BaseRulesUtils.Expression.expr;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class BaseRulesUtils {

    public static final String SHOULD_MATCH_ATTRIBUTE = "shouldMatch";

    public static final String RETURN_NULL = "returnNull";
    public static final String RETURN_ARGUMENT = "returnArgument";
    public static final String RETURN_PRIMITIVE_TRUE = "returnPrimitiveTrue";
    public static final String RETURN_PRIMITIVE_FALSE = "returnPrimitiveFalse";
    public static final String RETURN_WRAPPED_TRUE = "returnWrappedTrue";
    public static final String RETURN_WRAPPED_FALSE = "returnWrappedFalse";

    public static final String PRIMITIVE_TRUE_PROPERTY = "primitiveTrueProperty";
    public static final String PRIMITIVE_FALSE_PROPERTY = "primitiveFalseProperty";
    public static final String WRAPPED_TRUE_PROPERTY = "wrappedTrueProperty";
    public static final String WRAPPED_FALSE_PROPERTY = "wrappedFalseProperty";
    public static final String CURRENT_PROPERTY = "currentProperty";
    public static final String NULL_PROPERTY = "nullProperty";

    public static final String FACT_ONE = Introspector.decapitalize(FactOne.class.getSimpleName());
    public static final String FACT_TWO = Introspector.decapitalize(FactTwo.class.getSimpleName());

    public static final Expression TRUE_VALUE = expr(value(true), "value(true)");
    public static final Expression FALSE_VALUE = expr(value(false), "value(false)");
    public static final Expression NULL_VALUE = expr(value((Boolean) null, Boolean.class), "value(null)");

    static RulesExecutionConfig createConfig(List<Rule> rules) {
        return RulesExecutionConfig.builder()
                .withSequenceMode(true)
                .withFactTypes(asList(FactOne.class, FactTwo.class))
                .withRules(rules)
                .build();
    }

    static void assertRulesMatching(Map<String, List<Object>> result, List<Rule> rules, List<Object> facts) {
        for (Rule rule : rules) {
            boolean shouldMatch = rule.getAttributes().stream()
                    .filter(attr -> SHOULD_MATCH_ATTRIBUTE.equals(attr.getName()))
                    .map(Attribute::getValue)
                    .filter(Boolean.class::isInstance)
                    .map(Boolean.class::cast)
                    .findFirst().orElse(Boolean.FALSE);

            String ruleName = rule.getAttributes().stream().filter(a -> a.getName().equals("ruleName")).findFirst().get().getValue().toString();
            if (result.keySet().contains(ruleName)) {
                if (!shouldMatch) {
                    fail(format("Rule '%s' not expected to be matched", ruleName));
                }
                assertThat(result.get(ruleName))
                        .withFailMessage(format("Action of rule '%s' expected to get all facts as arguments, but it didn't", ruleName))
                        .isEqualTo(facts);

            } else if (shouldMatch) {
                fail(format("Rule '%s' expected to be matched, but wasn't", ruleName));
            }
        }
    }

    /**
     * Wraps Expression&lt;Boolean> with description.
     */
    static final class Expression {
        private final String description;
        private final Object expression;

        private Expression(String description, Object expression) {
            this.description = description;
            this.expression = expression;
        }

        public static Expression expr(com.sabre.oss.yare.dsl.Expression<Boolean> expression, String description) {
            return new Expression(description, expression);
        }

        public static Expression expr(ExpressionOperand<Boolean> expression, String description) {
            return new Expression(description, expression);
        }

        public static Expression expr(Operand<Boolean> operand, String description) {
            return new Expression(description, operand);
        }

        private static String getTypeDesc(Class<?> clazz) {
            return clazz.getCanonicalName() + " implementing " + Arrays.stream(clazz.getInterfaces()).map(Class::getSimpleName).collect(Collectors.joining(", ", "[", "]"));
        }

        @SuppressWarnings("unchecked")
        public com.sabre.oss.yare.dsl.Expression<Boolean> expression() {
            if (expression instanceof com.sabre.oss.yare.dsl.Expression) {
                return (com.sabre.oss.yare.dsl.Expression<Boolean>) expression;
            }
            throw new IllegalStateException(String.format("Requested Expression<Boolean> but found %s", getTypeDesc(expression.getClass())));
        }

        @SuppressWarnings("unchecked")
        public ExpressionOperand<Boolean> expressionOperand() {
            if (expression instanceof ExpressionOperand) {
                return (ExpressionOperand<Boolean>) expression;
            }
            throw new IllegalStateException(String.format("Requested ExpressionOperand<Boolean> but found %s", getTypeDesc(expression.getClass())));
        }

        @SuppressWarnings("unchecked")
        public Operand<Boolean> operand() {
            if (expression instanceof Operand) {
                return (Operand<Boolean>) expression;
            }
            throw new IllegalStateException(String.format("Requested Operand<Boolean> but found %s", getTypeDesc(expression.getClass())));
        }

        @Override
        public String toString() {
            return description;
        }
    }

    static class FactOne {
        private final Object current;

        FactOne() {
            this(null);
        }

        FactOne(Object value) {
            this.current = value;
        }

        public Object getCurrentProperty() {
            return current;
        }

        public boolean getPrimitiveTrueProperty() {
            return true;
        }

        public boolean getPrimitiveFalseProperty() {
            return false;
        }

        public Boolean getWrappedTrueProperty() {
            return Boolean.TRUE;
        }

        public Boolean getWrappedFalseProperty() {
            return Boolean.FALSE;
        }

        public Boolean getNullProperty() {
            return null;
        }
    }

    static class FactTwo {
        private final Object current;

        FactTwo() {
            this(null);
        }

        FactTwo(Object value) {
            this.current = value;
        }

        public Object getCurrentProperty() {
            return current;
        }

        public boolean getPrimitiveTrueProperty() {
            return true;
        }

        public boolean getPrimitiveFalseProperty() {
            return false;
        }

        public Boolean getWrappedTrueProperty() {
            return Boolean.TRUE;
        }

        public Boolean getWrappedFalseProperty() {
            return Boolean.FALSE;
        }

        public Boolean getNullProperty() {
            return null;
        }
    }

    public static class Functions {
        public <T> T returnArgument(T object) {
            return object;
        }

        public Boolean returnNull() {
            return null;
        }

        public boolean returnPrimitiveTrue() {
            return true;
        }

        public boolean returnPrimitiveFalse() {
            return false;
        }

        public Boolean returnWrappedTrue() {
            return Boolean.TRUE;
        }

        public Boolean returnWrappedFalse() {
            return Boolean.FALSE;
        }

    }

    public static class Actions {
        public void collect(Map<Object, Object> ctx, String ruleName, Object factOne, Object factTwo) {
            ctx.put(ruleName, asList(factOne, factTwo));
        }

        public void collect(Map<Object, Object> ctx, String ruleName, Object factOne, Object factTwo, Object object) {
            ctx.put(ruleName, asList(factOne, factTwo, object));
        }
    }
}
