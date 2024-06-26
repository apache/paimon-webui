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

import org.apache.paimon.web.server.data.dto.JobSubmitDTO;
import org.apache.paimon.web.server.data.dto.ResultFetchDTO;
import org.apache.paimon.web.server.data.dto.StopJobDTO;
import org.apache.paimon.web.server.data.model.JobInfo;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.data.vo.JobStatisticsVO;
import org.apache.paimon.web.server.data.vo.JobStatusVO;
import org.apache.paimon.web.server.data.vo.JobVO;
import org.apache.paimon.web.server.data.vo.ResultDataVO;
import org.apache.paimon.web.server.service.JobService;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Job submit api controller. */
@Slf4j
@RestController
@RequestMapping("/api/job")
public class JobController {

    @Autowired private JobService jobService;

    @SaCheckPermission("playground:job:submit")
    @PostMapping("/submit")
    public R<JobVO> submit(@RequestBody JobSubmitDTO jobSubmitDTO) {
        try {
            return R.succeed(jobService.submitJob(jobSubmitDTO));
        } catch (Exception e) {
            log.error("Exception with submitting a job.", e);
            return R.failed(Status.JOB_SUBMIT_ERROR);
        }
    }

    @SaIgnore
    @PostMapping("/fetch")
    public R<ResultDataVO> fetchResult(@RequestBody ResultFetchDTO resultFetchDTO) {
        try {
            return R.succeed(jobService.fetchResult(resultFetchDTO));
        } catch (Exception e) {
            log.error("Exception with fetching result data.", e);
            return R.failed(Status.RESULT_FETCH_ERROR);
        }
    }

    @SaCheckPermission("playground:job:list")
    @GetMapping("/list")
    public R<List<JobVO>> list() {
        return R.succeed(jobService.listJobs());
    }

    @SaCheckPermission("playground:job:list")
    @GetMapping("/list/page")
    public R<List<JobVO>> listJobsByPage(int current, int size) {
        return R.succeed(jobService.listJobsByPage(current, size));
    }

    @SaIgnore
    @GetMapping("/status/get/{jobId}")
    public R<JobStatusVO> getJobStatus(@PathVariable("jobId") String jobId) {
        JobInfo job = jobService.getJobById(jobId);
        JobStatusVO jobStatusVO =
                JobStatusVO.builder().jobId(job.getJobId()).status(job.getStatus()).build();
        return R.succeed(jobStatusVO);
    }

    @SaCheckPermission("playground:job:query")
    @GetMapping("/statistics/get")
    public R<JobStatisticsVO> getJobStatistics() {
        return R.succeed(jobService.getJobStatistics());
    }

    @SaCheckPermission("playground:job:query")
    @GetMapping("/logs/get")
    public R<String> getLogs() {
        return R.succeed(jobService.getLogsByUserId(StpUtil.getLoginIdAsString()));
    }

    @SaIgnore
    @GetMapping("/logs/clear")
    public R<String> clearLogs() {
        return R.succeed(jobService.clearLog(StpUtil.getLoginIdAsString()));
    }

    @SaCheckPermission("playground:job:stop")
    @PostMapping("/stop")
    public R<Void> stop(@RequestBody StopJobDTO stopJobDTO) {
        try {
            jobService.stop(stopJobDTO);
            return R.succeed();
        } catch (Exception e) {
            log.error("Exception with stopping a job.", e);
            return R.failed(Status.JOB_STOP_ERROR);
        }
    }

    @SaIgnore
    @PostMapping("/refresh")
    public R<Void> refresh() {
        jobService.refreshJobStatus("Flink");
        return R.succeed();
    }
}
