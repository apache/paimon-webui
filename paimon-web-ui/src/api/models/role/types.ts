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

export interface RoleDetail {
  checkedKeys: number[]
  menus: RoleMenu[]
}

export interface RoleMenu {
  id: number
  label: string
  children: RoleMenu[]
}

export interface Role {
  id: number
  createTime: string
  updateTime?: string
  roleName: string
  roleKey: string
  sort: number
  enabled: boolean
  isDelete: boolean
  admin?: boolean
  remark?: string
  flag: boolean
  menuIds: null
  permissions: null
}

export interface RoleParams {
  roleName?: string
  currentPage: number
  pageSize: number
}

export interface RoleDTO {
  id?: number
  roleName: string
  roleKey: string
  enabled: boolean
  remark?: string
  menuIds: number[]
}
