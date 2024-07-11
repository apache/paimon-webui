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

import org.apache.paimon.web.server.configrue.JacksonConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Test for {@link JacksonConfig}. */
@SpringBootTest
public class JacksonConfigTests {

    @Autowired private ObjectMapper objectMapper;

    private static final String DATE_TIME_FORMATTED_JSON_STR = "\"2024-06-26 13:01:30\"";
    private static final String DATE_TIME_UNFORMATTED_JSON_STR = "\"2024-06-26T13:01:30Z\"";
    private static final String DATE_FORMATTED_JSON_STR = "\"2024-06-26\"";
    private static final String TIME_FORMATTED_JSON_STR = "\"13:01:30\"";
    private static final String DATE_TIME_STR = "2024-06-26 13:01:30";
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Test
    public void testLocalDateTimeSerialization() throws IOException {
        LocalDateTime dateTime = LocalDateTime.of(2024, 6, 26, 13, 1, 30);
        String json = objectMapper.writeValueAsString(dateTime);
        assertEquals(DATE_TIME_FORMATTED_JSON_STR, json);
    }

    @Test
    public void testLocalDateTimeDeserialization() throws IOException {
        LocalDateTime dateTime =
                objectMapper.readValue(DATE_TIME_FORMATTED_JSON_STR, LocalDateTime.class);
        assertEquals(LocalDateTime.of(2024, 6, 26, 13, 1, 30), dateTime);
    }

    @Test
    public void testInvalidLocalDateTimeDeserialization() {
        assertThrows(
                com.fasterxml.jackson.databind.exc.InvalidFormatException.class,
                () -> objectMapper.readValue(DATE_TIME_UNFORMATTED_JSON_STR, LocalDateTime.class));
    }

    @Test
    public void testLocalDateSerialization() throws IOException {
        LocalDate dateTime = LocalDate.of(2024, 6, 26);
        String json = objectMapper.writeValueAsString(dateTime);
        assertEquals(DATE_FORMATTED_JSON_STR, json);
    }

    @Test
    public void testLocalDateDeserialization() throws IOException {
        LocalDate dateTime = objectMapper.readValue(DATE_FORMATTED_JSON_STR, LocalDate.class);
        assertEquals(LocalDate.of(2024, 6, 26), dateTime);
    }

    @Test
    public void testLocalTimeSerialization() throws IOException {
        LocalTime dateTime = LocalTime.of(13, 1, 30);
        String json = objectMapper.writeValueAsString(dateTime);
        assertEquals(TIME_FORMATTED_JSON_STR, json);
    }

    @Test
    public void testLocalTimeDeserialization() throws IOException {
        LocalTime dateTime = objectMapper.readValue(TIME_FORMATTED_JSON_STR, LocalTime.class);
        assertEquals(LocalTime.of(13, 1, 30), dateTime);
    }

    @Test
    public void testDateSerialization() throws IOException {
        Date date = parseDate(DATE_TIME_STR);
        String json = objectMapper.writeValueAsString(date);
        assertEquals(DATE_TIME_FORMATTED_JSON_STR, json);
    }

    @Test
    public void testDateDeserialization() throws IOException {
        Date date = parseDate(DATE_TIME_STR);
        Date dateDeserialized = objectMapper.readValue(DATE_TIME_FORMATTED_JSON_STR, Date.class);
        assertEquals(dateDeserialized, date);
    }

    private Date parseDate(String date) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(JacksonConfigTests.DATE_TIME_PATTERN);

        LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);

        Instant instant = zonedDateTime.toInstant();
        return Date.from(instant);
    }
}
