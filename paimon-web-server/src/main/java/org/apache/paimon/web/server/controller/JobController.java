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
import org.apache.paimon.web.server.service.JobService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Job submit api controller. */
@Slf4j
@RestController
@RequestMapping("/api/job")
public class JobController {

    private static final String STREAMING_MODE = "Streaming";
    private static final String BATCH_MODE = "Batch";
    private static final String SHOW_JOBS_STATEMENT = "SHOW JOBS";

    @Autowired private JobService jobService;

    @PostMapping("/submit")
    public void submit(@RequestBody JobSubmitDTO jobSubmitDTO) {
        jobService.getExecutor(jobSubmitDTO);
    }
}
