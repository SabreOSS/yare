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

package com.sabre.oss.yare.dsl;

import com.sabre.oss.yare.common.converter.DefaultTypeConverters;
import com.sabre.oss.yare.common.converter.TypeConverter;
import com.sabre.oss.yare.core.model.Attribute;
import com.sabre.oss.yare.core.model.ExpressionFactory;
import com.sabre.oss.yare.core.model.Fact;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.core.reference.PlaceholderUtils;
import com.sabre.oss.yare.model.validator.DefaultRuleValidator;
import com.sabre.oss.yare.model.validator.ValidationResult;
import com.sabre.oss.yare.model.validator.ValidationResults;
import com.sabre.oss.yare.model.validator.Validator;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link RuleDsl} is a class providing simple DSL for {@link Rule}s creation.
 * <p>
 * Example:
 * <pre>
 * import com.sabre.oss.yare.dsl.RuleDsl.*;
 *
 * RuleDsl.ruleBuilder()
 *      .name("example.Rule001")
 *      .fact("fact1", FactOne.class)
 *      .fact("fact2", FactTwo.class)
 *      .attribute("my.custom.property", "my value")
 *      .predicate(
 *              or(
 *                      lessOrEqual(
 *                              function("math.add", Long.class,
 *                                      param("arg1", value("${fact1.counter}")),
 *                                      param("arg2", value("${fact2.counter}"))),
 *                              value(190L)),
 *                      match(
 *                              value("{fact1.mode}"),
 *                              value("my string value"))
 *                 ))
 *      .action("example.ActionOne",
 *              param("ctx", value("${ctx}")),
 *              param("ruleName", value("${ruleName}")),
 *              param("value", value("String value")))
 *      .action("example.ActionTwo")
 *      .build();
 * </pre>
 */
public final class RuleDsl {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RuleDsl.class);
    private static final Validator validator = DefaultRuleValidator.getRuleValidator();
    private static final TypeConverter converter = DefaultTypeConverters.getDefaultTypeConverter();

    private RuleDsl() {
    }

    /**
     * Creates new Rule's builder instance.
     *
     * @return Rule's builder instance
     */
    public static RuleBuilder ruleBuilder() {
        return new RuleBuilder();
    }

    /**
     * Creates reference to a field.
     *
     * @param ref  instance identifier
     * @param path path
     * @param type class describing the field type
     * @param <T>  type of the field
     * @return field operand
     * @deprecated Please create field reference directly by passing placeholder in {@link #value(String)} with format: "${ref.path}"
     */
    @Deprecated
    public static <T> Operand<T> field(String ref, String path, Class<T> type) {
        return value(String.format("${%s.%s}", ref, path));
    }

    /**
     * Casts operand to operand of collection.
     *
     * @param operand  operand
     * @param itemType class describing type of collection items
     * @param <T>      type of items in collection
     * @return collection operand
     */
    public static <T> CollectionOperand<T> castToCollection(Operand<?> operand, Class<T> itemType) {
        return operand::getExpression;
    }

    /**
     * Creates Collection&lt;T&gt; Class instance. It can be used for nested {@link #values(Class, Operand[])}.
     *
     * @param type class describing type of collection items
     * @param <T>  type of items in collection
     * @return collection class
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<Collection<T>> collectionTypeOf(Class<T> type) {
        Collection<T> collection = Collections.emptyList();
        return (Class<Collection<T>>) collection.getClass();
    }

    /**
     * Creates reference to a field.
     *
     * @param field combined (with dot) instance identifier and the path to a field
     * @param type  class describing the field type
     * @param <T>   type of the field
     * @return field operand
     * @deprecated Please create field reference directly by passing placeholder in {@link #value(String)} with format: "${field}"
     */
    @Deprecated
    public static <T> Operand<T> field(String field, Class<T> type) {
        return value(String.format("${%s}", field));
    }

    /**
     * Creates reference to a field.
     *
     * @param ref  instance identifier
     * @param path field (property) path
     * @return field operand
     * @deprecated Please create field reference directly by passing placeholder in {@link #value(String)} with format: "${ref.path}"
     */
    @Deprecated
    public static Operand<Object> field(String ref, String path) {
        return field(ref, path, Object.class);
    }

    /**
     * Creates reference to a field.
     *
     * @param field combined (with dot) instance identifier and the path of field
     * @return field operand
     * @deprecated Please create field reference directly by passing placeholder in {@link #value(String)} with format: "${field}"
     */
    @Deprecated
    public static Operand<Object> field(String field) {
        return field(field, Object.class);
    }

    /**
     * Creates reference to a bound instance (fact, attribute or predefined attribute).
     *
     * @param reference instance identifier
     * @param type      class describing referenced instance
     * @param <T>       type of the instance
     * @return reference operand
     * @deprecated Please create reference directly by passing placeholder in {@link #value(String)} with format: "${reference}"
     */
    @Deprecated
    public static <T> Operand<T> reference(String reference, Class<T> type) {
        return field(reference, type);
    }

    /**
     * Creates reference to a bound instance  (fact, attribute or predefined attribute).
     *
     * @param reference instance identifier
     * @return reference operand
     * @deprecated Please create reference directly by passing placeholder in {@link #value(String)} with format: "${reference}"
     */
    @Deprecated
    public static Operand<Object> reference(String reference) {
        return field(reference);
    }

    /**
     * Creates function expression / operand.
     *
     * @param name       name of the created function
     * @param returnType class describing the return type
     * @param args       function arguments
     * @param <T>        return type
     * @return function call expression
     */
    public static <T> ExpressionOperand<T> function(String name, Class<T> returnType, Parameter... args) {
        return (paramName, builder) -> {
            String actualName = paramName != null ? paramName : name;
            if (args.length != 0) {
                List<com.sabre.oss.yare.core.model.Expression> arguments = Arrays.stream(args)
                        .map(arg -> arg.getExpression(builder))
                        .collect(Collectors.toList());
                return ExpressionFactory.functionOf(actualName, returnType, name, arguments);
            }
            return ExpressionFactory.functionOf(actualName, returnType, name);
        };
    }

    /**
     * Creates function expression / operand.
     *
     * @param name name of the created function
     * @param args function arguments
     * @return function call expression
     */
    public static <T> ExpressionOperand<T> function(String name, Parameter... args) {
        Class<T> type = (Class<T>) com.sabre.oss.yare.core.model.Expression.UNDEFINED;
        return function(name, type, args);
    }

    /**
     * Creates parameter based on a passed operand.
     *
     * @param name    name of the parameter
     * @param operand operand
     * @param <T>     operand type
     * @return parameter
     */
    public static <T> Parameter param(String name, Operand<T> operand) {
        return builder -> operand.getExpression(name, builder);
    }

    /**
     * Creates a constant simple type value operand based on a string representation.
     *
     * @param value string representation of the value
     * @param type  type of the value
     * @param <T>   value type
     * @return value operand
     */
    public static <T> ExpressionOperand<T> value(String value, String type) {
        return (name, builder) -> {
            Type t = converter.fromString(Type.class, type);
            Object v = converter.fromString(t, value);
            return ExpressionFactory.valueOf(name, t, v);
        };
    }

    /**
     * Creates a constant simple or complex type value operand based on a value instance.
     *
     * @param value value instance
     * @param type  type of the value
     * @param <T>   value type
     * @return value operand
     */
    public static <T> ExpressionOperand<T> value(T value, Type type) {
        return (name, builder) -> ExpressionFactory.valueOf(name, type, value);
    }

    /**
     * Creates a constant simple type value operand based on a string representation.
     *
     * @param value string representation of the value
     * @param type  type of the value
     * @param <T>   value type
     * @return value operand
     */
    public static <T> ExpressionOperand<T> value(String value, Type type) {
        return (name, builder) -> ExpressionFactory.valueOf(name, type, converter.fromString(type, value));
    }

    /**
     * Creates a constant value operand.
     *
     * @param value string representation of the value
     * @param <T>   value type
     * @return value operand
     */
    public static <T> ExpressionOperand<T> value(T value) {
        Type type = value != null ? value.getClass() : com.sabre.oss.yare.core.model.Expression.UNDEFINED;
        return (name, builder) -> ExpressionFactory.valueOf(name, type, value);
    }

    /**
     * Creates a String-based value operand. References to attributes/facts or fact fields can be accessed using
     * placeholders, e.g. "${factName.field}". To create value formatted as placeholder it is possible
     * to escape it, e.g. "\\${factName.field}".
     *
     * @param value string representation of the value
     * @param <T>   value type
     * @return value operand
     */
    public static <T> ExpressionOperand<T> value(String value) {
        return (name, builder) -> ExpressionFactory.valueOf(name, String.class, value);
    }

    /**
     * Creates an operand representing a collection of expressions.
     *
     * @param type   class describing collection item type
     * @param values values of the collection
     * @param <T>    collection item type
     * @return operand representing collection of constant values
     */
    @SafeVarargs
    public static <T> CollectionOperand<T> values(Class<T> type, Operand<T>... values) {
        return (name, builder) -> {
            List<com.sabre.oss.yare.core.model.Expression> expressions = Stream.of(values)
                    .map(v -> v.getExpression(null, builder))
                    .collect(Collectors.toList());
            return ExpressionFactory.valuesOf(name, extractParametrizedType(type), expressions);
        };
    }

    /**
     * Creates operand representing a collection of constant values.
     *
     * @param type   class describing collection item type
     * @param values values of the collection
     * @param <T>    collection item type
     * @return operand representing collection of constant values
     */
    @SafeVarargs
    public static <T> CollectionOperand<T> values(Class<T> type, T... values) {
        return (name, builder) -> {
            List<com.sabre.oss.yare.core.model.Expression> expressions = Stream.of(values)
                    .map(RuleDsl::escapeStrings)
                    .map(v -> ExpressionFactory.valueOf(null, type, v))
                    .collect(Collectors.toList());
            return ExpressionFactory.valuesOf(name, extractParametrizedType(type), expressions);
        };
    }

    /**
     * Creates a logical AND expression for given logical expressions.
     *
     * @param booleanExpressions logical expressions
     * @return logical AND expression
     */
    @SafeVarargs
    public static Expression<Boolean> and(Expression<Boolean>... booleanExpressions) {
        return (name, builder) -> {
            List<com.sabre.oss.yare.core.model.Expression> expressions = Stream.of(booleanExpressions)
                    .map(e -> e.getExpression(null, builder))
                    .collect(Collectors.toList());
            return ExpressionFactory.operatorOf(name, Boolean.class, "and", expressions);
        };
    }

    /**
     * Creates a logical OR expression for given logical expressions.
     *
     * @param booleanExpressions logical boolean expression
     * @return logical OR expression
     */
    @SafeVarargs
    public static Expression<Boolean> or(Expression<Boolean>... booleanExpressions) {
        return (name, builder) -> {
            List<com.sabre.oss.yare.core.model.Expression> expressions = Stream.of(booleanExpressions)
                    .map(e -> e.getExpression(null, builder))
                    .collect(Collectors.toList());
            return ExpressionFactory.operatorOf(name, Boolean.class, "or", expressions);
        };
    }

    /**
     * Creates a logical negation (NOT) for given logical expression.
     *
     * @param booleanExpression expression to negate
     * @return logical NOT expression
     */
    public static Expression<Boolean> not(Expression<Boolean> booleanExpression) {
        return (name, builder) -> ExpressionFactory.operatorOf(name, Boolean.class, "not", booleanExpression.getExpression(null, builder));
    }

    /**
     * Creates operator-related logical expression (relational expression).
     *
     * @param type     operator name (type)
     * @param operands operands of any types
     * @return operator-related expression
     */
    public static Expression<Boolean> operator(String type, Operand<?>... operands) {
        return (name, builder) -> {
            List<com.sabre.oss.yare.core.model.Expression> expressions = Stream.of(operands)
                    .map(e -> e.getExpression(null, builder))
                    .collect(Collectors.toList());
            return ExpressionFactory.operatorOf(name, Boolean.class, type, expressions);
        };
    }

    /**
     * Creates EQUAL relational expression between two operands ((i.e. {@code left} == {@code right}).
     *
     * @param left  left operand
     * @param right right operand
     * @param <T>   type of operands
     * @return logical equality expression
     */
    public static <T> Expression<Boolean> equal(Operand<T> left, Operand<T> right) {
        return operator(Operator.EQUAL, left, right);
    }

    /**
     * Creates LESS relational expression between two operands (i.e. {@code left} &lt; {@code right}).
     *
     * @param left  left operand
     * @param right right operand
     * @param <T>   type of operands
     * @return LESS relational expression
     */
    public static <T> Expression<Boolean> less(Operand<T> left, Operand<T> right) {
        return operator(Operator.LESS, left, right);
    }

    /**
     * Creates LESS OR EQUAL relational expression between two operands (i.e. {@code left} &lt;= {@code right}).
     *
     * @param left  left operand
     * @param right right operand
     * @param <T>   type of operands
     * @return LESS OR EQUAL relational expression
     */
    public static <T> Expression<Boolean> lessOrEqual(Operand<T> left, Operand<T> right) {
        return operator(Operator.LESS_EQUAL, left, right);
    }

    /**
     * Creates GREATER relational expression between two operands (i.e. {@code left} &gt; {@code right}).
     *
     * @param left  left operand
     * @param right right operand
     * @param <T>   type of operands
     * @return GREATER relational expression
     */
    public static <T> Expression<Boolean> greater(Operand<T> left, Operand<T> right) {
        return operator(Operator.GREATER, left, right);
    }

    /**
     * Creates GREATER OR EQUAL relational expression between two operands (i.e. {@code left} &gt;= {@code right}).
     *
     * @param left  left operand
     * @param right right operand
     * @param <T>   type of operands
     * @return GREATER OR EQUAL relational expression
     */
    public static <T> Expression<Boolean> greaterOrEqual(Operand<T> left, Operand<T> right) {
        return operator(Operator.GREATER_EQUAL, left, right);
    }

    /**
     * Creates MATCH logical expression between two operands (i.e. {@code left} matches_to {@code right}).
     *
     * @param left  left operand
     * @param right right operand
     * @param <T>   type of operands
     * @return MATCH logical expression
     */
    public static <T> Expression<Boolean> match(Operand<T> left, Operand<T> right) {
        return operator(Operator.MATCH, left, right);
    }

    /**
     * Creates CONTAINS logical expression between two operands (i.e. {@code left} contains_all_of {@code right}).
     *
     * @param left  left operand
     * @param right right operand
     * @param <T>   type of operands
     * @return CONTAINS logical expression
     */
    public static <T> Expression<Boolean> contains(CollectionOperand<T> left, CollectionOperand<T> right) {
        return operator(Operator.CONTAINS, left, right);
    }

    /**
     * Creates CONTAINS logical expression between two operands (i.e. {@code left} contains {@code right}).
     *
     * @param left  left operand
     * @param right right operand
     * @param <T>   type of operands
     * @return CONTAINS logical expression
     */
    public static <T> Expression<Boolean> contains(CollectionOperand<T> left, Operand<T> right) {
        return operator(Operator.CONTAINS, left, right);
    }

    /**
     * Creates CONTAINS ANY logical expression between two operands (i.e. {@code left} contains_any_from {@code right}).
     *
     * @param left  left operand
     * @param right right operand
     * @param <T>   type of operands
     * @return CONTAINS ANY logical expression
     */
    public static <T> Expression<Boolean> containsAny(CollectionOperand<T> left, CollectionOperand<T> right) {
        return operator(Operator.CONTAINS_ANY, left, right);
    }

    /**
     * Creates IS NULL logical expression for given operand.
     *
     * @param operand operand
     * @return IS_NULL logical expression
     */
    public static Expression<Boolean> isNull(Operand<?> operand) {
        return operator(Operator.IS_NULL, operand);
    }

    /**
     * Creates IS TRUE logical expression for given operand.
     *
     * @param operand operand
     * @return IS TRUE logical expression
     */
    public static Expression<Boolean> isTrue(Operand<Boolean> operand) {
        return operator(Operator.IS_TRUE, operand);
    }

    /**
     * Creates IS FALSE logical expression for given operand.
     *
     * @param operand operand
     * @return FALSE logical expression
     */
    public static Expression<Boolean> isFalse(Operand<Boolean> operand) {
        return operator(Operator.IS_FALSE, operand);
    }

    /**
     * Predefined operators.
     */
    public interface Operator {
        String EQUAL = "equal";
        String LESS = "less";
        String LESS_EQUAL = "less-or-equal";
        String GREATER = "greater";
        String GREATER_EQUAL = "greater-or-equal";
        String MATCH = "match";
        String CONTAINS = "contains";
        String CONTAINS_ANY = "contains-any";
        String IS_NULL = "is-null";
        String IS_TRUE = "is-true";
        String IS_FALSE = "is-false";
    }

    private static <T> Type extractParametrizedType(Class<T> type) {
        String parametrizedType = String.format("java.util.List<%s>", converter.toString(Type.class, type));
        return converter.fromString(Type.class, parametrizedType);
    }

    private static <T> Object escapeStrings(T value) {
        return value instanceof String ? PlaceholderUtils.escape((String) value) : value;
    }

    public static final class RuleBuilder {
        private final Set<Attribute> attributes = new LinkedHashSet<>();
        private final ArrayList<Fact> facts = new ArrayList<>();
        private final ArrayList<Action> actions = new ArrayList<>();
        private Expression<Boolean> predicate;

        private RuleBuilder() {
        }

        /**
         * Sets rule name.
         *
         * @param name name of the rule
         * @return this ruleBuilder instance
         */
        public RuleBuilder name(String name) {
            return attribute("ruleName", name);
        }

        /**
         * Defines rule fact which can be accessed in predicate.
         * Fact instances are bound to the identifier during rule evaluation.
         *
         * @param identifier fact identifier
         * @param type       class describing fact type
         * @return this ruleBuilder instance
         */
        public RuleBuilder fact(String identifier, Class<?> type) {
            facts.add(new Fact(identifier, type));
            return this;
        }

        /**
         * Defines rule attribute which can be accessed in predicate.
         *
         * @param name  attribute identifier
         * @param value attribute value
         * @return this ruleBuilder instance
         */
        public RuleBuilder attribute(String name, Object value) {
            Type type = value != null ? value.getClass() : com.sabre.oss.yare.core.model.Expression.UNDEFINED;
            attributes.add(new Attribute(name, type, value));
            return this;
        }

        /**
         * Sets whether rule is ignored.
         *
         * @param value ignored flag
         * @return this ruleBuilder instance
         */
        public RuleBuilder ignored(boolean value) {
            return attribute("ignored", value);
        }

        /**
         * Sets priority of the rule. The lower the value the higher the priority.
         *
         * @param value priority value
         * @return this ruleBuilder instance
         */
        public RuleBuilder priority(long value) {
            return attribute("priority", value);
        }

        /**
         * Sets {@code expression} as rule's predicate.
         *
         * @param predicate expression used as a predicate
         * @return this ruleBuilder instance
         */
        public RuleBuilder predicate(Expression<Boolean> predicate) {
            this.predicate = predicate;
            return this;
        }

        /**
         * Adds actions - functions performed when rule matches.
         *
         * @param name action name
         * @param args action arguments
         * @return this ruleBuilder instance
         */
        public RuleBuilder action(String name, Parameter... args) {
            actions.add(new Action(name, args));
            return this;
        }

        /**
         * Validates and builds {@link Rule}.
         *
         * @return rule
         */
        public Rule build() {
            return build(true);
        }

        /**
         * Builds {@link Rule} with optional validation if {@code validate} flag set to true.
         *
         * @param validate validate flag
         * @return rule
         */
        public Rule build(boolean validate) {
            Rule rule = createRule();

            if (validate) {
                ValidationResults results = validator.validate(rule);
                List<String> errors = results.getResults().stream()
                        .filter(result -> result.getLevel() == ValidationResult.Level.ERROR)
                        .map(result -> "[" + result.getLevel() + "] " + result.getMessage())
                        .collect(Collectors.toList());
                List<String> warnings = results.getResults().stream()
                        .filter(result -> result.getLevel() == ValidationResult.Level.WARNING)
                        .map(result -> "[" + result.getLevel() + "] " + result.getMessage())
                        .collect(Collectors.toList());
                if (!errors.isEmpty()) {
                    errors.addAll(warnings);
                    throw new IllegalStateException("Rule validation error(s):\n" + String.join("\n", errors));
                }
                if (!warnings.isEmpty()) {
                    log.warn(String.join("\n", warnings));
                }
            }
            return rule;
        }

        private Rule createRule() {
            List<com.sabre.oss.yare.core.model.Expression.Action> actions = this.actions.stream()
                    .map(this::createAction)
                    .collect(Collectors.toList());
            com.sabre.oss.yare.core.model.Expression predicateExpression = predicate != null ? predicate.getExpression(null, this) : null;

            return new Rule(
                    new LinkedHashSet<>(attributes),
                    new ArrayList<>(facts),
                    predicateExpression,
                    actions);
        }

        private com.sabre.oss.yare.core.model.Expression.Action createAction(Action action) {
            List<com.sabre.oss.yare.core.model.Expression> params = Arrays.stream(action.parameters)
                    .map(parameter -> parameter.getExpression(this))
                    .collect(Collectors.toList());
            return ExpressionFactory.actionOf(action.name, action.name, params);
        }
    }

    private static final class Action {
        private String name;
        private Parameter[] parameters;

        private Action(String name, Parameter[] parameters) {
            this.name = name;
            this.parameters = parameters;
        }
    }
}
