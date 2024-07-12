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

export interface Job {
  submitId: string
  jobId: string
  jobName: string
  fileName: string
  type: string
  executeMode: string
  clusterId: string
  sessionId: string
  uid: number
  status?: string
  shouldFetchResult: boolean
  resultData: ResultDataItem[]
  token: number
  startTime: string
  endTime: string
}

export interface JobSubmitDTO {
  jobName: string
  fileName: string
  taskType: string
  clusterId: string
  config?: {
    [key: string]: string
  }
  statements: string
  streaming: boolean
  maxRows: number
}

export interface JobResultData {
  resultData: ResultDataItem[]
  columns: number
  rows: number
  token: number
}

export interface ResultDataItem {
  [key: string]: any
}

export interface ResultFetchDTO {
  submitId: string
  clusterId: string
  sessionId: string
  taskType: string
  token: number
}

export interface JobStatus {
  jobId: string
  status: string
}

export interface StopJobDTO {
  clusterId: string
  jobId: string
  taskType: string
  withSavepoint: boolean
}
