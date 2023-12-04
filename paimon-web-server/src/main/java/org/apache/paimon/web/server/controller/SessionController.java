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

package org.apache.paimon.web.server.controller;

import org.apache.paimon.web.server.data.dto.SessionDTO;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.data.vo.SessionVO;
import org.apache.paimon.web.server.service.SessionService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Session api controller. */
@Slf4j
@RestController
@RequestMapping("/api/session")
public class SessionController {

    @Autowired private SessionService sessionService;

    /**
     * Creates a session based on the provided session data transfer object (DTO).
     *
     * @param sessionDTO the session DTO containing the necessary data to create a session
     * @return a response entity indicating success or failure
     */
    @PostMapping("/create")
    public R<Void> createSession(@RequestBody SessionDTO sessionDTO) {
        try {
            if (sessionService.checkCatalogNameUnique(sessionDTO)) {
                return R.failed(Status.SESSION_NAME_IS_EXIST, sessionDTO.getName());
            }
            return sessionService.createSession(sessionDTO) ? R.succeed() : R.failed();
        } catch (Exception e) {
            log.error("Exception with creating session.", e);
            return R.failed(Status.SESSION_CREATE_ERROR);
        }
    }

    /**
     * Closes a session corresponding to the session data transfer object (DTO) provided.
     *
     * @param sessionDTO the session DTO containing the identifier of the session to be closed
     * @return a response entity indicating success or failure
     */
    @PostMapping("/close")
    public R<Void> closeSession(@RequestBody SessionDTO sessionDTO) {
        try {
            return sessionService.closeSession(sessionDTO) ? R.succeed() : R.failed();
        } catch (Exception e) {
            log.error("Exception with closing session.", e);
            return R.failed(Status.SESSION_CLOSE_ERROR);
        }
    }

    /**
     * Triggers a heartbeat for the session identified by the provided session DTO. This is
     * typically used to keep the session alive.
     *
     * @param sessionDTO the session DTO containing the identifier of the session for which the
     *     heartbeat is to be triggered
     * @return a response entity indicating success or failure
     */
    @PostMapping("/heartbeat")
    public R<Void> triggerSessionHeartbeat(@RequestBody SessionDTO sessionDTO) {
        try {
            return sessionService.triggerSessionHeartbeat(sessionDTO) > 0
                    ? R.succeed()
                    : R.failed();
        } catch (Exception e) {
            log.error("Exception with triggering session heartbeat.", e);
            return R.failed(Status.TRIGGER_SESSION_HEARTBEAT_ERROR);
        }
    }

    /**
     * Retrieves a list of all active sessions.
     *
     * @return a response entity containing the list of session value objects
     */
    @GetMapping("/list")
    public R<List<SessionVO>> listSessions() {
        try {
            List<SessionVO> sessions = sessionService.getAllSessions();
            return R.succeed(sessions);
        } catch (Exception e) {
            log.error("Exception with listing sessions.", e);
            return R.failed(Status.SESSION_LIST_ERROR);
        }
    }
}
