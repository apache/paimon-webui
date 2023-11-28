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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  This class represents the result of a job.
 *  Including flink job and spark Job.
 */
public class SubmitResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String jobId;
    private final String status;
    private final List<Map<String, Object>> data;

    private SubmitResult(Builder builder) {
        this.jobId = builder.jobId;
        this.status = builder.status;
        this.data = builder.data;
    }

    public String getJobId() {
        return jobId;
    }

    public String getStatus() {
        return status;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** The builder for SubmitResult. */
    public static class Builder {
        private String jobId;
        private String status;
        private List<Map<String, Object>> data = new ArrayList<>();

        public Builder jobId(String jobId) {
            this.jobId = jobId;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder data(List<Map<String, Object>> data) {
            this.data = data;
            return this;
        }

        public Builder addData(Map<String, Object> dataItem) {
            this.data.add(dataItem);
            return this;
        }

        public SubmitResult build() {
            return new SubmitResult(this);
        }
    }

    @Override
    public String toString() {
        return "SubmitResult{" +
                "jobId='" + jobId + '\'' +
                ", status='" + status + '\'' +
                ", data=" + data +
                '}';
    }
}
