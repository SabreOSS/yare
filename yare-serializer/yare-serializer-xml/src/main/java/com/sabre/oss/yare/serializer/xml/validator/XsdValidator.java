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

package com.sabre.oss.yare.serializer.xml.validator;

import com.sabre.oss.yare.model.converter.RuleConversionException;
import com.sabre.oss.yare.serializer.validator.SchemaValidationError;
import com.sabre.oss.yare.serializer.validator.SchemaValidationResults;
import com.sabre.oss.yare.serializer.validator.SchemaValidator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class XsdValidator implements SchemaValidator {
    private static final String SCHEMA_RESOURCE_FILE = "schema/v1.0/yare-rules.xsd";
    private static final Schema schema = loadSchemaDefinition();

    @Override
    public SchemaValidationResults validate(String rule) {
        ValidationHandler errorHandler = new ValidationHandler();
        Set<SchemaValidationError> validationErrors = errorHandler.validationErrors;
        try {
            Source source = new StreamSource(new StringReader(rule));
            Validator validator = schema.newValidator();
            validator.setErrorHandler(errorHandler);
            validator.validate(source);
        } catch (SAXParseException e) {
            validationErrors.add(saxParseExceptionToSchemaValidationError(e));
        } catch (SAXException e) {
            validationErrors.add(SchemaValidationError.of(e.getMessage()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return validationErrors.isEmpty() ? SchemaValidationResults.success() : SchemaValidationResults.ofErrors(validationErrors);
    }

    private static Schema loadSchemaDefinition() {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            return schemaFactory.newSchema(XsdValidator.class.getClassLoader().getResource(SCHEMA_RESOURCE_FILE));
        } catch (SAXException e) {
            throw new RuleConversionException("Rule Schema cannot be initialized", e);
        }
    }

    private static SchemaValidationError saxParseExceptionToSchemaValidationError(SAXParseException exception) {
        return SchemaValidationError.of(exception.getLineNumber(), exception.getColumnNumber(), exception.getMessage());
    }

    private static class ValidationHandler implements ErrorHandler {
        private final Set<SchemaValidationError> validationErrors = new LinkedHashSet<>();

        @Override
        public void warning(SAXParseException exception) {
            validationErrors.add(saxParseExceptionToSchemaValidationError(exception));
        }

        @Override
        public void error(SAXParseException exception) {
            validationErrors.add(saxParseExceptionToSchemaValidationError(exception));
        }

        @Override
        public void fatalError(SAXParseException exception) {
            validationErrors.add(saxParseExceptionToSchemaValidationError(exception));
        }
    }
}
