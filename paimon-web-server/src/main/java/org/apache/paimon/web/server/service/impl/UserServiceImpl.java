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

import org.apache.paimon.web.server.data.dto.LoginDto;
import org.apache.paimon.web.server.data.enums.UserType;
import org.apache.paimon.web.server.data.model.User;
import org.apache.paimon.web.server.data.result.exception.BaseException;
import org.apache.paimon.web.server.data.result.exception.user.UserDisabledException;
import org.apache.paimon.web.server.data.result.exception.user.UserNotExistsException;
import org.apache.paimon.web.server.data.result.exception.user.UserPasswordNotMatchException;
import org.apache.paimon.web.server.mapper.UserMapper;
import org.apache.paimon.web.server.service.LdapService;
import org.apache.paimon.web.server.service.UserService;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/** UserServiceImpl. */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired private LdapService ldapService;
    @Autowired private UserMapper userMapper;

    /**
     * login by username and password.
     *
     * @param loginDto login info
     * @return {@link String}
     */
    @Override
    public String login(LoginDto loginDto) throws BaseException {
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();

        User user =
                loginDto.isLdapLogin()
                        ? ldapLogin(username, password)
                        : localLogin(username, password);
        if (!user.getEnabled()) {
            throw new UserDisabledException();
        }

        StpUtil.login(user.getId(), loginDto.isRememberMe());

        return StpUtil.getTokenValue();
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
        if (!user.getPassword().equals(password)) {
            throw new UserPasswordNotMatchException();
        }
        return user;
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
     * @param user query params
     * @return user list
     */
    @Override
    public List<User> selectAllocatedList(User user) {
        return userMapper.selectAllocatedList(user);
    }

    /**
     * Query the list of unassigned user roles.
     *
     * @param user query params
     * @return user list
     */
    @Override
    public List<User> selectUnallocatedList(User user) {
        return userMapper.selectUnallocatedList(user);
    }
}
