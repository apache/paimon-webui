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
  metadata: '元数据管理',
  cdc: 'CDC 集成',
  playground: 'Playground',
  menu_manager: '菜单管理',
  user_manager: '用户管理',
  role_manager: '角色管理',
  cluster_manager: '集群管理',
  catalog_manager: 'Catalog 管理',
  database_manager: 'Database 管理',
  table_manager: 'Table 管理',
  column_manager: 'Column 管理',
  option_manager: 'Option 管理',
  schema_manager: 'Schema 管理',
  snapshot_manager: 'Snapshot 管理',
  manifest_manager: 'Manifest 管理',
  datafile_manager: 'Datafile 管理',
  cdc_job_manager: 'CDC 任务管理',
  playground_job_manager: 'Playground 任务管理',
  menu_query: '菜单查询',
  menu_add: '菜单新增',
  menu_update: '菜单修改',
  menu_delete: '菜单删除',
  user_query: '用户查询',
  user_add: '用户新增',
  user_update: '用户修改',
  user_delete: '用户删除',
  user_reset: '重置密码',
  role_query: '角色查询',
  role_add: '角色新增',
  role_update: '角色修改',
  role_delete: '角色删除',
  cluster_query: '集群查询',
  cluster_add: '集群新增',
  cluster_update: '集群修改',
  cluster_delete: '集群删除',
  catalog_create: 'Catalog 创建',
  catalog_remove: 'Catalog 删除',
  database_create: 'Database 创建',
  database_drop: 'Database 删除',
  table_create: 'Table 创建',
  table_update: 'Table 更新',
  table_drop: 'Table 删除',
  column_add: 'Column 创建',
  column_drop: 'Column 删除',
  option_add: 'Option 创建',
  option_remove: 'Option 删除',
  cdc_job_query: 'CDC 任务查询',
  cdc_job_update: 'CDC 任务更新',
  cdc_job_delete: 'CDC 任务删除',
  cdc_job_submit: 'CDC 任务提交',
  cdc_job_create: 'CDC 任务创建',
  playground_job_query: 'Playground 任务查询',
  playground_job_submit: 'Playground 任务提交',
  playground_job_stop: 'Playground 任务停止',
}

const cluster = {
  cluster_name: '集群名称',
  cluster_type: '集群类型',
  cluster_status: '集群状态',
  deployment_type: '部署模式',
  cluster_host: '集群地址',
  cluster_port: '集群端口',
  test_cluster: '测试集群连通性',
  enabled: '是否启用',
  create: '新增集群',
  update: '更新集群',
}

export default { user, role, roleKey, cluster }
