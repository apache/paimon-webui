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

import org.apache.paimon.web.flink.executor.Executor;
import org.apache.paimon.web.flink.job.FlinkJobResult;
import org.apache.paimon.web.flink.operation.FlinkSqlOperationType;
import org.apache.paimon.web.flink.operation.SqlCategory;
import org.apache.paimon.web.flink.parser.StatementParser;

import org.apache.flink.core.execution.JobClient;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.types.Row;
import org.apache.flink.util.CloseableIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class SqlTaskHandler {

    private final Executor executor;

    public SqlTaskHandler(Executor executor) {
        this.executor = executor;
    }

    public FlinkJobResult handle(String sql) {
        String[] statements = StatementParser.parse(sql);
        boolean jobSubmitted = false;
        FlinkJobResult result = null;

        for (String statement : statements) {
            if (jobSubmitted) {
                break;
            }

            FlinkSqlOperationType operationType = FlinkSqlOperationType.getOperationType(statement);
            result = new FlinkJobResult();

            if (operationType.getCategory() == SqlCategory.DQL) {
                try {
                    TableResult tableResult = executor.executeSql(statement);
                    Optional<JobClient> jobClient = tableResult.getJobClient();
                    if (jobClient.isPresent()) {
                        result.setJobId(jobClient.get().getJobID().toString());
                        result.setStatus(jobClient.get().getJobStatus().get().toString());
                    }
                    final FlinkJobResult currentResult = result;
                    // result.setData(convertTableResultToData(tableResult));
                    CompletableFuture.runAsync(
                            () -> {
                                collectData(tableResult, currentResult);
                            });
                    jobSubmitted = true;
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else if (operationType.getCategory() == SqlCategory.DML) {
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
                    jobSubmitted = true;
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                executor.executeSql(statement);
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

    private List<Map<String, Object>> convertTableResultToData(TableResult tableResult) {
        List<Map<String, Object>> data = new ArrayList<>();
        try (CloseableIterator<Row> iterator = tableResult.collect()) {
            while (iterator.hasNext()) {
                Row row = iterator.next();
                Map<String, Object> rowData = new HashMap<>();
                List<String> columnNames = tableResult.getResolvedSchema().getColumnNames();
                for (int i = 0; i < row.getArity(); i++) {
                    rowData.put(columnNames.get(i), row.getField(i));
                }
                data.add(rowData);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return data;
    }
}
