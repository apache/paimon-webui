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

package org.apache.paimon.web.server.service.impl;

import org.apache.paimon.web.server.constant.Constants;
import org.apache.paimon.web.server.data.enums.MenuType;
import org.apache.paimon.web.server.data.model.SysMenu;
import org.apache.paimon.web.server.data.model.User;
import org.apache.paimon.web.server.data.tree.TreeSelect;
import org.apache.paimon.web.server.data.vo.MetaVO;
import org.apache.paimon.web.server.data.vo.RouterVO;
import org.apache.paimon.web.server.mapper.RoleMenuMapper;
import org.apache.paimon.web.server.mapper.SysMenuMapper;
import org.apache.paimon.web.server.mapper.SysRoleMapper;
import org.apache.paimon.web.server.service.SysMenuService;
import org.apache.paimon.web.server.util.StringUtils;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Menu service. */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
        implements SysMenuService {
    public static final String PREMISSION_STRING = "perms[\"{0}\"]";

    @Autowired private SysMenuMapper menuMapper;

    @Autowired private SysRoleMapper roleMapper;

    @Autowired private RoleMenuMapper roleMenuMapper;

    /**
     * Query menu list by user.
     *
     * @return menu list
     */
    @Override
    public List<SysMenu> selectMenuList() {
        return selectMenuList(new SysMenu());
    }

    /**
     * Query menu list.
     *
     * @param menu query params
     * @return menu list
     */
    @Override
    public List<SysMenu> selectMenuList(SysMenu menu) {
        List<SysMenu> menuList;
        int userId = StpUtil.getLoginIdAsInt();
        if (User.isAdmin(userId)) {
            menuList = menuMapper.selectMenuList(menu);
        } else {
            menuList = menuMapper.selectMenuListByUserId(menu, userId);
        }
        return menuList;
    }

    /**
     * Query permissions by user ID.
     *
     * @param userId user ID
     * @return permission List
     */
    @Override
    public Set<String> selectMenuPermsByUserId(Integer userId) {
        List<String> perms = menuMapper.selectMenuPermsByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * Query permissions by role ID.
     *
     * @param roleId role ID
     * @return permission List
     */
    @Override
    public Set<String> selectMenuPermsByRoleId(Integer roleId) {
        List<String> perms = menuMapper.selectMenuPermsByRoleId(roleId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * Query menu list by user ID.
     *
     * @param userId user ID
     * @return menu list
     */
    @Override
    public List<SysMenu> selectMenuTreeByUserId(Integer userId) {
        List<SysMenu> menus = null;
        if (userId != null && userId == 1) {
            menus = menuMapper.selectMenuTreeAll();
        } else {
            menus = menuMapper.selectMenuTreeByUserId(userId);
        }
        return getChildPerms(menus, 0);
    }

    /**
     * Query menu tree information by role ID.
     *
     * @param roleId role ID
     * @return selected menu list
     */
    @Override
    public List<Integer> selectMenuListByRoleId(Integer roleId) {
        return menuMapper.selectMenuListByRoleId(roleId);
    }

    /**
     * Build router by menu.
     *
     * @param menus menu list
     * @return router list
     */
    @Override
    public List<RouterVO> buildMenus(List<SysMenu> menus) {
        List<RouterVO> routers = new LinkedList<RouterVO>();
        for (SysMenu menu : menus) {
            RouterVO router = new RouterVO();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQuery());
            router.setMeta(
                    new MetaVO(
                            menu.getMenuName(),
                            menu.getIcon(),
                            menu.getIsCache() == 1,
                            menu.getPath()));
            List<SysMenu> cMenus = menu.getChildren();
            if (!CollectionUtils.isEmpty(cMenus) && MenuType.DIR.getType().equals(menu.getType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            } else if (isMenuFrame(menu)) {
                router.setMeta(null);
                List<RouterVO> childrenList = new ArrayList<RouterVO>();
                RouterVO children = new RouterVO();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(StringUtils.capitalize(menu.getPath()));
                children.setMeta(
                        new MetaVO(
                                menu.getMenuName(),
                                menu.getIcon(),
                                menu.getIsCache() == 1,
                                menu.getPath()));
                children.setQuery(menu.getQuery());
                childrenList.add(children);
                router.setChildren(childrenList);
            } else if (menu.getParentId() == 0 && isInnerLink(menu)) {
                router.setMeta(new MetaVO(menu.getMenuName(), menu.getIcon()));
                router.setPath("/");
                List<RouterVO> childrenList = new ArrayList<RouterVO>();
                RouterVO children = new RouterVO();
                String routerPath = innerLinkReplaceEach(menu.getPath());
                children.setPath(routerPath);
                children.setComponent(Constants.INNER_LINK);
                children.setName(StringUtils.capitalize(routerPath));
                children.setMeta(new MetaVO(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * Builder menu tree.
     *
     * @param menus menu list
     * @return menu tree
     */
    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        List<Integer> tempList = menus.stream().map(SysMenu::getId).collect(Collectors.toList());
        for (Iterator<SysMenu> iterator = menus.iterator(); iterator.hasNext(); ) {
            SysMenu menu = (SysMenu) iterator.next();
            if (!tempList.contains(menu.getParentId())) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList;
    }

    /**
     * Builder tree select by menu.
     *
     * @param menus menu list
     * @return menu tree select
     */
    @Override
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus) {
        List<SysMenu> menuTrees = buildMenuTree(menus);
        return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * Query menu info by menu ID.
     *
     * @param menuId menu ID
     * @return menu info
     */
    @Override
    public SysMenu selectMenuById(Integer menuId) {
        return menuMapper.selectMenuById(menuId);
    }

    /**
     * Is there a menu sub node present.
     *
     * @param menuId menu ID
     * @return result
     */
    @Override
    public boolean hasChildByMenuId(Integer menuId) {
        int result = menuMapper.hasChildByMenuId(menuId);
        return result > 0;
    }

    /**
     * Query menu usage quantity.
     *
     * @param menuId menu ID
     * @return result
     */
    @Override
    public boolean checkMenuExistRole(Integer menuId) {
        int result = roleMenuMapper.checkMenuExistRole(menuId);
        return result > 0;
    }

    /**
     * Add menu.
     *
     * @param menu menu info
     * @return result
     */
    @Override
    public boolean insertMenu(SysMenu menu) {
        return this.save(menu);
    }

    /**
     * Update menu.
     *
     * @param menu menu info
     * @return result
     */
    @Override
    public boolean updateMenu(SysMenu menu) {
        return this.updateById(menu);
    }

    /**
     * Delete menu.
     *
     * @param menuId menu ID
     * @return result
     */
    @Override
    public boolean deleteMenuById(Integer menuId) {
        return this.removeById(menuId);
    }

    /**
     * Verify if the menu name is unique.
     *
     * @param menu menu info
     * @return result
     */
    @Override
    public boolean checkMenuNameUnique(SysMenu menu) {
        Integer menuId = menu.getId() == null ? -1 : menu.getId();
        SysMenu info = menuMapper.checkMenuNameUnique(menu.getMenuName(), menu.getParentId());
        return info != null && !menuId.equals(info.getId());
    }

    /**
     * Get router name.
     *
     * @param menu menu info
     * @return router name
     */
    public String getRouteName(SysMenu menu) {
        String routerName = StringUtils.capitalize(menu.getPath());
        if (isMenuFrame(menu)) {
            routerName = StringUtils.EMPTY;
        }
        return routerName;
    }

    /**
     * Get router path.
     *
     * @param menu menu info
     * @return router path
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = menu.getPath();
        if (menu.getParentId() != 0 && isInnerLink(menu)) {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        if (0 == menu.getParentId()
                && MenuType.DIR.getType().equals(menu.getType())
                && Constants.NO_FRAME == menu.getIsFrame()) {
            routerPath = "/" + menu.getPath();
        } else if (isMenuFrame(menu)) {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * Get component information.
     *
     * @param menu menu info
     * @return component info
     */
    public String getComponent(SysMenu menu) {
        String component = Constants.LAYOUT;
        if (StringUtils.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu)) {
            component = menu.getComponent();
        } else if (StringUtils.isEmpty(menu.getComponent())
                && menu.getParentId() != 0
                && isInnerLink(menu)) {
            component = Constants.INNER_LINK;
        } else if (StringUtils.isEmpty(menu.getComponent()) && isParentView(menu)) {
            component = Constants.PARENT_VIEW;
        }
        return component;
    }

    /**
     * Is it a menu internal jump.
     *
     * @param menu menu info
     * @return result
     */
    public boolean isMenuFrame(SysMenu menu) {
        return menu.getParentId() == 0
                && MenuType.MENU.getType().equals(menu.getType())
                && menu.getIsFrame().equals(Constants.NO_FRAME);
    }

    /**
     * Is it an internal chain component.
     *
     * @param menu menu info
     * @return result
     */
    public boolean isInnerLink(SysMenu menu) {
        return menu.getIsFrame().equals(Constants.NO_FRAME) && StringUtils.isHttp(menu.getPath());
    }

    /**
     * Is parent_view component.
     *
     * @param menu menu info
     * @return result
     */
    public boolean isParentView(SysMenu menu) {
        return menu.getParentId() != 0 && MenuType.DIR.getType().equals(menu.getType());
    }

    /**
     * Get all child nodes by the parent node ID.
     *
     * @param list menu list
     * @param parentId parent ID
     * @return menu list
     */
    public List<SysMenu> getChildPerms(List<SysMenu> list, int parentId) {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        for (SysMenu t : list) {
            if (t.getParentId() == parentId) {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    private void recursionFn(List<SysMenu> list, SysMenu t) {
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t) {
        List<SysMenu> tlist = new ArrayList<SysMenu>();
        for (SysMenu n : list) {
            if (n.getParentId().equals(t.getId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    private boolean hasChild(List<SysMenu> list, SysMenu t) {
        return getChildList(list, t).size() > 0;
    }

    public String innerLinkReplaceEach(String path) {
        return StringUtils.replaceEach(
                path,
                new String[] {Constants.HTTP, Constants.HTTPS, Constants.WWW, "."},
                new String[] {"", "", "", "/"});
    }
}
