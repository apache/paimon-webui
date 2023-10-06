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

export default [
  {
    path: '/',
    name: 'homepage',
    meta: { title: '首页' },
    redirect: { name: 'playground' },
    component: () => import('@/layouts/content'),
    children: [
      {
        path: '/playground',
        name: 'playground',
        meta: { title: '查询控制台' },
        redirect: { name: 'playground-query' },
        component: () => import('@/views/playground'),
        children: [
          {
            path: '/playground/query',
            name: 'playground-query',
            meta: { title: '查询' },
            component: () => import('@/views/playground/components/query')
          },
          {
            path: '/playground/workbench',
            name: 'playground-workbench',
            meta: { title: '工作台' },
            component: () => import('@/views/playground/components/workbench')
          },
        ]
      },
    ]
  }
]
