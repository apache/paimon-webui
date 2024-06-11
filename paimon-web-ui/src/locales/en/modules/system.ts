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
  add: 'Add',
  username: 'Username',
  nickname: 'Nickname',
  mobile: 'Mobile',
  email: 'Email',
  password: 'Password',

  create: 'Create User',
  update: 'Update User',

  enabled: 'Enabled',
  roleIds: 'Role',
}

const role = {
  create: 'Create Role',
  update: ' Update Role',
  role_name: 'Role name',
  role_key: 'Role Key',
  enabled: 'Enabled',
  remark: 'Remark',
  permission_setting: 'Permission Setting',
  no_permission: 'No Permission',
}

const roleKey = {
  system: 'System',
  metadata: 'MataData',
  cdc: 'CDC Ingestion',
  playground: 'Playground',
  menu_manager: 'Menu Manager',
  user_manager: 'User Manager',
  role_manager: 'Role Manager',
  cluster_manager: 'Cluster Manager',
  catalog_manager: 'Catalog Manager',
  database_manager: 'Database Manager',
  table_manager: 'Table Manager',
  column_manager: 'Column Manager',
  option_manager: 'Option Manager',
  schema_manager: 'Schema Manager',
  snapshot_manager: 'Snapshot Manager',
  manifest_manager: 'Manifest Manager',
  datafile_manager: 'Datafile Manager',
  cdc_job_manager: 'CDC Job Manager',
  playground_job_manager: 'Playground Job Manager',
  menu_query: 'Menu Query',
  menu_add: 'Menu Add',
  menu_update: 'Menu Update',
  menu_delete: 'Menu Delete',
  user_query: 'User Query',
  user_add: 'User Add',
  user_update: 'User Update',
  user_delete: 'User Delete',
  user_reset: 'User Reset',
  role_query: 'Role Query',
  role_add: 'Role Add',
  role_update: 'Role Update',
  role_delete: 'Role Delete',
  cluster_query: 'Cluster Query',
  cluster_add: 'Cluster Create',
  cluster_update: 'Cluster Update',
  cluster_delete: 'Cluster Delete',
  catalog_create: 'Catalog Create',
  catalog_remove: 'Catalog Delete',
  database_create: 'Database Create',
  database_drop: 'Database Delete',
  table_create: 'Table Create',
  table_update: 'Table Update',
  table_drop: 'Table Delete',
  column_add: 'Column Create',
  column_drop: 'Column Delete',
  option_add: 'Option Create',
  option_remove: 'Option Delete',
  cdc_job_query: 'CDC Job Query',
  cdc_job_update: 'CDC Job Update',
  cdc_job_delete: 'CDC Job Delete',
  cdc_job_submit: 'CDC Job Submit',
  cdc_job_create: 'CDC Job Create',
  playground_job_query: 'Playground job Query',
  playground_job_submit: 'Playground Job Submit',
  playground_job_stop: 'Playground Job Stop',
}

const cluster = {
  cluster_name: 'Cluster Name',
  cluster_type: 'Cluster Type',
  cluster_host: 'Cluster Host',
  cluster_port: 'Cluster Port',
  enabled: 'Enabled',
  create: 'Create Cluster',
  update: 'Update Cluster',
}

export default { user, role, roleKey, cluster }
