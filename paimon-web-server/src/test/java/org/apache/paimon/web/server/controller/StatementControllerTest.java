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

import org.apache.paimon.web.server.data.model.StatementInfo;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Test for {@link StatementController}. */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StatementControllerTest extends ControllerTestBase {

    private static final String statementPath = "/api/statement";

    private static final int statementId = 1;
    private static final String statementName = "test_query";

    @Test
    @Order(1)
    public void testAddStatement() throws Exception {
        StatementInfo statementInfo = new StatementInfo();
        statementInfo.setId(statementId);
        statementInfo.setStatementName(statementName);
        statementInfo.setTaskType("Flink");
        statementInfo.setIsStreaming(false);
        statementInfo.setStatements("select * from table");

        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(statementPath)
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(statementInfo))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<?> result = ObjectMapperUtils.fromJSON(responseString, R.class);
        assertEquals(200, result.getCode());

        StatementInfo statement = getStatementInfo();
        assertEquals(statementName, statement.getStatementName());
        assertEquals(false, statement.getIsStreaming());
        assertEquals("Flink", statement.getTaskType());
        assertEquals("select * from table", statement.getStatements());
    }

    @Test
    @Order(2)
    public void testGetStatement() throws Exception {
        StatementInfo statementInfo = getStatementInfo();
        assertEquals(statementName, statementInfo.getStatementName());
        assertEquals(false, statementInfo.getIsStreaming());
        assertEquals("Flink", statementInfo.getTaskType());
        assertEquals("select * from table", statementInfo.getStatements());
    }

    @Test
    @Order(3)
    public void testListStatements() throws Exception {
        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(statementPath + "/list")
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        PageR<StatementInfo> r =
                ObjectMapperUtils.fromJSON(
                        responseString, new TypeReference<PageR<StatementInfo>>() {});
        assertTrue(
                r.getData() != null
                        && ((r.getTotal() > 0 && r.getData().size() > 0)
                                || (r.getTotal() == 0 && r.getData().size() == 0)));

        StatementInfo statementInfo = r.getData().get(0);
        assertEquals(statementName, statementInfo.getStatementName());
        assertEquals(false, statementInfo.getIsStreaming());
        assertEquals("Flink", statementInfo.getTaskType());
        assertEquals("select * from table", statementInfo.getStatements());
    }

    @Test
    @Order(4)
    public void testUpdateStatement() throws Exception {
        StatementInfo statementInfo = new StatementInfo();
        statementInfo.setId(statementId);
        statementInfo.setStatements("select * from table limit 10");

        mockMvc.perform(
                        MockMvcRequestBuilders.put(statementPath)
                                .cookie(cookie)
                                .content(ObjectMapperUtils.toJSON(statementInfo))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        assertNotNull(getStatementInfo());
        assertEquals("select * from table limit 10", getStatementInfo().getStatements());
    }

    @Test
    @Order(5)
    public void testDeleteStatement() throws Exception {
        String delResponseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.delete(statementPath + "/" + statementId)
                                        .cookie(cookie)
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

    private StatementInfo getStatementInfo() throws Exception {
        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(statementPath + "/" + statementId)
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<StatementInfo> r =
                ObjectMapperUtils.fromJSON(
                        responseString, new TypeReference<R<StatementInfo>>() {});
        assertEquals(200, r.getCode());
        return r.getData();
    }
}
