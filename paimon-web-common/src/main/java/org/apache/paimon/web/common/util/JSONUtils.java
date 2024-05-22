/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.paimon.web.common.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import static com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL;
import static com.fasterxml.jackson.databind.MapperFeature.REQUIRE_SETTERS_FOR_GETTERS;

/** Json utils. */
@Slf4j
public class JSONUtils {

    private static final ObjectMapper OBJECT_MAPPER =
            JsonMapper.builder()
                    .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
                    .configure(READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
                    .configure(REQUIRE_SETTERS_FOR_GETTERS, true)
                    .addModule(
                            new SimpleModule()
                                    .addSerializer(
                                            LocalDateTime.class, new LocalDateTimeSerializer())
                                    .addDeserializer(
                                            LocalDateTime.class, new LocalDateTimeDeserializer()))
                    .defaultTimeZone(TimeZone.getDefault())
                    .defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                    .build();

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    private JSONUtils() {
        throw new UnsupportedOperationException("Construct JSONUtils");
    }

    public static synchronized void setTimeZone(TimeZone timeZone) {
        OBJECT_MAPPER.setTimeZone(timeZone);
    }

    public static ArrayNode createArrayNode() {
        return OBJECT_MAPPER.createArrayNode();
    }

    public static ObjectNode createObjectNode() {
        return OBJECT_MAPPER.createObjectNode();
    }

    public static String getString(JsonNode jsonNode, String fieldName) {
        return getString(jsonNode, fieldName, "");
    }

    public static String getString(JsonNode jsonNode, String fieldName, String defaultValue) {
        if (jsonNode == null) {
            return defaultValue;
        }
        JsonNode node = jsonNode.get(fieldName);
        if (node == null || !node.isTextual()) {
            return defaultValue;
        }
        return node.asText();
    }

    public static Integer getInteger(JsonNode jsonNode, String fieldName) {
        return getInteger(jsonNode, fieldName, 0);
    }

    public static Integer getInteger(JsonNode jsonNode, String fieldName, Integer defaultValue) {
        if (jsonNode == null) {
            return defaultValue;
        }
        JsonNode node = jsonNode.get(fieldName);
        if (node == null || !node.isInt()) {
            return defaultValue;
        }
        return node.asInt();
    }

    public static List<String> getList(JsonNode jsonNode, String fieldName) {
        return getList(jsonNode, fieldName, String.class);
    }

    public static <E> List<E> getList(JsonNode jsonNode, String fieldName, Class<E> clazz) {
        if (jsonNode == null) {
            return new ArrayList<>();
        }
        JsonNode child = jsonNode.get(fieldName);
        if (!(child instanceof ArrayNode) && !(child instanceof POJONode)) {
            return new ArrayList<>();
        }
        List<?> childArray;
        if (child instanceof POJONode) {
            Object pojo = ((POJONode) child).getPojo();
            childArray = (List<?>) pojo;
        } else {
            childArray = (List<?>) child;
        }

        List<E> result = new ArrayList<>();
        childArray.forEach(
                e -> {
                    result.add(JSONUtils.parseObject(JSONUtils.toJsonString(e), clazz));
                });
        return result;
    }

    /**
     * object to json string.
     *
     * @param object object
     * @return json string
     */
    public static String toJsonString(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Object json deserialization exception.", e);
        }
    }

    public static @Nullable <T> T parseObject(String json, Class<T> clazz) {
        if (Strings.isNullOrEmpty(json)) {
            return null;
        }

        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            log.error("Parse object exception, jsonStr: {}, class: {}", json, clazz, e);
        }
        return null;
    }

    /** LocalDateTimeSerializer. */
    public static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public void serialize(
                LocalDateTime value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            gen.writeString(value.format(formatter));
        }
    }

    /** LocalDateTimeDeserializer. */
    public static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext context)
                throws IOException {
            return LocalDateTime.parse(p.getValueAsString(), formatter);
        }
    }
}
