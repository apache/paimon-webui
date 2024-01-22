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

import org.apache.paimon.web.engine.flink.sql.gataway.TestBase;
import org.apache.paimon.web.engine.flink.sql.gateway.client.SqlGatewayClient;
import org.apache.paimon.web.engine.flink.sql.gateway.executor.FlinkSqlGatewayExecutor;
import org.apache.paimon.web.engine.flink.sql.gateway.model.SessionEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test for {@link FlinkSqlGatewayExecutor}. */
public class FlinkSqlGatewayExecutorTest extends TestBase {

    SqlGatewayClient client;
    FlinkSqlGatewayExecutor executor;

    private static final String SESSION_NAME = "test_session";

    @BeforeEach
    void before() throws Exception {
        client = new SqlGatewayClient(targetAddress, port);
        SessionEntity session = client.openSession(SESSION_NAME);
        executor = new FlinkSqlGatewayExecutor(session);
    }

    @Test
    public void testStop() throws Exception {
        executor.stop("1", false);
    }
}
