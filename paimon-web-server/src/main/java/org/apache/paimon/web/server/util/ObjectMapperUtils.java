/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.paimon.web.server.util;

import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.Map;

import static com.fasterxml.jackson.core.JsonFactory.Feature.INTERN_FIELD_NAMES;
import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS;
import static com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance;

/** jackson common object mapper. */
public class ObjectMapperUtils {
    private static final String EMPTY_JSON = "{}";
    private static final String EMPTY_ARRAY_JSON = "[]";
    private static final ObjectMapper MAPPER;

    static {
        MAPPER =
                new ObjectMapper(
                        new JsonFactoryBuilder()
                                .configure(INTERN_FIELD_NAMES, false)
                                .configure(ALLOW_UNESCAPED_CONTROL_CHARS, true)
                                .build());
        MAPPER.disable(FAIL_ON_UNKNOWN_PROPERTIES);
        MAPPER.enable(ALLOW_COMMENTS);
        MAPPER.registerModule(new ParameterNamesModule());
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static String toJSON(@Nullable Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Please do not use formatted JSON when outputting logs.
     *
     * <p>For better readability
     */
    public static String toPrettyJson(@Nullable Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void toJSON(@Nullable Object obj, OutputStream writer) {
        if (obj == null) {
            return;
        }
        try {
            MAPPER.writeValue(writer, obj);
        } catch (IOException e) {
            throw wrapException(e);
        }
    }

    public static <T> T fromJSON(@Nullable byte[] bytes, Class<T> valueType) {
        if (bytes == null) {
            return null;
        }
        try {
            return MAPPER.readValue(bytes, valueType);
        } catch (IOException e) {
            throw wrapException(e);
        }
    }

    private static RuntimeException wrapException(IOException e) {
        return new UncheckedIOException(e);
    }

    public static <T> T fromJSON(@Nullable String json, Class<T> valueType) {
        if (json == null) {
            return null;
        }
        try {
            return MAPPER.readValue(json, valueType);
        } catch (IOException e) {
            throw wrapException(e);
        }
    }

    public static <T> T fromJSON(@Nullable String json, TypeReference<T> type) {
        if (json == null) {
            return null;
        }
        try {
            return MAPPER.readValue(json, type);
        } catch (IOException e) {
            throw wrapException(e);
        }
    }

    public static <T> T fromJSON(Object value, Class<T> valueType) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return fromJSON((String) value, valueType);
        } else if (value instanceof byte[]) {
            return fromJSON((byte[]) value, valueType);
        } else {
            return null;
        }
    }

    public static <T> T value(Object rawValue, Class<T> type) {
        return MAPPER.convertValue(rawValue, type);
    }

    public static <T> T update(T rawValue, String newProperty) {
        try {
            return MAPPER.readerForUpdating(rawValue).readValue(newProperty);
        } catch (IOException e) {
            throw wrapException(e);
        }
    }

    public static <T> T value(Object rawValue, TypeReference<T> type) {
        return MAPPER.convertValue(rawValue, type);
    }

    public static <T> T value(Object rawValue, JavaType type) {
        return MAPPER.convertValue(rawValue, type);
    }

    public static <T> T unwrapJsonP(String raw, Class<T> type) {
        return fromJSON(unwrapJsonP(raw), type);
    }

    private static String unwrapJsonP(String raw) {
        raw = StringUtils.trim(raw);
        raw = StringUtils.removeEnd(raw, ";");
        raw = raw.substring(raw.indexOf('(') + 1);
        raw = raw.substring(0, raw.lastIndexOf(')'));
        raw = StringUtils.trim(raw);
        return raw;
    }

    public static <E, T extends Collection<E>> T fromJSON(
            String json, Class<? extends Collection> collectionType, Class<E> valueType) {
        if (StringUtils.isEmpty(json)) {
            json = EMPTY_ARRAY_JSON;
        }
        try {
            return MAPPER.readValue(
                    json, defaultInstance().constructCollectionType(collectionType, valueType));
        } catch (IOException e) {
            throw wrapException(e);
        }
    }

    /** use {@link #fromJson(String)} instead. */
    public static <K, V, T extends Map<K, V>> T fromJSON(
            String json, Class<? extends Map> mapType, Class<K> keyType, Class<V> valueType) {
        if (StringUtils.isEmpty(json)) {
            json = EMPTY_JSON;
        }
        try {
            return MAPPER.readValue(
                    json, defaultInstance().constructMapType(mapType, keyType, valueType));
        } catch (IOException e) {
            throw wrapException(e);
        }
    }

    public static <T> T fromJSON(InputStream inputStream, Class<T> type) {
        try {
            return MAPPER.readValue(inputStream, type);
        } catch (IOException e) {
            throw wrapException(e);
        }
    }

    public static <E, T extends Collection<E>> T fromJSON(
            byte[] bytes, Class<? extends Collection> collectionType, Class<E> valueType) {
        try {
            return MAPPER.readValue(
                    bytes, defaultInstance().constructCollectionType(collectionType, valueType));
        } catch (IOException e) {
            throw wrapException(e);
        }
    }

    public static <E, T extends Collection<E>> T fromJSON(
            InputStream inputStream,
            Class<? extends Collection> collectionType,
            Class<E> valueType) {
        try {
            return MAPPER.readValue(
                    inputStream,
                    defaultInstance().constructCollectionType(collectionType, valueType));
        } catch (IOException e) {
            throw wrapException(e);
        }
    }

    public static Map<String, Object> fromJson(InputStream is) {
        return fromJSON(is, Map.class, String.class, Object.class);
    }

    public static Map<String, Object> fromJson(String string) {
        return fromJSON(string, Map.class, String.class, Object.class);
    }

    public static Map<String, Object> fromJson(byte[] bytes) {
        return fromJSON(bytes, Map.class, String.class, Object.class);
    }

    /** use {@link #fromJson(byte[])} instead. */
    public static <K, V, T extends Map<K, V>> T fromJSON(
            byte[] bytes, Class<? extends Map> mapType, Class<K> keyType, Class<V> valueType) {
        try {
            return MAPPER.readValue(
                    bytes, defaultInstance().constructMapType(mapType, keyType, valueType));
        } catch (IOException e) {
            throw wrapException(e);
        }
    }

    /** use {@link #fromJson(InputStream)} instead. */
    public static <K, V, T extends Map<K, V>> T fromJSON(
            InputStream inputStream,
            Class<? extends Map> mapType,
            Class<K> keyType,
            Class<V> valueType) {
        try {
            return MAPPER.readValue(
                    inputStream, defaultInstance().constructMapType(mapType, keyType, valueType));
        } catch (IOException e) {
            throw wrapException(e);
        }
    }

    public static boolean isJSON(String jsonStr) {
        if (StringUtils.isBlank(jsonStr)) {
            return false;
        }
        try (JsonParser parser = new ObjectMapper().getFactory().createParser(jsonStr)) {
            while (parser.nextToken() != null) {
                // do nothing.
            }
            return true;
        } catch (IOException ioe) {
            return false;
        }
    }

    public static boolean isBadJSON(String jsonStr) {
        return !isJSON(jsonStr);
    }

    public static ObjectMapper mapper() {
        return MAPPER;
    }
}
