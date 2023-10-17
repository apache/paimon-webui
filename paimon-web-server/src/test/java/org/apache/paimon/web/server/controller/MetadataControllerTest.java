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

import org.apache.paimon.web.server.data.dto.QueryMetadataDto;
import org.apache.paimon.web.server.data.model.TableColumn;
import org.apache.paimon.web.server.data.model.TableInfo;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.util.ObjectMapperUtils;
import org.apache.paimon.web.server.util.PaimonDataType;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Tests for {@link MetadataController}. */
@SpringBootTest
@AutoConfigureMockMvc
public class MetadataControllerTest extends ControllerTestBase {

    private static final String METADATA_PATH = "/api/metadata";

    private static final String tablePath = "/api/table";

    private static final String catalogName = "paimon_catalog";

    private static final String databaseName = "paimon_database";

    private static final String tableName = "paimon_table";

    @BeforeTestClass
    public void createTable() throws Exception {
        List<TableColumn> tableColumns = new ArrayList<>();
        TableColumn id =
                new TableColumn("id", PaimonDataType.builder().type("INT").build(), "", false, "0");
        TableColumn name =
                new TableColumn(
                        "name", PaimonDataType.builder().type("STRING").build(), "", false, "0");
        tableColumns.add(id);
        tableColumns.add(name);
        TableInfo tableInfo =
                TableInfo.builder()
                        .catalogName(catalogName)
                        .databaseName(databaseName)
                        .tableName(tableName)
                        .tableColumns(tableColumns)
                        .partitionKey(Lists.newArrayList())
                        .tableOptions(Maps.newHashMap())
                        .build();

        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(tablePath + "/create")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(tableInfo))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<Void> r = ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<Void>>() {});
        assertEquals(200, r.getCode());
    }

    @Test
    public void testGetSchemaInfo() throws Exception {
        QueryMetadataDto queryMetadataInfoDto = new QueryMetadataDto();
        queryMetadataInfoDto.setCatalogName(catalogName);
        queryMetadataInfoDto.setDatabaseName(databaseName);
        queryMetadataInfoDto.setTableName(tableName);

        String response =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(METADATA_PATH + "/querySchemaInfo")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(queryMetadataInfoDto))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<Void> result = ObjectMapperUtils.fromJSON(response, new TypeReference<R<Void>>() {});
        assertEquals(200, result.getCode());
    }

    @Test
    public void testGetManifestInfo() throws Exception {
        QueryMetadataDto queryMetadataDto = new QueryMetadataDto();
        queryMetadataDto.setCatalogName(catalogName);
        queryMetadataDto.setDatabaseName(databaseName);
        queryMetadataDto.setTableName(tableName);

        String response =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(METADATA_PATH + "/queryManifestInfo")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(queryMetadataDto))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<Void> result = ObjectMapperUtils.fromJSON(response, new TypeReference<R<Void>>() {});
        assertEquals(200, result.getCode());
    }

    @Test
    public void testGetDataFileInfo() throws Exception {
        QueryMetadataDto queryMetadataDto = new QueryMetadataDto();
        queryMetadataDto.setCatalogName(catalogName);
        queryMetadataDto.setDatabaseName(databaseName);
        queryMetadataDto.setTableName(tableName);

        String response =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(METADATA_PATH + "/queryDataFileInfo")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(queryMetadataDto))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<Void> result = ObjectMapperUtils.fromJSON(response, new TypeReference<R<Void>>() {});
        assertEquals(200, result.getCode());
    }

    @Test
    public void testGetSnapshotInfo() throws Exception {
        QueryMetadataDto queryMetadataDto = new QueryMetadataDto();
        queryMetadataDto.setCatalogName(catalogName);
        queryMetadataDto.setDatabaseName(databaseName);
        queryMetadataDto.setTableName(tableName);

        String response =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(METADATA_PATH + "/querySnapshotInfo")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(queryMetadataDto))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<Void> result = ObjectMapperUtils.fromJSON(response, new TypeReference<R<Void>>() {});
        assertEquals(200, result.getCode());
    }

    @AfterTestClass
    public void dropTable() throws Exception {
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
    }
}
