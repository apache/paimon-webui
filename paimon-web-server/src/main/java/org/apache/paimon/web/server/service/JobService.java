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

import org.apache.paimon.web.server.data.dto.JobSubmitDTO;
import org.apache.paimon.web.server.data.dto.ResultFetchDTO;
import org.apache.paimon.web.server.data.dto.StopJobDTO;
import org.apache.paimon.web.server.data.model.JobInfo;
import org.apache.paimon.web.server.data.vo.JobStatisticsVO;
import org.apache.paimon.web.server.data.vo.JobVO;
import org.apache.paimon.web.server.data.vo.ResultDataVO;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/** Job Service. */
public interface JobService extends IService<JobInfo> {

    /**
     * Submits a new job based on the provided job submission data.
     *
     * @param jobSubmitDTO the data transfer object containing job submission details
     * @return the job view object after submission
     */
    JobVO submitJob(JobSubmitDTO jobSubmitDTO);

    /**
     * Fetches the result of a job specified by the result fetch data transfer object.
     *
     * @param resultFetchDTO the data transfer object containing job result fetching details
     * @return result data view object containing the job results
     */
    ResultDataVO fetchResult(ResultFetchDTO resultFetchDTO);

    /**
     * Lists all jobs.
     *
     * @return a list of job view objects
     */
    List<JobVO> listJobs();

    /**
     * Lists jobs in a paginated format.
     *
     * @param current the current page number
     * @param size the number of jobs per page
     * @return a list of job view objects for the specified page
     */
    List<JobVO> listJobsByPage(int current, int size);

    /**
     * Retrieves detailed information about a job identified by its job ID.
     *
     * @param id the unique identifier of the job
     * @return job information object
     */
    JobInfo getJobById(String id);

    /**
     * Retrieves statistics about jobs.
     *
     * @return a job statistics view object containing aggregated job data
     */
    JobStatisticsVO getJobStatistics();

    /**
     * Stops a job as specified by the stop job data transfer object.
     *
     * @param stopJobDTO the data transfer object containing job stopping details
     */
    void stop(StopJobDTO stopJobDTO);

    /**
     * Refreshes the status of a job based on the specified task type.
     *
     * @param taskType the type of task for which the job status needs to be updated
     */
    void refreshJobStatus(String taskType);

    /**
     * Fetches log entries for a given user ID.
     *
     * @param userId the user's ID
     * @return log entries as a string
     */
    String getLogsByUserId(String userId);

    /**
     * Clears the log associated with the specified user.
     *
     * @param userId the user's ID
     * @return log entries as a string
     */
    String clearLog(String userId);
}
