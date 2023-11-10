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

package org.apache.paimon.web.server.controller;

import org.apache.paimon.web.server.data.model.User;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.service.UserService;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.apache.paimon.web.server.data.result.enums.Status.USER_NOT_EXIST;

/** User api controller. */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired private UserService userService;

    /**
     * get user by id.
     *
     * @param id user-id
     * @return {@link R} with {@link User}
     */
    @SaCheckPermission("system:user:query")
    @GetMapping("/{id}")
    public R<User> getUserById(@PathVariable("id") Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return R.failed(USER_NOT_EXIST);
        }
        user.setPassword(null);
        return R.succeed(user);
    }
}
