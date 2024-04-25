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

package org.apache.paimon.web.server.service;

import org.apache.paimon.web.engine.flink.common.executor.Executor;
import org.apache.paimon.web.server.data.dto.JobSubmitDTO;
import org.apache.paimon.web.server.data.model.JobInfo;

import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;

/** Job Service. */
public interface JobService extends IService<JobInfo> {

    boolean shouldCreateSession();

    Executor getExecutor(JobSubmitDTO jobSubmitDTO) throws Exception;

    boolean saveJob(JobInfo jobInfo);

    boolean updateJobStatusAndEndTime(String jobId, String newStatus, LocalDateTime endTime);

    boolean updateJobStatusAndStartTime(String jobId, String newStatus, LocalDateTime startTime);

    List<JobInfo> listJobsByPage(int current, int size);

    JobInfo getJobByJobId(String jobId);
}
