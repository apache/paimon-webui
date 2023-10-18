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

package org.apache.paimon.web.flink.app;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.paimon.web.flink.common.ExecutionMode;
import org.apache.paimon.web.flink.config.DBConfig;
import org.apache.paimon.web.flink.config.FlinkJobConfiguration;
import org.apache.paimon.web.flink.context.ApplicationExecutorContext;
import org.apache.paimon.web.flink.executor.ApplicationExecutorFactory;
import org.apache.paimon.web.flink.executor.Executor;
import org.apache.paimon.web.flink.operation.FlinkSqlOperationType;
import org.apache.paimon.web.flink.operation.SqlCategory;
import org.apache.paimon.web.flink.parser.StatementParser;
import org.apache.paimon.web.flink.utils.FlinkJobConfUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/** User jar package startup class. */
public class MainApp {

    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) {
        InputStream inputStream =
                MainApp.class.getClassLoader().getResourceAsStream("application.properties");
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
            DBConfig dbConfig = DBConfig.build(properties);
            Integer id = Integer.valueOf(args[0]);
            Map<String, String> taskConfig = FlinkJobConfUtil.getJobTaskConfig(id, dbConfig);
            if (MapUtils.isEmpty(taskConfig)) {
                logger.error(
                        "get flink job task info error! job info is Empty! Check whether the flink job task with the id {} exists",
                        id);
                return;
            }

            FlinkJobConfiguration.Builder builder = FlinkJobConfiguration.builder();
            Map<String, String> configMap = new HashMap<>();
            String jobName = taskConfig.get("job_name");
            String checkpointPath = taskConfig.get("checkpoint_path");
            if (StringUtils.isNotBlank(checkpointPath)) {
                // enable Checkpoint
                configMap.put("execution.checkpointing.enabled", "true");
                // Set the default Checkpoint interval to 10 minute
                String interval = taskConfig.get("checkpoint_interval");
                if (StringUtils.isBlank(interval)) {
                    interval = "600000";
                }
                configMap.put("execution.checkpointing.interval", interval);
                configMap.put(CheckpointingOptions.CHECKPOINTS_DIRECTORY.key(), checkpointPath);
            }

            String savepointPath = taskConfig.get("savepoint_path");
            if (StringUtils.isNotBlank(savepointPath)) {
                configMap.put(CheckpointingOptions.SAVEPOINT_DIRECTORY.key(), savepointPath);
            }

            String parallelism = taskConfig.get("parallelism");
            if (StringUtils.isBlank(parallelism)) {
                parallelism = "1";
            }
            configMap.put("parallelism.default", parallelism);

            String runtimeMode = taskConfig.get("execution_runtime_mode");
            if (runtimeMode != null
                    && runtimeMode.equalsIgnoreCase(RuntimeExecutionMode.BATCH.toString())) {
                builder.executionMode(ExecutionMode.BATCH);
            } else {
                builder.executionMode(ExecutionMode.STREAMING);
            }

            Configuration configuration = Configuration.fromMap(configMap);
            ApplicationExecutorContext applicationExecutorContext =
                    new ApplicationExecutorContext(
                            configuration, builder.build().getExecutionMode());
            Executor executor =
                    new ApplicationExecutorFactory().createExecutor(applicationExecutorContext);

            String flinkSql = taskConfig.get("flink_sql");
            String[] statements = StatementParser.parse(flinkSql);
            boolean hasExecuted = false;
            List<String> insertStatements = new ArrayList<>();

            for (String statement : statements) {
                FlinkSqlOperationType operationType =
                        FlinkSqlOperationType.getOperationType(statement);

                if (operationType.getCategory() == SqlCategory.SET) {
                    continue;
                }

                if (operationType.getCategory() == SqlCategory.DQL) {
                    executor.executeSql(statement);
                    hasExecuted = true;
                    break;
                } else if (operationType.getCategory() == SqlCategory.DML) {
                    if (Objects.equals(
                            operationType.getType(), FlinkSqlOperationType.INSERT.getType())) {
                        insertStatements.add(statement);
                        if (!builder.build().isUseStatementSet()) {
                            break;
                        }
                    } else if (Objects.equals(
                                    operationType.getType(), FlinkSqlOperationType.UPDATE.getType())
                            || Objects.equals(
                                    operationType.getType(),
                                    FlinkSqlOperationType.DELETE.getType())) {
                        executor.executeSql(statement);
                        hasExecuted = true;
                        break;
                    } else {
                        executor.executeSql(statement);
                    }
                }
            }

            if (!hasExecuted && CollectionUtils.isNotEmpty(insertStatements)) {
                if (builder.build().isUseStatementSet()) {
                    executor.executeStatementSet(insertStatements);
                } else {
                    executor.executeSql(insertStatements.get(0));
                }
            }

            applicationExecutorContext.getEnvironment().execute(jobName);
        } catch (Exception e) {
            logger.error("org.apache.paimon.web.flink.app.MainApp run error:", e);
        }
    }
}
