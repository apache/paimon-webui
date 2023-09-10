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
/*import { Notification } from '@douyinfe/semi-ui';*/
import {UserState} from "@src/types/User/data";
import {Result} from "@src/types/Public/data";

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

        /*console.log(localStorage.getItem('token'))*/

        // Here you can set request headers like：config.headers['Token'] = localStorage.getItem('Token').
        config.headers['Authorization'] = localStorage.getItem('token');

        /*config.headers = {
            "Authorization": localStorage.getItem('token')
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
        const { data: reponseData , config: reponseConfig} = response;

        // 处理 config 获取 baseUrl 和 url  拼接 判断是登录接口 , 如果是 拿到相应的数据 存储到 localStorage 中 并设置到请求头中
        if (reponseConfig.url === '/login') {
            const {data} = reponseData as Result<UserState>
            /*console.log('登录接口', data)*/
            //todo: use store
            localStorage.setItem('token', data.saTokenInfo.tokenValue)
        }

        // Handle response data
       /* Notification.error({
            title: 'Error',
            content: `${reponseData.msg}`
        });*/
        return response;
    },
    (error: AxiosError) => {
        // Handle response errors.
        console.log('response:', error);
        return Promise.reject(error);
    }
);

const httpGet = async <T, E>(url: string, param?: E, beforeCallBack? : () => void , afterCallBack? : () => void): Promise<T> => {
    beforeCallBack && beforeCallBack()
    const {data} = await httpClient.get<T>(url, {params: param})
    afterCallBack && afterCallBack()
    return data
}

const httpPost = async <T, E>(url: string, body: E, beforeCallBack? : () => void , afterCallBack? : () => void): Promise<T> => {
    beforeCallBack && beforeCallBack()
    const {data} = await httpClient.post<T>(url, body)
    afterCallBack && afterCallBack()
    return data
}

const httpDelete = async <T, E extends { [key: string]: any } | string>(url: string, params: E, beforeCallBack?: () => void, afterCallBack?: () => void): Promise<T> => {
    beforeCallBack && beforeCallBack();
    let urlWithParams = url;
    if (typeof params === 'string') {
        urlWithParams += `/${params}`;
    } else {
        urlWithParams += `/${Object.values(params).join('/')}`;
    }
    const { data } = await httpClient.delete(urlWithParams);
    afterCallBack && afterCallBack();
    return data;
};

const httpFormPost = async <T, E>(url: string, body?: E , beforeCallBack? : () => void , afterCallBack? : () => void): Promise<T> => {
    beforeCallBack && beforeCallBack()
    const {data} = await httpClient.post(url, body, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
    afterCallBack && afterCallBack()
    return data
}

const http = {
    httpGet,
    httpPost,
    httpDelete,
    httpFormPost
}

export default http;