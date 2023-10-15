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

package org.apache.paimon.web.flink.submit.request;

import org.apache.paimon.web.flink.common.SubmitMode;

import java.util.Map;

/** Represents a submit request for a job. */
public class SubmitRequest {

    private final String flinkConfigPath;
    private final Map<String, String> flinkConfigMap;
    private final SubmitMode executionTarget;
    private final String savepointPath;
    private final String checkpointPath;
    private final String checkpointInterval;
    private final String flinkLibPath;
    private final String jobName;
    private final String hadoopConfigPath;
    private final String userJarPath;
    private final String userJarParams;
    private final String userJarMainAppClass;
    private final String jobManagerMemory;
    private final String taskManagerMemory;
    private final Integer taskSlots;

    private SubmitRequest(Builder builder) {
        this.flinkConfigPath = builder.flinkConfigPath;
        this.flinkConfigMap = builder.flinkConfigMap;
        this.executionTarget = builder.executionTarget;
        this.savepointPath = builder.savepointPath;
        this.checkpointPath = builder.checkpointPath;
        this.checkpointInterval = builder.checkpointInterval;
        this.flinkLibPath = builder.flinkLibPath;
        this.jobName = builder.jobName;
        this.hadoopConfigPath = builder.hadoopConfigPath;
        this.userJarPath = builder.userJarPath;
        this.userJarParams = builder.userJarParams;
        this.userJarMainAppClass = builder.userJarMainAppClass;
        this.jobManagerMemory = builder.jobManagerMemory;
        this.taskManagerMemory = builder.taskManagerMemory;
        this.taskSlots = builder.taskSlots;
    }

    public String getFlinkConfigPath() {
        return flinkConfigPath;
    }

    public Map<String, String> getFlinkConfigMap() {
        return flinkConfigMap;
    }

    public String getExecutionTarget() {
        return executionTarget.getName();
    }

    public String getSavepointPath() {
        return savepointPath;
    }

    public String getCheckpointPath() {
        return checkpointPath;
    }

    public String getCheckpointInterval() {
        return checkpointInterval;
    }

    public String getFlinkLibPath() {
        return flinkLibPath;
    }

    public String getJobName() {
        return jobName;
    }

    public String getHadoopConfigPath() {
        return hadoopConfigPath;
    }

    public String getUserJarPath() {
        return userJarPath;
    }

    public String getUserJarParams() {
        return userJarParams;
    }

    public String getUserJarMainAppClass() {
        return userJarMainAppClass;
    }

    public String getJobManagerMemory() {
        return jobManagerMemory;
    }

    public String getTaskManagerMemory() {
        return taskManagerMemory;
    }

    public Integer getTaskSlots() {
        return taskSlots;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Builder. */
    public static class Builder {

        private String flinkConfigPath;
        private Map<String, String> flinkConfigMap;
        private SubmitMode executionTarget;
        private String savepointPath;
        private String checkpointPath;
        private String checkpointInterval;
        private String flinkLibPath;
        private String jobName;
        private String hadoopConfigPath;
        private String userJarPath;
        private String userJarParams;
        private String userJarMainAppClass;
        private String jobManagerMemory;
        private String taskManagerMemory;
        private int taskSlots;

        public Builder flinkConfigPath(String flinkConfigPath) {
            this.flinkConfigPath = flinkConfigPath;
            return this;
        }

        public Builder flinkConfigMap(Map<String, String> flinkConfigMap) {
            this.flinkConfigMap = flinkConfigMap;
            return this;
        }

        public Builder executionTarget(SubmitMode executionTarget) {
            this.executionTarget = executionTarget;
            return this;
        }

        public Builder savepointPath(String savepointPath) {
            this.savepointPath = savepointPath;
            return this;
        }

        public Builder checkpointPath(String checkpointPath) {
            this.checkpointPath = checkpointPath;
            return this;
        }

        public Builder checkpointInterval(String checkpointInterval) {
            this.checkpointInterval = checkpointInterval;
            return this;
        }

        public Builder flinkLibPath(String flinkLibPath) {
            this.flinkLibPath = flinkLibPath;
            return this;
        }

        public Builder jobName(String jobName) {
            this.jobName = jobName;
            return this;
        }

        public Builder hadoopConfigPath(String hadoopConfigPath) {
            this.hadoopConfigPath = hadoopConfigPath;
            return this;
        }

        public Builder userJarPath(String userJarPath) {
            this.userJarPath = userJarPath;
            return this;
        }

        public Builder userJarParams(String userJarParams) {
            this.userJarParams = userJarParams;
            return this;
        }

        public Builder userJarMainAppClass(String userJarMainAppClass) {
            this.userJarMainAppClass = userJarMainAppClass;
            return this;
        }

        public Builder jobManagerMemory(String jobManagerMemory) {
            this.jobManagerMemory = jobManagerMemory;
            return this;
        }

        public Builder taskManagerMemory(String taskManagerMemory) {
            this.taskManagerMemory = taskManagerMemory;
            return this;
        }

        public Builder taskSlots(int taskSlots) {
            this.taskSlots = taskSlots;
            return this;
        }

        public SubmitRequest build() {
            return new SubmitRequest(this);
        }
    }
}
