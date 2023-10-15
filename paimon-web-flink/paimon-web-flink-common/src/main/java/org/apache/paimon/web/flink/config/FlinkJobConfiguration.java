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
import org.apache.paimon.web.flink.common.ExecutionMode;

import java.util.Map;

/** FlinkJobConfiguration is a class that holds the configuration for a Flink job. */
public class FlinkJobConfiguration {

    private String executionTarget;
    private ExecutionMode executionMode;
    private ContextMode contextMode;
    private String host;
    private int port;
    private String[] jarFilePath;
    private boolean isUseStatementSet;
    private Map<String, String> taskConfig;

    public ExecutionMode getExecutionMode() {
        return executionMode;
    }

    public void setExecutionMode(ExecutionMode executionMode) {
        this.executionMode = executionMode;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String[] getJarFilePath() {
        return jarFilePath;
    }

    public void setJarFilePath(String[] jarFilePath) {
        this.jarFilePath = jarFilePath;
    }

    public Map<String, String> getTaskConfig() {
        return taskConfig;
    }

    public void setTaskConfig(Map<String, String> taskConfig) {
        this.taskConfig = taskConfig;
    }

    public String getExecutionTarget() {
        return executionTarget;
    }

    public void setExecutionTarget(String executionTarget) {
        this.executionTarget = executionTarget;
    }

    public boolean isUseStatementSet() {
        return isUseStatementSet;
    }

    public void setUseStatementSet(boolean useStatementSet) {
        isUseStatementSet = useStatementSet;
    }

    public ContextMode getContextMode() {
        return contextMode;
    }

    public void setContextMode(ContextMode contextMode) {
        this.contextMode = contextMode;
    }
}
