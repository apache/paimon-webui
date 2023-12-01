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

package org.apache.paimon.web.gateway.config;

import org.apache.paimon.web.flink.sql.gateway.model.SessionEntity;

import java.util.HashMap;
import java.util.Map;

/** Execution config. */
public class ExecuteConfig {

    private final boolean isStreaming;
    private final SessionEntity sessionEntity;
    private final Map<String, String> config;

    private ExecuteConfig(Builder builder) {
        this.isStreaming = builder.isStreaming;
        this.sessionEntity = builder.sessionEntity;
        this.config = builder.config;
    }

    public boolean isStreaming() {
        return isStreaming;
    }

    public SessionEntity getSessionEntity() {
        return sessionEntity;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** The builder for ExecuteConfig. */
    public static class Builder {
        private boolean isStreaming;
        private SessionEntity sessionEntity;
        private Map<String, String> config = new HashMap<>();

        public Builder setStreaming(boolean isStreaming) {
            this.isStreaming = isStreaming;
            return this;
        }

        public Builder setSessionEntity(SessionEntity sessionEntity) {
            this.sessionEntity = sessionEntity;
            return this;
        }

        public Builder setConfig(Map<String, String> config) {
            this.config = config;
            return this;
        }

        public ExecuteConfig build() {
            return new ExecuteConfig(this);
        }
    }
}
