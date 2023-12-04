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
import org.apache.paimon.web.flink.sql.gateway.model.SessionEntity;
import org.apache.paimon.web.gateway.config.ExecuteConfig;
import org.apache.paimon.web.gateway.enums.TaskType;
import org.apache.paimon.web.gateway.provider.ExecutorFactoryProvider;
import org.apache.paimon.web.server.data.dto.JobSubmitDTO;
import org.apache.paimon.web.server.data.dto.ResultFetchDTO;
import org.apache.paimon.web.server.data.model.JobInfo;
import org.apache.paimon.web.server.data.model.SelectHistory;
import org.apache.paimon.web.server.data.model.SessionInfo;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.data.vo.JobVO;
import org.apache.paimon.web.server.data.vo.ResultDataVO;
import org.apache.paimon.web.server.service.JobService;
import org.apache.paimon.web.server.service.SelectHistoryService;
import org.apache.paimon.web.server.service.SessionService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/** Job submit api controller. */
@Slf4j
@RestController
@RequestMapping("/api/job")
public class JobSubmitController {

    private static final String STREAMING_MODE = "Streaming";
    private static final String BATCH_MODE = "Batch";

    private static final Map<String, Executor> AVAILABLE_EXECUTOR_LIST = new LinkedHashMap<>();

    @Autowired private JobService jobService;

    @Autowired private SessionService sessionService;

    @Autowired private SelectHistoryService selectHistoryService;

    /**
     * Submits a job for execution based on the provided JobSubmitDTO.
     *
     * @param jobSubmitDTO Data transfer object containing the job submission details.
     * @return A response containing the job view object or an error status.
     */
    @PostMapping("/submit")
    public R<JobVO> submitJob(@RequestBody JobSubmitDTO jobSubmitDTO) {
        try {
            Executor executor = getExecutor(jobSubmitDTO);
            AVAILABLE_EXECUTOR_LIST.put(jobSubmitDTO.getSessionId(), executor);
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
            Executor executor = AVAILABLE_EXECUTOR_LIST.get(resultFetchDTO.getSessionId());
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

    private Executor getExecutor(JobSubmitDTO jobSubmitDTO) throws Exception {
        SessionInfo sessionInfo = sessionService.selectSessionById(jobSubmitDTO.getSessionId());
        SessionEntity sessionEntity = buildSessionEntity(sessionInfo);
        ExecuteConfig config =
                ExecuteConfig.builder()
                        .setConfig(jobSubmitDTO.getConfig())
                        .setSessionEntity(sessionEntity)
                        .setStreaming(jobSubmitDTO.isStreaming())
                        .build();
        ExecutorFactoryProvider provider = new ExecutorFactoryProvider(config);
        TaskType taskType = TaskType.fromValue(jobSubmitDTO.getTaskType());
        ExecutorFactory executorFactory = provider.getExecutorFactory(taskType);
        return executorFactory.createExecutor();
    }

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

    private JobInfo buildJobInfo(SubmitResult submitResult, JobSubmitDTO jobSubmitDTO) {
        JobInfo.JobInfoBuilder builder =
                JobInfo.builder()
                        .jobId(submitResult.getJobId())
                        .type(jobSubmitDTO.getTaskType())
                        .statements(jobSubmitDTO.getStatements())
                        .sessionId(jobSubmitDTO.getSessionId());
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

    private JobVO buildJobVO(SubmitResult submitResult, JobSubmitDTO jobSubmitDTO) {
        JobVO.JobVOBuilder builder =
                JobVO.builder()
                        .type(jobSubmitDTO.getTaskType())
                        .shouldFetchResult(submitResult.shouldFetchResult())
                        .submitId(submitResult.getSubmitId())
                        .sessionId(jobSubmitDTO.getSessionId())
                        .resultData(submitResult.getData())
                        .token(0L);
        if (StringUtils.isNotBlank(submitResult.getJobId())) {
            builder.jobId(submitResult.getJobId());
        }
        if (StringUtils.isNotBlank(submitResult.getStatus())) {
            builder.status(submitResult.getStatus());
        }
        return builder.build();
    }
}
