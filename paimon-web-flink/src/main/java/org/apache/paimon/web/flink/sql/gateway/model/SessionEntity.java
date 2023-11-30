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

package org.apache.paimon.web.flink.sql.gateway.model;

import java.util.Map;

/** The session entity */
public class SessionEntity {

    private final String sessionId;
    private final String sessionName;
    private final String address;
    private final Integer port;
    private final Map<String, String> properties;
    private final Integer status;

    private SessionEntity(Builder builder) {
        this.sessionId = builder.sessionId;
        this.sessionName = builder.sessionName;
        this.address = builder.address;
        this.port = builder.port;
        this.properties = builder.properties;
        this.status = builder.status;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public String getAddress() {
        return address;
    }

    public Integer getPort() {
        return port;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public Integer getStatus() {
        return status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String sessionId;
        private String sessionName;
        private String address;
        private Integer port;
        private Map<String, String> properties;
        private Integer status;

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder sessionName(String sessionName) {
            this.sessionName = sessionName;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder port(Integer port) {
            this.port = port;
            return this;
        }

        public Builder properties(Map<String, String> properties) {
            this.properties = properties;
            return this;
        }

        public Builder status(Integer status) {
            this.status = status;
            return this;
        }

        public SessionEntity build() {
            return new SessionEntity(this);
        }
    }
}
