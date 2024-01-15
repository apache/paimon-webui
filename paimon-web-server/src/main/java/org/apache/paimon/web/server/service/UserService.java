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

import org.apache.paimon.web.server.data.dto.LoginDTO;
import org.apache.paimon.web.server.data.model.User;
import org.apache.paimon.web.server.data.result.exception.BaseException;
import org.apache.paimon.web.server.data.vo.UserInfoVO;
import org.apache.paimon.web.server.data.vo.UserVO;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/** User Service. */
public interface UserService extends IService<User> {

    /**
     * Gets a user by ID.
     *
     * @param id the user ID
     * @return the UserVO or null if not found
     */
    UserVO getUserById(Integer id);

    /**
     * Selects users with pagination.
     *
     * @param page the pagination information
     * @param user the filter criteria
     * @return list of UserVO
     */
    List<UserVO> selectUserList(IPage<User> page, User user);

    /**
     * Lists all users.
     *
     * @return list of all UserVOs
     */
    List<UserVO> listUsers();

    /**
     * Checks if the username is unique.
     *
     * @param user the user to check
     * @return true if unique, false otherwise
     */
    boolean checkUserNameUnique(User user);

    /**
     * login by username and password.
     *
     * @param loginDTO login params
     * @return {@link String}
     */
    UserInfoVO login(LoginDTO loginDTO) throws BaseException;

    /**
     * Query the list of assigned user roles.
     *
     * @param user query params
     * @return user list
     */
    List<User> selectAllocatedList(User user);

    /**
     * Query the list of unassigned user roles.
     *
     * @param user query params
     * @return user list
     */
    List<User> selectUnallocatedList(User user);

    int insertUser(User user);
}
