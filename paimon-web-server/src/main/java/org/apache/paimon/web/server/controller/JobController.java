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

import org.apache.paimon.web.common.executor.Executor;
import org.apache.paimon.web.common.executor.ExecutorFactory;
import org.apache.paimon.web.common.result.FetchResultParams;
import org.apache.paimon.web.common.result.SubmitResult;
import org.apache.paimon.web.common.status.JobStatus;
import org.apache.paimon.web.flink.sql.gateway.model.SessionEntity;
import org.apache.paimon.web.gateway.config.ExecuteConfig;
import org.apache.paimon.web.gateway.enums.TaskType;
import org.apache.paimon.web.gateway.provider.ExecutorFactoryProvider;
import org.apache.paimon.web.server.data.dto.JobSubmitDTO;
import org.apache.paimon.web.server.data.dto.ResultFetchDTO;
import org.apache.paimon.web.server.data.dto.StopJobDTO;
import org.apache.paimon.web.server.data.model.JobInfo;
import org.apache.paimon.web.server.data.model.SelectHistory;
import org.apache.paimon.web.server.data.model.SessionInfo;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.data.vo.JobVO;
import org.apache.paimon.web.server.data.vo.ResultDataVO;
import org.apache.paimon.web.server.service.JobExecutorService;
import org.apache.paimon.web.server.service.JobService;
import org.apache.paimon.web.server.service.SelectHistoryService;
import org.apache.paimon.web.server.service.SessionService;
import org.apache.paimon.web.server.util.LocalDateTimeUtil;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/** Job submit api controller. */
@Slf4j
@RestController
@RequestMapping("/api/job")
public class JobController {

    private static final String STREAMING_MODE = "Streaming";
    private static final String BATCH_MODE = "Batch";
    private static final String SHOW_JOBS_STATEMENT = "SHOW JOBS";

    @Autowired private JobExecutorService jobExecutorService;

    @Autowired private JobService jobService;

    @Autowired private SessionService sessionService;

    @Autowired private SelectHistoryService selectHistoryService;

    /**
     * Initializes the service by loading all active sessions and preparing their respective
     * executors.
     */
    @PostConstruct
    public void init() {
        initializeExecutorsIfNeeded();
    }

    /**
     * Initializes executors for all active sessions if they have not already been initialized. This
     * method is scheduled to run at a fixed interval, starting after an initial delay. It checks
     * for active sessions and creates and adds an executor to the {@code JobExecutorService} for
     * each session that does not already have one.
     *
     * <p>This method is designed to be called asynchronously and will log information about the
     * initialization status. If no active sessions are found, it logs a message and exits. If
     * executors are successfully initialized, it logs a success message. Any exceptions encountered
     * during the initialization process are logged as errors.
     *
     * <p>This method is annotated with {@code @Async} to run in a background thread and with
     * {@code @Scheduled} to run at regular intervals defined by the {@code initialDelay} and {@code
     * fixedDelay} parameters.
     *
     * @throws Exception if there is any issue during the executor initialization process. This
     *     exception is caught and logged within the method, and does not propagate.
     */
    @Async
    @Scheduled(initialDelay = 60000, fixedDelay = 30000)
    public void initializeExecutorsIfNeeded() {
        try {
            List<SessionInfo> activeSessions = sessionService.getAllActiveSessions();
            if (activeSessions.isEmpty()) {
                log.info("No active sessions found. Will retry later.");
                return;
            }

            for (SessionInfo sessionInfo : activeSessions) {
                if (jobExecutorService.getExecutor(sessionInfo.getSessionId()) == null) {
                    SessionEntity sessionEntity = buildSessionEntity(sessionInfo);
                    ExecuteConfig config =
                            ExecuteConfig.builder().setSessionEntity(sessionEntity).build();
                    TaskType taskType = TaskType.fromValue(sessionInfo.getType().toUpperCase());
                    ExecutorFactoryProvider provider = new ExecutorFactoryProvider(config);
                    ExecutorFactory executorFactory = provider.getExecutorFactory(taskType);
                    Executor executor = executorFactory.createExecutor();

                    jobExecutorService.addExecutor(sessionEntity.getSessionId(), executor);
                }
            }

            log.info("Executors have been initialized successfully.");
        } catch (Exception e) {
            log.error("Exception during executors initialization", e);
        }
    }

    /**
     * Submits a job for execution based on the provided JobSubmitDTO.
     *
     * @param jobSubmitDTO Data transfer object containing the job submission details.
     * @return A response containing the job view object or an error status.
     */
    @PostMapping("/submit")
    public R<JobVO> submit(@RequestBody JobSubmitDTO jobSubmitDTO) {
        try {
            Executor executor = jobExecutorService.getExecutor(jobSubmitDTO.getSessionId());
            String pipelineName = getPipelineName(jobSubmitDTO.getStatements());
            if (StringUtils.isNotBlank(pipelineName)) {
                jobSubmitDTO.setJobName(pipelineName);
            } else {
                pipelineName = jobSubmitDTO.getJobName();
                jobSubmitDTO.setStatements(
                        addPipelineNameStatement(pipelineName, jobSubmitDTO.getStatements()));
            }
            SubmitResult submitResult = executor.executeSql(jobSubmitDTO.getStatements());
            if (StringUtils.isNotBlank(submitResult.getJobId())) {
                JobInfo jobInfo = buildJobInfo(submitResult, jobSubmitDTO);
                jobService.saveJob(jobInfo);
            }
            if (jobSubmitDTO.isQuery()) {
                SelectHistory selectHistory =
                        SelectHistory.builder()
                                .taskType(jobSubmitDTO.getTaskType())
                                .isStreaming(jobSubmitDTO.isStreaming())
                                .sessionId(jobSubmitDTO.getSessionId())
                                .statements(jobSubmitDTO.getStatements())
                                .build();
                selectHistoryService.saveSelectHistory(selectHistory);
            }
            return R.succeed(buildJobVO(submitResult, jobSubmitDTO));
        } catch (Exception e) {
            log.error("Exception with submitting a job.", e);
            return R.failed(Status.JOB_SUBMIT_ERROR);
        }
    }

    /**
     * Fetches the results of a previously submitted job based on the provided ResultFetchDTO.
     *
     * @param resultFetchDTO Data transfer object containing the result fetch details.
     * @return A response containing the result data view object or an error status.
     */
    @PostMapping("/fetch")
    public R<ResultDataVO> fetchResults(@RequestBody ResultFetchDTO resultFetchDTO) {
        try {
            long token = resultFetchDTO.getToken() + 1;
            Executor executor = jobExecutorService.getExecutor(resultFetchDTO.getSessionId());
            FetchResultParams params =
                    FetchResultParams.builder()
                            .sessionId(resultFetchDTO.getSessionId())
                            .submitId(resultFetchDTO.getSubmitId())
                            .token(token)
                            .build();
            SubmitResult submitResult = executor.fetchResults(params);
            ResultDataVO resultDataVO =
                    ResultDataVO.builder().resultData(submitResult.getData()).token(token).build();
            return R.succeed(resultDataVO);
        } catch (Exception e) {
            log.error("Exception with fetching result data.", e);
            return R.failed(Status.RESULT_FETCH_ERROR);
        }
    }

    /**
     * Retrieves a list of all jobs.
     *
     * <p>This method returns all the jobs available in the system without pagination. Each job is
     * converted to a {@link JobVO} before being returned.
     *
     * @return a {@link R} object containing a list of {@link JobVO} instances representing all jobs
     */
    @GetMapping("/list")
    public R<List<JobVO>> list() {
        List<JobInfo> jobInfos = jobService.list();
        List<JobVO> jobVOList =
                jobInfos.stream().map(this::convertJobInfoToJobVO).collect(Collectors.toList());
        return R.succeed(jobVOList);
    }

    /**
     * Retrieves a paginated list of jobs.
     *
     * <p>This method provides a subset of all jobs based on the given pagination parameters. Each
     * job is converted to a {@link JobVO} before being returned.
     *
     * @param current the current page number to be returned
     * @param size the number of records per page
     * @return a {@link R} object containing a list of {@link JobVO} instances for the requested
     *     page
     */
    @GetMapping("/list/page")
    public R<List<JobVO>> listJobsByPage(int current, int size) {
        List<JobInfo> jobInfos = jobService.listJobsByPage(current, size);
        List<JobVO> jobVOList =
                jobInfos.stream().map(this::convertJobInfoToJobVO).collect(Collectors.toList());
        return R.succeed(jobVOList);
    }

    /**
     * Stops a running job based on the provided job details.
     *
     * <p>This method attempts to stop a running job identified by the {@link StopJobDTO}. It also
     * updates the job's status and end time in the database if stopping the job is successful.
     *
     * @param stopJobDTO the {@link StopJobDTO} containing the job ID and stop conditions
     * @return a {@link R} object representing the outcome of the operation. A success status is
     *     returned if the job is stopped and updated successfully; otherwise, a failure status is
     *     returned.
     * @throws Exception if stopping the job fails
     */
    @PostMapping("/stop")
    public R<Void> stop(@RequestBody StopJobDTO stopJobDTO) {
        try {
            Executor executor = jobExecutorService.getExecutor(stopJobDTO.getSessionId());
            boolean status =
                    executor.stop(
                            stopJobDTO.getJobId(),
                            stopJobDTO.isWithSavepoint(),
                            stopJobDTO.isWithDrain());
            if (status) {
                boolean updateStatus =
                        jobService.updateJobStatusAndEndTime(
                                stopJobDTO.getJobId(),
                                JobStatus.CANCELED.getValue(),
                                LocalDateTime.now());
                if (!updateStatus) {
                    log.error(
                            "Failed to update job status in the database for jobId: {}",
                            stopJobDTO.getJobId());
                    return R.failed(Status.JOB_UPDATE_STATUS_ERROR);
                }
            }
            return R.succeed();
        } catch (Exception e) {
            log.error("Exception with stopping a job.", e);
            return R.failed(Status.JOB_STOP_ERROR);
        }
    }

    /**
     * Periodically refreshes the status of Flink jobs.
     *
     * <p>This scheduled task fetches the status of all jobs from the active Flink SQL Gateway
     * session and updates the job status in the system database accordingly. It is executed
     * asynchronously to avoid blocking the main thread. The task runs first after an initial delay
     * of 60 seconds and subsequently with a fixed delay of 10 seconds between the end of the last
     * invocation and the start of the next.
     */
    @Async
    @Scheduled(initialDelay = 60000, fixedDelay = 10000)
    public void refreshFlinkJobStatus() {
        try {
            Optional<SessionInfo> optionalSessionInfo =
                    sessionService.getAllActiveSessions().stream()
                            .filter(
                                    session ->
                                            session.getType()
                                                    .toUpperCase()
                                                    .equals(TaskType.FLINK_SQL_GATEWAY.getValue()))
                            .findFirst();
            if (!optionalSessionInfo.isPresent()) {
                log.info("No active Flink SQL Gateway session found. Skipping job status refresh.");
                return;
            }
            SessionInfo sessionInfo = optionalSessionInfo.get();
            Executor executor = jobExecutorService.getExecutor(sessionInfo.getSessionId());
            SubmitResult submitResult = executor.executeSql(SHOW_JOBS_STATEMENT);
            List<Map<String, Object>> jobsData = submitResult.getData();
            for (Map<String, Object> jobData : jobsData) {
                String jobId = (String) jobData.get("job id");
                String jobStatus = (String) jobData.get("status");
                String utcTimeString = (String) jobData.get("start time");
                LocalDateTime startTime =
                        LocalDateTimeUtil.convertUtcStringToLocalDateTime(utcTimeString);
                JobInfo job = jobService.getJobByJobId(jobId);
                if (job != null) {
                    String currentStatus = job.getStatus();
                    if (!jobStatus.equals(currentStatus)) {
                        if (JobStatus.RUNNING.getValue().equals(jobStatus)) {
                            jobService.updateJobStatusAndStartTime(jobId, jobStatus, startTime);
                        } else if (JobStatus.FINISHED.getValue().equals(jobStatus)
                                || JobStatus.CANCELED.getValue().equals(jobStatus)) {
                            LocalDateTime endTime =
                                    job.getEndTime() == null
                                            ? LocalDateTime.now()
                                            : job.getEndTime();
                            jobService.updateJobStatusAndEndTime(jobId, jobStatus, endTime);
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

    /** Constructs a {@link SessionEntity} from a {@link SessionInfo} instance. */
    private SessionEntity buildSessionEntity(SessionInfo sessionInfo) {
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
        return SessionEntity.builder()
                .sessionId(sessionInfo.getSessionId())
                .sessionName(sessionInfo.getSessionName())
                .status(sessionInfo.getStatus())
                .properties(propertiesMap)
                .port(sessionInfo.getPort())
                .address(sessionInfo.getAddress())
                .build();
    }

    /**
     * Creates a {@link JobInfo} object from the results of a job submission and the job submission
     * details.
     */
    private JobInfo buildJobInfo(SubmitResult submitResult, JobSubmitDTO jobSubmitDTO) {
        JobInfo.JobInfoBuilder builder =
                JobInfo.builder()
                        .jobId(submitResult.getJobId())
                        .type(jobSubmitDTO.getTaskType())
                        .statements(jobSubmitDTO.getStatements())
                        .status(JobStatus.CREATED.getValue())
                        .sessionId(jobSubmitDTO.getSessionId());
        String jobName = jobSubmitDTO.getJobName() != null ? jobSubmitDTO.getJobName() : "";
        builder.jobName(jobName);
        if (StringUtils.isNotBlank(submitResult.getStatus())) {
            builder.status(submitResult.getStatus());
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

    /** Constructs a {@link JobVO} object from the submission result and job submission details. */
    private JobVO buildJobVO(SubmitResult submitResult, JobSubmitDTO jobSubmitDTO) {
        JobVO.JobVOBuilder builder =
                JobVO.builder()
                        .type(jobSubmitDTO.getTaskType())
                        .shouldFetchResult(submitResult.shouldFetchResult())
                        .submitId(submitResult.getSubmitId())
                        .sessionId(jobSubmitDTO.getSessionId())
                        .resultData(submitResult.getData())
                        .jobName(jobSubmitDTO.getJobName())
                        .token(0L);
        String executeMode = jobSubmitDTO.isStreaming() ? STREAMING_MODE : BATCH_MODE;
        builder.executeMode(executeMode);
        if (StringUtils.isNotBlank(submitResult.getJobId())) {
            builder.jobId(submitResult.getJobId());
        }
        if (StringUtils.isNotBlank(submitResult.getStatus())) {
            builder.status(submitResult.getStatus());
        }
        return builder.build();
    }

    /** Converts a {@link JobInfo} object into a {@link JobVO} object. */
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
        return builder.build();
    }

    /** Extracts the pipeline name from a given SQL statement. */
    private String getPipelineName(String statements) {
        String regex = "set\\s+'pipeline\\.name'\\s*=\\s*'([^']+)'";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(statements);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /** Prepends a SQL statement to set the pipeline name to the provided SQL statements. */
    private String addPipelineNameStatement(String pipelineName, String statements) {
        return "SET 'pipeline.name' = '" + pipelineName + "';\n" + statements;
    }
}
