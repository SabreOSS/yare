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

package com.sabre.oss.yare.serializer.json.validator;

import com.sabre.oss.yare.serializer.json.utils.JsonResourceUtils;
import com.sabre.oss.yare.serializer.validator.SchemaValidationError;
import com.sabre.oss.yare.serializer.validator.SchemaValidationResults;
import com.sabre.oss.yare.serializer.validator.SchemaValidator;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;

import java.util.Set;
import java.util.stream.Collectors;

public class JsonSchemaValidator implements SchemaValidator {
    private static final String SCHEMA_RESOURCE_FILE = "/schema/v1.0/yare-rules.json";
    private static final JSONObject schema = new JSONObject(JsonResourceUtils.getJsonResourceAsString(SCHEMA_RESOURCE_FILE));

    @Override
    public SchemaValidationResults validate(String rule) {
        try {
            JSONObject jsonRule = new JSONObject(rule);
            SchemaLoader.load(schema).validate(jsonRule);
            return SchemaValidationResults.success();
        } catch (ValidationException e) {
            return mapAsSchemaValidationResults(e);
        } catch (Exception e) {
            return mapAsSchemaValidationResults(e);
        }
    }

    private SchemaValidationResults mapAsSchemaValidationResults(ValidationException e) {
        Set<SchemaValidationError> results = e.getCausingExceptions().stream()
                .map(ValidationException::getMessage)
                .map(SchemaValidationError::of)
                .collect(Collectors.toSet());
        return results.isEmpty()
                ? SchemaValidationResults.ofError(SchemaValidationError.of(e.getMessage()))
                : SchemaValidationResults.ofErrors(results);
    }

    private SchemaValidationResults mapAsSchemaValidationResults(Exception e) {
        String violationMessage = String.format("Could not validate JSON. Exception %s has been thrown with message: %s",
                e.getClass().getSimpleName(), e.getMessage());
        return SchemaValidationResults.ofError(SchemaValidationError.of(violationMessage));
    }
}
