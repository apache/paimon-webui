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

package org.apache.paimon.web.engine.flink.sql.gataway.client;

import org.apache.paimon.web.engine.flink.sql.gataway.TestBase;
import org.apache.paimon.web.engine.flink.sql.gateway.client.SqlGatewayClient;
import org.apache.paimon.web.engine.flink.sql.gateway.model.SessionEntity;

import org.apache.commons.collections.MapUtils;
import org.apache.flink.table.gateway.rest.message.statement.FetchResultsResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Test for {@link SqlGatewayClient}. */
public class SqlGatewayClientTest extends TestBase {

    SqlGatewayClient client;
    SessionEntity session;

    private static final String SESSION_NAME = "test_session";

    @BeforeEach
    void before() throws Exception {
        client = new SqlGatewayClient(targetAddress, port);
        session = client.openSession(SESSION_NAME);
    }

    @Test
    public void testGetSessionConfig() throws Exception {
        Map<String, String> sessionConfig = client.getSessionConfig(session.getSessionId());
        assertTrue(MapUtils.isNotEmpty(sessionConfig));
    }

    @Test
    public void testCloseSession() throws Exception {
        String status = client.closeSession(session.getSessionId());
        assertEquals("CLOSED", status);
    }

    @Test
    public void testExecuteStatement() throws Exception {
        String operationHandle = client.executeStatement(session.getSessionId(), "SELECT 1", null);
        assertNotNull(operationHandle);
    }

    @Test
    public void testCompleteStatementHints() throws Exception {
        List<String> list = client.completeStatementHints(session.getSessionId(), "CREATE TA");
        assertFalse(list.isEmpty());
    }

    @Test
    public void testFetchResults() throws Exception {
        String operationHandle = client.executeStatement(session.getSessionId(), "SELECT 1", null);
        FetchResultsResponseBody fetchResultsResponseBody =
                client.fetchResults(session.getSessionId(), operationHandle, 0);
        assertNotNull(fetchResultsResponseBody);
        assertEquals("PAYLOAD", fetchResultsResponseBody.getResultType().name());
        FetchResultsResponseBody fetchResultsResponseBodyNext =
                client.fetchResults(session.getSessionId(), operationHandle, 1);
        assertNotNull(fetchResultsResponseBodyNext);
        assertEquals("EOS", fetchResultsResponseBodyNext.getResultType().name());
    }

    @Test
    public void testGetOperationStatus() throws Exception {
        String operationHandle = client.executeStatement(session.getSessionId(), "SELECT 1", null);
        String operationStatus = client.getOperationStatus(session.getSessionId(), operationHandle);
        assertNotNull(operationStatus);
    }

    @Test
    public void testCancelOperation() throws Exception {
        String operationHandle = client.executeStatement(session.getSessionId(), "SELECT 1", null);
        String status = client.cancelOperation(session.getSessionId(), operationHandle);
        assertEquals("CANCELED", status);
    }

    @Test
    public void testCloseOperation() throws Exception {
        String operationHandle = client.executeStatement(session.getSessionId(), "SELECT 1", null);
        String status = client.closeOperation(session.getSessionId(), operationHandle);
        assertEquals("CLOSED", status);
    }
}
