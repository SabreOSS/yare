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
import com.sabre.oss.yare.dsl.Operand;
import com.sabre.oss.yare.core.model.Rule;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.sabre.oss.yare.dsl.RuleDsl.*;
import static com.sabre.oss.yare.engine.integration.BaseRulesUtils.createConfig;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class ArgumentPassingTest extends AbstractBaseRulesTest {
    private static final String RULE_NAME = "Rule";

    @Test
    void shouldProperlyPassJavaBeanAsActionParam() {
        // given
        MyBean actionArgument = new MyBean(new MyBean("actionParamParent"), "actionParam");
        List<Rule> rules = Collections.singletonList(createRule(RULE_NAME, value(actionArgument)));
        RuleSession session = createRuleEngine(createConfig(rules)).createSession("any");
        Map<String, List<Object>> ctx = new HashMap<>();

        // when
        Map<String, List<Object>> result = session.execute(ctx, asList(new BaseRulesUtils.FactOne(), new BaseRulesUtils.FactTwo()));

        // then
        @SuppressWarnings("unchecked")
        MyBean extra = (MyBean) result.get(RULE_NAME).get(2);
        assertThat(extra).isEqualTo(actionArgument);
    }

    @Test
    void shouldProperlyPassJavaBeanAsActionParamCollectionItem() {
        // given
        MyBean actionArgument = new MyBean(new MyBean("actionParamParent"), "actionParam");
        List<Rule> rules = Collections.singletonList(createRule(RULE_NAME, values(MyBean.class, actionArgument)));
        RuleSession session = createRuleEngine(createConfig(rules)).createSession("any");
        Map<String, List<Object>> ctx = new HashMap<>();

        // when
        Map<String, List<Object>> result = session.execute(ctx, asList(new BaseRulesUtils.FactOne(), new BaseRulesUtils.FactTwo()));

        // then
        @SuppressWarnings("unchecked")
        List<MyBean> extra = (List<MyBean>) result.get(RULE_NAME).get(2);
        assertThat(extra).containsExactly(actionArgument);
    }

    @Test
    void shouldProperlyPassNullJavaBeanAsActionParam() {
        // given
        List<Rule> rules = Collections.singletonList(createRule(RULE_NAME, value((MyBean) null, MyBean.class)));
        RuleSession session = createRuleEngine(createConfig(rules)).createSession("any");
        Map<String, List<Object>> ctx = new HashMap<>();

        // when
        Map<String, List<Object>> result = session.execute(ctx, Arrays.asList(new BaseRulesUtils.FactOne(), new BaseRulesUtils.FactTwo()));

        // then
        @SuppressWarnings("unchecked")
        List<MyBean> extra = (List<MyBean>) result.get(RULE_NAME).get(2);
        assertThat(extra).isNull();
    }

    @Test
    void shouldProperlyPassNullJavaBeanAsActionParamCollectionItem() {
        // given
        List<Rule> rules = Collections.singletonList(createRule(RULE_NAME, values(MyBean.class, (MyBean) null)));
        RuleSession session = createRuleEngine(createConfig(rules)).createSession("any");
        Map<String, List<Object>> ctx = new HashMap<>();

        // when
        Map<String, List<Object>> result = session.execute(ctx, Arrays.asList(new BaseRulesUtils.FactOne(), new BaseRulesUtils.FactTwo()));

        // then
        @SuppressWarnings("unchecked")
        List<MyBean> extra = (List<MyBean>) result.get(RULE_NAME).get(2);
        assertThat(extra).containsExactly(new MyBean[]{null});
    }

    private Rule createRule(String name, Operand<?> operand) {
        return ruleBuilder()
                .name(name)
                .fact("factOne", BaseRulesUtils.FactOne.class)
                .fact("factTwo", BaseRulesUtils.FactTwo.class)
                .predicate(value(true))
                .action("collectMore",
                        param("context", reference("ctx")),
                        param("ruleName", reference("ruleName")),
                        param("factOne", reference("factOne")),
                        param("factTwo", reference("factTwo")),
                        param("extra", operand))
                .build(false);
    }

    private static final class MyBean {
        private final MyBean parent;
        private final String value;

        private MyBean() {
            this(null, null);
        }

        private MyBean(String value) {
            this(null, value);
        }

        private MyBean(MyBean parent, String value) {
            this.value = value;
            this.parent = parent;
        }

        private MyBean getParent() {
            return parent;
        }

        private String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MyBean myBean = (MyBean) o;
            return Objects.equals(parent, myBean.parent) &&
                    Objects.equals(value, myBean.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(parent, value);
        }
    }
}
