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

import org.apache.paimon.web.server.data.dto.LoginDTO;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.vo.UserInfoVO;
import org.apache.paimon.web.server.service.UserService;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Login api controller. */
@Slf4j
@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired private CacheManager cacheManager;

    @Autowired private UserService userService;

    /**
     * login by username and password.
     *
     * @param loginDTO login info
     * @return token string
     */
    @PostMapping("/login")
    public R<UserInfoVO> login(@RequestBody LoginDTO loginDTO) {
        return R.succeed(userService.login(loginDTO));
    }

    /**
     * get token info.
     *
     * @return token info
     */
    @GetMapping("/tokenInfo")
    public R<SaTokenInfo> tokenInfo() {
        return R.succeed(StpUtil.getTokenInfo());
    }

    /** logout. */
    @PostMapping("/logout")
    public R<Void> logout() {
        Cache userCache = cacheManager.getCache("userCache");
        if (userCache != null) {
            userCache.evict(StpUtil.getLoginIdAsInt());
        }
        StpUtil.logout(StpUtil.getLoginIdAsInt());
        return R.succeed();
    }
}
