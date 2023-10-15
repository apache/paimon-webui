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

import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
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
import java.util.concurrent.ExecutionException;

/**
 * This class is responsible for handling SQL tasks in a Flink job. It takes SQL statements, parses
 * and executes them using a provided Executor instance, and returns the results in a FlinkJobResult
 * object.
 *
 * <p>The handle method is the main entry point for executing SQL tasks. It parses the SQL
 * statements, determines the type of each statement (DQL or DML), and executes them accordingly.
 * DQL statements are executed asynchronously and the results are collected in a separate thread.
 * DML statements are executed synchronously and the job status is returned immediately.
 *
 * <p>The class also provides methods for collecting data from a TableResult (collectData) and
 * converting a TableResult into a List of Maps (convertTableResultToData).
 */
public class FlinkSubmitHandler {

    private Executor executor;
    private final FlinkJobConfiguration jobConfig;

    public FlinkSubmitHandler(FlinkJobConfiguration jobConfig) {
        this.jobConfig = jobConfig;
    }

    public static FlinkSubmitHandler build(FlinkJobConfiguration jobConfig) {
        return new FlinkSubmitHandler(jobConfig);
    }

    public void init() {
        executor = getExecutor();
    }

    public FlinkJobResult handle(String sql) {
        String[] statements = StatementParser.parse(sql);
        FlinkJobResult result = null;

        if (getContextMode() != ContextMode.APPLICATION) {
            result = handleNoneApplicationMode(statements);
        } else {

        }

        return result;
    }

    private FlinkJobResult handleNoneApplicationMode(String[] statements) {
        boolean hasExecuted = false;
        List<String> insertStatements = new ArrayList<>();
        FlinkJobResult result = new FlinkJobResult();
        for (String statement : statements) {
            FlinkSqlOperationType operationType = FlinkSqlOperationType.getOperationType(statement);

            if (operationType.getCategory() == SqlCategory.SET) {
                continue;
            }

            if (operationType.getCategory() == SqlCategory.DQL) {
                try {
                    TableResult tableResult = executor.executeSql(statement);
                    Optional<JobClient> jobClient = tableResult.getJobClient();
                    if (jobClient.isPresent()) {
                        result.setJobId(jobClient.get().getJobID().toString());
                        result.setStatus(jobClient.get().getJobStatus().get().toString());
                    }
                    final FlinkJobResult currentResult = result;

                    CompletableFuture.runAsync(
                            () -> {
                                collectData(tableResult, currentResult);
                            });
                    hasExecuted = true;
                    break;
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else if (operationType.getCategory() == SqlCategory.DML) {
                if (Objects.equals(operationType.getType(), FlinkSqlOperationType.INSERT.getType())) {
                    insertStatements.add(statement);
                    if (!jobConfig.isUseStatementSet()) {
                        break;
                    }
                } else if (Objects.equals(operationType.getType(), FlinkSqlOperationType.UPDATE.getType()) ||
                        Objects.equals(operationType.getType(), FlinkSqlOperationType.DELETE.getType())) {
                    try {
                        JobClient jobClient =
                                executor.executeSql(statement)
                                        .getJobClient()
                                        .orElseThrow(
                                                () ->
                                                        new RuntimeException(
                                                                "JobClient is not present."));
                        result.setJobId(jobClient.getJobID().toString());
                        result.setStatus(jobClient.getJobStatus().get().toString());
                        hasExecuted = true;
                        break;
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                executor.executeSql(statement);
            }
        }

        if (!hasExecuted && CollectionUtils.isNotEmpty(insertStatements)) {
            if (jobConfig.isUseStatementSet()) {
                executor.executeStatementSet(insertStatements);
            } else {
                executor.executeSql(insertStatements.get(0));
            }
        }

        return result;
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
                result.addData(rowData);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Executor getExecutor() {
        Configuration configuration;
        if (MapUtils.isNotEmpty(jobConfig.getTaskConfig())) {
            configuration = Configuration.fromMap(jobConfig.getTaskConfig());
        }else {
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
            case YARN_PRE_JOB:
            case YARN_SESSION:
            case YARN_APPLICATION:
            case KUBERNETES_SESSION:
            case KUBERNETES_APPLICATION:
                return ContextMode.APPLICATION;
            default:
                throw new UnsupportedOperationException("Unsupported execution target: " + mode.getName());
        }
    }
}
