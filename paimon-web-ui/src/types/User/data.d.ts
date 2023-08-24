/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import {BaseBeanColumns, ExcludeNameAndEnableColumns} from "@src/types/Public/data";

/**
 * user login params
 */
interface LoginParams {
    username: string;
    password: string;
    rememberMe: boolean;
    ldapLogin: boolean;
}

/**
 * if login success, return user info and token
 */
export interface UserState {
    status: boolean;
    saTokenInfo: SaTokenInfo;
    roleList: Role[];
    tenantList: Tenant[];
    menuList: SysMenu[];
    user: User;
}


/**
 * user info
 */
export interface User extends ExcludeNameAndEnableColumns {
    username: string;
    nickname?: string;
    password?: string;
    avatar?: string;
    worknum?: string;
    userType: number;
    mobile?: string;
    enabled: boolean;
    isDelete: boolean;
}


export interface SaTokenInfo  {
    tokenName: string;
    tokenValue: string;
    isLogin: boolean;
    loginId: number;
    loginType: string;
    tokenTimeout: number;
    sessionTimeout: number;
    tokenSessionTimeout: number;
    tokenActivityTimeout: number;
    loginDevice: string;
    tag: string;
}


export interface Tenant extends ExcludeNameAndEnableColumns {
    tenantCode: string;
    isDelete: boolean;
    note?: string;
}

export interface Role  extends ExcludeNameAndEnableColumns  {
    tenantId: number;
    tenant: Tenant;
    roleCode?: string;
    roleName?: string;
    isDelete: boolean;
    note?: string;
}


export interface SysMenu extends BaseBeanColumns  {
    parentId: number, // 父级
    orderNum: number, // 排序
    path: string, // 路由
    component: string, // 组件
    type: string,// C菜单 F按钮 M目录
    display: boolean, // 菜单状态(0显示 1隐藏)
    perms: string, // 权限标识
    icon: string, // 图标
    rootMenu: boolean,
    note: string,
    children: SysMenu[],
}
