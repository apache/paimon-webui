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

import org.apache.paimon.web.server.data.model.SysMenu;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.tree.TreeSelect;
import org.apache.paimon.web.server.data.vo.RoleMenuTreeselectVo;
import org.apache.paimon.web.server.util.ObjectMapperUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Test for SysMenuController. */
@SpringBootTest
@AutoConfigureMockMvc
public class SysMenuControllerTest extends ControllerTestBase {

    private static final String menuPath = "/api/menu";

    @Test
    public void testList() throws Exception {
        String result =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(menuPath + "/list")
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        R<List<SysMenu>> r =
                ObjectMapperUtils.fromJSON(result, new TypeReference<R<List<SysMenu>>>() {});
        assertEquals(200, r.getCode());
        assertNotNull(r.getData());
        assertTrue(r.getData().size() > 0);
    }

    @Test
    public void testGetInfo() throws Exception {
        String result =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(menuPath + "/1")
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        R<SysMenu> r = ObjectMapperUtils.fromJSON(result, new TypeReference<R<SysMenu>>() {});
        assertEquals(200, r.getCode());
        assertNotNull(r.getData());
        assertEquals(1, (int) r.getData().getId());
    }

    @Test
    public void testGetTreeselect() throws Exception {
        String result =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(menuPath + "/treeselect")
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        R<List<TreeSelect>> r =
                ObjectMapperUtils.fromJSON(result, new TypeReference<R<List<TreeSelect>>>() {});
        assertEquals(200, r.getCode());
        assertNotNull(r.getData());
        assertTrue(r.getData().size() > 0);
    }

    @Test
    public void testGetRoleMenuTreeselect() throws Exception {
        String result =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(menuPath + "/roleMenuTreeselect/1")
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        R<RoleMenuTreeselectVo> r =
                ObjectMapperUtils.fromJSON(result, new TypeReference<R<RoleMenuTreeselectVo>>() {});
        assertEquals(200, r.getCode());
        assertNotNull(r.getData());
        assertNotNull(r.getData().getMenus());
        assertNotNull(r.getData().getCheckedKeys());
    }
}
