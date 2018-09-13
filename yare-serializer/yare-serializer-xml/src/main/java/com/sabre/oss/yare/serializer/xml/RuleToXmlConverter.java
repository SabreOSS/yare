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

package com.sabre.oss.yare.serializer.xml;

import com.google.common.io.Resources;
import com.sabre.oss.yare.common.converter.DefaultTypeConverters;
import com.sabre.oss.yare.common.converter.TypeConverter;
import com.sabre.oss.yare.core.model.Attribute;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.model.converter.RuleConversionException;
import com.sabre.oss.yare.model.converter.RuleConverter;
import com.sabre.oss.yare.serializer.model.ObjectFactory;
import com.sabre.oss.yare.serializer.model.RuleSer;
import com.sabre.oss.yare.serializer.validator.SchemaValidationException;
import com.sabre.oss.yare.serializer.validator.SchemaValidationResults;
import com.sabre.oss.yare.serializer.xml.mapper.converter.rule.ToRuleConverter;
import com.sabre.oss.yare.serializer.xml.mapper.converter.xml.ToXmlConverter;
import com.sabre.oss.yare.serializer.xml.validator.XsdValidator;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.util.Arrays.asList;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static javax.xml.bind.Marshaller.JAXB_FRAGMENT;
import static javax.xml.stream.XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES;
import static javax.xml.stream.XMLInputFactory.SUPPORT_DTD;

public final class RuleToXmlConverter implements RuleConverter {
    private static final ObjectFactory objectFactory = new ObjectFactory();
    private static final RuleToXmlConverter instance = new RuleToXmlConverter();
    private static final Schema schema = loadSchemaDefinition();

    private final TypeConverter defaultTypeConverter = DefaultTypeConverters.getDefaultTypeConverter();
    private final ToXmlConverter toXmlConverter = new ToXmlConverter(defaultTypeConverter);
    private final ToRuleConverter toRuleConverter = new ToRuleConverter(defaultTypeConverter);
    private final XsdValidator validator = new XsdValidator();
    private final JAXBContext jaxbContext;

    private RuleToXmlConverter(Class<?>... extraClasses) {
        try {
            List<Class<?>> classes = new ArrayList<>(extraClasses.length + 1);
            classes.add(RuleSer.class);
            classes.addAll(asList(extraClasses));
            jaxbContext = JAXBContext.newInstance(classes.toArray(new Class[0]));
        } catch (JAXBException e) {
            throw new RuleConversionException("Rule JaxbContext cannot be initialized", e);
        }
    }

    /**
     * Returns instance of {@link RuleToXmlConverter}.
     *
     * @param extraClasses custom POJO classes used in your {@link Rule} representation (object/string)
     * @return {@link RuleToXmlConverter} instance
     */
    public static RuleToXmlConverter getInstance(Class<?>... extraClasses) {
        return extraClasses.length == 0
                ? instance
                : new RuleToXmlConverter(extraClasses);
    }

    private static Schema loadSchemaDefinition() {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
            return schemaFactory.newSchema(Resources.getResource("schema/v1.0/yare-rules.xsd"));
        } catch (SAXException e) {
            throw new RuleConversionException("Rule Schema cannot be initialized", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String marshal(Rule rule) throws RuleConversionException {
        try {
            RuleSer ruleSer = toXmlConverter.map(rule);

            StringWriter sw = new StringWriter();
            createMarshaller().marshal(objectFactory.createRule(ruleSer), sw);

            return sw.toString();
        } catch (Exception e) {
            Attribute ruleNameAttribute = rule.getAttribute("ruleName");
            String ruleName = ruleNameAttribute != null ? ruleNameAttribute.getValue().toString() : null;
            throw new RuleConversionException(String.format("Rule cannot be converted to XML:\n%s", ruleName), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rule unmarshal(String value) throws RuleConversionException {
        validateAgainstSchema(value);
        try {
            Unmarshaller unmarshaller = createUnmarshaller();
            XMLInputFactory xmlInputFactory = getXmlInputFactory();
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new StringReader(value));
            RuleSer ruleSer = ((JAXBElement<RuleSer>) unmarshaller.unmarshal(xmlStreamReader)).getValue();
            return toRuleConverter.map(ruleSer);
        } catch (Exception e) {
            throw new RuleConversionException(String.format("Rule cannot be converted to Object:\n%s", value), e);
        }
    }

    private Marshaller createMarshaller() throws RuleConversionException {
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(JAXB_FRAGMENT, Boolean.TRUE);
            marshaller.setSchema(schema);
            return marshaller;
        } catch (Exception e) {
            throw new RuleConversionException("Rule marshaller cannot be initialized", e);
        }
    }

    private void validateAgainstSchema(String value) {
        SchemaValidationResults results = validator.validate(value);
        if (results.hasErrors()) {
            throw new SchemaValidationException("Given XML rule does not satisfy schema", results.getResults());
        }
    }

    private Unmarshaller createUnmarshaller() throws RuleConversionException {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setSchema(schema);
            return unmarshaller;
        } catch (JAXBException e) {
            throw new RuleConversionException("Rule unmarshaller cannot be initialized", e);
        }
    }

    private XMLInputFactory getXmlInputFactory() {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
        xmlInputFactory.setProperty(IS_SUPPORTING_EXTERNAL_ENTITIES, FALSE);
        xmlInputFactory.setProperty(SUPPORT_DTD, FALSE);
        return xmlInputFactory;
    }
}
