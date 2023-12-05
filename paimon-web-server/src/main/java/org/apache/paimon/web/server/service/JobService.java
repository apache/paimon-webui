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

import org.apache.paimon.web.server.data.model.JobInfo;

import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;

/** Job Service. */
public interface JobService extends IService<JobInfo> {

    /**
     * Persists a new job information record in the datastore.
     *
     * @param jobInfo The JobInfo object representing the job details to be saved.
     * @return {@code true} if the job information was successfully saved, {@code false} otherwise.
     */
    boolean saveJob(JobInfo jobInfo);

    /**
     * Updates the status and end time of a job based on the given job ID.
     *
     * <p>This method is used to set a new status and record the end time for a job. It only affects
     * the job that matches the provided job ID.
     *
     * @param jobId the unique identifier of the job to update
     * @param newStatus the new status to set for the job
     * @param endTime the end time to record for the job
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
    boolean updateJobStatusAndEndTime(String jobId, String newStatus, LocalDateTime endTime);

    /**
     * Updates the status and start time of a job based on the given job ID.
     *
     * <p>This method is used to set a new status and record the start time for a job. It targets
     * the job that matches the provided job ID.
     *
     * @param jobId the unique identifier of the job to update
     * @param newStatus the new status to set for the job
     * @param startTime the start time to record for the job
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
    boolean updateJobStatusAndStartTime(String jobId, String newStatus, LocalDateTime startTime);

    /**
     * Retrieves a paginated list of job information.
     *
     * <p>This method is used to fetch a subset of jobs for pagination purposes. It returns a list
     * of {@link JobInfo} objects based on the current page and size parameters.
     *
     * @param current the current page number to retrieve
     * @param size the number of {@link JobInfo} records to retrieve per page
     * @return a list of {@link JobInfo} objects for the specified page and size
     */
    List<JobInfo> listJobsByPage(int current, int size);

    /**
     * Retrieves the job information for a specific job ID.
     *
     * <p>This method fetches the details of a single job using its unique identifier.
     *
     * @param jobId the unique identifier of the job to retrieve
     * @return the {@link JobInfo} object containing the job details, or {@code null} if not found
     */
    JobInfo getJobByJobId(String jobId);
}
