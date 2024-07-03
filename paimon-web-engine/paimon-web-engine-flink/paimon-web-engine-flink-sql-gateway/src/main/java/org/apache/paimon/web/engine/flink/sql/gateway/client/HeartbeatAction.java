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

import org.apache.paimon.web.engine.flink.common.status.HeartbeatStatus;
import org.apache.paimon.web.engine.flink.sql.gateway.model.HeartbeatEntity;

/** Using to execute cluster heartbeat action. */
public interface HeartbeatAction {

    /**
     * Execute cluster action to obtain cluster status.
     *
     * @return cluster heartbeat entity.
     */
    HeartbeatEntity checkClusterHeartbeat();

    /**
     * Build a heartbeat entity representing an error based on the cluster status. This method is
     * used to generate a heartbeat object when the cluster status is abnormal, recording the
     * current time and the error status of the cluster.
     *
     * @param status The current status of the cluster, used to set the status field of the
     *     heartbeat entity.
     * @return Returns a completed heartbeat entity, including the current timestamp and status
     *     information.
     */
    default HeartbeatEntity buildResulHeartbeatEntity(HeartbeatStatus status) {
        return HeartbeatEntity.builder().status(status.name()).build();
    }
}
