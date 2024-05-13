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

module.exports = (mockUtil) => ({
  '/role/list': mockUtil({
    "total": "2",
    "success": true,
    "data": [
      {
        "id": 1,
        "createTime": "2024-05-11T13:49:27",
        "updateTime": null,
        "roleName": "admin",
        "roleKey": "admin",
        "sort": 1,
        "enabled": true,
        "isDelete": false,
        "remark": null,
        "flag": false,
        "menuIds": null,
        "permissions": null
      },
      {
        "id": 2,
        "createTime": "2024-05-11T13:49:27",
        "updateTime": null,
        "roleName": "common",
        "roleKey": "common",
        "sort": 2,
        "enabled": true,
        "isDelete": false,
        "remark": null,
        "flag": false,
        "menuIds": null,
        "permissions": null
      }
    ]
  }),
})
