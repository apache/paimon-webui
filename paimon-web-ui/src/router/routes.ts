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

import type { RouteRecordRaw } from 'vue-router'
import playground_routes from './modules/playground'
import metadata_routes from './modules/metadata'
import cdc_ingestion_routes from './modules/cdc_ingestion'
import system from './modules/system'
import job from './modules/job'

/**
 * Basic page
 */
const basePage: RouteRecordRaw = {
  path: '/',
  name: 'homepage',
  meta: { title: 'Home' },
  redirect: { name: 'playground' },
  component: () => import('@/layouts/content'),
  children: [
    playground_routes,
    metadata_routes,
    cdc_ingestion_routes,
    system,
    job,
  ]
}
/**
 * Login page
 */
const loginPage: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/login')
  }
]


const routes: RouteRecordRaw[] = [basePage, ...loginPage]

export default routes
