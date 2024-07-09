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

import type { ExecutionMode, JobDetails } from './type'
import type { Job, JobResultData } from '@/api/models/job/types/job'

export interface JobState {
  jobs: Record<string, JobDetails>
  jobLog?: string
}

export const useJobStore = defineStore({
  id: 'job',
  state: (): JobState => ({
    jobs: {},
    jobLog: '',
  }),
  persist: false,
  getters: {
    getJobDetails(state): (key: string) => JobDetails | undefined {
      return key => state.jobs[key]
    },
    getCurrentJob(state): (key: string) => Job | null {
      return key => state.jobs[key]?.job
    },
    getExecutionMode(state): (key: string) => string | undefined {
      return key => state.jobs[key]?.executionMode
    },
    getJobStatus(state): (key: string) => string {
      return (key) => {
        if (!state.jobs[key])
          return 'UNKNOWN'

        return state.jobs[key].jobStatus
      }
    },
    getJobResultData(state): (key: string) => JobResultData | null {
      return key => state.jobs[key]?.jobResultData
    },
    getExecutionTime(state): (key: string) => number {
      return (key) => {
        if (!state.jobs[key])
          return 0

        return state.jobs[key].executionTime
      }
    },
    getLoading(state): (key: string) => boolean {
      return key => state.jobs[key].loading
    },
    getColumns(state): (key: string) => number {
      return (key) => {
        const initResultData = state.jobs[key]?.job?.resultData
        const refreshedResultData = state.jobs[key]?.jobResultData?.resultData
        if (initResultData && initResultData.length > 0)
          return Object.keys(initResultData[0]).length

        if (refreshedResultData && refreshedResultData.length > 0)
          return Object.keys(refreshedResultData[0]).length

        return 0
      }
    },
    getRows(state): (key: string) => number {
      return (key) => {
        const initResultData = state.jobs[key]?.job?.resultData
        const refreshedResultData = state.jobs[key]?.jobResultData?.resultData
        if (initResultData && initResultData.length > 0)
          return initResultData.length

        if (refreshedResultData && refreshedResultData.length > 0)
          return refreshedResultData.length

        return 0
      }
    },
    getJobLog(): string | undefined {
      return this.jobLog
    },
  },
  actions: {
    addJob(key: string, jobDetails: JobDetails) {
      this.jobs[key] = jobDetails
    },
    updateJobStatus(key: string, jobStatus: string) {
      if (this.jobs[key])
        this.jobs[key].jobStatus = jobStatus
    },
    updateExecutionMode(key: string, executionMode: ExecutionMode) {
      if (this.jobs[key])
        this.jobs[key].executionMode = executionMode
    },
    updateJob(key: string, currentJob: Job) {
      if (this.jobs[key])
        this.jobs[key].job = currentJob
    },
    updateLoading(key: string, loading: boolean) {
      if (this.jobs[key])
        this.jobs[key].loading = loading
    },
    updateJobResultData(key: string, jobResultData: JobResultData) {
      if (this.jobs[key])
        this.jobs[key].jobResultData = jobResultData
    },
    setJobLog(jobLog: string) {
      this.jobLog = jobLog
    },
    startJobTimer(key: string) {
      if (this.jobs[key]) {
        this.jobs[key].timerId = window.setInterval(() => {
          this.jobs[key].executionTime = Math.floor((Date.now() - this.jobs[key].startTime) / 1000)
        }, 3000)
      }
    },
    stopJobTimer(key: string) {
      if (this.jobs[key] && this.jobs[key].timerId) {
        clearInterval(this.jobs[key].timerId)
        this.jobs[key].executionTime = Math.floor((Date.now() - this.jobs[key].startTime) / 1000)
      }
    },
    resetJob(key: string) {
      if (this.jobs[key]) {
        this.jobs[key] = {
          executionMode: 'Streaming',
          job: null,
          jobResultData: null,
          jobStatus: '',
          executionTime: 0,
          startTime: 0,
          displayResult: false,
          loading: false,
        }
      }
    },
    removeJob(key: string) {
      if (this.jobs[key])
        delete this.jobs[key]
    },
  },
})
