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

package org.apache.paimon.web.server.service.impl;

import org.apache.paimon.web.server.data.dto.LoginDTO;
import org.apache.paimon.web.server.data.dto.RoleWithUserDTO;
import org.apache.paimon.web.server.data.dto.UserWithRolesDTO;
import org.apache.paimon.web.server.data.enums.UserType;
import org.apache.paimon.web.server.data.model.RoleMenu;
import org.apache.paimon.web.server.data.model.SysMenu;
import org.apache.paimon.web.server.data.model.SysRole;
import org.apache.paimon.web.server.data.model.User;
import org.apache.paimon.web.server.data.model.UserRole;
import org.apache.paimon.web.server.data.result.exception.BaseException;
import org.apache.paimon.web.server.data.result.exception.user.UserDisabledException;
import org.apache.paimon.web.server.data.result.exception.user.UserNotExistsException;
import org.apache.paimon.web.server.data.result.exception.user.UserPasswordNotMatchException;
import org.apache.paimon.web.server.data.vo.UserInfoVO;
import org.apache.paimon.web.server.data.vo.UserVO;
import org.apache.paimon.web.server.mapper.UserMapper;
import org.apache.paimon.web.server.mapper.UserRoleMapper;
import org.apache.paimon.web.server.service.LdapService;
import org.apache.paimon.web.server.service.RoleMenuService;
import org.apache.paimon.web.server.service.SysMenuService;
import org.apache.paimon.web.server.service.SysRoleService;
import org.apache.paimon.web.server.service.TenantService;
import org.apache.paimon.web.server.service.UserRoleService;
import org.apache.paimon.web.server.service.UserService;
import org.apache.paimon.web.server.util.StringUtils;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/** UserServiceImpl. */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired private LdapService ldapService;
    @Autowired private UserMapper userMapper;
    @Autowired private UserRoleService userRoleService;
    @Autowired private SysRoleService sysRoleService;
    @Autowired private RoleMenuService roleMenuService;
    @Autowired private SysMenuService sysMenuService;
    @Autowired private TenantService tenantService;
    @Autowired private UserRoleMapper userRoleMapper;

    @Override
    public UserVO getUserById(Integer id) {
        UserWithRolesDTO userWithRolesDTO = userMapper.selectUserWithRolesById(id);
        if (Objects.nonNull(userWithRolesDTO)) {
            return toVo(userWithRolesDTO);
        }
        return null;
    }

    @Override
    public List<UserVO> listUsers(IPage<User> page, User user) {
        return userMapper.listUsers(page, user).stream()
                .map(this::toVo)
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkUserNameUnique(User user) {
        int userId = user.getId() == null ? -1 : user.getId();
        User info = this.lambdaQuery().eq(User::getUsername, user.getUsername()).one();
        return info == null || info.getId() == userId;
    }

    /**
     * login by username and password.
     *
     * @param loginDTO login info
     * @return {@link String}
     */
    @Override
    public UserInfoVO login(LoginDTO loginDTO) throws BaseException {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        User user =
                loginDTO.isLdapLogin()
                        ? ldapLogin(username, password)
                        : localLogin(username, password);
        if (!user.getEnabled()) {
            throw new UserDisabledException();
        }
        // query user info
        UserInfoVO userInfoVo = getUserInfoVo(user);
        // todo: Currently do not bind tenants
        /*if (CollectionUtils.isEmpty(userInfoVo.getTenantList())) {
            throw new UserNotBindTenantException();
        }*/
        // Setting login user info to SaSession.
        StpUtil.login(user.getId(), loginDTO.isRememberMe());
        userInfoVo.setPermissions(StpUtil.getPermissionList());
        SaSession saSession = StpUtil.getSession();
        saSession.set(user.getId().toString(), userInfoVo);
        return userInfoVo;
    }

    /**
     * get user info. include user, role, menu. tenant.
     *
     * @param user user
     * @return {@link UserInfoVO}
     */
    private UserInfoVO getUserInfoVo(User user) {
        UserInfoVO userInfoVo = new UserInfoVO();
        userInfoVo.setUser(user);
        userInfoVo.setSaTokenInfo(StpUtil.getTokenInfo());

        // get user role list
        List<SysRole> sysRoles = new ArrayList<>();
        List<UserRole> userRoleList = userRoleService.selectUserRoleListByUserId(user);

        // get role list
        userRoleList.forEach(
                userRole -> {
                    sysRoles.add(sysRoleService.getById(userRole.getRoleId()));
                });
        userInfoVo.setRoleList(sysRoles);
        // get menu list
        List<SysMenu> sysMenus = new ArrayList<>();
        userRoleList.forEach(
                userRole -> {
                    roleMenuService
                            .list(
                                    new LambdaQueryWrapper<RoleMenu>()
                                            .eq(RoleMenu::getRoleId, userRole.getRoleId()))
                            .forEach(
                                    roleMenu -> {
                                        sysMenus.add(sysMenuService.getById(roleMenu.getMenuId()));
                                    });
                });
        userInfoVo.setSysMenuList(sysMenus);

        userInfoVo.setCurrentTenant(tenantService.getById(1));
        return userInfoVo;
    }

    private User localLogin(String username, String password) throws BaseException {
        User user =
                this.lambdaQuery()
                        .eq(User::getUsername, username)
                        .eq(User::getUserType, UserType.LOCAL.getCode())
                        .one();
        if (user == null) {
            throw new UserNotExistsException();
        }

        if (SaSecureUtil.md5(password).equals(user.getPassword())) {
            return user;
        } else {
            throw new UserPasswordNotMatchException();
        }
    }

    private User ldapLogin(String username, String password) throws BaseException {
        Optional<User> authenticate = ldapService.authenticate(username, password);
        if (!authenticate.isPresent()) {
            throw new UserPasswordNotMatchException();
        }

        User user =
                this.lambdaQuery()
                        .eq(User::getUsername, username)
                        .eq(User::getUserType, UserType.LDAP.getCode())
                        .one();
        if (user == null) {
            user = authenticate.get();
            this.save(user);
            // TODO assign default roles and tenants
        }
        return user;
    }

    /**
     * Query the list of assigned user roles.
     *
     * @param page the pagination information
     * @param roleWithUserDTO query params
     * @return user list
     */
    @Override
    public List<User> selectAllocatedList(
            IPage<RoleWithUserDTO> page, RoleWithUserDTO roleWithUserDTO) {
        return userMapper.selectAllocatedList(page, roleWithUserDTO);
    }

    /**
     * Query the list of unassigned user roles.
     *
     * @param page the pagination information
     * @param roleWithUserDTO query params
     * @return user list
     */
    @Override
    public List<User> selectUnallocatedList(
            IPage<RoleWithUserDTO> page, RoleWithUserDTO roleWithUserDTO) {
        return userMapper.selectUnallocatedList(page, roleWithUserDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertUser(User user) {
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        this.save(user);
        return insertUserRole(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateUser(User user) {
        this.updateById(user);
        userRoleMapper.deleteUserRoleByUserId(user.getId());
        return insertUserRole(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserByIds(Integer[] userIds) {
        userRoleMapper.deleteUserRole(userIds);
        return userMapper.deleteBatchIds(Arrays.asList(userIds));
    }

    @Override
    public boolean changePassword(User user) {
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        return this.updateById(user);
    }

    @Override
    public boolean updateUserStatus(User user) {
        Preconditions.checkArgument(user != null && user.getId() != null);
        return this.lambdaUpdate()
                .set(User::getEnabled, user.getEnabled())
                .eq(User::getId, user.getId())
                .update();
    }

    @Override
    public int allocateRole(User user) {
        return this.insertUserRole(user);
    }

    private int insertUserRole(User user) {
        int rows = 1;
        if (user.getRoleIds() != null && user.getRoleIds().length > 0) {
            List<UserRole> list = new ArrayList<>();
            for (Integer roleId : user.getRoleIds()) {
                UserRole userRole = new UserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(roleId);
                list.add(userRole);
            }
            if (!list.isEmpty()) {
                rows = userRoleMapper.batchUserRole(list);
            }
        }
        return rows;
    }

    private UserVO toVo(UserWithRolesDTO userWithRolesDTO) {
        return UserVO.builder()
                .id(userWithRolesDTO.getId())
                .username(userWithRolesDTO.getUsername())
                .nickname(
                        StringUtils.isNotEmpty(userWithRolesDTO.getNickname())
                                ? userWithRolesDTO.getNickname()
                                : "")
                .userType(userWithRolesDTO.getUserType())
                .mobile(
                        StringUtils.isNotEmpty(userWithRolesDTO.getMobile())
                                ? userWithRolesDTO.getMobile()
                                : "")
                .email(
                        StringUtils.isNotEmpty(userWithRolesDTO.getEmail())
                                ? userWithRolesDTO.getEmail()
                                : "")
                .enabled(userWithRolesDTO.getEnabled())
                .createTime(userWithRolesDTO.getCreateTime())
                .updateTime(userWithRolesDTO.getUpdateTime())
                .roles(userWithRolesDTO.getRoles())
                .build();
    }
}
