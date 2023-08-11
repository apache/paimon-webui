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

import axios, { AxiosResponse, AxiosError } from 'axios';

const httpClient = axios.create({
    baseURL: '/api',
    timeout:  30 * 1000,
    withCredentials: false,
    headers: {
        'Content-Type': 'application/json'
    }
});

// request interceptor.
httpClient.interceptors.request.use(
    (config: any) => {
        // Here you can set request headers like：config.headers['Token'] = localStorage.getItem('Token').
       /* config.headers = {
            "Authorization": store.getState().user.token
        }*/
        return config;
    },
    (error: AxiosError) => {
        // Handle request errors
        console.log('request:', error);
        return Promise.reject(error);
    }
);

// response interceptor.
httpClient.interceptors.response.use(
    (response: AxiosResponse) => {
        // Here you can process the response data.
        return response;
    },
    (error: AxiosError) => {
        // Handle response errors.
        console.log('response:', error);
        return Promise.reject(error);
    }
);

const httpGet = async <T, E>(url: string, param?: E): Promise<T> => {
    const {data} = await httpClient.get<T>(url, {params: param})
    return data
}

const httpPost = async <T, E>(url: string, param: E): Promise<T> => {
    const {data} = await httpClient.post<T>(url, param)
    return data
}

const httpDelete = async <T, E>(url: string, param: E): Promise<T> => {
    const {data} = await httpClient.delete(url, {params: param})
    return data
}

const httpFormPost = async <T, E>(url: string, params?: E): Promise<T> => {
    const {data} = await httpClient.post(url, params, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
    return data
}

const http = {
    httpGet,
    httpPost,
    httpDelete,
    httpFormPost
}

export default http;