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

import type { ExecutionMode } from './type'
import type { Job, JobResultData } from '@/api/models/job/types/job'

export interface JobState {
  executionMode: ExecutionMode
  currentJob: Job | null
  jobResultData: JobResultData | null
  jobStatus: string
  executionTime: string
  jobLog: string
}

export const useJobStore = defineStore({
  id: 'job',
  state: (): JobState => ({
    executionMode: 'Streaming',
    currentJob: null,
    jobResultData: null,
    jobStatus: '',
    executionTime: '0m:0s',
    jobLog: '',
  }),
  persist: false,
  getters: {
    getExecutionMode(): ExecutionMode {
      return this.executionMode
    },
    getCurrentJob(): Job | null {
      return this.currentJob
    },
    getJobResultData(): JobResultData | null {
      return this.jobResultData
    },
    getColumns(): number {
      if (this.currentJob && this.currentJob.resultData && this.currentJob.resultData.length > 0)
        return Object.keys(this.currentJob.resultData[0]).length
      else if (this.jobResultData && this.jobResultData.resultData && this.jobResultData.resultData.length > 0)
        return Object.keys(this.jobResultData.resultData[0]).length
      else
        return 0
    },
    getRows(): number {
      if (this.currentJob && this.currentJob.resultData && this.currentJob.resultData.length > 0)
        return this.currentJob.resultData.length
      else if (this.jobResultData && this.jobResultData.resultData && this.jobResultData.resultData.length > 0)
        return this.jobResultData.resultData.length
      else
        return 0
    },
    getJobStatus(): string {
      return this.jobStatus
    },
    getExecutionTime(): string {
      return this.executionTime
    },
    getJobLog(): string {
      return this.jobLog
    },
  },
  actions: {
    setExecutionMode(executionMode: ExecutionMode) {
      this.executionMode = executionMode
    },
    setCurrentJob(currentJob: Job) {
      this.currentJob = currentJob
    },
    setJobResultData(jobResultData: JobResultData) {
      this.jobResultData = jobResultData
    },
    setJobStatus(jobStatus: string) {
      this.jobStatus = jobStatus
    },
    setExecutionTime(executionTime: string) {
      this.executionTime = executionTime
    },
    setJobLog(jobLog: string) {
      this.jobLog = jobLog
    },
    resetCurrentResult() {
      this.currentJob = null
      this.jobResultData = null
      this.jobStatus = ''
      this.executionTime = '0m:0s'
    },
  },
})
