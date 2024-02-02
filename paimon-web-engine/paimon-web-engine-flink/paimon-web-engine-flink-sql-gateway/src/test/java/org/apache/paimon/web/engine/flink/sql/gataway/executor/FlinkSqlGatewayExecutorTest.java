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

package org.apache.paimon.web.engine.flink.sql.gataway.executor;

import org.apache.paimon.web.engine.flink.common.result.ExecutionResult;
import org.apache.paimon.web.engine.flink.common.result.FetchResultParams;
import org.apache.paimon.web.engine.flink.sql.gataway.TestBase;
import org.apache.paimon.web.engine.flink.sql.gateway.client.SqlGatewayClient;
import org.apache.paimon.web.engine.flink.sql.gateway.executor.FlinkSqlGatewayExecutor;
import org.apache.paimon.web.engine.flink.sql.gateway.model.SessionEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Test for {@link FlinkSqlGatewayExecutor}. */
public class FlinkSqlGatewayExecutorTest extends TestBase {

    SqlGatewayClient client;
    SessionEntity session;
    FlinkSqlGatewayExecutor executor;

    private static final String SESSION_NAME = "test_session";

    @BeforeEach
    void before() throws Exception {
        client = new SqlGatewayClient(targetAddress, port);
        session = client.openSession(SESSION_NAME);
        executor = new FlinkSqlGatewayExecutor(session);
    }

    @Test
    public void testExecuteSql() throws Exception {
        ExecutionResult executionResult = executor.executeSql(StatementsConstant.statement);
        assertNotNull(executionResult);
        assertNotNull(executionResult.getJobId());
    }

    @Test
    public void testExecuteStatementSetSql() throws Exception {
        ExecutionResult executionResult = executor.executeSql(StatementsConstant.statementSetSql);
        assertNotNull(executionResult);
        assertNotNull(executionResult.getJobId());
    }

    @Test
    public void testExecutorStatementWithoutResult() throws Exception {
        ExecutionResult executionResult = executor.executeSql(StatementsConstant.createStatement);
        assertNull(executionResult);
    }

    @Test
    public void testExecuteDQLStatementWithPendingInsertStatements() {
        Exception exception =
                assertThrows(
                        UnsupportedOperationException.class,
                        () -> {
                            executor.executeSql(
                                    StatementsConstant.selectStatementWithPendingInsertStatements);
                        });
        String expectedMessage = "Cannot execute DQL statement with pending INSERT statements.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testExecuteBadSqlStatement() {
        Exception exception =
                assertThrows(
                        UnsupportedOperationException.class,
                        () -> {
                            executor.executeSql(StatementsConstant.badStatement);
                        });
        String expectedMessage = "Unsupported operation type: CREAT";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testFetchResults() throws Exception {
        ExecutionResult executionResult = executor.executeSql(StatementsConstant.selectStatement);
        assertNotNull(executionResult);
        assertNotNull(executionResult.getJobId());
        assertNotNull(executionResult.getSubmitId());
        assertTrue(executionResult.shouldFetchResult());
        FetchResultParams params =
                FetchResultParams.builder()
                        .sessionId(session.getSessionId())
                        .submitId(executionResult.getSubmitId())
                        .token(1L)
                        .build();
        ExecutionResult fetchResult = executor.fetchResults(params);
        assertFalse(fetchResult.getData().isEmpty());
    }
}
