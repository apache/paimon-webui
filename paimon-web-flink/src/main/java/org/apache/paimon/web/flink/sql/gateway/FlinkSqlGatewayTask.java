/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.paimon.web.flink.sql.gateway;

import org.apache.paimon.web.common.data.constant.SqlConstants;
import org.apache.paimon.web.common.data.vo.SubmitResult;
import org.apache.paimon.web.flink.task.FlinkTask;
import org.apache.paimon.web.task.SubmitJob;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.runtime.rest.messages.EmptyMessageParameters;
import org.apache.flink.runtime.rest.messages.EmptyRequestBody;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.gateway.api.operation.OperationHandle;
import org.apache.flink.table.gateway.api.results.ResultSet;
import org.apache.flink.table.gateway.api.session.SessionHandle;
import org.apache.flink.table.gateway.rest.header.session.OpenSessionHeaders;
import org.apache.flink.table.gateway.rest.header.statement.ExecuteStatementHeaders;
import org.apache.flink.table.gateway.rest.header.statement.FetchResultsHeaders;
import org.apache.flink.table.gateway.rest.message.session.OpenSessionRequestBody;
import org.apache.flink.table.gateway.rest.message.session.OpenSessionResponseBody;
import org.apache.flink.table.gateway.rest.message.session.SessionMessageParameters;
import org.apache.flink.table.gateway.rest.message.statement.ExecuteStatementRequestBody;
import org.apache.flink.table.gateway.rest.message.statement.ExecuteStatementResponseBody;
import org.apache.flink.table.gateway.rest.message.statement.FetchResultsResponseBody;
import org.apache.flink.table.gateway.rest.message.statement.FetchResultsTokenParameters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class FlinkSqlGatewayTask implements SubmitJob {
    private static final int REQUEST_WAITE_TIME = 1000;

    private final SqlGateWayRestClient sqlGateWayRestClient;
    private final SessionHandle sessionHandle;

    public FlinkSqlGatewayTask(String address, int port) throws Exception {
        this.sqlGateWayRestClient = new SqlGateWayRestClient(address, port);
        // openSession global
        OpenSessionHeaders openSessionHeaders = OpenSessionHeaders.getInstance();
        OpenSessionResponseBody openSessionResponseBody =
                sqlGateWayRestClient
                        .sendRequest(
                                openSessionHeaders,
                                EmptyMessageParameters.getInstance(),
                                new OpenSessionRequestBody("test", new HashMap<>()))
                        .get();
        this.sessionHandle =
                new SessionHandle(UUID.fromString(openSessionResponseBody.getSessionHandle()));
    }

    @Override
    public SubmitResult execute(String statement) throws Exception {
        // executeStatement
        ExecuteStatementResponseBody executeStatementResponseBody =
                sqlGateWayRestClient
                        .sendRequest(
                                ExecuteStatementHeaders.getInstance(),
                                new SessionMessageParameters(sessionHandle),
                                new ExecuteStatementRequestBody(statement, null, new HashMap<>()))
                        .get();
        OperationHandle operationHandle =
                new OperationHandle(
                        UUID.fromString(executeStatementResponseBody.getOperationHandle()));
        // fetchResults (async get data)
        FetchResultsTokenParameters fetchResultsTokenParameters =
                new FetchResultsTokenParameters(sessionHandle, operationHandle, 0L);
        FetchResultsResponseBody fetchResultsResponseBody =
                sqlGateWayRestClient
                        .sendRequest(
                                FetchResultsHeaders.getInstance(),
                                fetchResultsTokenParameters,
                                EmptyRequestBody.getInstance())
                        .get();
        String resultType = fetchResultsResponseBody.getResultType();
        while (ResultSet.ResultType.valueOf(resultType) != ResultSet.ResultType.PAYLOAD) {
            fetchResultsResponseBody =
                    sqlGateWayRestClient
                            .sendRequest(
                                    FetchResultsHeaders.getInstance(),
                                    fetchResultsTokenParameters,
                                    EmptyRequestBody.getInstance())
                            .get();
            resultType = fetchResultsResponseBody.getResultType();
            Thread.sleep(REQUEST_WAITE_TIME);
        }
        // to convert data
        ResultSet results = fetchResultsResponseBody.getResults();
        List<RowData> data = results.getData();
        List<Map<String, Object>> datas =
                FlinkTask.rowDatasToList(results.getResultSchema().getColumnNames(), data);
        return SubmitResult.builder().data(datas).build();
    }

    @Override
    public boolean stop(String statement) throws Exception {
        // todo Need to be implemented here
        return false;
    }

    @Override
    public boolean checkStatus() {
        try {
            execute(SqlConstants.VALIDATE_SQL);
            return true;
        } catch (Exception e) {
            log.error("Flink checkStatus error", e);
        }
        return false;
    }
}
