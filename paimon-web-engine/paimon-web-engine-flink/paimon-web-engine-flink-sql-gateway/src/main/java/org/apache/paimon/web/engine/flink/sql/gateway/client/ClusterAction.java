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

import org.apache.paimon.web.engine.flink.common.status.ClusterStatus;
import org.apache.paimon.web.engine.flink.sql.gateway.model.HeartbeatEntity;

/** Using to execute cluster action. */
public interface ClusterAction {

    /**
     * Execute cluster action to obtain cluster status.
     *
     * @return cluster heartbeat entity.
     */
    HeartbeatEntity checkClusterHeartbeat();

    /**
     * Constructs a cluster heartbeat pair indicating an error state.
     *
     * <p>This method is utilized when the cluster is in an error state or its status cannot be
     * determined, generating a heartbeat pair that includes the current timestamp to represent the
     * uncertainty. The heartbeat pair comprises the cluster status and the timestamp, serving to
     * mark the unknown state of the cluster and record the time of the error occurrence.
     *
     * @return An immutable Pair object containing the cluster status as UNKNOWN and the current
     *     timestamp.
     */
    default HeartbeatEntity buildClusterHeartbeatOfError(ClusterStatus status) {
        return HeartbeatEntity.builder()
                .lastHeartbeat(System.currentTimeMillis())
                .status(status.name())
                .build();
    }
}
