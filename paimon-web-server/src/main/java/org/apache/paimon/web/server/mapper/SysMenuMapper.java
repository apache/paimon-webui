/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.paimon.web.server.mapper;

import org.apache.paimon.web.server.data.model.SysMenu;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** Menu mapper. */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    /**
     * Query system menu list.
     *
     * @param menu query params
     * @return result
     */
    List<SysMenu> selectMenuList(@Param("menu") SysMenu menu);

    /**
     * Query all menu perms.
     *
     * @return permission List
     */
    List<String> selectMenuPerms();

    /**
     * Query system menu list by user.
     *
     * @param menu query params
     * @return menu list
     */
    List<SysMenu> selectMenuListByUserId(
            @Param("menu") SysMenu menu, @Param("userId") Integer userId);

    /**
     * Query permissions based on role ID.
     *
     * @param roleId role ID
     * @return permission List
     */
    List<String> selectMenuPermsByRoleId(Integer roleId);

    /**
     * Query permissions by user ID.
     *
     * @param userId user ID
     * @return permission List
     */
    List<String> selectMenuPermsByUserId(Integer userId);

    /**
     * Query all menu list.
     *
     * @return menu list
     */
    List<SysMenu> selectMenuTreeAll();

    /**
     * Query menu list by user ID.
     *
     * @param userId user ID
     * @return menu list
     */
    List<SysMenu> selectMenuTreeByUserId(Integer userId);

    /**
     * Query menu tree information based on role ID.
     *
     * @param roleId role ID
     * @return selected menu list
     */
    List<Integer> selectMenuListByRoleId(Integer roleId);

    /**
     * Query information by menu ID.
     *
     * @param menuId menu ID
     * @return menu info
     */
    SysMenu selectMenuById(Integer menuId);

    /**
     * Is there a menu sub node present.
     *
     * @param menuId menu ID
     * @return result
     */
    int hasChildByMenuId(Integer menuId);

    /**
     * Verify if the menu name is unique.
     *
     * @param menuName menu name
     * @param parentId parent ID
     * @return result
     */
    SysMenu checkMenuNameUnique(
            @Param("menuName") String menuName, @Param("parentId") Integer parentId);
}
