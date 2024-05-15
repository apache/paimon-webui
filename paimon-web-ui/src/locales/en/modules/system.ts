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
  email: 'Email'
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
  menu_manager: 'Menu Manager',
  user_manager: 'User Manager',
  role_manager: 'Role Manager',
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
  role_delete: 'Role Delete'
}

export default { user, role, roleKey }
