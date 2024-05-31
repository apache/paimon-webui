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

package org.apache.paimon.web.server.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/** Local date time util. */
public class LocalDateTimeUtil {

    public static LocalDateTime convertUtcStringToLocalDateTime(String utcTimeStr) {
        OffsetDateTime utcTime =
                OffsetDateTime.parse(utcTimeStr + "Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        ZonedDateTime beijingTime = utcTime.atZoneSameInstant(ZoneId.of("Asia/Shanghai"));
        return beijingTime.toLocalDateTime();
    }

    public static String getFormattedDateTime(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return time.format(formatter);
    }
}
