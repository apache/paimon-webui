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

package org.apache.paimon.web.server.mapper;

import org.apache.paimon.web.server.data.model.SysRole;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** Role Mapper. */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    List<SysRole> selectRoleList(IPage<SysRole> page, @Param("role") SysRole role);

    /**
     * Query roles by user ID.
     *
     * @param userId user ID
     * @return role list
     */
    List<SysRole> selectRolePermissionByUserId(Integer userId);

    /**
     * Query all roles.
     *
     * @return role list
     */
    List<SysRole> selectRoleAll();

    /**
     * Obtain a list of role selection boxes by user ID.
     *
     * @param userId user ID
     * @return result
     */
    List<Integer> selectRoleListByUserId(Integer userId);

    /**
     * Query role info by role ID.
     *
     * @param roleId role ID
     * @return role info
     */
    SysRole selectRoleById(Integer roleId);

    /**
     * Query role info by user.
     *
     * @param userName user name
     * @return role list
     */
    List<SysRole> selectRolesByUserName(String userName);
}
