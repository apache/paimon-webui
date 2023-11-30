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

import org.apache.flink.table.data.RowData;
import org.apache.flink.table.gateway.rest.message.statement.FetchResultsResponseBody;
import org.apache.paimon.web.common.executor.Executor;
import org.apache.paimon.web.common.result.SubmitResult;
import org.apache.paimon.web.flink.parser.StatementParser;
import org.apache.paimon.web.flink.sql.gateway.client.SqlGatewayClient;
import org.apache.paimon.web.flink.sql.gateway.model.SessionEntity;

/** The flink sql gateway implementation of the {@link Executor}. */
public class FlinkSqlGatewayExecutor implements Executor {

    private static final Long DEFAULT_FETCH_TOKEN = 0L;
    private static final String STOP_JOB_SQL = "STOP JOB '%s'";

    private final SqlGatewayClient client;
    private final SessionEntity session;

    public FlinkSqlGatewayExecutor(SessionEntity session) throws Exception {
        this.session = session;
        this.client = new SqlGatewayClient(session.getAddress(), session.getPort());
    }

    @Override
    public SubmitResult executeSql(String multiStatement) throws Exception {
        String[] statements = StatementParser.parse(multiStatement);
        for (String statement : statements) {}

        return null;
    }

    @Override
    public boolean stop(String jobId) throws Exception {
        String statement = String.format(STOP_JOB_SQL, jobId);
        String operationId = client.executeStatement(session.getSessionId(), statement, null);
        FetchResultsResponseBody fetchResultsResponseBody = client.fetchResults(session.getSessionId(), operationId, DEFAULT_FETCH_TOKEN);
        RowData rowData = fetchResultsResponseBody.getResults().getData().get(0);
        return false;
    }
}
