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

import org.apache.paimon.web.engine.flink.common.executor.Executor;
import org.apache.paimon.web.engine.flink.common.executor.ExecutorFactory;
import org.apache.paimon.web.engine.flink.sql.gateway.model.SessionEntity;
import org.apache.paimon.web.gateway.config.ExecutionConfig;
import org.apache.paimon.web.gateway.enums.EngineType;
import org.apache.paimon.web.gateway.provider.ExecutorFactoryProvider;
import org.apache.paimon.web.server.data.dto.JobSubmitDTO;
import org.apache.paimon.web.server.data.dto.SessionDTO;
import org.apache.paimon.web.server.data.model.ClusterInfo;
import org.apache.paimon.web.server.data.model.JobInfo;
import org.apache.paimon.web.server.data.vo.UserVO;
import org.apache.paimon.web.server.mapper.JobMapper;
import org.apache.paimon.web.server.service.ClusterService;
import org.apache.paimon.web.server.service.JobService;
import org.apache.paimon.web.server.service.SessionService;
import org.apache.paimon.web.server.service.UserService;
import org.apache.paimon.web.server.service.UserSessionManager;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/** The implementation of {@link JobService}. */
@Service
public class JobServiceImpl extends ServiceImpl<JobMapper, JobInfo> implements JobService {

    @Autowired private JobMapper jobMapper;

    @Autowired private UserSessionManager sessionManager;

    @Autowired private UserService userService;

    @Autowired private SessionService sessionService;

    @Autowired private ClusterService clusterService;

    @Override
    public boolean shouldCreateSession() {
        if (StpUtil.isLogin()) {
            UserVO user = userService.getUserById(StpUtil.getLoginIdAsInt());
            SessionEntity session = sessionManager.getSession(user.getUsername());
            if (session != null) {
                SessionDTO sessionDTO = new SessionDTO();
                sessionDTO.setHost(session.getHost());
                sessionDTO.setPort(session.getPort());
                return sessionService.triggerSessionHeartbeat(sessionDTO) <= 0;
            }
        }
        return true;
    }

    @Override
    public Executor getExecutor(JobSubmitDTO jobSubmitDTO) throws Exception {
        if (shouldCreateSession()) {
            ClusterInfo clusterInfo = clusterService.getById(jobSubmitDTO.getClusterId());
            SessionDTO sessionDTO = new SessionDTO();
            sessionDTO.setHost(clusterInfo.getHost());
            sessionDTO.setPort(clusterInfo.getPort());
            sessionService.createSession(sessionDTO);
        }
        if (StpUtil.isLogin()) {
            UserVO user = userService.getUserById(StpUtil.getLoginIdAsInt());
            SessionEntity session = sessionManager.getSession(user.getUsername());
            ExecutionConfig config =
                    ExecutionConfig.builder()
                            .isStreaming(jobSubmitDTO.isStreaming())
                            .sessionEntity(session)
                            .build();
            EngineType engineType = EngineType.fromName(jobSubmitDTO.getTaskType().toUpperCase());
            ExecutorFactoryProvider provider = new ExecutorFactoryProvider(config);
            ExecutorFactory executorFactory = provider.getExecutorFactory(engineType);
            return executorFactory.createExecutor();
        }
        return null;
    }

    @Override
    public boolean saveJob(JobInfo jobInfo) {
        return this.save(jobInfo);
    }

    @Override
    public boolean updateJobStatusAndEndTime(
            String jobId, String newStatus, LocalDateTime endTime) {
        JobInfo jobInfo = new JobInfo();
        jobInfo.setStatus(newStatus);
        jobInfo.setEndTime(endTime);
        return this.update(jobInfo, new QueryWrapper<JobInfo>().eq("job_id", jobId));
    }

    @Override
    public boolean updateJobStatusAndStartTime(
            String jobId, String newStatus, LocalDateTime startTime) {
        JobInfo jobInfo = new JobInfo();
        jobInfo.setStatus(newStatus);
        jobInfo.setStartTime(startTime);
        return this.update(jobInfo, new QueryWrapper<JobInfo>().eq("job_id", jobId));
    }

    @Override
    public List<JobInfo> listJobsByPage(int current, int size) {
        QueryWrapper<JobInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        Page<JobInfo> page = new Page<>(current, size);
        IPage<JobInfo> jobInfoIPage = jobMapper.selectPage(page, queryWrapper);
        return jobInfoIPage.getRecords();
    }

    @Override
    public JobInfo getJobByJobId(String jobId) {
        QueryWrapper<JobInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("job_id", jobId);
        return jobMapper.selectOne(queryWrapper);
    }
}
