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

package org.apache.paimon.web.common.result;

/** Represents the parameters required to fetch the results of a certain operation. */
public class FetchResultParams {

    private final String sessionId;
    private final String submitId;
    private final Long token;

    private FetchResultParams(Builder builder) {
        this.sessionId = builder.sessionId;
        this.submitId = builder.submitId;
        this.token = builder.token;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getSubmitId() {
        return submitId;
    }

    public Long getToken() {
        return token;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** The builder for FetchResultParams. */
    public static class Builder {
        private String sessionId;
        private String submitId;
        private Long token;

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder submitId(String submitId) {
            this.submitId = submitId;
            return this;
        }

        public Builder token(Long token) {
            this.token = token;
            return this;
        }

        public FetchResultParams build() {
            return new FetchResultParams(this);
        }
    }
}
