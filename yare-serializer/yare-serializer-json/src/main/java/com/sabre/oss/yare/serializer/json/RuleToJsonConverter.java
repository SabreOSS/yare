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

package com.sabre.oss.yare.serializer.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sabre.oss.yare.common.converter.DefaultTypeConverters;
import com.sabre.oss.yare.common.converter.TypeConverter;
import com.sabre.oss.yare.core.model.Attribute;
import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.model.converter.RuleConversionException;
import com.sabre.oss.yare.model.converter.RuleConverter;
import com.sabre.oss.yare.serializer.json.mapper.json.ToJsonConverter;
import com.sabre.oss.yare.serializer.json.mapper.rule.ToRuleConverter;

import java.io.IOException;
import java.util.Optional;

public class RuleToJsonConverter implements RuleConverter {
    private final TypeConverter defaultTypeConverter = DefaultTypeConverters.getDefaultTypeConverter();
    private final ToRuleConverter toRuleConverter = new ToRuleConverter(defaultTypeConverter);
    private final ToJsonConverter toJsonConverter = new ToJsonConverter(defaultTypeConverter);
    private final ObjectMapper objectMapper;

    public RuleToJsonConverter() {
        this.objectMapper = getObjectMapper();
    }

    public RuleToJsonConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        return objectMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String marshal(Rule rule) throws RuleConversionException {
        try {
            com.sabre.oss.yare.serializer.json.model.Rule jsonRule = toJsonConverter.convert(rule);
            return objectMapper.writeValueAsString(jsonRule);
        } catch (JsonProcessingException e) {
            String ruleName = Optional.ofNullable(rule.getAttribute("ruleName"))
                    .map(Attribute::getValue)
                    .map(Object::toString)
                    .orElse("");
            throw new RuleConversionException(String.format("Rule cannot be converted to JSON: %s", ruleName), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rule unmarshal(String value) throws RuleConversionException {
        try {
            com.sabre.oss.yare.serializer.json.model.Rule rule = objectMapper.readValue(value, com.sabre.oss.yare.serializer.json.model.Rule.class);
            return toRuleConverter.convert(rule);
        } catch (IOException e) {
            throw new RuleConversionException(String.format("Rule cannot be converted to Object:\n%s", value), e);
        }
    }
}
