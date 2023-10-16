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

package org.apache.paimon.web.flink.submit.result;

import lombok.Builder;

import java.util.List;

/** Submit the returned result of the flink job. */
public class SubmitResult {

    private boolean isSuccess;
    private String msg;
    private String appId;
    private String webUrl;
    private List<String> jobIds;

    private SubmitResult(Builder builder) {
        this.isSuccess = builder.isSuccess;
        this.msg = builder.msg;
        this.appId = builder.appId;
        this.webUrl = builder.webUrl;
        this.jobIds = builder.jobIds;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getMsg() {
        return msg;
    }

    public String getAppId() {
        return appId;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public List<String> getJobIds() {
        return jobIds;
    }

    public static SubmitResult.Builder builder() {
        return new SubmitResult.Builder();
    }

    /** The builder for SubmitResult. */
    public static class Builder {
        private boolean isSuccess;
        private String msg;
        private String appId;
        private String webUrl;
        private List<String> jobIds;

        public Builder isSuccess(boolean isSuccess) {
            this.isSuccess = isSuccess;
            return this;
        }

        public Builder msg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder appId(String appId) {
            this.appId = appId;
            return this;
        }

        public Builder webUrl(String webUrl) {
            this.webUrl = webUrl;
            return this;
        }

        public Builder jobIds(List<String> jobIds) {
            this.jobIds = jobIds;
            return this;
        }

        public SubmitResult build() {
            return new SubmitResult(this);
        }
    }
}
