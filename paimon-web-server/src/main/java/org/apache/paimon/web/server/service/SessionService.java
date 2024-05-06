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

package org.apache.paimon.web.server.service;

import org.apache.paimon.web.engine.flink.sql.gateway.model.SessionEntity;
import org.apache.paimon.web.server.data.dto.SessionDTO;

/** Session Service. */
public interface SessionService {

    /**
     * Creates a new session.
     *
     * @param sessionDTO the data transfer object containing session details
     */
    void createSession(SessionDTO sessionDTO);

    /**
     * Closes an existing session.
     *
     * @param sessionDTO the data transfer object containing session details
     */
    void closeSession(SessionDTO sessionDTO);

    /**
     * Triggers a heartbeat update for a session.
     *
     * @param sessionDTO the data transfer object containing session details
     * @return the status code after triggering the heartbeat
     */
    int triggerSessionHeartbeat(SessionDTO sessionDTO);

    /**
     * Retrieves the session for a given user ID within a specified cluster.
     *
     * @param uid the unique identifier of the user
     * @param clusterId the identifier of the cluster
     * @return the SessionEntity for the specified user and cluster, or null if no session is found
     */
    SessionEntity getSession(Integer uid, Integer clusterId);
}
