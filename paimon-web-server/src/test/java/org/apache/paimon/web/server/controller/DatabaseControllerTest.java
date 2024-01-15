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
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.vo.DatabaseVO;
import org.apache.paimon.web.server.util.ObjectMapperUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

/** Test for DatabaseController. */
@SpringBootTest
@AutoConfigureMockMvc
public class DatabaseControllerTest extends ControllerTestBase {

    private static final String catalogPath = "/api/catalog";
    private static final String databasePath = "/api/database";

    private static final String databaseName = "test_db";

    private static final String catalogName = "paimon_catalog";

    private Integer catalogId;

    @BeforeEach
    public void setup() throws Exception {
        CatalogDTO catalog = new CatalogDTO();
        catalog.setType("filesystem");
        catalog.setName(catalogName);
        catalog.setWarehouse(tempFile.toUri().toString());
        catalog.setDelete(false);

        // create catalog.
        mockMvc.perform(
                        MockMvcRequestBuilders.post(catalogPath + "/create")
                                .cookie(cookie)
                                .content(ObjectMapperUtils.toJSON(catalog))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // get catalog id.
        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(catalogPath + "/list")
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        R<List<CatalogInfo>> r =
                ObjectMapperUtils.fromJSON(
                        responseString, new TypeReference<R<List<CatalogInfo>>>() {});
        catalogId = r.getData().get(0).getId();

        // create database.
        DatabaseDTO database = new DatabaseDTO();
        database.setCatalogId(catalogId);
        database.setName(databaseName);
        database.setCatalogName(catalogName);
        database.setIgnoreIfExists(true);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(databasePath + "/create")
                                .cookie(cookie)
                                .content(ObjectMapperUtils.toJSON(database))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @AfterEach
    public void after() throws Exception {
        CatalogDTO removeCatalog = new CatalogDTO();
        removeCatalog.setId(catalogId);
        removeCatalog.setName(catalogName);
        mockMvc.perform(
                        MockMvcRequestBuilders.post(catalogPath + "/remove")
                                .cookie(cookie)
                                .content(ObjectMapperUtils.toJSON(removeCatalog))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testListDatabases() throws Exception {
        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(databasePath + "/list")
                                        .cookie(cookie)
                                        .param("catalogId", String.valueOf(catalogId))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<List<DatabaseVO>> r =
                ObjectMapperUtils.fromJSON(
                        responseString, new TypeReference<R<List<DatabaseVO>>>() {});
        assertEquals(200, r.getCode());
        assertNotNull(r.getData());
        assertEquals(1, r.getData().size());
        assertEquals(databaseName, r.getData().get(0).getName());
    }

    @Test
    public void testDropDatabase() throws Exception {
        DatabaseDTO dropDatabase = new DatabaseDTO();
        dropDatabase.setCatalogName(catalogName);
        dropDatabase.setCatalogId(catalogId);
        dropDatabase.setName(databaseName);
        dropDatabase.setIgnoreIfExists(true);
        dropDatabase.setCascade(true);

        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(databasePath + "/drop")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(dropDatabase))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<Void> remove =
                ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<Void>>() {});
        assertEquals(200, remove.getCode());
    }
}
