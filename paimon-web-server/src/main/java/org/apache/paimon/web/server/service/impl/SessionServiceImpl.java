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

package org.apache.paimon.web.server.service.impl;

import org.apache.paimon.web.engine.flink.sql.gateway.client.SqlGatewayClient;
import org.apache.paimon.web.engine.flink.sql.gateway.model.SessionEntity;
import org.apache.paimon.web.server.data.dto.SessionDTO;
import org.apache.paimon.web.server.data.vo.UserVO;
import org.apache.paimon.web.server.service.SessionService;
import org.apache.paimon.web.server.service.UserService;
import org.apache.paimon.web.server.service.UserSessionManager;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/** The implementation of {@link SessionService}. */
@Service
public class SessionServiceImpl implements SessionService {

    private static final Integer ACTIVE_STATUS = 1;
    private static final Integer INACTIVE_STATUS = 0;

    @Autowired private UserSessionManager sessionManager;

    @Autowired private UserService userService;

    @Override
    public void createSession(SessionDTO sessionDTO) {
        try {
            SqlGatewayClient client =
                    new SqlGatewayClient(sessionDTO.getHost(), sessionDTO.getPort());
            if (getCurrentUser() != null) {
                String sessionName = getCurrentUser().getUsername() + "_" + UUID.randomUUID();
                SessionEntity sessionEntity = client.openSession(sessionName);
                sessionManager.addSession(getCurrentUser().getUsername(), sessionEntity);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create session", e);
        }
    }

    @Override
    public void closeSession(SessionDTO sessionDTO) {
        try {
            SqlGatewayClient client =
                    new SqlGatewayClient(sessionDTO.getHost(), sessionDTO.getPort());
            if (getCurrentUser() != null) {
                SessionEntity session = sessionManager.getSession(getCurrentUser().getUsername());
                if (session != null) {
                    client.closeSession(session.getSessionId());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to close session", e);
        }
    }

    @Override
    public int triggerSessionHeartbeat(SessionDTO sessionDTO) {
        try {
            if (getCurrentUser() != null) {
                SqlGatewayClient client =
                        new SqlGatewayClient(sessionDTO.getHost(), sessionDTO.getPort());
                SessionEntity session = sessionManager.getSession(getCurrentUser().getUsername());
                client.triggerSessionHeartbeat(session.getSessionId());
            }
        } catch (Exception e) {
            return INACTIVE_STATUS;
        }
        return ACTIVE_STATUS;
    }

    private UserVO getCurrentUser() {
        if (StpUtil.isLogin()) {
            return userService.getUserById(StpUtil.getLoginIdAsInt());
        }
        return null;
    }
}
