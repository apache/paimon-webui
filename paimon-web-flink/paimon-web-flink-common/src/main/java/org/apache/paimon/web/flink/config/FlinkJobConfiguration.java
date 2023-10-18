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

package org.apache.paimon.web.flink.config;

import org.apache.paimon.web.flink.common.ContextMode;

import org.apache.flink.api.common.RuntimeExecutionMode;

import java.util.Map;

/** FlinkJobConfiguration is a class that holds the configuration for a Flink job. */
public class FlinkJobConfiguration {

    private final String executionTarget;
    private final RuntimeExecutionMode executionMode;
    private final ContextMode contextMode;
    private final String host;
    private final Integer port;
    private final String[] jarFilePath;
    private final boolean isUseStatementSet;
    private final Map<String, String> taskConfig;
    private final String jobName;
    private final String jmMemory;
    private final String tmMemory;
    private final Integer parallelism;
    private final String flinkVersion;
    private final String flinkConfigPath;
    private final String hadoopConfigPath;
    private final String checkpointPath;
    private final String checkpointInterval;
    private final String savepointPath;
    private final String userJarPath;
    private final String userJarMainAppClass;
    private final String flinkLibPath;
    private final String jobId;
    private final String applicationId;
    private final String flinkWebUrl;
    private final String jobStatus;
    private final String adminUser;
    private final String otherParams;
    private final String flinkSql;
    private final String userJarParams;
    private final Integer taskSlots;

    private FlinkJobConfiguration(Builder builder) {
        this.executionTarget = builder.executionTarget;
        this.executionMode = builder.executionMode;
        this.contextMode = builder.contextMode;
        this.host = builder.host;
        this.port = builder.port;
        this.jarFilePath = builder.jarFilePath;
        this.isUseStatementSet = builder.isUseStatementSet;
        this.taskConfig = builder.taskConfig;
        this.jobName = builder.jobName;
        this.jmMemory = builder.jmMemory;
        this.tmMemory = builder.tmMemory;
        this.parallelism = builder.parallelism;
        this.flinkVersion = builder.flinkVersion;
        this.flinkConfigPath = builder.flinkConfigPath;
        this.hadoopConfigPath = builder.hadoopConfigPath;
        this.checkpointPath = builder.checkpointPath;
        this.checkpointInterval = builder.checkpointInterval;
        this.savepointPath = builder.savepointPath;
        this.userJarPath = builder.userJarPath;
        this.userJarMainAppClass = builder.userJarMainAppClass;
        this.flinkLibPath = builder.flinkLibPath;
        this.jobId = builder.jobId;
        this.applicationId = builder.applicationId;
        this.flinkWebUrl = builder.flinkWebUrl;
        this.jobStatus = builder.jobStatus;
        this.adminUser = builder.adminUser;
        this.otherParams = builder.otherParams;
        this.flinkSql = builder.flinkSql;
        this.userJarParams = builder.userJarParams;
        this.taskSlots = builder.taskSlots;
    }

    public String getExecutionTarget() {
        return executionTarget;
    }

    public RuntimeExecutionMode getExecutionMode() {
        return executionMode;
    }

    public ContextMode getContextMode() {
        return contextMode;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String[] getJarFilePath() {
        return jarFilePath;
    }

    public boolean isUseStatementSet() {
        return isUseStatementSet;
    }

    public Map<String, String> getTaskConfig() {
        return taskConfig;
    }

    public String getJobName() {
        return jobName;
    }

    public String getJmMemory() {
        return jmMemory;
    }

    public String getTmMemory() {
        return tmMemory;
    }

    public Integer getParallelism() {
        return parallelism;
    }

    public String getFlinkVersion() {
        return flinkVersion;
    }

    public String getFlinkConfigPath() {
        return flinkConfigPath;
    }

    public String getHadoopConfigPath() {
        return hadoopConfigPath;
    }

    public String getCheckpointPath() {
        return checkpointPath;
    }

    public String getCheckpointInterval() {
        return checkpointInterval;
    }

    public String getSavepointPath() {
        return savepointPath;
    }

    public String getUserJarPath() {
        return userJarPath;
    }

    public String getUserJarMainAppClass() {
        return userJarMainAppClass;
    }

    public String getFlinkLibPath() {
        return flinkLibPath;
    }

    public String getJobId() {
        return jobId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getFlinkWebUrl() {
        return flinkWebUrl;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public String getAdminUser() {
        return adminUser;
    }

    public String getOtherParams() {
        return otherParams;
    }

    public String getFlinkSql() {
        return flinkSql;
    }

    public String getUserJarParams() {
        return userJarParams;
    }

    public Integer getTaskSlots() {
        return taskSlots;
    }

    public static FlinkJobConfiguration.Builder builder() {
        return new FlinkJobConfiguration.Builder();
    }

    /** The builder for FlinkJobConfiguration. */
    public static class Builder {
        private String executionTarget;
        private RuntimeExecutionMode executionMode;
        private ContextMode contextMode;
        private String host;
        private Integer port;
        private String[] jarFilePath;
        private boolean isUseStatementSet;
        private Map<String, String> taskConfig;
        private String jobName;
        private String jmMemory;
        private String tmMemory;
        private Integer parallelism;
        private String flinkVersion;
        private String flinkConfigPath;
        private String hadoopConfigPath;
        private String checkpointPath;
        private String checkpointInterval;
        private String savepointPath;
        private String userJarPath;
        private String userJarMainAppClass;
        private String flinkLibPath;
        private String jobId;
        private String applicationId;
        private String flinkWebUrl;
        private String jobStatus;
        private String adminUser;
        private String otherParams;
        private String flinkSql;
        private String userJarParams;
        private Integer taskSlots;

        public Builder executionTarget(String executionTarget) {
            this.executionTarget = executionTarget;
            return this;
        }

        public Builder executionMode(RuntimeExecutionMode executionMode) {
            this.executionMode = executionMode;
            return this;
        }

        public Builder contextMode(ContextMode contextMode) {
            this.contextMode = contextMode;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(Integer port) {
            this.port = port;
            return this;
        }

        public Builder jarFilePath(String[] jarFilePath) {
            this.jarFilePath = jarFilePath;
            return this;
        }

        public Builder isUseStatementSet(boolean isUseStatementSet) {
            this.isUseStatementSet = isUseStatementSet;
            return this;
        }

        public Builder taskConfig(Map<String, String> taskConfig) {
            this.taskConfig = taskConfig;
            return this;
        }

        public Builder jobName(String jobName) {
            this.jobName = jobName;
            return this;
        }

        public Builder jmMemory(String jmMemory) {
            this.jmMemory = jmMemory;
            return this;
        }

        public Builder tmMemory(String tmMemory) {
            this.tmMemory = tmMemory;
            return this;
        }

        public Builder parallelism(Integer parallelism) {
            this.parallelism = parallelism;
            return this;
        }

        public Builder flinkVersion(String flinkVersion) {
            this.flinkVersion = flinkVersion;
            return this;
        }

        public Builder flinkConfigPath(String flinkConfigPath) {
            this.flinkConfigPath = flinkConfigPath;
            return this;
        }

        public Builder hadoopConfigPath(String hadoopConfigPath) {
            this.hadoopConfigPath = hadoopConfigPath;
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

        public Builder savepointPath(String savepointPath) {
            this.savepointPath = savepointPath;
            return this;
        }

        public Builder userJarPath(String userJarPath) {
            this.userJarPath = userJarPath;
            return this;
        }

        public Builder userJarMainAppClass(String userJarMainAppClass) {
            this.userJarMainAppClass = userJarMainAppClass;
            return this;
        }

        public Builder flinkLibPath(String flinkLibPath) {
            this.flinkLibPath = flinkLibPath;
            return this;
        }

        public Builder jobId(String jobId) {
            this.jobId = jobId;
            return this;
        }

        public Builder applicationId(String applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        public Builder flinkWebUrl(String flinkWebUrl) {
            this.flinkWebUrl = flinkWebUrl;
            return this;
        }

        public Builder jobStatus(String jobStatus) {
            this.jobStatus = jobStatus;
            return this;
        }

        public Builder adminUser(String adminUser) {
            this.adminUser = adminUser;
            return this;
        }

        public Builder otherParams(String otherParams) {
            this.otherParams = otherParams;
            return this;
        }

        public Builder flinkSql(String flinkSql) {
            this.flinkSql = flinkSql;
            return this;
        }

        public Builder userJarParams(String userJarParams) {
            this.userJarParams = userJarParams;
            return this;
        }

        public Builder taskSlots(Integer taskSlots) {
            this.taskSlots = taskSlots;
            return this;
        }

        public FlinkJobConfiguration build() {
            return new FlinkJobConfiguration(this);
        }
    }
}
