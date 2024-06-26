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

package org.apache.paimon.web.server.configurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class JacksonConfigTests {

    @Autowired private ObjectMapper objectMapper;

    private static final String TIME_FORMATTED_JSON_STR = "\"2024-06-26 13:01:30\"";
    private static final String TIME_UNFORMATTED_JSON_STR = "\"2024-06-26T13:01:30Z\"";

    @Test
    public void testLocalDateTimeSerialization() throws IOException {
        LocalDateTime dateTime = LocalDateTime.of(2024, 6, 26, 13, 1, 30);
        String json = objectMapper.writeValueAsString(dateTime);
        assertEquals(TIME_FORMATTED_JSON_STR, json);
    }

    @Test
    public void testLocalDateTimeDeserialization() throws IOException {
        LocalDateTime dateTime =
                objectMapper.readValue(TIME_FORMATTED_JSON_STR, LocalDateTime.class);
        assertEquals(LocalDateTime.of(2024, 6, 26, 13, 1, 30), dateTime);
    }

    @Test
    public void testInvalidLocalDateTimeDeserialization() {
        assertThrows(
                com.fasterxml.jackson.databind.exc.InvalidFormatException.class,
                () -> objectMapper.readValue(TIME_UNFORMATTED_JSON_STR, LocalDateTime.class));
    }
}
