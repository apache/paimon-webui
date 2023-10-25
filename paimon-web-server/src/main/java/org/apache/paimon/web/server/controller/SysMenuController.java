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

package org.apache.paimon.web.server.controller;

import org.apache.paimon.web.server.constant.Constants;
import org.apache.paimon.web.server.data.model.SysMenu;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.data.tree.TreeSelect;
import org.apache.paimon.web.server.data.vo.RoleMenuTreeselectVo;
import org.apache.paimon.web.server.data.vo.RouterVo;
import org.apache.paimon.web.server.service.SysMenuService;
import org.apache.paimon.web.server.util.StringUtils;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** menu controller. */
@RestController
@RequestMapping("/api/menu")
public class SysMenuController {
    @Autowired private SysMenuService menuService;

    /** Get menu list. */
    @SaCheckPermission("system:menu:list")
    @GetMapping("/list")
    public R<List<SysMenu>> list(SysMenu menu) {
        List<SysMenu> menus = menuService.selectMenuList(menu);
        return R.succeed(menus);
    }

    /** Get menu info by menuId. */
    @SaCheckPermission("system:menu:query")
    @GetMapping(value = "/{menuId}")
    public R<SysMenu> getInfo(@PathVariable Integer menuId) {
        return R.succeed(menuService.selectMenuById(menuId));
    }

    /** Get menu drop-down tree list. */
    @GetMapping("/treeselect")
    public R<List<TreeSelect>> treeselect(SysMenu menu) {
        List<SysMenu> menus = menuService.selectMenuList(menu);
        return R.succeed(menuService.buildMenuTreeSelect(menus));
    }

    /** Load the corresponding character menu list tree. */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public R<RoleMenuTreeselectVo> roleMenuTreeselect(@PathVariable("roleId") Integer roleId) {
        List<SysMenu> menus = menuService.selectMenuList();

        List<TreeSelect> treeMenus = menuService.buildMenuTreeSelect(menus);
        List<Integer> checkedKeys = menuService.selectMenuListByRoleId(roleId);
        return R.succeed(new RoleMenuTreeselectVo(checkedKeys, treeMenus));
    }

    /** add new menu. */
    @SaCheckPermission("system:menu:add")
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysMenu menu) {
        if (!menuService.checkMenuNameUnique(menu)) {
            return R.failed(Status.MENU_NAME_IS_EXIST, menu.getMenuName());
        } else if (Constants.YES_FRAME == menu.getIsFrame()
                && !StringUtils.isHttp(menu.getPath())) {
            return R.failed(Status.MENU_PATH_INVALID, menu.getPath());
        }
        return menuService.insertMenu(menu) ? R.succeed() : R.failed();
    }

    /** update menu. */
    @SaCheckPermission("system:menu:edit")
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysMenu menu) {
        if (!menuService.checkMenuNameUnique(menu)) {
            return R.failed(Status.MENU_NAME_IS_EXIST, menu.getMenuName());
        } else if (Constants.YES_FRAME == menu.getIsFrame()
                && !StringUtils.isHttp(menu.getPath())) {
            return R.failed(Status.MENU_PATH_INVALID, menu.getPath());
        }
        return menuService.updateMenu(menu) ? R.succeed() : R.failed();
    }

    /** delete menu. */
    @SaCheckPermission("system:menu:remove")
    @DeleteMapping("/{menuId}")
    public R<Void> remove(@PathVariable("menuId") Integer menuId) {
        if (menuService.hasChildByMenuId(menuId) || menuService.checkMenuExistRole(menuId)) {
            return R.failed(Status.MENU_IN_USED);
        }
        return menuService.deleteMenuById(menuId) ? R.succeed() : R.failed();
    }

    /** Get router list. */
    @GetMapping("/getRouters")
    public R<List<RouterVo>> getRouters() {
        int userId = StpUtil.getLoginIdAsInt();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return R.succeed(menuService.buildMenus(menus));
    }
}
