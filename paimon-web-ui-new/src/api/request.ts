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

import { createAxle } from '@varlet/axle'
import { createUseAxle, type UseAxleInstance, type UseAxleOptions } from '@varlet/axle/use'

import type ResponseOptions from './types'


type RequestConfigOptions<D, R = any, P = Record<string, any>> = Omit<UseAxleOptions<D, ResponseOptions<R>, P>, 'data'> & Pick<UseAxleOptions<D, R, P>, 'data'>

type AxleConfigOptions<R = any, P = Record<string, any>> = Omit<RequestConfigOptions<R, P>, 'url' | 'runner'>

export type HttpRequestOptions<R, P = any> = Omit<AxleConfigOptions<R, P>, 'data'>

export const axle = createAxle({
  baseURL: '/api'
})
const { t } = useLocaleHooks()
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
      debugger
      window.$message.error(t(msg))
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
  // any transform in here
})

class HttpRequest {
  private request<R = any, P = any>(options: RequestConfigOptions<R, P>): UseAxleInstance<R, ResponseOptions<P>, Record<string, any>> {
    return useAxle(options)
  }

  get<R = any, P = any>(url: string, options?: AxleConfigOptions<R, P>) {
    return this.request<R, P>({
      url,
      runner: axle.get,
      data: null as any,
      ...options
    })
  }

  post<R = any, P = any>(url: string, options: HttpRequestOptions<R, P> = {}) {
    debugger
    
    return this.request<R, P>({
      url,
      runner: axle.post,
      data: null as any,
      ...options
    })
  }

  put<R = any, P = any>(url: string, options: HttpRequestOptions<R, P> = {}) {
    return this.request<R, P>({
      url,
      runner: axle.put,
      data: null as any,
      ...options
    })
  }

  delete<R = any, P = any>(url: string, options: HttpRequestOptions<R, P> = {}) {
    return this.request<R, P>({
      url,
      runner: axle.delete,
      data: null as any,
      ...options
    })
  }
}

export default new HttpRequest()
