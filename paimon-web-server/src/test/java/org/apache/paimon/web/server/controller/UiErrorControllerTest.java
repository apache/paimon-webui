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

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

/** Test for {@link UiErrorController}. */
@SpringBootTest
@AutoConfigureMockMvc
public class UiErrorControllerTest extends ControllerTestBase {

    private static final String NOT_FOUND = "/ui/login";

    @Test
    public void testError() throws Exception {
        MockHttpServletResponse response =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(NOT_FOUND)
                                        .cookie(cookie)
                                        .accept(MediaType.ALL))
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse();
    }
}
