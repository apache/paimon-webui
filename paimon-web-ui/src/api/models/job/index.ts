/* Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License. */

import httpRequest from '../../request'
import type {JobSubmitDTO, Job, ResultFetchDTO, JobResultData, JobStatus, StopJobDTO} from "@/api/models/job/types/job"
import type {ResponseOptions} from "@/api/types"

/**
 * # Submit a job
 */
export function submitJob(jobData: JobSubmitDTO) {
  return httpRequest.post<ResponseOptions<Job>>('/job/submit', jobData)
}

/**
 * # Fetch the result of a submitted job
 */
export function fetchResult(resultFetchDTO: ResultFetchDTO) {
  return httpRequest.post<ResponseOptions<JobResultData>>('/job/fetch', resultFetchDTO)
}

/**
 * # Refresh the status of jobs
 */
export function refreshJobStatus() {
  return httpRequest.post<ResponseOptions<void>>('/job/refresh')
}

/**
 * # Fetch the status of a specific job by its ID
 */
export function getJobStatus(jobId: string) {
  return httpRequest.get<ResponseOptions<JobStatus>>(`/job/status/get/${jobId}`)
}

/**
 * # Stop a job
 */
export function stopJob(stopJobDTO: StopJobDTO) {
  return httpRequest.post<ResponseOptions<void>>('/job/stop', stopJobDTO)
}