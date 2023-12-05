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

import org.apache.paimon.web.server.data.dto.SessionDTO;
import org.apache.paimon.web.server.data.model.SessionInfo;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/** Catalog Service. */
public interface SessionService extends IService<SessionInfo> {

    /**
     * Checks if the catalog name provided in the sessionDTO is unique.
     *
     * @param sessionDTO the data transfer object containing the session details
     * @return true if the catalog name is unique, false otherwise
     */
    boolean checkCatalogNameUnique(SessionDTO sessionDTO);

    /**
     * Creates a new session based on the provided session data transfer object (DTO).
     *
     * @param sessionDTO the session DTO containing the necessary data to create a new session
     * @return {@code true} if the session was successfully created, {@code false} otherwise
     * @throws Exception if there is an issue during the session creation process
     */
    boolean createSession(SessionDTO sessionDTO) throws Exception;

    /**
     * Closes an existing session identified by the session data transfer object (DTO).
     *
     * @param sessionDTO the session DTO containing the identifier of the session to be closed
     * @return {@code true} if the session was successfully closed, {@code false} otherwise
     * @throws Exception if there is an issue during the session closure process
     */
    boolean closeSession(SessionDTO sessionDTO) throws Exception;

    /**
     * Triggers a heartbeat event for an existing session identified by the session DTO. This is
     * typically used to keep the session active.
     *
     * @param sessionDTO the session DTO containing the identifier of the session for which the
     *     heartbeat is to be triggered
     * @return a positive integer if the heartbeat was successfully triggered, 0 otherwise
     * @throws Exception if there is an issue during the heartbeat triggering process
     */
    int triggerSessionHeartbeat(SessionDTO sessionDTO) throws Exception;

    /**
     * Retrieves all sessions that are currently active and converts them into a list of session
     * view objects.
     *
     * @return a list of {@link SessionInfo} representing the active sessions
     */
    List<SessionInfo> getAllActiveSessions();
}
