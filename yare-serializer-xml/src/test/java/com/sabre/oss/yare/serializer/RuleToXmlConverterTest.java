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

package com.sabre.oss.yare.serializer;

import com.sabre.oss.yare.common.converter.DefaultTypeConverters;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.model.converter.RuleConversionException;
import com.sabre.oss.yare.serializer.ConverterTest.Isbn;
import com.sabre.oss.yare.serializer.ConverterTest.MyList;
import com.sabre.oss.yare.serializer.ConverterTest.MyMap;
import com.sabre.oss.yare.serializer.model.ObjectFactory;
import com.sabre.oss.yare.serializer.model.RuleSer;
import com.sabre.oss.yare.serializer.xml.RuleToXmlConverter;
import com.sabre.oss.yare.serializer.xml.mapper.converter.rule.ToRuleConverter;
import com.sabre.oss.yare.serializer.xml.mapper.converter.xml.ToXmlConverter;
import org.junit.jupiter.api.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.MarshalException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.InputStream;

import static com.sabre.oss.yare.serializer.ResourceUtils.getResourceAsString;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RuleToXmlConverterTest {
    private static final ObjectFactory objectFactory = new ObjectFactory();

    private final RuleToXmlConverter toXmlConverter = RuleToXmlConverter.getInstance(Isbn.class, MyList.class, MyMap.class);
    private final ToRuleConverter toRuleConverter = new ToRuleConverter(DefaultTypeConverters.getDefaultTypeConverter());
    private final ToXmlConverter toXmlConverter1 = new ToXmlConverter(DefaultTypeConverters.getDefaultTypeConverter());
    private final String validXmlRuleWithBuildInObjectTypes = substringAfterLast(getResourceAsString("/serializer/validXmlRuleWithBuildInObjectTypes.xml"), "-->");
    private final String validXmlRuleWithCustomObjectTypes = substringAfterLast(getResourceAsString("/serializer/validXmlRuleWithCustomObjectTypes.xml"), "-->");

    @Test
    void shouldMarshallValidRuleWithBuildInObjectTypes() {
        // given
        Rule ruleObject = toRuleConverter.map(ConverterTest.constructValidRuleWithBuildInObjectTypes());

        // when
        String ruleAsString = toXmlConverter.marshal(ruleObject);

        // then
        assertThat(ruleAsString).isXmlEqualTo(validXmlRuleWithBuildInObjectTypes);
    }

    @Test
    void shouldMarshallValidRuleWithCustomObjectTypes() {
        // given
        Rule ruleObject = toRuleConverter.map(ConverterTest.constructValidRuleWithCustomObjectTypes());

        // when
        String ruleAsString = toXmlConverter.marshal(ruleObject);

        // then
        assertThat(ruleAsString).isXmlEqualTo(validXmlRuleWithCustomObjectTypes);
    }

    @Test
    void shouldUnmarshalValidRuleWithBuildInObjectTypes() {
        // given
        Rule validRule = toRuleConverter.map(ConverterTest.constructValidRuleWithBuildInObjectTypes());

        // when
        Rule ruleObject = toXmlConverter.unmarshal(validXmlRuleWithBuildInObjectTypes);

        // then
        assertThat(ruleObject.getAttributes()).containsAll(validRule.getAttributes());
        assertThat(ruleObject).isEqualTo(validRule);
    }

    @Test
    void shouldUnmarshalValidRuleWithCustomObjectTypes() {
        // given
        Rule validRule = toRuleConverter.map(ConverterTest.constructValidRuleWithCustomObjectTypes());

        // when
        Rule ruleObject = toXmlConverter.unmarshal(validXmlRuleWithCustomObjectTypes);

        // then
        assertThat(ruleObject.getAttributes()).containsAll(validRule.getAttributes());
        assertThat(ruleObject).isEqualTo(validRule);
    }

    @Test
    void shouldProperlyMarshalAndUnmarshalValidRuleWithCustomObjectTypes() {
        // given
        Rule validRule = toRuleConverter.map(ConverterTest.constructValidRuleWithCustomObjectTypes());

        // when
        Rule rule = toXmlConverter.unmarshal(toXmlConverter.marshal(validRule));

        // then
        assertThat(rule).isEqualTo(validRule);
    }

    @Test
    void shouldCheckValidRuleAgainstSchema() throws Exception {
        // given
        RuleSer ruleObject = ConverterTest.constructValidRuleWithBuildInObjectTypes();

        JAXBContext jc = JAXBContext.newInstance(RuleSer.class, Isbn.class);
        JAXBSource source = new JAXBSource(jc, objectFactory.createRule(ruleObject));
        Validator validator = getSchema().newValidator();
        validator.setErrorHandler(new ThrowingErrorHandler());

        // when / then
        validator.validate(source);
    }

    @Test
    void shouldCheckInvalidRuleAgainstSchema() throws Exception {
        // given
        Rule ruleObject = ConverterTest.constructInvalidRule();

        JAXBContext jc = JAXBContext.newInstance(Rule.class);
        JAXBSource source = new JAXBSource(jc, objectFactory.createRule(toXmlConverter1.map(ruleObject)));
        Validator validator = getSchema().newValidator();
        validator.setErrorHandler(new ThrowingErrorHandler());

        // when
        assertThatThrownBy(() -> validator.validate(source))
                // then
                .isInstanceOf(SAXParseException.class);
    }

    @Test
    void shouldMarshallRuleConsistentWithSchema() {
        // given
        Rule ruleObject = toRuleConverter.map(ConverterTest.constructValidRuleWithCustomObjectTypes());
        // when
        String ruleAsString = toXmlConverter.marshal(ruleObject);
        // then
        assertThat(ruleAsString).isXmlEqualTo(validXmlRuleWithCustomObjectTypes);
    }

    @Test
    void shouldNotMarshallRuleInconsistentWithSchema() {
        // given
        Rule ruleObject = ConverterTest.constructInvalidRule();
        // when
        assertThatThrownBy(() -> toXmlConverter.marshal(ruleObject))
                // then
                .isInstanceOf(RuleConversionException.class).hasCauseExactlyInstanceOf(MarshalException.class);
    }

    @Test
    void shouldMarshalAndUnmarshalRule() {
        // given
        Rule validRule = toRuleConverter.map(ConverterTest.constructValidRuleWithCustomObjectTypes());
        // when
        String data = toXmlConverter.marshal(validRule);
        Rule rule = toXmlConverter.unmarshal(data);

        // then
        assertThat(rule).isEqualTo(validRule);
    }

    @Test
    void shouldUnmarshalRuleConsistentWithSchema() {
        // when
        Rule ruleObject = toXmlConverter.unmarshal(validXmlRuleWithBuildInObjectTypes);
        // then
        assertThat(ruleObject).isEqualTo(toRuleConverter.map(ConverterTest.constructValidRuleWithBuildInObjectTypes()));
    }

    @Test
    void shouldNotUnmarshalRuleInconsistentWithSchema() {
        // given
        String invalidXmlRule = "" +
                "<yare:Rule xmlns:yare=\"http://www.sabre.com/schema/oss/yare/rules/v1\">\n" +
                "    <yare:unexpectedElement/>\n" +
                "</yare:Rule>";

        // when
        assertThatThrownBy(() -> toXmlConverter.unmarshal(invalidXmlRule))
                // then
                .isInstanceOf(RuleConversionException.class).hasCauseExactlyInstanceOf(UnmarshalException.class);
    }

    private Schema getSchema() throws SAXException {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return sf.newSchema(new StreamSource(getResourceAsStream("/schema/yare-rules.xsd")));
    }

    private InputStream getResourceAsStream(String resource) {
        InputStream resourceAsStream = getClass().getResourceAsStream(resource);
        if (resourceAsStream == null) {
            throw new IllegalArgumentException("Cannot find " + resource);
        }
        return resourceAsStream;
    }

    private static class ThrowingErrorHandler implements ErrorHandler {
        @Override
        public void warning(SAXParseException exception) throws SAXException {
            throw exception;
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            throw exception;
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            throw exception;
        }
    }
}
