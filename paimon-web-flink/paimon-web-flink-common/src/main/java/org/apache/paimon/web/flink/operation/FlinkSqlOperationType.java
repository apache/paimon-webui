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

package org.apache.paimon.web.flink.operation;

/**
 * This enum represents the types of operations that can be performed in Flink SQL. It includes
 * operations like SELECT, CREATE, DROP, ALTER, INSERT, etc. Each operation type is associated with
 * a string value, and there are methods to compare the operation type with a string, check if the
 * operation type is INSERT, and get the operation type from a SQL statement.
 *
 * <p>Typically, you would use this enum to categorize a SQL statement and perform different actions
 * based on the operation type.
 */
public enum FlinkSqlOperationType {
    SELECT("SELECT"),
    CREATE("CREATE"),
    DROP("DROP"),
    ALTER("ALTER"),
    TRUNCATE("TRUNCATE"),
    INSERT("INSERT"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    DESC("DESC"),
    DESCRIBE("DESCRIBE"),
    EXPLAIN("EXPLAIN"),
    USE("USE"),
    SHOW("SHOW"),
    LOAD("LOAD"),
    UNLOAD("UNLOAD"),
    SET("SET"),
    CALL("CALL"),
    NO_SUPPORTED("NO_SUPPORTED");

    private String type;

    FlinkSqlOperationType(String type) {
        this.type = type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    /**
     * Gets the operation type from a SQL statement.
     *
     * @param sql the SQL statement
     * @return the operation type
     */
    public static FlinkSqlOperationType getOperationType(String sql) {
        String sqlTrim = sql.replaceAll("[\\s\\t\\n\\r]", "").trim().toUpperCase();
        FlinkSqlOperationType type = FlinkSqlOperationType.NO_SUPPORTED;
        for (FlinkSqlOperationType sqlType : FlinkSqlOperationType.values()) {
            if (sqlTrim.startsWith(sqlType.getType())) {
                type = sqlType;
                break;
            }
        }
        return type;
    }

    public SqlCategory getCategory() {
        switch (this) {
            case CREATE:
            case DROP:
            case ALTER:
            case TRUNCATE:
                return SqlCategory.DDL;
            case INSERT:
            case UPDATE:
            case DELETE:
                return SqlCategory.DML;
            case SELECT:
            case DESC:
            case DESCRIBE:
            case EXPLAIN:
            case SHOW:
            case CALL:
                return SqlCategory.DQL;
            case SET:
                return SqlCategory.SET;
            default:
                return SqlCategory.OTHER;
        }
    }
}
