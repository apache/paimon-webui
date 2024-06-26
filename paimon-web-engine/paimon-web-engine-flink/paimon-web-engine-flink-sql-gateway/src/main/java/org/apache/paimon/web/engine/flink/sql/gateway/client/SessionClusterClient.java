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

package org.apache.paimon.web.engine.flink.sql.gateway.client;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.flink.runtime.rest.handler.legacy.messages.ClusterOverviewWithVersion;
import org.apache.flink.runtime.rest.messages.ClusterOverviewHeaders;
import org.apache.flink.runtime.rest.messages.EmptyMessageParameters;
import org.apache.flink.runtime.rest.messages.EmptyRequestBody;
import org.apache.paimon.web.engine.flink.sql.gateway.utils.SqlGateWayRestClient;

import java.util.Objects;

/**
 * The flink session client provides some operations on the flink session cluster,
 * such as obtaining the cluster status. etc.
 * The flink client implementation of the {@link ClusterAction}.
 */
public class SessionClusterClient implements ClusterAction{

    private final SqlGateWayRestClient restClient;

    public SessionClusterClient(String sessionClusterHost, int sessionClusterPort) throws Exception {
        this.restClient = new SqlGateWayRestClient(sessionClusterHost, sessionClusterPort);
    }

    @Override
    public ImmutablePair<ClusterStatus, Long> checkClusterHeartbeat() throws Exception{
        ClusterOverviewWithVersion heartbeat = restClient
                .sendRequest(
                        ClusterOverviewHeaders.getInstance(),
                        EmptyMessageParameters.getInstance(),
                        EmptyRequestBody.getInstance())
                .get();
        if (Objects.nonNull(heartbeat)) {
           return this.buildClusterHeartbeatOfSuccess();
        }
       return this.buildClusterHeartbeatOfError();
    }
}
