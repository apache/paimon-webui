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

package org.apache.paimon.web.server.controller;

import org.apache.paimon.web.server.data.dto.LoginDto;
import org.apache.paimon.web.server.data.model.SysRole;
import org.apache.paimon.web.server.data.result.PageR;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.util.ObjectMapperUtils;
import org.apache.paimon.web.server.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Test for SysRoleController. */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SysRoleControllerTest {

    private static final String rolePath = "/api/role";
    private static final String loginPath = "/api/login";
    private static final String logoutPath = "/api/logout";

    private static final int roleId = 3;
    private static final String roleName = "test";

    @Value("${spring.application.name}")
    private String tokenName;

    @Autowired private MockMvc mockMvc;

    private String token;

    @BeforeEach
    public void before() throws Exception {
        LoginDto login = new LoginDto();
        login.setUsername("admin");
        login.setPassword("21232f297a57a5a743894a0e4a801fc3");

        String result =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(loginPath)
                                        .content(ObjectMapperUtils.toJSON(login))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        R<?> r = ObjectMapperUtils.fromJSON(result, R.class);
        assertEquals(200, r.getCode());

        assertTrue(StringUtils.isNotBlank(r.getData().toString()));

        this.token = r.getData().toString();
    }

    @AfterEach
    public void after() throws Exception {
        String result =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(logoutPath)
                                        .header(tokenName, token)
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

    @Test
    @Order(1)
    public void testAddRole() throws Exception {
        SysRole sysRole = new SysRole();
        sysRole.setId(roleId);
        sysRole.setRoleName(roleName);
        sysRole.setRoleKey(roleName);
        sysRole.setSort(3);
        sysRole.setEnabled(true);
        sysRole.setIsDelete(false);
        sysRole.setRemark(roleName);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(rolePath)
                                .header(tokenName, token)
                                .content(ObjectMapperUtils.toJSON(sysRole))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(2)
    public void testQueryRole() throws Exception {
        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(rolePath + "/" + roleId)
                                        .header(tokenName, token)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<SysRole> r =
                ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<SysRole>>() {});
        assertEquals(200, r.getCode());
        assertNotNull(r.getData());
        assertEquals(r.getData().getRoleName(), roleName);
    }

    @Test
    @Order(3)
    public void testEditRole() throws Exception {
        String newRoleName = roleName + "-edit";
        SysRole sysRole = new SysRole();
        sysRole.setId(roleId);
        sysRole.setRoleName(newRoleName);
        sysRole.setRoleKey(newRoleName);
        sysRole.setSort(3);
        sysRole.setEnabled(true);
        sysRole.setIsDelete(false);
        sysRole.setRemark(newRoleName);

        mockMvc.perform(
                        MockMvcRequestBuilders.put(rolePath)
                                .header(tokenName, token)
                                .content(ObjectMapperUtils.toJSON(sysRole))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(rolePath + "/" + roleId)
                                        .header(tokenName, token)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<SysRole> r =
                ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<SysRole>>() {});
        assertEquals(200, r.getCode());
        assertNotNull(r.getData());
        assertEquals(r.getData().getRoleName(), newRoleName);
    }

    @Test
    @Order(5)
    public void testDeleteRole() throws Exception {
        String delResponseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.delete(rolePath + "/" + roleId)
                                        .header(tokenName, token)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<?> result = ObjectMapperUtils.fromJSON(delResponseString, R.class);
        assertEquals(200, result.getCode());
    }

    @Test
    @Order(4)
    public void testGetRoleList() throws Exception {
        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(rolePath + "/list")
                                        .header(tokenName, token)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        PageR<?> r = ObjectMapperUtils.fromJSON(responseString, PageR.class);
        assertNotNull(r);
        assertTrue(
                r.getData() != null
                        && ((r.getTotal() > 0 && r.getData().size() > 0)
                                || (r.getTotal() == 0 && r.getData().size() == 0)));
    }
}
