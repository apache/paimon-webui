/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.paimon.web.server.service;

import org.apache.paimon.web.server.constant.Constants;

import cn.dev33.satoken.stp.StpInterface;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Permission service by sa-token. TODO add cache */
@Component
public class PermissionService implements StpInterface {
    @Autowired private SysRoleService roleService;

    @Autowired private SysMenuService menuService;

    /**
     * Get permission list by user ID.
     *
     * @param userId user id
     * @param loginType login type
     * @return permission list
     */
    @Override
    public List<String> getPermissionList(Object userId, String loginType) {
        Preconditions.checkArgument(userId != null);
        int userIdNum = Integer.parseInt(userId.toString());
        Set<String> perms = new HashSet<String>();
        if (userIdNum == Constants.ADMIN_ID) {
            perms.add(Constants.ALL_PERMISSION);
        } else {
            List<Integer> roles = roleService.selectRoleListByUserId(userIdNum);
            if (!CollectionUtils.isEmpty(roles)) {
                for (int roleId : roles) {
                    Set<String> rolePerms = menuService.selectMenuPermsByRoleId(roleId);
                    perms.addAll(rolePerms);
                }
            } else {
                perms.addAll(menuService.selectMenuPermsByUserId(userIdNum));
            }
        }
        return new ArrayList<>(perms);
    }

    /**
     * Get role list by user ID.
     *
     * @param userId user ID
     * @param loginType login type
     * @return role list
     */
    @Override
    public List<String> getRoleList(Object userId, String loginType) {
        Preconditions.checkArgument(userId != null);
        Set<String> roles =
                new HashSet<String>(
                        roleService.selectRolePermissionByUserId(
                                Integer.valueOf(userId.toString())));
        return new ArrayList<>(roles);
    }
}
