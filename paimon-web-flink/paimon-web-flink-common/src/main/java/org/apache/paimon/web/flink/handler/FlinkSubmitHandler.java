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

package org.apache.paimon.web.flink.handler;

import org.apache.paimon.web.flink.common.ContextMode;
import org.apache.paimon.web.flink.common.SubmitMode;
import org.apache.paimon.web.flink.config.FlinkJobConfiguration;
import org.apache.paimon.web.flink.context.ApplicationExecutorContext;
import org.apache.paimon.web.flink.context.LocalExecutorContext;
import org.apache.paimon.web.flink.context.RemoteExecutorContext;
import org.apache.paimon.web.flink.context.params.RemoteParams;
import org.apache.paimon.web.flink.executor.ApplicationExecutorFactory;
import org.apache.paimon.web.flink.executor.Executor;
import org.apache.paimon.web.flink.executor.LocalExecutorFactory;
import org.apache.paimon.web.flink.executor.RemoteExecutorFactory;
import org.apache.paimon.web.flink.job.FlinkJobResult;
import org.apache.paimon.web.flink.operation.FlinkSqlOperationType;
import org.apache.paimon.web.flink.operation.SqlCategory;
import org.apache.paimon.web.flink.parser.StatementParser;
import org.apache.paimon.web.flink.submit.Submitter;
import org.apache.paimon.web.flink.submit.request.SubmitRequest;
import org.apache.paimon.web.flink.submit.result.SubmitResult;

import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.types.Row;
import org.apache.flink.util.CloseableIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * This class is responsible for handling SQL tasks in a Flink job. It takes SQL statements, parses
 * and executes them using a provided Executor instance, and returns the results in a FlinkJobResult
 * object.
 */
public class FlinkSubmitHandler {

    private Executor executor;
    private final FlinkJobConfiguration jobConfig;

    private FlinkSubmitHandler(FlinkJobConfiguration jobConfig) {
        this.jobConfig = jobConfig;
    }

    public static FlinkSubmitHandler build(FlinkJobConfiguration jobConfig) {
        return new FlinkSubmitHandler(jobConfig);
    }

    public void init() {
        executor = getExecutor();
    }

    public FlinkJobResult handleExecuteSql(String sql) {
        if (executor == null) {
            init();
        }

        String[] statements = StatementParser.parse(sql);

        return getContextMode() != ContextMode.APPLICATION
                ? handleNoneApplicationMode(statements)
                : handleApplicationMode(statements);
    }

    private FlinkJobResult handleApplicationMode(String[] statements) {
        FlinkJobResult result = new FlinkJobResult();
        SubmitRequest request = buildSubmitRequest();

        SubmitResult submitResult = Submitter.submit(request);
        result.setJobId(submitResult.getAppId());
        result.setJobIds(submitResult.getJobIds());
        result.setSuccess(submitResult.isSuccess());
        result.setJobManagerAddress(submitResult.getWebUrl());
        return result;
    }

    private FlinkJobResult handleNoneApplicationMode(String[] statements) {
        boolean hasExecuted = false;
        List<String> insertStatements = new ArrayList<>();
        FlinkJobResult result = new FlinkJobResult();
        try {
            for (String statement : statements) {
                FlinkSqlOperationType operationType =
                        FlinkSqlOperationType.getOperationType(statement);

                if (operationType.getCategory() == SqlCategory.SET) {
                    continue;
                }

                if (operationType.getCategory() == SqlCategory.DQL) {
                    TableResult tableResult = executor.executeSql(statement);
                    setResult(tableResult, result);
                    final FlinkJobResult currentResult = result;

                    CompletableFuture.runAsync(() -> collectData(tableResult, currentResult));
                    hasExecuted = true;
                    break;
                } else if (operationType.getCategory() == SqlCategory.DML) {
                    if (Objects.equals(
                            operationType.getType(), FlinkSqlOperationType.INSERT.getType())) {
                        insertStatements.add(statement);
                        if (!jobConfig.isUseStatementSet()) {
                            break;
                        }
                    } else if (Objects.equals(
                                    operationType.getType(), FlinkSqlOperationType.UPDATE.getType())
                            || Objects.equals(
                                    operationType.getType(),
                                    FlinkSqlOperationType.DELETE.getType())) {
                        TableResult tableResult = executor.executeSql(statement);
                        setResult(tableResult, result);

                        hasExecuted = true;
                        break;
                    }
                } else {
                    executor.executeSql(statement);
                }
            }

            if (!hasExecuted && CollectionUtils.isNotEmpty(insertStatements)) {
                TableResult tableResult;
                if (jobConfig.isUseStatementSet()) {
                    tableResult = executor.executeStatementSet(insertStatements);
                } else {
                    tableResult = executor.executeSql(insertStatements.get(0));
                }
                setResult(tableResult, result);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private void setResult(TableResult tableResult, FlinkJobResult result) throws Exception {
        Optional<JobClient> jobClient = tableResult.getJobClient();
        if (jobClient.isPresent()) {
            result.setJobId(jobClient.get().getJobID().toString());
            result.setStatus(jobClient.get().getJobStatus().get().toString());
        }
    }

    private void collectData(TableResult tableResult, FlinkJobResult result) {
        try (CloseableIterator<Row> iterator = tableResult.collect()) {
            while (iterator.hasNext()) {
                Row row = iterator.next();
                Map<String, Object> rowData = new HashMap<>();
                List<String> columnNames = tableResult.getResolvedSchema().getColumnNames();
                for (int i = 0; i < row.getArity(); i++) {
                    rowData.put(columnNames.get(i), row.getField(i));
                }
                result.setData(rowData);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SubmitRequest buildSubmitRequest() {
        SubmitRequest.Builder builder = SubmitRequest.builder();

        Optional.ofNullable(jobConfig.getFlinkConfigPath()).ifPresent(builder::flinkConfigPath);
        Optional.ofNullable(jobConfig.getTaskConfig()).ifPresent(builder::flinkConfigMap);
        Optional.ofNullable(jobConfig.getExecutionTarget())
                .map(SubmitMode::of)
                .ifPresent(builder::executionTarget);
        Optional.ofNullable(jobConfig.getSavepointPath()).ifPresent(builder::savepointPath);
        Optional.ofNullable(jobConfig.getCheckpointPath()).ifPresent(builder::checkpointPath);
        Optional.ofNullable(jobConfig.getCheckpointInterval())
                .ifPresent(builder::checkpointInterval);
        Optional.ofNullable(jobConfig.getFlinkLibPath()).ifPresent(builder::flinkLibPath);
        Optional.ofNullable(jobConfig.getJobName()).ifPresent(builder::jobName);
        Optional.ofNullable(jobConfig.getHadoopConfigPath()).ifPresent(builder::hadoopConfigPath);
        Optional.ofNullable(jobConfig.getUserJarPath()).ifPresent(builder::userJarPath);
        Optional.ofNullable(jobConfig.getUserJarParams()).ifPresent(builder::userJarParams);
        Optional.ofNullable(jobConfig.getUserJarMainAppClass())
                .ifPresent(builder::userJarMainAppClass);
        Optional.ofNullable(jobConfig.getJmMemory()).ifPresent(builder::jobManagerMemory);
        Optional.ofNullable(jobConfig.getTmMemory()).ifPresent(builder::taskManagerMemory);
        Optional.ofNullable(jobConfig.getTaskSlots()).ifPresent(builder::taskSlots);

        return builder.build();
    }

    private Executor getExecutor() {
        Configuration configuration;
        if (MapUtils.isNotEmpty(jobConfig.getTaskConfig())) {
            configuration = Configuration.fromMap(jobConfig.getTaskConfig());
        } else {
            configuration = new Configuration();
        }

        switch (getContextMode()) {
            case LOCAL:
                LocalExecutorContext localExecutorContext =
                        new LocalExecutorContext(configuration, jobConfig.getExecutionMode());
                return new LocalExecutorFactory().createExecutor(localExecutorContext);
            case REMOTE:
                RemoteParams remoteParams =
                        ArrayUtils.isNotEmpty(jobConfig.getJarFilePath())
                                ? new RemoteParams(
                                        jobConfig.getHost(),
                                        jobConfig.getPort(),
                                        jobConfig.getJarFilePath())
                                : new RemoteParams(jobConfig.getHost(), jobConfig.getPort());
                RemoteExecutorContext remoteExecutorContext =
                        new RemoteExecutorContext(
                                remoteParams, configuration, jobConfig.getExecutionMode());
                return new RemoteExecutorFactory().createExecutor(remoteExecutorContext);
            case APPLICATION:
                ApplicationExecutorContext applicationExecutorContext =
                        new ApplicationExecutorContext(configuration, jobConfig.getExecutionMode());
                return new ApplicationExecutorFactory().createExecutor(applicationExecutorContext);
            default:
                throw new UnsupportedOperationException("Unsupported execution context mode.");
        }
    }

    private ContextMode getContextMode() {
        SubmitMode mode = SubmitMode.of(jobConfig.getExecutionTarget());
        Preconditions.checkNotNull(mode, "execution target can not be null.");
        switch (mode) {
            case LOCAL:
                return ContextMode.LOCAL;
            case STANDALONE:
                return ContextMode.REMOTE;
            case YARN_PER_JOB:
            case YARN_SESSION:
            case YARN_APPLICATION:
            case KUBERNETES_SESSION:
            case KUBERNETES_APPLICATION:
                return ContextMode.APPLICATION;
            default:
                throw new UnsupportedOperationException(
                        "Unsupported execution target: " + mode.getName());
        }
    }
}
