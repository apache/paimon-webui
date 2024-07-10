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
import type { Cluster, ClusterDTO, ClusterNameParams } from './types'
import type { ResponseOptions } from '@/api/types'

/**
 * # List Cluster
 */
export function getClusterList() {
  return httpRequest.createHooks!<ResponseOptions<Cluster[]>, ClusterNameParams>({
    url: '/cluster/list',
    method: 'get',
  })
}

/**
 * # List Cluster by Deployment Mode
 */
export function getClusterListByDeploymentMode(deploymentMode: string, pageNum: number, pageSize: number) {
  return httpRequest.get('/cluster/list', {
    deploymentMode,
    pageNum,
    pageSize,
  })
}

/**
 * # List Cluster by Type
 */
export function getClusterListByType(type: string, pageNum: number, pageSize: number) {
  return httpRequest.get('/cluster/list', {
    type,
    pageNum,
    pageSize,
  })
}

/**
 * # Create Cluster
 */
export function createCluster() {
  return httpRequest.createHooks!<unknown, ClusterDTO>({
    url: '/cluster',
    method: 'post',
  })
}

/**
 * # Update Cluster
 */
export function updateCluster() {
  return httpRequest.createHooks!<unknown, ClusterDTO>({
    url: '/cluster',
    method: 'put',
  })
}

/**
 * # Delete a Cluster
 */
export function deleteCluster(userId: number) {
  return httpRequest.delete!<unknown, ClusterDTO>(`/cluster/${userId}`)
}

/**
 * # Check Cluster Status
 */
export function checkClusterStatus() {
  return httpRequest.createHooks!<unknown, ClusterDTO>({
    url: '/cluster/check',
    method: 'post',
  })
}
