/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.paimon.web.server.controller;

import org.apache.paimon.utils.StringUtils;
import org.apache.paimon.web.flink.submit.FlinkJobSubmit;
import org.apache.paimon.web.flink.submit.SubmitType;
import org.apache.paimon.web.flink.submit.result.SubmitResult;
import org.apache.paimon.web.server.data.model.FlinkJobTask;
import org.apache.paimon.web.server.data.result.PageR;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.service.FlinkJobTaskService;
import org.apache.paimon.web.server.util.PageSupport;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.JobStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/** Flink Job Task api controller. */
@Slf4j
@RestController
@RequestMapping("/api/flink/job")
public class FlinkJobTaskController {

    @Autowired private FlinkJobTaskService flinkJobTaskService;

    /**
     * submit a Flink Job Task.
     *
     * @param id The flinkJobTask record id
     * @return submit Flink Job Task.
     */
    @GetMapping("/submitJob/{id}")
    public R<Void> submitJob(@PathVariable("id") Integer id) {
        try {
            FlinkJobTask flinkJob =
                    flinkJobTaskService.getOne(
                            new LambdaQueryWrapper<FlinkJobTask>().eq(FlinkJobTask::getId, id));
            Map<String, String> flinkConfigMap = null;
            if (!StringUtils.isBlank(flinkJob.getOtherParams())) {
                flinkConfigMap = JSONUtil.parse(flinkJob.getOtherParams()).toBean(Map.class);
            }
            Map<String, Object> conf = JSONUtil.parse(flinkJob).toBean(Map.class);
            conf.put("userJarParams", id.toString());
            FlinkJobSubmit flinkJobSubmit =
                    SubmitType.get(flinkJob.getExecutionTarget()).getFlinkJobSubmit();
            flinkJobSubmit.buildConf(conf, flinkConfigMap);
            SubmitResult result = flinkJobSubmit.submitFlinkSql();
            if (result.isSuccess()) {
                flinkJob.setJobStatus(JobStatus.RUNNING.toString());
                flinkJob.setFlinkWebUrl(result.getWebUrl());
                flinkJob.setApplicationId(result.getAppId());
                flinkJob.setJobId(result.getJobIds().get(0));
            } else {
                flinkJob.setJobStatus(JobStatus.FAILED.toString());
                log.error("submit flink job task error:{}", result.getMsg());
            }
            flinkJobTaskService.updateById(flinkJob);
            return result.isSuccess() ? R.succeed() : R.failed();
        } catch (Exception e) {
            log.error("submit flink job task error:", e);
            return R.failed(Status.FLINK_JOB_TASK_SUBMIT_ERROR);
        }
    }

    /**
     * stop a Flink Job Task.
     *
     * @param id The flinkJobTask record id
     * @return stop Flink Job Task.
     */
    @PostMapping("/stop/id")
    public R<Void> stop(@PathVariable Integer id) {
        try {
            return R.succeed();
        } catch (Exception e) {
            log.error("stop flink job task error:", e);
            return R.failed(Status.FLINK_JOB_TASK_STOP_ERROR);
        }
    }

    /**
     * Create or update a Flink Job Task.
     *
     * @param flinkJobTask The flinkJobTask
     * @return The created flinkJobTask.
     */
    @PostMapping("/createOrUpdateFlinkJobTask")
    public R<Void> createOrUpdateFlinkJobTask(@Validated @RequestBody FlinkJobTask flinkJobTask) {
        if (flinkJobTask.getId() == null && flinkJobTaskService.checkJobNameUnique(flinkJobTask)) {
            return R.failed(Status.FLINK_JOB_NAME_IS_EXIST, flinkJobTask.getJobName());
        }
        try {
            flinkJobTask.setOtherParams(JSONUtil.toJsonStr(flinkJobTask.getParams()));
            return flinkJobTaskService.saveOrUpdate(flinkJobTask) ? R.succeed() : R.failed();
        } catch (Exception e) {
            log.error("createOrUpdateFlinkJobTask error:", e);
            return R.failed(Status.FLINK_JOB_TASK_CREATE_ERROR);
        }
    }

    /** Query flink Job Task the list by page. */
    @GetMapping("/listByPage")
    public PageR<FlinkJobTask> listByPage(FlinkJobTask flinkJobTask) {
        IPage<FlinkJobTask> page = PageSupport.startPage();
        List<FlinkJobTask> list =
                flinkJobTaskService.list(
                        new LambdaQueryWrapper<FlinkJobTask>()
                                .like(FlinkJobTask::getAdminUser, flinkJobTask.getAdminUser()));
        return PageR.<FlinkJobTask>builder()
                .success(true)
                .total(page.getTotal())
                .data(list)
                .build();
    }

    /**
     * delete a flink job task by its ID.
     *
     * @param jobId The ID of the flink job task to be removed.
     * @return A response indicating the success or failure of the delete operation.
     */
    @DeleteMapping("/delete/{jobId}")
    public R<Void> delete(@PathVariable Integer jobId) {
        return flinkJobTaskService.removeById(jobId) ? R.succeed() : R.failed();
    }
}
