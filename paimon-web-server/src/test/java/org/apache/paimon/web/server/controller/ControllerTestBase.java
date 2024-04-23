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
import org.apache.paimon.web.server.data.result.PageR;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.util.ObjectMapperUtils;
import org.apache.paimon.web.server.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** ControllerTestBase. */
@SpringBootTest
@AutoConfigureMockMvc
public class ControllerTestBase {

    private static final String loginPath = "/api/login";
    private static final String logoutPath = "/api/logout";

    @Value("${spring.application.name}")
    private String tokenName;

    @Autowired public MockMvc mockMvc;

    public static MockCookie cookie;

    @TempDir java.nio.file.Path tempFile;

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

    protected R<?> getR(ResultActions perform) throws Exception {
        MockHttpServletResponse response =
                perform.andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse();
        String result = response.getContentAsString();
        return ObjectMapperUtils.fromJSON(result, R.class);
    }

    protected R<?> getR(MockHttpServletResponse response) throws Exception {
        String result = response.getContentAsString();
        return ObjectMapperUtils.fromJSON(result, R.class);
    }

    protected <T> R<T> getR(MockHttpServletResponse response, TypeReference<R<T>> typeReference)
            throws Exception {
        String result = response.getContentAsString();
        return ObjectMapperUtils.fromJSON(result, typeReference);
    }

    protected PageR<?> getPageR(MockHttpServletResponse response) throws Exception {
        String result = response.getContentAsString();
        return ObjectMapperUtils.fromJSON(result, PageR.class);
    }

    protected <T> PageR<T> getPageR(
            MockHttpServletResponse response, TypeReference<PageR<T>> typeReference)
            throws Exception {
        String result = response.getContentAsString();
        return ObjectMapperUtils.fromJSON(result, typeReference);
    }

    protected void checkMvcResult(MockHttpServletResponse response, int exceptedStatus)
            throws Exception {
        R<?> r = getR(response);
        assertEquals(exceptedStatus, r.getCode());
    }

    protected void checkMvcPageResult(MockHttpServletResponse response, boolean exceptedStatus)
            throws Exception {
        PageR<?> pageR = getPageR(response);
        assertEquals(pageR.isSuccess(), exceptedStatus);
    }
}
