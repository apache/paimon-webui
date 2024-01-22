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

package org.apache.paimon.web.engine.flink.sql.gateway.executor;

import org.apache.paimon.web.engine.flink.common.executor.Executor;
import org.apache.paimon.web.engine.flink.common.operation.FlinkSqlOperationType;
import org.apache.paimon.web.engine.flink.common.parser.StatementParser;
import org.apache.paimon.web.engine.flink.common.result.ExecutionResult;
import org.apache.paimon.web.engine.flink.common.result.FetchResultParams;
import org.apache.paimon.web.engine.flink.sql.gateway.client.SqlGatewayClient;
import org.apache.paimon.web.engine.flink.sql.gateway.model.SessionEntity;
import org.apache.paimon.web.engine.flink.sql.gateway.utils.CollectResultUtil;
import org.apache.paimon.web.engine.flink.sql.gateway.utils.FlinkSqlStatementSetBuilder;
import org.apache.paimon.web.engine.flink.sql.gateway.utils.FormatSqlExceptionUtil;

import org.apache.flink.table.gateway.api.results.ResultSet;
import org.apache.flink.table.gateway.rest.message.statement.FetchResultsResponseBody;
import org.apache.flink.table.gateway.service.utils.SqlExecutionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** The flink sql gateway implementation of the {@link Executor}. */
public class FlinkSqlGatewayExecutor implements Executor {

    private static final Long DEFAULT_FETCH_TOKEN = 0L;
    private static final String STOP_JOB_BASE_SQL = "STOP JOB '%s'";
    private static final String WITH_SAVEPOINT = " WITH SAVEPOINT";

    private final SqlGatewayClient client;
    private final SessionEntity session;

    public FlinkSqlGatewayExecutor(SessionEntity session) throws Exception {
        this.session = session;
        this.client = new SqlGatewayClient(session.getHost(), session.getPort());
    }

    @Override
    public ExecutionResult executeSql(String multiStatement) throws SqlExecutionException {
        String[] statements = StatementParser.parse(multiStatement);
        List<String> insertStatements = new ArrayList<>();
        ExecutionResult executionResult = null;

        for (String statement : statements) {
            FlinkSqlOperationType operationType = FlinkSqlOperationType.getOperationType(statement);

            switch (Objects.requireNonNull(operationType).getCategory()) {
                case DQL:
                    if (!insertStatements.isEmpty()) {
                        throw new SqlExecutionException(
                                "Cannot execute DQL statement with pending INSERT statements.");
                    }
                    executionResult = executeDqlStatement(statement, operationType);
                    break;
                case DML:
                    if (operationType.getType().equals(FlinkSqlOperationType.INSERT.getType())) {
                        insertStatements.add(statement);
                    } else {
                        executionResult = executeDmlStatement(statement);
                    }
                    break;
                default:
                    executeStatement(statement);
                    break;
            }

            if (executionResult != null) {
                return executionResult;
            }
        }

        if (!insertStatements.isEmpty()) {
            String combinedStatement =
                    FlinkSqlStatementSetBuilder.buildStatementSet(insertStatements);
            executionResult = executeDmlStatement(combinedStatement);
        }

        return executionResult;
    }

    private ExecutionResult executeDqlStatement(
            String statement, FlinkSqlOperationType operationType) throws SqlExecutionException {
        try {
            String operationId = client.executeStatement(session.getSessionId(), statement, null);
            FetchResultsResponseBody results =
                    client.fetchResults(session.getSessionId(), operationId, DEFAULT_FETCH_TOKEN);
            ExecutionResult.Builder builder =
                    CollectResultUtil.collectSqlGatewayResult(results.getResults())
                            .submitId(operationId);
            if (operationType.getType().equals(FlinkSqlOperationType.SELECT.getType())) {
                builder.jobId(getJobIdFromResults(results)).shouldFetchResult(true);
            }
            return builder.build();
        } catch (Exception e) {
            String errorMessage = FormatSqlExceptionUtil.formatSqlExceptionMessage(statement);
            throw new SqlExecutionException(errorMessage, e);
        }
    }

    private ExecutionResult executeDmlStatement(String statement) throws SqlExecutionException {
        try {
            String operationId = client.executeStatement(session.getSessionId(), statement, null);
            FetchResultsResponseBody results =
                    client.fetchResults(session.getSessionId(), operationId, DEFAULT_FETCH_TOKEN);
            return new ExecutionResult.Builder()
                    .submitId(operationId)
                    .jobId(getJobIdFromResults(results))
                    .build();
        } catch (Exception e) {
            String errorMessage = FormatSqlExceptionUtil.formatSqlExceptionMessage(statement);
            throw new SqlExecutionException(errorMessage, e);
        }
    }

    private void executeStatement(String statement) throws SqlExecutionException {
        try {
            client.executeStatement(session.getSessionId(), statement, null);
        } catch (Exception e) {
            String errorMessage = FormatSqlExceptionUtil.formatSqlExceptionMessage(statement);
            throw new SqlExecutionException(errorMessage, e);
        }
    }

    private String getJobIdFromResults(FetchResultsResponseBody results) {
        return Objects.requireNonNull(results.getJobID(), "Job ID not found in results").toString();
    }

    @Override
    public ExecutionResult fetchResults(FetchResultParams params) throws Exception {
        FetchResultsResponseBody fetchResultsResponseBody =
                client.fetchResults(params.getSessionId(), params.getSubmitId(), params.getToken());
        ResultSet.ResultType resultType = fetchResultsResponseBody.getResultType();
        if (resultType == ResultSet.ResultType.EOS) {
            return ExecutionResult.builder().shouldFetchResult(false).build();
        }
        ExecutionResult.Builder builder =
                CollectResultUtil.collectSqlGatewayResult(fetchResultsResponseBody.getResults());
        builder.submitId(params.getSubmitId());
        return builder.build();
    }

    @Override
    public void stop(String jobId, boolean withSavepoint) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder(String.format(STOP_JOB_BASE_SQL, jobId));
        if (withSavepoint) {
            sqlBuilder.append(WITH_SAVEPOINT);
        }
        client.executeStatement(session.getSessionId(), sqlBuilder.toString(), null);
    }
}
