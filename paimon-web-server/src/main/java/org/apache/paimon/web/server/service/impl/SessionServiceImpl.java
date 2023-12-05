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

import org.apache.paimon.web.common.executor.Executor;
import org.apache.paimon.web.common.executor.ExecutorFactory;
import org.apache.paimon.web.flink.sql.gateway.client.SqlGatewayClient;
import org.apache.paimon.web.flink.sql.gateway.model.SessionEntity;
import org.apache.paimon.web.gateway.config.ExecuteConfig;
import org.apache.paimon.web.gateway.enums.TaskType;
import org.apache.paimon.web.gateway.provider.ExecutorFactoryProvider;
import org.apache.paimon.web.server.data.dto.SessionDTO;
import org.apache.paimon.web.server.data.model.SessionInfo;
import org.apache.paimon.web.server.mapper.SessionMapper;
import org.apache.paimon.web.server.service.JobExecutorService;
import org.apache.paimon.web.server.service.SessionService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/** The implementation of {@link SessionService}. */
@Service
public class SessionServiceImpl extends ServiceImpl<SessionMapper, SessionInfo>
        implements SessionService {

    @Autowired private SessionMapper sessionMapper;

    @Autowired private JobExecutorService jobExecutorService;

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

        ExecuteConfig config = ExecuteConfig.builder().setSessionEntity(sessionEntity).build();
        TaskType taskType = TaskType.fromValue(sessionDTO.getType().toUpperCase());
        ExecutorFactoryProvider provider = new ExecutorFactoryProvider(config);
        ExecutorFactory executorFactory = provider.getExecutorFactory(taskType);
        Executor executor = executorFactory.createExecutor();
        jobExecutorService.addExecutor(sessionEntity.getSessionId(), executor);

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
    public List<SessionInfo> getAllActiveSessions() {
        QueryWrapper<SessionInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        return sessionMapper.selectList(queryWrapper);
    }
}
