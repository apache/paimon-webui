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

package org.apache.paimon.web.engine.flink.sql.gateway.model;

import java.util.Map;

/** The session entity. */
public class SessionEntity {

    private final String sessionId;
    private final String sessionName;
    private final String host;
    private final int port;
    private final Map<String, String> properties;
    private final int status;

    private SessionEntity(String sessionId, String sessionName, String host, int port, Map<String, String> properties, int status) {
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.host = host;
        this.port = port;
        this.properties = properties;
        this.status = status;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public String getHost() {
        return host;
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

    /** The builder for {@link SessionEntity}. */
    public static class Builder {
        private String sessionId;
        private String sessionName;
        private String host;
        private int port;
        private Map<String, String> properties;
        private int status;

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder sessionName(String sessionName) {
            this.sessionName = sessionName;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder properties(Map<String, String> properties) {
            this.properties = properties;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public SessionEntity build() {
            return new SessionEntity(sessionId, sessionName, host, port, properties, status);
        }
    }
}
