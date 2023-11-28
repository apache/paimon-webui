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
import org.apache.paimon.web.common.executor.Executor;
import org.apache.paimon.web.common.result.SubmitResult;
import org.apache.paimon.web.flink.parser.StatementParser;
import org.apache.paimon.web.flink.sql.gateway.client.SqlGateWayRestClient;
import org.apache.paimon.web.flink.sql.gateway.client.SqlGatewayClient;

import java.util.HashMap;
import java.util.UUID;

/** The flink sql gateway implementation of the {@link Executor}. */
public class FlinkSqlGatewayExecutor implements Executor{

    private static final int REQUEST_WAITE_TIME = 1000;

    private final SqlGatewayClient client;
    private final SessionHandle sessionHandle;

    public FlinkSqlGatewayExecutor(String address, int port) throws Exception{
        this.client = new SqlGatewayClient(address, port);
        this.sessionHandle = client.createSession();

    }

    @Override
    public SubmitResult executeSql(String multiStatement) throws Exception {
        String[] statements = StatementParser.parse(multiStatement);
        for (String statement : statements) {

        }
        return null;
    }

    @Override
    public boolean stop(String statement) throws Exception {
        return false;
    }
}
