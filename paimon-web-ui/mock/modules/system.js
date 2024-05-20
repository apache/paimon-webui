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

  '/menu/roleMenuTreeselect/:roleId': mockUtil({
    "code": 200,
    "msg": "Successfully",
    "data": {
      "checkedKeys": [],
      "menus": [
        {
          "id": 1,
          "label": "system",
          "children": [
            {
              "id": 300,
              "label": "menu_manager",
              "children": [
                {
                  "id": 3000,
                  "label": "menu_query"
                },
                {
                  "id": 3001,
                  "label": "menu_add"
                },
                {
                  "id": 3002,
                  "label": "menu_update"
                },
                {
                  "id": 3003,
                  "label": "menu_delete"
                }
              ]
            },
            {
              "id": 100,
              "label": "user_manager",
              "children": [
                {
                  "id": 1000,
                  "label": "user_query"
                },
                {
                  "id": 1001,
                  "label": "user_add"
                },
                {
                  "id": 1002,
                  "label": "user_update"
                },
                {
                  "id": 1003,
                  "label": "user_delete"
                },
                {
                  "id": 1004,
                  "label": "user_reset"
                }
              ]
            },
            {
              "id": 200,
              "label": "role_manager",
              "children": [
                {
                  "id": 2000,
                  "label": "role_query"
                },
                {
                  "id": 2001,
                  "label": "role_add"
                },
                {
                  "id": 2002,
                  "label": "role_update"
                },
                {
                  "id": 2003,
                  "label": "role_delete"
                }
              ]
            }
          ]
        }
      ]
    }
  })
})
