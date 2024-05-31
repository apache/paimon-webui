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
import type { Job, JobResultData, JobStatus, JobSubmitDTO, ResultFetchDTO, StopJobDTO } from '@/api/models/job/types/job'
import type { ResponseOptions } from '@/api/types'

/**
 * # Submit a job
 */
export function submitJob(jobSubmitDTO: JobSubmitDTO) {
  return httpRequest.post<JobSubmitDTO, ResponseOptions<Job>>('/job/submit', jobSubmitDTO)
}

/**
 * # Fetch the result of a submitted job
 */
export function fetchResult(resultFetchDTO: ResultFetchDTO) {
  return httpRequest.post<ResultFetchDTO, ResponseOptions<JobResultData>>('/job/fetch', resultFetchDTO)
}

/**
 * # Refresh the status of jobs
 */
export function refreshJobStatus() {
  return httpRequest.post('/job/refresh')
}

/**
 * # Fetch the status of a specific job by its ID
 */
export function getJobStatus(jobId: string) {
  return httpRequest.get<string, ResponseOptions<JobStatus>>(`/job/status/get/${jobId}`)
}

/**
 * # Stop a job
 */
export function stopJob(stopJobDTO: StopJobDTO) {
  return httpRequest.post<StopJobDTO, ResponseOptions<void>>('/job/stop', stopJobDTO)
}
