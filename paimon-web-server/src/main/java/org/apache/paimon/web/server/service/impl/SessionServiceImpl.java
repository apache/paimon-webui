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

import org.apache.paimon.web.flink.sql.gateway.client.SqlGatewayClient;
import org.apache.paimon.web.flink.sql.gateway.model.SessionEntity;
import org.apache.paimon.web.server.data.dto.SessionDTO;
import org.apache.paimon.web.server.data.model.SessionInfo;
import org.apache.paimon.web.server.data.vo.SessionVO;
import org.apache.paimon.web.server.mapper.SessionMapper;
import org.apache.paimon.web.server.service.SessionService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/** The implementation of {@link SessionService}. */
@Service
public class SessionServiceImpl extends ServiceImpl<SessionMapper, SessionInfo>
        implements SessionService {

    @Autowired private SessionMapper sessionMapper;

    @Override
    public boolean checkCatalogNameUnique(SessionDTO sessionDTO) {
        SessionInfo info =
                this.lambdaQuery().eq(SessionInfo::getSessionName, sessionDTO.getName()).one();
        return Objects.nonNull(info);
    }

    @Override
    public boolean createSession(SessionDTO sessionDTO) throws Exception {
        SqlGatewayClient client =
                new SqlGatewayClient(sessionDTO.getAddress(), sessionDTO.getPort());
        SessionEntity sessionEntity = client.openSession(sessionDTO.getName());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonConfig;
        try {
            jsonConfig = objectMapper.writeValueAsString(sessionEntity.getProperties());
        } catch (Exception e) {
            jsonConfig = "{}";
        }
        SessionInfo sessionInfo =
                SessionInfo.builder()
                        .sessionId(sessionEntity.getSessionId())
                        .sessionName(sessionEntity.getSessionName())
                        .address(sessionEntity.getAddress())
                        .port(sessionEntity.getPort())
                        .status(sessionEntity.getStatus())
                        .properties(jsonConfig)
                        .type(sessionDTO.getType())
                        .build();
        return this.save(sessionInfo);
    }

    @Override
    public boolean closeSession(SessionDTO sessionDTO) throws Exception {
        SqlGatewayClient client =
                new SqlGatewayClient(sessionDTO.getAddress(), sessionDTO.getPort());
        QueryWrapper<SessionInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("session_name", sessionDTO.getName());
        SessionInfo sessionInfo = sessionMapper.selectOne(queryWrapper);
        return client.closeSession(sessionInfo.getSessionId());
    }

    @Override
    public int triggerSessionHeartbeat(SessionDTO sessionDTO) throws Exception {
        SqlGatewayClient client =
                new SqlGatewayClient(sessionDTO.getAddress(), sessionDTO.getPort());
        QueryWrapper<SessionInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("session_name", sessionDTO.getName());
        SessionInfo sessionInfo = sessionMapper.selectOne(queryWrapper);
        Integer status = client.triggerSessionHeartbeat(sessionInfo.getSessionId());
        UpdateWrapper<SessionInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("session_id", sessionInfo.getSessionId());
        return sessionMapper.update(SessionInfo.builder().status(status).build(), updateWrapper);
    }

    @Override
    public List<SessionVO> getAllSessions() {
        QueryWrapper<SessionInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        List<SessionInfo> sessionInfos = sessionMapper.selectList(queryWrapper);
        return sessionInfos.stream().map(this::convertToSessionVO).collect(Collectors.toList());
    }

    @Override
    public SessionInfo selectSessionById(String sessionId) {
        return sessionMapper.selectById(sessionId);
    }

    private SessionVO convertToSessionVO(SessionInfo sessionInfo) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> propertiesMap = null;
        try {
            propertiesMap =
                    objectMapper.readValue(
                            sessionInfo.getProperties(),
                            new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            propertiesMap = new HashMap<>();
        }
        return SessionVO.builder()
                .sessionId(sessionInfo.getSessionId())
                .sessionName(sessionInfo.getSessionName())
                .address(sessionInfo.getAddress())
                .port(sessionInfo.getPort())
                .properties(propertiesMap)
                .status(sessionInfo.getStatus())
                .build();
    }
}
