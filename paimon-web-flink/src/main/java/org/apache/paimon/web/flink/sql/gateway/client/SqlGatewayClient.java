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

package org.apache.paimon.web.flink.sql.gateway.client;

import org.apache.flink.runtime.rest.messages.EmptyMessageParameters;
import org.apache.flink.table.gateway.api.operation.OperationHandle;
import org.apache.flink.table.gateway.api.session.SessionHandle;
import org.apache.flink.table.gateway.rest.header.session.OpenSessionHeaders;
import org.apache.flink.table.gateway.rest.header.statement.ExecuteStatementHeaders;
import org.apache.flink.table.gateway.rest.message.session.OpenSessionRequestBody;
import org.apache.flink.table.gateway.rest.message.session.OpenSessionResponseBody;
import org.apache.flink.table.gateway.rest.message.session.SessionMessageParameters;
import org.apache.flink.table.gateway.rest.message.statement.ExecuteStatementRequestBody;
import org.apache.flink.table.gateway.rest.message.statement.ExecuteStatementResponseBody;
import org.apache.flink.util.ConfigurationException;

import java.util.HashMap;
import java.util.UUID;

/**
 * The client of flink sql gateway provides some operations of flink sql gateway.
 * such as creating session, execute statement, fetch result, etc.
 */
public class SqlGatewayClient {

    private static final String SESSION_NAME_PREFIX = "flink_sql_gateway_session";

    private final SqlGateWayRestClient restClient;

    public SqlGatewayClient(String address, int port) throws Exception {
        this.restClient = new SqlGateWayRestClient(address, port);
    }

    public SessionHandle createSession() throws Exception{
        OpenSessionHeaders openSessionHeaders = OpenSessionHeaders.getInstance();
        OpenSessionResponseBody openSessionResponseBody =
                restClient
                        .sendRequest(
                                openSessionHeaders,
                                EmptyMessageParameters.getInstance(),
                                new OpenSessionRequestBody(SESSION_NAME_PREFIX + "_" + UUID.randomUUID(), new HashMap<>()))
                        .get();
        return new SessionHandle(UUID.fromString(openSessionResponseBody.getSessionHandle()));
    }

    public OperationHandle executeStatement(SessionHandle sessionHandle, String statement) throws Exception{
        ExecuteStatementResponseBody executeStatementResponseBody =
                restClient
                        .sendRequest(
                                ExecuteStatementHeaders.getInstance(),
                                new SessionMessageParameters(sessionHandle),
                                new ExecuteStatementRequestBody(statement, null, new HashMap<>()))
                        .get();

        return new OperationHandle(UUID.fromString(executeStatementResponseBody.getOperationHandle()));
    }
}
