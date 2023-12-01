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

package org.apache.paimon.web.gateway.provider;

import org.apache.paimon.web.common.executor.ExecutorFactory;
import org.apache.paimon.web.flink.executor.FlinkExecutorFactory;
import org.apache.paimon.web.flink.sql.gateway.executor.FlinkSqlGatewayExecutorFactory;
import org.apache.paimon.web.gateway.config.ExecuteConfig;
import org.apache.paimon.web.gateway.enums.TaskType;
import org.apache.paimon.web.spark.executor.SparkExecutorFactory;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.configuration.Configuration;

/**
 * ExecutorFactoryProvider is responsible for providing the appropriate ExecutorFactory based on the
 * given TaskType.
 */
public class ExecutorFactoryProvider {

    private final ExecuteConfig executeConfig;

    public ExecutorFactoryProvider(ExecuteConfig executeConfig) {
        this.executeConfig = executeConfig;
    }

    public ExecutorFactory getExecutorFactory(TaskType type) {
        switch (type) {
            case SPARK:
                return new SparkExecutorFactory();
            case FLINK:
                RuntimeExecutionMode mode =
                        executeConfig.isStreaming()
                                ? RuntimeExecutionMode.STREAMING
                                : RuntimeExecutionMode.BATCH;
                Configuration configuration = Configuration.fromMap(executeConfig.getConfig());
                return new FlinkExecutorFactory(mode, configuration);
            case FLINK_SQL_GATEWAY:
                return new FlinkSqlGatewayExecutorFactory(executeConfig.getSessionEntity());
            default:
                throw new IllegalArgumentException("Unsupported TaskType: " + type);
        }
    }
}
