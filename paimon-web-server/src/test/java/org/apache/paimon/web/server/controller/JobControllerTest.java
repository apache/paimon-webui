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

import org.apache.paimon.web.server.constant.StatementsConstant;
import org.apache.paimon.web.server.data.dto.JobSubmitDTO;
import org.apache.paimon.web.server.data.dto.LoginDTO;
import org.apache.paimon.web.server.data.dto.ResultFetchDTO;
import org.apache.paimon.web.server.data.dto.StopJobDTO;
import org.apache.paimon.web.server.data.model.ClusterInfo;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.vo.JobStatisticsVO;
import org.apache.paimon.web.server.data.vo.JobStatusVO;
import org.apache.paimon.web.server.data.vo.JobVO;
import org.apache.paimon.web.server.data.vo.ResultDataVO;
import org.apache.paimon.web.server.service.ClusterService;
import org.apache.paimon.web.server.util.ObjectMapperUtils;
import org.apache.paimon.web.server.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests for {@link JobController}. */
@SpringBootTest
@AutoConfigureMockMvc
public class JobControllerTest extends FlinkSQLGatewayTestBase {

    private static final String loginPath = "/api/login";
    private static final String logoutPath = "/api/logout";
    private static final String jobPath = "/api/job";

    @Value("${spring.application.name}")
    private String tokenName;

    @Autowired public MockMvc mockMvc;

    public static MockCookie cookie;

    @Autowired private ClusterService clusterService;

    @BeforeEach
    public void before() throws Exception {
        LoginDTO login = new LoginDTO();
        login.setUsername("admin");
        login.setPassword("admin");
        MockHttpServletResponse response =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(loginPath)
                                        .content(ObjectMapperUtils.toJSON(login))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse();
        String result = response.getContentAsString();
        R<?> r = ObjectMapperUtils.fromJSON(result, R.class);
        assertEquals(200, r.getCode());

        assertTrue(StringUtils.isNotBlank(r.getData().toString()));

        cookie = (MockCookie) response.getCookie(tokenName);
        ClusterInfo cluster =
                ClusterInfo.builder()
                        .clusterName("test_cluster")
                        .host(targetAddress)
                        .port(port)
                        .status(1)
                        .type("Flink")
                        .build();
        boolean res = clusterService.save(cluster);
        assertTrue(res);
    }

    @AfterEach
    public void after() throws Exception {
        String result =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(logoutPath)
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        R<?> r = ObjectMapperUtils.fromJSON(result, R.class);
        assertEquals(200, r.getCode());
    }

    @Test
    public void testSubmitJob() throws Exception {
        QueryWrapper<ClusterInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cluster_name", "test_cluster");
        ClusterInfo one = clusterService.getOne(queryWrapper);
        JobSubmitDTO jobSubmitDTO = new JobSubmitDTO();
        jobSubmitDTO.setJobName("flink-job-test");
        jobSubmitDTO.setTaskType("Flink");
        jobSubmitDTO.setClusterId(String.valueOf(one.getId()));
        jobSubmitDTO.setStatements(StatementsConstant.statement);

        String responseString = submit(jobSubmitDTO);

        R<JobVO> r = ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<JobVO>>() {});
        assertEquals(200, r.getCode());
        assertNotNull(r.getData());
        assertEquals("flink-job-test", r.getData().getJobName());
        assertEquals("Flink", r.getData().getType());
        assertEquals(1, r.getData().getUid());
    }

    @Test
    public void testFetchResult() throws Exception {
        QueryWrapper<ClusterInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cluster_name", "test_cluster");
        ClusterInfo one = clusterService.getOne(queryWrapper);
        JobSubmitDTO jobSubmitDTO = new JobSubmitDTO();
        jobSubmitDTO.setJobName("flink-job-test-fetch-result");
        jobSubmitDTO.setTaskType("Flink");
        jobSubmitDTO.setClusterId(String.valueOf(one.getId()));
        jobSubmitDTO.setStatements(StatementsConstant.selectStatement);

        String responseString = submit(jobSubmitDTO);
        R<JobVO> r = ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<JobVO>>() {});
        assertEquals(200, r.getCode());

        if (r.getData().getShouldFetchResult()) {
            ResultFetchDTO resultFetchDTO = new ResultFetchDTO();
            resultFetchDTO.setSubmitId(r.getData().getSubmitId());
            resultFetchDTO.setSessionId(r.getData().getSessionId());
            resultFetchDTO.setTaskType(r.getData().getType());
            resultFetchDTO.setToken(r.getData().getToken());

            String fetchResultString =
                    mockMvc.perform(
                                    MockMvcRequestBuilders.post(jobPath + "/fetch")
                                            .cookie(cookie)
                                            .content(ObjectMapperUtils.toJSON(resultFetchDTO))
                                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                                            .accept(MediaType.APPLICATION_JSON_VALUE))
                            .andExpect(MockMvcResultMatchers.status().isOk())
                            .andDo(MockMvcResultHandlers.print())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();
            R<ResultDataVO> fetchResult =
                    ObjectMapperUtils.fromJSON(
                            fetchResultString, new TypeReference<R<ResultDataVO>>() {});
            assertEquals(200, fetchResult.getCode());
            assertEquals(1, fetchResult.getData().getResultData().size());
            assertEquals(1, fetchResult.getData().getToken());
        }
    }

    @Test
    public void testListJobs() throws Exception {
        QueryWrapper<ClusterInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cluster_name", "test_cluster");
        ClusterInfo one = clusterService.getOne(queryWrapper);
        JobSubmitDTO jobSubmitDTO = new JobSubmitDTO();
        jobSubmitDTO.setJobName("flink-job-test-list-jobs");
        jobSubmitDTO.setTaskType("Flink");
        jobSubmitDTO.setClusterId(String.valueOf(one.getId()));
        jobSubmitDTO.setStatements(StatementsConstant.selectStatement);

        String responseString = submit(jobSubmitDTO);
        R<JobVO> r = ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<JobVO>>() {});
        assertEquals(200, r.getCode());

        String listJobResponseStr =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(jobPath + "/list")
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        R<List<JobVO>> listJobRes =
                ObjectMapperUtils.fromJSON(
                        listJobResponseStr, new TypeReference<R<List<JobVO>>>() {});
        assertEquals(200, listJobRes.getCode());
        assertEquals(1, listJobRes.getData().size());
        assertEquals("flink-job-test-list-jobs", listJobRes.getData().get(0).getJobName());
    }

    @Test
    public void testGetJobStatus() throws Exception {
        QueryWrapper<ClusterInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cluster_name", "test_cluster");
        ClusterInfo one = clusterService.getOne(queryWrapper);
        JobSubmitDTO jobSubmitDTO = new JobSubmitDTO();
        jobSubmitDTO.setJobName("flink-job-test-get-job-status");
        jobSubmitDTO.setTaskType("Flink");
        jobSubmitDTO.setClusterId(String.valueOf(one.getId()));
        jobSubmitDTO.setStatements(StatementsConstant.selectStatement);

        String responseString = submit(jobSubmitDTO);
        R<JobVO> r = ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<JobVO>>() {});
        assertEquals(200, r.getCode());

        String listJobResponseStr = getJobStatus(r.getData().getJobId());
        R<JobStatusVO> getJobStatusRes =
                ObjectMapperUtils.fromJSON(
                        listJobResponseStr, new TypeReference<R<JobStatusVO>>() {});
        assertEquals(200, getJobStatusRes.getCode());
        assertEquals(r.getData().getJobId(), getJobStatusRes.getData().getJobId());
        assertNotNull(getJobStatusRes.getData().getStatus());
    }

    @Test
    public void testGetJobStatistics() throws Exception {
        QueryWrapper<ClusterInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cluster_name", "test_cluster");
        ClusterInfo one = clusterService.getOne(queryWrapper);
        JobSubmitDTO jobSubmitDTO = new JobSubmitDTO();
        jobSubmitDTO.setJobName("flink-job-test-get-job-statistics");
        jobSubmitDTO.setTaskType("Flink");
        jobSubmitDTO.setClusterId(String.valueOf(one.getId()));
        jobSubmitDTO.setStatements(StatementsConstant.selectStatement);
        String responseString = submit(jobSubmitDTO);
        R<JobVO> r = ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<JobVO>>() {});
        assertEquals(200, r.getCode());

        String getJobStatisticsResponseStr =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(jobPath + "/statistics/get")
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        R<JobStatisticsVO> getJobStatisticsRes =
                ObjectMapperUtils.fromJSON(
                        getJobStatisticsResponseStr, new TypeReference<R<JobStatisticsVO>>() {});
        assertEquals(200, getJobStatisticsRes.getCode());
        assertEquals(1, getJobStatisticsRes.getData().getTotalNum());
    }

    @Test
    public void testStopJob() throws Exception {
        QueryWrapper<ClusterInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cluster_name", "test_cluster");
        ClusterInfo one = clusterService.getOne(queryWrapper);
        JobSubmitDTO jobSubmitDTO = new JobSubmitDTO();
        jobSubmitDTO.setJobName("flink-job-test-stop-job");
        jobSubmitDTO.setTaskType("Flink");
        jobSubmitDTO.setClusterId(String.valueOf(one.getId()));
        jobSubmitDTO.setStatements(StatementsConstant.selectStatement);
        String responseString = submit(jobSubmitDTO);
        R<JobVO> r = ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<JobVO>>() {});
        assertEquals(200, r.getCode());

        String jobStatus = getJobStatus(r.getData().getJobId());
        R<JobStatusVO> getJobStatusRes =
                ObjectMapperUtils.fromJSON(jobStatus, new TypeReference<R<JobStatusVO>>() {});
        while (!getJobStatusRes.getData().getStatus().equals("RUNNING")) {
            jobStatus = getJobStatus(r.getData().getJobId());
            getJobStatusRes =
                    ObjectMapperUtils.fromJSON(jobStatus, new TypeReference<R<JobStatusVO>>() {});
        }

        StopJobDTO stopJobDTO = new StopJobDTO();
        stopJobDTO.setJobId(r.getData().getJobId());
        stopJobDTO.setSessionId(r.getData().getSessionId());
        stopJobDTO.setTaskType(r.getData().getType());
        String stopJobString =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(jobPath + "/stop")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(stopJobDTO))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        R<Void> stopJobRes =
                ObjectMapperUtils.fromJSON(stopJobString, new TypeReference<R<Void>>() {});
        assertEquals(200, stopJobRes.getCode());

        jobStatus = getJobStatus(r.getData().getJobId());
        getJobStatusRes =
                ObjectMapperUtils.fromJSON(jobStatus, new TypeReference<R<JobStatusVO>>() {});
        assertEquals(200, getJobStatusRes.getCode());
        assertEquals("CANCELED", getJobStatusRes.getData().getStatus());
    }

    private String submit(JobSubmitDTO jobSubmitDTO) throws Exception {
        return mockMvc.perform(
                        MockMvcRequestBuilders.post(jobPath + "/submit")
                                .cookie(cookie)
                                .content(ObjectMapperUtils.toJSON(jobSubmitDTO))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    private String getJobStatus(String jobId) throws Exception {
        return mockMvc.perform(
                        MockMvcRequestBuilders.get(jobPath + "/status/get/" + jobId)
                                .cookie(cookie)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}
