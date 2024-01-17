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

import org.apache.paimon.web.server.data.dto.CdcJobDefinitionDTO;
import org.apache.paimon.web.server.data.model.CdcJobDefinition;
import org.apache.paimon.web.server.data.result.PageR;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.util.ObjectMapperUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Test for {@link CdcJobDefinitionController} . */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CdcJobDefinitionControllerTest extends ControllerTestBase {

    private static final String cdcJobDefinitionPath = "/api/cdc-job-definition";

    private CdcJobDefinitionDTO cdcJobDefinitionDto() {
        CdcJobDefinitionDTO cdcJobDefinitionDTO = new CdcJobDefinitionDTO();
        cdcJobDefinitionDTO.setName("1");
        cdcJobDefinitionDTO.setCdcType(0);
        cdcJobDefinitionDTO.setConfig("d");
        cdcJobDefinitionDTO.setDescription("d");
        return cdcJobDefinitionDTO;
    }

    @Test
    @Order(1)
    public void testCreateCdcJob() throws Exception {
        MockHttpServletResponse response =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(cdcJobDefinitionPath + "/create")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(cdcJobDefinitionDto()))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse();
        checkMvcResult(response, 200);
    }

    @Test
    @Order(2)
    public void testGetCdcJobDefinition() throws Exception {
        MockHttpServletResponse response =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(cdcJobDefinitionPath + "/list")
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE)
                                        .param("currentPage", "1")
                                        .param("pageSize", "10")
                                        .param("withConfig", "true"))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse();
        PageR<CdcJobDefinition> result =
                getPageR(response, new TypeReference<PageR<CdcJobDefinition>>() {});
        CdcJobDefinitionDTO cdcJobDefinitionDTO = cdcJobDefinitionDto();
        assertEquals(1, result.getTotal());
        MockHttpServletResponse getCdcJobDefinitionResponse =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(
                                                cdcJobDefinitionPath
                                                        + "/"
                                                        + result.getData().get(0).getId())
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse();
        R<CdcJobDefinition> getResult =
                getR(getCdcJobDefinitionResponse, new TypeReference<R<CdcJobDefinition>>() {});
        CdcJobDefinition cdcJobDefinition = getResult.getData();
        CdcJobDefinition realRdcJobDefinition =
                CdcJobDefinition.builder()
                        .name(cdcJobDefinitionDTO.getName())
                        .config(cdcJobDefinitionDTO.getConfig())
                        .cdcType(cdcJobDefinitionDTO.getCdcType())
                        .createUser(cdcJobDefinitionDTO.getCreateUser())
                        .description(cdcJobDefinitionDTO.getDescription())
                        .build();
        assertEquals(realRdcJobDefinition.getName(), cdcJobDefinition.getName());
        assertEquals(realRdcJobDefinition.getDescription(), cdcJobDefinition.getDescription());
        assertEquals(realRdcJobDefinition.getConfig(), cdcJobDefinition.getConfig());
    }
}
