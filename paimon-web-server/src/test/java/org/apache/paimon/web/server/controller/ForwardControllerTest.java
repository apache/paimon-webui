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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/** Test for {@link ForwardController}. */
@SpringBootTest
@AutoConfigureMockMvc
public class ForwardControllerTest extends ControllerTestBase {

    @MockBean private HttpServletRequest request;

    @Test
    public void testForwardIndex() throws Exception {
        MvcResult result =
                mockMvc.perform(
                                MockMvcRequestBuilders.get("/ui/login")
                                        .requestAttr(
                                                RequestDispatcher.ERROR_STATUS_CODE,
                                                HttpStatus.NOT_FOUND.value()))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn();
        Assertions.assertEquals("forward:/ui/index.html", result.getModelAndView().getViewName());
    }
}
