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
import type { User, UserDTO, UserParams } from './types'
import type { ResponseOptions } from '@/api/types'

/**
 * # List user
 */
export function getUserList() {
  return httpRequest.createHooks!<ResponseOptions<User[]>, UserParams>({
    url: '/user/list',
    method: 'get',
  })
}

/**
 * # Create user
 */
export function createUser() {
  return httpRequest.createHooks!<unknown, UserDTO>({
    url: '/user',
    method: 'post',
  })
}

/**
 * # Update user
 */
export function updateUser() {
  return httpRequest.createHooks!<unknown, UserDTO>({
    url: '/user',
    method: 'put',
  })
}

/**
 * # delete a user
 */
export function deleteUser(userId: number) {
  return httpRequest.delete!<unknown, UserDTO>(`/user/${userId}`)
}

/**
 * # Change password
 */
export function changePassword(user: UserDTO) {
  return httpRequest.post('/user/change/password', user)
}
