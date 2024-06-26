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

package org.apache.paimon.web.gateway.enums;

/** The {@code EngineType} enum defines the types of engines that can be supported. */
public enum EngineType {
    SPARK,
    FLINK_SQL_GATEWAY,
    FLINK_SESSION;

    public static EngineType fromName(String name) {
        for (EngineType type : values()) {
            if (type.name().equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown engine type value: " + name);
    }
    public static EngineType format(String name) {
        if (name == null || name.isEmpty()) {
            throw  new IllegalArgumentException("The engine type value is null");
        }
        String trimmed = name.trim();
        return fromName(trimmed.replaceAll("\\s+", "_"));
    }
}
