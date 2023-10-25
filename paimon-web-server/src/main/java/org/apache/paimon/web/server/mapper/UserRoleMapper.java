/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.paimon.web.server.mapper;

import org.apache.paimon.web.server.data.model.UserRole;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** User Role Mapper. */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
    /**
     * Delete user and role associations by user ID.
     *
     * @param userId user ID
     * @return result
     */
    int deleteUserRoleByUserId(Integer userId);

    /**
     * Batch delete user and role associations by user IDs.
     *
     * @param ids user IDs
     * @return result
     */
    int deleteUserRole(Integer[] ids);

    /**
     * Query the number of roles used by role ID.
     *
     * @param roleId role ID
     * @return result
     */
    int countUserRoleByRoleId(Integer roleId);

    /**
     * Batch Add User Role Information.
     *
     * @param userRoleList user-role list
     * @return result
     */
    int batchUserRole(List<UserRole> userRoleList);

    /**
     * Delete user-role association information.
     *
     * @param userRole user-role association
     * @return result
     */
    int deleteUserRoleInfo(UserRole userRole);

    /**
     * Batch Unauthorization of User Roles.
     *
     * @param roleId role ID
     * @param userIds user IDs
     * @return result
     */
    int deleteUserRoleInfos(@Param("roleId") Integer roleId, @Param("userIds") Integer[] userIds);
}
