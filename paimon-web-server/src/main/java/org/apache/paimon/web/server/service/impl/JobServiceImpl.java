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
import org.apache.paimon.web.engine.flink.common.result.ExecutionResult;
import org.apache.paimon.web.engine.flink.common.result.FetchResultParams;
import org.apache.paimon.web.engine.flink.common.status.JobStatus;
import org.apache.paimon.web.engine.flink.sql.gateway.model.SessionEntity;
import org.apache.paimon.web.gateway.config.ExecutionConfig;
import org.apache.paimon.web.gateway.enums.EngineType;
import org.apache.paimon.web.gateway.provider.ExecutorFactoryProvider;
import org.apache.paimon.web.server.data.dto.JobSubmitDTO;
import org.apache.paimon.web.server.data.dto.ResultFetchDTO;
import org.apache.paimon.web.server.data.dto.SessionDTO;
import org.apache.paimon.web.server.data.dto.StopJobDTO;
import org.apache.paimon.web.server.data.model.ClusterInfo;
import org.apache.paimon.web.server.data.model.JobInfo;
import org.apache.paimon.web.server.data.vo.JobStatisticsVO;
import org.apache.paimon.web.server.data.vo.JobVO;
import org.apache.paimon.web.server.data.vo.ResultDataVO;
import org.apache.paimon.web.server.data.vo.UserVO;
import org.apache.paimon.web.server.mapper.JobMapper;
import org.apache.paimon.web.server.service.ClusterService;
import org.apache.paimon.web.server.service.JobService;
import org.apache.paimon.web.server.service.SessionService;
import org.apache.paimon.web.server.service.UserService;
import org.apache.paimon.web.server.service.UserSessionManager;
import org.apache.paimon.web.server.util.LocalDateTimeUtil;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/** The implementation of {@link JobService}. */
@Service
@Slf4j
public class JobServiceImpl extends ServiceImpl<JobMapper, JobInfo> implements JobService {

    private static final String STREAMING_MODE = "Streaming";
    private static final String BATCH_MODE = "Batch";
    private static final String SHOW_JOBS_STATEMENT = "SHOW JOBS";

    @Autowired private JobMapper jobMapper;

    @Autowired private UserSessionManager sessionManager;

    @Autowired private UserService userService;

    @Autowired private SessionService sessionService;

    @Autowired private ClusterService clusterService;

    private boolean shouldCreateSession() {
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

    private Executor getExecutor(String clusterId, String taskType) {
        try {
            if (shouldCreateSession()) {
                ClusterInfo clusterInfo = clusterService.getById(clusterId);
                SessionDTO sessionDTO = new SessionDTO();
                sessionDTO.setHost(clusterInfo.getHost());
                sessionDTO.setPort(clusterInfo.getPort());
                sessionService.createSession(sessionDTO);
            }
            if (StpUtil.isLogin()) {
                UserVO user = userService.getUserById(StpUtil.getLoginIdAsInt());
                SessionEntity session = sessionManager.getSession(user.getUsername());
                ExecutionConfig config = ExecutionConfig.builder().sessionEntity(session).build();
                EngineType engineType = EngineType.fromName(taskType.toUpperCase());
                ExecutorFactoryProvider provider = new ExecutorFactoryProvider(config);
                ExecutorFactory executorFactory = provider.getExecutorFactory(engineType);
                return executorFactory.createExecutor();
            }
        } catch (Exception e) {
            log.error("Failed to create executor: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public JobVO submitJob(JobSubmitDTO jobSubmitDTO) {
        String pipelineName = getPipelineName(jobSubmitDTO.getStatements());
        if (StringUtils.isNotBlank(pipelineName)) {
            jobSubmitDTO.setJobName(pipelineName);
        } else {
            pipelineName = jobSubmitDTO.getJobName();
            jobSubmitDTO.setStatements(
                    addPipelineNameStatement(pipelineName, jobSubmitDTO.getStatements()));
        }

        Executor executor =
                this.getExecutor(jobSubmitDTO.getClusterId(), jobSubmitDTO.getTaskType());
        if (executor == null) {
            throw new RuntimeException("No executor available for the job submission.");
        }

        try {
            ExecutionResult executionResult = executor.executeSql(jobSubmitDTO.getStatements());
            if (StringUtils.isNotBlank(executionResult.getJobId())) {
                JobInfo jobInfo = buildJobInfo(executionResult, jobSubmitDTO);
                this.save(jobInfo);
            }
            return buildJobVO(executionResult, jobSubmitDTO);
        } catch (Exception e) {
            throw new RuntimeException("Error executing job: " + e.getMessage(), e);
        }
    }

    @Override
    public ResultDataVO fetchResult(ResultFetchDTO resultFetchDTO) {
        long token = resultFetchDTO.getToken() + 1;
        try {
            Executor executor =
                    this.getExecutor(resultFetchDTO.getSessionId(), resultFetchDTO.getTaskType());
            if (executor == null) {
                throw new RuntimeException("No executor available for result fetching.");
            }
            FetchResultParams params =
                    FetchResultParams.builder()
                            .sessionId(resultFetchDTO.getSessionId())
                            .submitId(resultFetchDTO.getSubmitId())
                            .token(token)
                            .build();
            ExecutionResult executionResult = executor.fetchResults(params);
            return ResultDataVO.builder()
                    .resultData(executionResult.getData())
                    .token(token)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching result:" + e.getMessage(), e);
        }
    }

    @Override
    public List<JobVO> listJobs() {
        List<JobInfo> jobInfos = this.list();
        return jobInfos.stream().map(this::convertJobInfoToJobVO).collect(Collectors.toList());
    }

    @Override
    public List<JobVO> listJobsByPage(int current, int size) {
        QueryWrapper<JobInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        Page<JobInfo> page = new Page<>(current, size);
        IPage<JobInfo> jobInfoIPage = jobMapper.selectPage(page, queryWrapper);
        List<JobInfo> records = jobInfoIPage.getRecords();
        return records.stream().map(this::convertJobInfoToJobVO).collect(Collectors.toList());
    }

    @Override
    public JobInfo getJobByJobId(String jobId) {
        QueryWrapper<JobInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("job_id", jobId);
        return jobMapper.selectOne(queryWrapper);
    }

    @Override
    public JobStatisticsVO getJobStatistics() {
        List<JobInfo> jobInfos = this.list();

        long totalNum = jobInfos.size();
        long runningNum =
                jobInfos.stream()
                        .filter(job -> JobStatus.RUNNING.getValue().equals(job.getStatus()))
                        .count();
        long finishedNum =
                jobInfos.stream()
                        .filter(job -> JobStatus.FINISHED.getValue().equals(job.getStatus()))
                        .count();
        long canceledNum =
                jobInfos.stream()
                        .filter(job -> JobStatus.CANCELED.getValue().equals(job.getStatus()))
                        .count();
        long failedNum =
                jobInfos.stream()
                        .filter(job -> JobStatus.FAILED.getValue().equals(job.getStatus()))
                        .count();

        return JobStatisticsVO.builder()
                .totalNum(totalNum)
                .runningNum(runningNum)
                .finishedNum(finishedNum)
                .canceledNum(canceledNum)
                .failedNum(failedNum)
                .build();
    }

    @Override
    public void stop(StopJobDTO stopJobDTO) {
        try {
            Executor executor = getExecutor(stopJobDTO.getSessionId(), stopJobDTO.getTaskType());
            if (executor == null) {
                throw new RuntimeException("No executor available for job stopping.");
            }
            executor.stop(stopJobDTO.getJobId(), stopJobDTO.isWithSavepoint());
            boolean updateStatus =
                    updateJobStatusAndEndTime(
                            stopJobDTO.getJobId(),
                            JobStatus.CANCELED.getValue(),
                            LocalDateTime.now());
            if (!updateStatus) {
                log.error(
                        "Failed to update job status in the database for jobId: {}",
                        stopJobDTO.getJobId());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error stopping job:" + e.getMessage(), e);
        }
    }

    @Async
    @Scheduled(initialDelay = 60000, fixedDelay = 10000)
    public void refreshFlinkJobStatus() {
        try {
            ClusterInfo clusterInfo = getRandomCluster();
            if (clusterInfo == null) {
                throw new IllegalStateException("Failed to obtain cluster information.");
            }
            Executor executor =
                    getExecutor(String.valueOf(clusterInfo.getId()), clusterInfo.getType());
            if (executor == null) {
                throw new RuntimeException("No executor available for status refreshing.");
            }
            ExecutionResult submitResult = executor.executeSql(SHOW_JOBS_STATEMENT);
            List<Map<String, Object>> jobsData = submitResult.getData();
            for (Map<String, Object> jobData : jobsData) {
                String jobId = (String) jobData.get("job id");
                String jobStatus = (String) jobData.get("status");
                String utcTimeString = (String) jobData.get("start time");
                LocalDateTime startTime =
                        LocalDateTimeUtil.convertUtcStringToLocalDateTime(utcTimeString);
                JobInfo job = getJobByJobId(jobId);
                if (job != null) {
                    String currentStatus = job.getStatus();
                    if (!jobStatus.equals(currentStatus)) {
                        if (JobStatus.RUNNING.getValue().equals(jobStatus)) {
                            updateJobStatusAndStartTime(jobId, jobStatus, startTime);
                        } else if (JobStatus.FINISHED.getValue().equals(jobStatus)
                                || JobStatus.CANCELED.getValue().equals(jobStatus)) {
                            LocalDateTime endTime =
                                    job.getEndTime() == null
                                            ? LocalDateTime.now()
                                            : job.getEndTime();
                            updateJobStatusAndEndTime(jobId, jobStatus, endTime);
                        }
                    }
                } else {
                    log.warn("Job with ID {} not found in the database.", jobId);
                }
            }
        } catch (Exception e) {
            log.error("Exception with refreshing job status.", e);
        }
    }

    private boolean updateJobStatusAndEndTime(
            String jobId, String newStatus, LocalDateTime endTime) {
        JobInfo jobInfo = new JobInfo();
        jobInfo.setStatus(newStatus);
        jobInfo.setEndTime(endTime);
        return this.update(jobInfo, new QueryWrapper<JobInfo>().eq("job_id", jobId));
    }

    private boolean updateJobStatusAndStartTime(
            String jobId, String newStatus, LocalDateTime startTime) {
        JobInfo jobInfo = new JobInfo();
        jobInfo.setStatus(newStatus);
        jobInfo.setStartTime(startTime);
        return this.update(jobInfo, new QueryWrapper<JobInfo>().eq("job_id", jobId));
    }

    private JobVO convertJobInfoToJobVO(JobInfo jobInfo) {
        JobVO.JobVOBuilder builder =
                JobVO.builder()
                        .jobId(jobInfo.getJobId())
                        .jobName(jobInfo.getJobName())
                        .type(jobInfo.getType())
                        .executeMode(jobInfo.getExecuteMode())
                        .sessionId(jobInfo.getSessionId());
        if (jobInfo.getStartTime() != null) {
            builder.startTime(jobInfo.getStartTime());
        }
        if (jobInfo.getEndTime() != null) {
            builder.endTime(jobInfo.getEndTime());
        }
        if (StringUtils.isNotBlank(jobInfo.getStatus())) {
            builder.status(jobInfo.getStatus());
        }
        return builder.build();
    }

    private JobInfo buildJobInfo(ExecutionResult executionResult, JobSubmitDTO jobSubmitDTO) {
        JobInfo.JobInfoBuilder builder =
                JobInfo.builder()
                        .jobId(executionResult.getJobId())
                        .type(jobSubmitDTO.getTaskType())
                        .statements(jobSubmitDTO.getStatements())
                        .status(JobStatus.CREATED.getValue())
                        .sessionId(jobSubmitDTO.getClusterId());
        String jobName = jobSubmitDTO.getJobName() != null ? jobSubmitDTO.getJobName() : "";
        builder.jobName(jobName);
        if (StringUtils.isNotBlank(executionResult.getStatus())) {
            builder.status(executionResult.getStatus());
        }
        Map<String, String> config =
                MapUtils.isNotEmpty(jobSubmitDTO.getConfig())
                        ? jobSubmitDTO.getConfig()
                        : new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonConfig;
        try {
            jsonConfig = objectMapper.writeValueAsString(config);
        } catch (Exception e) {
            jsonConfig = "{}";
        }
        builder.config(jsonConfig);
        String executeMode = jobSubmitDTO.isStreaming() ? STREAMING_MODE : BATCH_MODE;
        builder.executeMode(executeMode);
        return builder.build();
    }

    private JobVO buildJobVO(ExecutionResult executionResult, JobSubmitDTO jobSubmitDTO) {
        JobVO.JobVOBuilder builder =
                JobVO.builder()
                        .type(jobSubmitDTO.getTaskType())
                        .shouldFetchResult(executionResult.shouldFetchResult())
                        .submitId(executionResult.getSubmitId())
                        .sessionId(jobSubmitDTO.getClusterId())
                        .resultData(executionResult.getData())
                        .jobName(jobSubmitDTO.getJobName())
                        .token(0L);
        String executeMode = jobSubmitDTO.isStreaming() ? STREAMING_MODE : BATCH_MODE;
        builder.executeMode(executeMode);
        if (StringUtils.isNotBlank(executionResult.getJobId())) {
            builder.jobId(executionResult.getJobId());
        }
        if (StringUtils.isNotBlank(executionResult.getStatus())) {
            builder.status(executionResult.getStatus());
        }
        return builder.build();
    }

    private String getPipelineName(String statements) {
        String regex = "set\\s+'pipeline\\.name'\\s*=\\s*'([^']+)'";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(statements);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String addPipelineNameStatement(String pipelineName, String statements) {
        return "SET 'pipeline.name' = '" + pipelineName + "';\n" + statements;
    }

    private ClusterInfo getRandomCluster() {
        List<ClusterInfo> clusters = clusterService.list();
        if (clusters == null || clusters.isEmpty()) {
            log.error("No clusters available.");
            return null;
        }

        Random random = new Random();
        int index = random.nextInt(clusters.size());
        return clusters.get(index);
    }
}
