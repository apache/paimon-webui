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

package org.apache.paimon.web.flink.sql.gateway.executor;

import org.apache.paimon.web.common.executor.Executor;
import org.apache.paimon.web.common.result.FetchResultParams;
import org.apache.paimon.web.common.result.SubmitResult;
import org.apache.paimon.web.flink.exception.SqlExecutionException;
import org.apache.paimon.web.flink.operation.FlinkSqlOperationType;
import org.apache.paimon.web.flink.parser.StatementParser;
import org.apache.paimon.web.flink.sql.gateway.client.SqlGatewayClient;
import org.apache.paimon.web.flink.sql.gateway.model.SessionEntity;
import org.apache.paimon.web.flink.utils.CollectResultUtil;
import org.apache.paimon.web.flink.utils.FlinkSqlStatementSetBuilder;
import org.apache.paimon.web.flink.utils.FormatSqlExceptionUtil;

import org.apache.flink.table.data.StringData;
import org.apache.flink.table.gateway.api.results.ResultSet;
import org.apache.flink.table.gateway.rest.message.statement.FetchResultsResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** The flink sql gateway implementation of the {@link Executor}. */
public class FlinkSqlGatewayExecutor implements Executor {

    private static final Long DEFAULT_FETCH_TOKEN = 0L;
    private static final String STOP_JOB_BASE_SQL = "STOP JOB '%s'";
    private static final String WITH_SAVEPOINT = " WITH SAVEPOINT";
    private static final String WITH_DRAIN = " WITH DRAIN";
    private static final String EXECUTE_SUCCESS = "OK";

    private final SqlGatewayClient client;
    private final SessionEntity session;

    public FlinkSqlGatewayExecutor(SessionEntity session) throws Exception {
        this.session = session;
        this.client = new SqlGatewayClient(session.getAddress(), session.getPort());
    }

    @Override
    public SubmitResult executeSql(String multiStatement) throws SqlExecutionException {
        String[] statements = StatementParser.parse(multiStatement);
        List<String> insertStatements = new ArrayList<>();

        for (String statement : statements) {
            FlinkSqlOperationType operationType = FlinkSqlOperationType.getOperationType(statement);

            switch (operationType.getCategory()) {
                case DQL:
                    if (insertStatements.isEmpty()) {
                        return executeDqlStatement(statement, operationType);
                    }
                    break;
                case DML:
                    if (operationType.getType().equals(FlinkSqlOperationType.INSERT.getType())) {
                        insertStatements.add(statement);
                    } else if (insertStatements.isEmpty()) {
                        return executeDmlStatement(statement);
                    }
                    break;
                default:
                    executeStatement(statement);
                    break;
            }
        }

        return executeInsertStatements(insertStatements);
    }

    private SubmitResult executeDqlStatement(String statement, FlinkSqlOperationType operationType)
            throws SqlExecutionException {
        try {
            String operationId = client.executeStatement(session.getSessionId(), statement, null);
            FetchResultsResponseBody results =
                    client.fetchResults(session.getSessionId(), operationId, DEFAULT_FETCH_TOKEN);
            SubmitResult.Builder builder =
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

    private SubmitResult executeDmlStatement(String statement) throws SqlExecutionException {
        try {
            String operationId = client.executeStatement(session.getSessionId(), statement, null);
            FetchResultsResponseBody results =
                    client.fetchResults(session.getSessionId(), operationId, DEFAULT_FETCH_TOKEN);
            return new SubmitResult.Builder()
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

    private SubmitResult executeInsertStatements(List<String> insertStatements)
            throws SqlExecutionException {
        if (!insertStatements.isEmpty()) {
            try {
                String combinedStatement =
                        FlinkSqlStatementSetBuilder.buildStatementSet(insertStatements);
                String operationId =
                        client.executeStatement(session.getSessionId(), combinedStatement, null);
                FetchResultsResponseBody results =
                        client.fetchResults(
                                session.getSessionId(), operationId, DEFAULT_FETCH_TOKEN);
                return new SubmitResult.Builder()
                        .submitId(operationId)
                        .jobId(getJobIdFromResults(results))
                        .build();
            } catch (Exception e) {
                String errorMessage =
                        FormatSqlExceptionUtil.formatSqlBatchExceptionMessage(insertStatements);
                throw new SqlExecutionException(errorMessage, e);
            }
        }
        return SubmitResult.builder()
                .submitId(UUID.randomUUID().toString())
                .status(EXECUTE_SUCCESS)
                .build();
    }

    private String getJobIdFromResults(FetchResultsResponseBody results) {
        return Objects.requireNonNull(results.getJobID(), "Job ID not found in results").toString();
    }

    @Override
    public SubmitResult fetchResults(FetchResultParams params) throws Exception {
        FetchResultsResponseBody fetchResultsResponseBody =
                client.fetchResults(params.getSessionId(), params.getSubmitId(), params.getToken());
        ResultSet.ResultType resultType = fetchResultsResponseBody.getResultType();
        if (resultType == ResultSet.ResultType.EOS) {
            return SubmitResult.builder().shouldFetchResult(false).build();
        }
        SubmitResult.Builder builder =
                CollectResultUtil.collectSqlGatewayResult(fetchResultsResponseBody.getResults());
        builder.submitId(params.getSubmitId());
        builder.status(EXECUTE_SUCCESS);
        return builder.build();
    }

    @Override
    public boolean stop(String jobId, boolean withSavepoint, boolean withDrain) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder(String.format(STOP_JOB_BASE_SQL, jobId));
        if (withSavepoint) {
            sqlBuilder.append(WITH_SAVEPOINT);
        }
        if (withDrain) {
            sqlBuilder.append(WITH_DRAIN);
        }
        String operationId =
                client.executeStatement(session.getSessionId(), sqlBuilder.toString(), null);
        FetchResultsResponseBody fetchResultsResponseBody =
                client.fetchResults(session.getSessionId(), operationId, DEFAULT_FETCH_TOKEN);
        StringData field = fetchResultsResponseBody.getResults().getData().get(0).getString(0);
        return EXECUTE_SUCCESS.equals(field.toString());
    }
}
