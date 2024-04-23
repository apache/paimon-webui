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


import type { CdcJobDefinition } from './interface'
import httpRequest from '@/api/request'
export * from './interface'

/**
 * # Create cdc job definition
 */
export const createCdcJob = (cdcJobDefinition: CdcJobDefinition) => {
  return httpRequest.post('/cdc-job-definition/create', cdcJobDefinition)
}

/**
 * # Update cdc job definition
 */
export const updateCdcJob = (cdcJobDefinition: CdcJobDefinition) => {
  return httpRequest.put(`/cdc-job-definition/update`, cdcJobDefinition)
}

/**
 * # List all cdc job definitions
 */
export const listAllCdcJob = (withConfig: boolean, currentPage: number, pageSize: number) => {
  return httpRequest.get('/cdc-job-definition/list', { withConfig, currentPage, pageSize })
}

/**
 * # Get cdc job definition detail
 */
export const getCdcJobDefinition = (id: number) => {
  return httpRequest.get(`/cdc-job-definition/${id}`)
}

/**
 * # Delete cdc job definition
 */
export const deleteCdcJobDefinition = (id: number) => {
  return httpRequest.delete(`/cdc-job-definition/${id}`)
}
