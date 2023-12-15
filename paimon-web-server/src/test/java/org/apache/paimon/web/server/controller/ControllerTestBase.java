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

import org.apache.paimon.web.server.data.dto.CatalogDTO;
import org.apache.paimon.web.server.data.dto.DatabaseDTO;
import org.apache.paimon.web.server.data.dto.LoginDTO;
import org.apache.paimon.web.server.data.dto.TableDTO;
import org.apache.paimon.web.server.data.model.TableColumn;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.util.ObjectMapperUtils;
import org.apache.paimon.web.server.util.PaimonDataType;
import org.apache.paimon.web.server.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** ControllerTestBase. */
@SpringBootTest
@AutoConfigureMockMvc
public class ControllerTestBase {

    private static final String loginPath = "/api/login";
    private static final String logoutPath = "/api/logout";

    @Value("${spring.application.name}")
    protected String tokenName;

    @Autowired public MockMvc mockMvc;

    public static MockCookie cookie;

    protected R<?> getR(ResultActions perform) throws Exception {
        MockHttpServletResponse response =
                perform.andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse();
        String result = response.getContentAsString();
        return ObjectMapperUtils.fromJSON(result, R.class);
    }

    @BeforeEach
    public void before() throws Exception {
        LoginDTO login = new LoginDTO();
        login.setUsername("admin");
        login.setPassword("admin");
        MockHttpServletResponse response =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(loginPath)
                                        .content(ObjectMapperUtils.toJSON(login))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse();
        String result = response.getContentAsString();
        R<?> r = ObjectMapperUtils.fromJSON(result, R.class);
        assertEquals(200, r.getCode());
        assertTrue(StringUtils.isNotBlank(r.getData().toString()));
        cookie = (MockCookie) response.getCookie(tokenName);
    }

    @AfterEach
    public void after() throws Exception {
        String result =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(logoutPath)
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        R<?> r = ObjectMapperUtils.fromJSON(result, R.class);
        assertEquals(200, r.getCode());
    }
}
