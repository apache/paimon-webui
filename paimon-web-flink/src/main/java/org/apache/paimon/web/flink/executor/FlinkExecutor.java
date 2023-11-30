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

package org.apache.paimon.web.flink.executor;

import org.apache.paimon.web.common.executor.Executor;
import org.apache.paimon.web.common.result.SubmitResult;
import org.apache.paimon.web.flink.operation.FlinkSqlOperationType;
import org.apache.paimon.web.flink.operation.SqlCategory;
import org.apache.paimon.web.flink.parser.StatementParser;
import org.apache.paimon.web.flink.utils.CollectResultUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** The flink implementation of the {@link Executor}. */
public class FlinkExecutor implements Executor {

    private final StreamExecutionEnvironment env;
    private final TableEnvironment tableEnv;

    public FlinkExecutor(StreamExecutionEnvironment env, TableEnvironment tableEnv) {
        this.env = env;
        this.tableEnv = tableEnv;
    }

    @Override
    public SubmitResult executeSql(String multiStatement) throws Exception {
        List<String> insertStatements = new ArrayList<>();

        String[] statements = StatementParser.parse(multiStatement);
        for (String statement : statements) {
            FlinkSqlOperationType operationType = FlinkSqlOperationType.getOperationType(statement);
            if (operationType.getCategory() == SqlCategory.DQL) {
                if (!insertStatements.isEmpty()) {
                    continue;
                }
                TableResult tableResult = tableEnv.executeSql(statement);
                if (operationType.getType().equals(FlinkSqlOperationType.SELECT.getType())) {
                    SubmitResult.Builder builder = CollectResultUtil.collectResult(tableResult);
                    setResult(tableResult, builder);
                    return builder.build();
                } else {
                    return CollectResultUtil.collectResult(tableResult).build();
                }
            } else if (operationType.getCategory() == SqlCategory.DML) {
                if (operationType.getType().equals(FlinkSqlOperationType.INSERT.getType())) {
                    insertStatements.add(statement);
                } else {
                    if (!insertStatements.isEmpty()) {
                        continue;
                    }
                    TableResult tableResult = tableEnv.executeSql(statement);
                    SubmitResult.Builder builder = SubmitResult.builder();
                    setResult(tableResult, builder);
                    return builder.build();
                }
            } else {
                tableEnv.executeSql(statement);
            }
        }

        if (CollectionUtils.isNotEmpty(insertStatements)) {
            SubmitResult.Builder builder = SubmitResult.builder();
            if (insertStatements.size() > 1) {
                StatementSet statementSet = tableEnv.createStatementSet();
                insertStatements.forEach(statementSet::addInsertSql);
                TableResult tableResult = statementSet.execute();
                setResult(tableResult, builder);
                return builder.build();
            } else {
                TableResult tableResult = tableEnv.executeSql(insertStatements.get(0));
                setResult(tableResult, builder);
                return builder.build();
            }
        }

        return null;
    }

    private void setResult(TableResult tableResult, SubmitResult.Builder builder) throws Exception {
        Optional<JobClient> jobClient = tableResult.getJobClient();
        if (jobClient.isPresent()) {
            builder.jobId(jobClient.get().getJobID().toString());
            builder.status(jobClient.get().getJobStatus().get().toString());
        }
    }

    @Override
    public boolean stop(String jobId) throws Exception {
        // TODO
        return false;
    }
}
