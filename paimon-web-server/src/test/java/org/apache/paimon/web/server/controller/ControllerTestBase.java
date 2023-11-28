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
    private static final String catalogPath = "/api/catalog";
    private static final String databasePath = "/api/database";
    private static final String tablePath = "/api/table";

    @Value("${spring.application.name}")
    private String tokenName;

    @Autowired public MockMvc mockMvc;

    public static MockCookie cookie;

    @TempDir java.nio.file.Path tempFile;

    private static final Integer catalogId = 1;

    private static final String catalogName = "paimon_catalog";

    private static final String databaseName = "paimon_database";

    private static final String tableName = "paimon_table";

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

        // create default catalog
        CatalogDTO catalog = new CatalogDTO();
        catalog.setId(catalogId);
        catalog.setType("filesystem");
        catalog.setName(catalogName);
        catalog.setWarehouse(tempFile.toUri().toString());
        catalog.setDelete(false);

        ResultActions createCatalogRa =
                mockMvc.perform(
                        MockMvcRequestBuilders.post(catalogPath + "/create")
                                .cookie(cookie)
                                .content(ObjectMapperUtils.toJSON(catalog))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE));
        R<?> createCatalogR = getR(createCatalogRa);
        assertEquals(200, createCatalogR.getCode());

        // create default database
        DatabaseDTO database = new DatabaseDTO();
        database.setName(databaseName);
        database.setCatalogName(catalogName);

        ResultActions createDatabaseRa =
                mockMvc.perform(
                        MockMvcRequestBuilders.post(databasePath + "/create")
                                .cookie(cookie)
                                .content(ObjectMapperUtils.toJSON(database))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE));
        R<?> createDatabaseR = getR(createDatabaseRa);
        assertEquals(200, createDatabaseR.getCode());

        // create default table
        List<TableColumn> tableColumns = new ArrayList<>();
        TableColumn id =
                new TableColumn("id", PaimonDataType.builder().type("INT").build(), "", false, "0");
        TableColumn name =
                new TableColumn(
                        "name", PaimonDataType.builder().type("STRING").build(), "", false, "0");
        tableColumns.add(id);
        tableColumns.add(name);
        TableDTO table =
                TableDTO.builder()
                        .catalogName(catalogName)
                        .databaseName(databaseName)
                        .name(tableName)
                        .tableColumns(tableColumns)
                        .partitionKey(Lists.newArrayList())
                        .tableOptions(Maps.newHashMap())
                        .build();

        MockHttpServletResponse createTableResultAction =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(tablePath + "/create")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(table))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse();
        String createTableResult = createTableResultAction.getContentAsString();
        R<?> createTableR = ObjectMapperUtils.fromJSON(createTableResult, R.class);
        assertEquals(200, createTableR.getCode());
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

        ResultActions dropTableRa =
                mockMvc.perform(
                        MockMvcRequestBuilders.delete(
                                        tablePath
                                                + "/drop/"
                                                + catalogName
                                                + "/"
                                                + databaseName
                                                + "/"
                                                + tableName)
                                .cookie(cookie)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE));
        R<?> dropDatabaseR = getR(dropTableRa);
        assertEquals(200, dropDatabaseR.getCode());

        DatabaseDTO database = new DatabaseDTO();
        database.setName(databaseName);
        database.setCatalogName(catalogName);
        ResultActions dropDatabasesRa =
                mockMvc.perform(
                        MockMvcRequestBuilders.post(databasePath + "/drop")
                                .cookie(cookie)
                                .content(ObjectMapperUtils.toJSON(database))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE));
        R<?> dropDatabasesR = getR(dropDatabasesRa);
        assertEquals(200, dropDatabasesR.getCode());

        CatalogDTO catalog = new CatalogDTO();
        catalog.setName(catalogName);
        ResultActions removeCatalogRa =
                mockMvc.perform(
                        MockMvcRequestBuilders.post(catalogPath + "/remove")
                                .cookie(cookie)
                                .content(ObjectMapperUtils.toJSON(catalog))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE));
        R<?> removeCatalogResult = getR(removeCatalogRa);
        assertEquals(200, removeCatalogResult.getCode());
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
}
