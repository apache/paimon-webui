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

import { createAxle, type AxleInstance } from '@varlet/axle'
import { createUseAxle } from '@varlet/axle/use'

import type { FetchOptions } from './types'

const axle: AxleInstance & { createHooks?: typeof createHooks } = createAxle({
  baseURL: '/api'
})
useMessage().success('dd')
axle.axios.interceptors.request.use(
  (config) => {
    // token here
    const headers = Object.assign({}, config.headers, {
      'token': '',
    });
    return Object.assign({}, config, { headers });
  },
  function (error) {
    return Promise.reject(error);
  }
);

axle.axios.interceptors.response.use(
  (response) => {
    const { code, msg } = response.data

    if (code !== 200 && msg) {
      // do something there
      return Promise.reject(response.data)
    }
    return response.data
  },
  (error) => {
    // do something there
    return Promise.reject(error)
  }
)

const useAxle = createUseAxle({
  axle
})

function createHooks<T, R>(options: FetchOptions<T, R>) {
  return useAxle(options)
}

axle.createHooks = createHooks

export default axle
