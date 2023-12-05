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

package org.apache.paimon.web.common.status;

/** Represents the various states that a job, such as Flink or Spark. */
public enum JobStatus {
    CREATED("CREATED"),
    RUNNING("RUNNING"),
    FINISHED("FINISHED"),
    FAILED("FAILED"),
    CANCELED("CANCELED");

    private final String value;

    public static JobStatus fromValue(String value) {
        for (JobStatus type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown JobStatus value: " + value);
    }

    JobStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
