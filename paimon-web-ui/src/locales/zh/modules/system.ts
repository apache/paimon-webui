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

const user = {
  add: '新增',
  username: '用户名',
  nickname: '昵称',
  mobile: '手机号',
  email: '邮箱',
  password: '密码',

  create: '新增用户',
  update: '更新用户',

  enabled: '是否启用',
  roleIds: '角色',
}

const role = {
  create: '新增角色',
  update: '更新角色',
  role_name: '角色名称',
  role_key: '角色编码',
  enabled: '是否启用',
  remark: '备注',
  permission_setting: '权限配置',
  no_permission: '暂无权限',
}

const roleKey = {
  system: '系统管理',
  menu_manager: '菜单管理',
  user_manager: '用户管理',
  role_manager: '角色管理',
  menu_query: '菜单查询',
  menu_add: '菜单新增',
  menu_update: '菜单修改',
  menu_delete: '菜单删除',
  user_query: '用户查询',
  user_add: '用户新增',
  user_update: '用户修改',
  user_delete: '用户删除',
  user_reset: '用户重置密码',
  role_query: '角色查询',
  role_add: '角色新增',
  role_update: '角色修改',
  role_delete: '角色删除',
}

const cluster = {
  cluster_name: '集群名称',
  cluster_type: '集群类型',
  cluster_host: '集群地址',
  cluster_port: '集群端口',
  enabled: '是否启用',
  create: '新增集群',
  update: '更新集群',
}

export default { user, role, roleKey, cluster }
