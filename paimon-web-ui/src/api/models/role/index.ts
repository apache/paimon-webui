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
import type { ResponseOptions } from '@/api/types'
import type { Role, RoleDTO, RoleDetail, RoleMenu, RoleParams } from './types/role'


/**
 * # create a role
 */
export const createRole = () => {
  return httpRequest.createHooks!<unknown, RoleDTO>({
    url: '/role',
    method: 'post'
  })
}


/**
 * # update a role
 */
export const updateRole = () => {
  return httpRequest.createHooks!<unknown, RoleDTO>({
    url: '/role',
    method: 'put'
  })
}

/**
 * # delete a role
 */
export const deleteRole = (roleId: number) => {
  return httpRequest.delete!<unknown, RoleDTO>(`/role/${roleId}`)
}


/**
 * # permission tree
 */
export const getPermissionTree = () => {
  return httpRequest.get!<string, ResponseOptions<RoleMenu[]>>(`/menu/treeselect`)
}


/**
 * # permission tree by role Id
 */
export const getPermissionByRoleId = (roleId: number) => {
  return httpRequest.get!<string, ResponseOptions<RoleDetail>>(`/menu/roleMenuTreeselect/${roleId}`)
}

/**
 * # List roles
 */
export const listRoles = () => {
  return httpRequest.createHooks!<ResponseOptions<Role[]>, RoleParams>({
    url: '/role/list',
    method: 'get',
  })
}
