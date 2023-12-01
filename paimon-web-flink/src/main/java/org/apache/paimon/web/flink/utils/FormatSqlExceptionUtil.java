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

package org.apache.paimon.web.flink.utils;

import java.util.List;

/**
 * Utility class for formatting SQL exception messages. This class provides static methods to format
 * exception messages for SQL queries and batch SQL statements, ensuring that long SQL texts are
 * truncated to a predefined maximum length.
 */
public class FormatSqlExceptionUtil {

    private static final int MAX_SQL_DISPLAY_LENGTH = 500;

    public static String formatSqlExceptionMessage(String sql) {
        return String.format("Failed to execute query statement: '%s'", formatSql(sql));
    }

    public static String formatSqlBatchExceptionMessage(List<String> sqlStatements) {
        String combinedStatements = String.join("; ", sqlStatements);
        return String.format(
                "Failed to execute insert statements: %s", formatSql(combinedStatements));
    }

    private static String formatSql(String sql) {
        if (sql.length() > MAX_SQL_DISPLAY_LENGTH) {
            return sql.substring(0, MAX_SQL_DISPLAY_LENGTH) + "...";
        } else {
            return sql;
        }
    }
}
