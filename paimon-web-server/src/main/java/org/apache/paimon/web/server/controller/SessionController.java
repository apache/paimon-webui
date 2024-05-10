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
import org.apache.paimon.web.server.data.model.ClusterInfo;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.service.ClusterService;
import org.apache.paimon.web.server.service.SessionService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Session api controller. */
@Slf4j
@RestController
@RequestMapping("/api/session")
public class SessionController {

    @Autowired private SessionService sessionService;

    @Autowired private ClusterService clusterService;

    @PostMapping("/create")
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 3000))
    public R<Void> createSession(Integer uid) {
        QueryWrapper<ClusterInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", "Flink");
        List<ClusterInfo> clusterInfos = clusterService.list(queryWrapper);
        for (ClusterInfo cluster : clusterInfos) {
            SessionDTO sessionDTO = new SessionDTO();
            sessionDTO.setHost(cluster.getHost());
            sessionDTO.setPort(cluster.getPort());
            sessionDTO.setClusterId(cluster.getId());
            sessionDTO.setUid(uid);
            sessionService.createSession(sessionDTO);
        }
        return R.succeed();
    }

    @Recover
    public R<Void> recover(Exception e, Integer uid) {
        log.error("After retries failed to create session for UID: {}", uid, e);
        return R.failed();
    }

    @PostMapping("/drop")
    public R<Void> dropSession(Integer uid) {
        QueryWrapper<ClusterInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", "Flink");
        List<ClusterInfo> clusterInfos = clusterService.list(queryWrapper);
        for (ClusterInfo cluster : clusterInfos) {
            SessionDTO sessionDTO = new SessionDTO();
            sessionDTO.setHost(cluster.getHost());
            sessionDTO.setPort(cluster.getPort());
            sessionDTO.setClusterId(cluster.getId());
            sessionDTO.setUid(uid);
            sessionService.closeSession(sessionDTO);
        }
        return R.succeed();
    }
}
