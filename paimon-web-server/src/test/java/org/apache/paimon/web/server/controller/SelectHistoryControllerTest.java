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

import org.apache.paimon.web.server.data.model.SelectHistory;
import org.apache.paimon.web.server.data.result.PageR;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.service.SelectHistoryService;
import org.apache.paimon.web.server.util.ObjectMapperUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Test for {@link SelectHistoryController}. */
@SpringBootTest
@AutoConfigureMockMvc
public class SelectHistoryControllerTest extends ControllerTestBase {

    private static final String selectHistoryPath = "/api/select/history";

    private static final int selectHistoryId = 1;
    private static final String selectHistoryName = "test";

    @Autowired private SelectHistoryService historyService;

    @BeforeEach
    public void setup() {
        SelectHistory selectHistory = new SelectHistory();
        selectHistory.setId(selectHistoryId);
        selectHistory.setName(selectHistoryName);
        selectHistory.setTaskType("Flink");
        selectHistory.setIsStreaming(false);
        selectHistory.setUid(1);
        selectHistory.setClusterId(1);
        selectHistory.setStatements("select * from table");
        historyService.saveSelectHistory(selectHistory);
    }

    @AfterEach
    public void after() {
        historyService.removeById(selectHistoryId);
    }

    @Test
    public void testSelectHistory() throws Exception {
        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(
                                                selectHistoryPath + "/" + selectHistoryId)
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<SelectHistory> r =
                ObjectMapperUtils.fromJSON(
                        responseString, new TypeReference<R<SelectHistory>>() {});
        assertEquals(200, r.getCode());
        assertNotNull(r.getData());
        assertEquals(selectHistoryName, r.getData().getName());
        assertEquals(1, r.getData().getUid());
        assertEquals(1, r.getData().getClusterId());
        assertEquals("Flink", r.getData().getTaskType());
        assertEquals(false, r.getData().getIsStreaming());
        assertEquals("select * from table", r.getData().getStatements());
    }

    @Test
    public void testListClusters() throws Exception {
        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(selectHistoryPath + "/list")
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        PageR<SelectHistory> r =
                ObjectMapperUtils.fromJSON(
                        responseString, new TypeReference<PageR<SelectHistory>>() {});
        assertTrue(
                r.getData() != null
                        && ((r.getTotal() > 0 && r.getData().size() > 0)
                                || (r.getTotal() == 0 && r.getData().size() == 0)));

        SelectHistory selectHistory = r.getData().get(0);
        assertEquals(selectHistoryName, selectHistory.getName());
        assertEquals(1, selectHistory.getUid());
        assertEquals(1, selectHistory.getClusterId());
        assertEquals("Flink", selectHistory.getTaskType());
        assertEquals(false, selectHistory.getIsStreaming());
        assertEquals("select * from table", selectHistory.getStatements());
    }
}
