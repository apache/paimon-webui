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
package org.apache.paimon.web.server.service;

import org.apache.paimon.web.server.data.model.SysMenu;
import org.apache.paimon.web.server.data.tree.TreeSelect;
import org.apache.paimon.web.server.data.vo.RouterVo;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/** Menu service. */
public interface SysMenuService extends IService<SysMenu> {
    /**
     * Query menu list by user.
     *
     * @return menu list
     */
    List<SysMenu> selectMenuList();

    /**
     * Query menu list.
     *
     * @param menu query params
     * @return menu list
     */
    List<SysMenu> selectMenuList(SysMenu menu);

    /**
     * Query permissions by user ID.
     *
     * @param userId user ID
     * @return permission List
     */
    Set<String> selectMenuPermsByUserId(Integer userId);

    /**
     * Query permissions by role ID.
     *
     * @param roleId role ID
     * @return permission List
     */
    Set<String> selectMenuPermsByRoleId(Integer roleId);

    /**
     * Query menu list by user ID.
     *
     * @param userId user ID
     * @return menu list
     */
    List<SysMenu> selectMenuTreeByUserId(Integer userId);

    /**
     * Query menu tree information by role ID.
     *
     * @param roleId role ID
     * @return selected menu list
     */
    List<Integer> selectMenuListByRoleId(Integer roleId);

    /**
     * Build router by menu.
     *
     * @param menus menu list
     * @return router list
     */
    List<RouterVo> buildMenus(List<SysMenu> menus);

    /**
     * Builder menu tree.
     *
     * @param menus menu list
     * @return menu tree
     */
    List<SysMenu> buildMenuTree(List<SysMenu> menus);

    /**
     * Builder tree select by menu.
     *
     * @param menus menu list
     * @return menu tree select
     */
    List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus);

    /**
     * Query menu info by menu ID.
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
    boolean hasChildByMenuId(Integer menuId);

    /**
     * Query menu usage quantity.
     *
     * @param menuId menu ID
     * @return result
     */
    boolean checkMenuExistRole(Integer menuId);

    /**
     * Add menu.
     *
     * @param menu menu info
     * @return result
     */
    boolean insertMenu(SysMenu menu);

    /**
     * Update menu.
     *
     * @param menu menu info
     * @return result
     */
    boolean updateMenu(SysMenu menu);

    /**
     * Delete menu.
     *
     * @param menuId menu ID
     * @return result
     */
    boolean deleteMenuById(Integer menuId);

    /**
     * Verify if the menu name is unique.
     *
     * @param menu menu info
     * @return result
     */
    boolean checkMenuNameUnique(SysMenu menu);
}
