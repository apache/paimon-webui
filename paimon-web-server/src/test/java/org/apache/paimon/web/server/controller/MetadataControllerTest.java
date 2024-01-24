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
import org.apache.paimon.web.server.data.dto.MetadataDTO;
import org.apache.paimon.web.server.data.dto.TableDTO;
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.model.TableColumn;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.vo.OptionVO;
import org.apache.paimon.web.server.util.ObjectMapperUtils;
import org.apache.paimon.web.server.util.PaimonDataType;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Tests for {@link MetadataController}. */
@SpringBootTest
@AutoConfigureMockMvc
public class MetadataControllerTest extends ControllerTestBase {

    private static final String catalogPath = "/api/catalog";
    private static final String databasePath = "/api/database";
    private static final String METADATA_PATH = "/api/metadata/query";

    private static final String tablePath = "/api/table";

    private static final String catalogName = "paimon_catalog";

    private static final String databaseName = "paimon_database";

    private static final String tableName = "paimon_table";

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

        // create table.
        List<TableColumn> tableColumns = new ArrayList<>();
        TableColumn id =
                TableColumn.builder()
                        .field("id")
                        .dataType(PaimonDataType.builder().type("INT").build())
                        .comment("pk")
                        .isPk(true)
                        .defaultValue("")
                        .build();
        TableColumn name =
                TableColumn.builder()
                        .field("name")
                        .dataType(PaimonDataType.builder().type("STRING").build())
                        .comment("")
                        .isPk(false)
                        .defaultValue("")
                        .build();
        TableColumn age =
                TableColumn.builder()
                        .field("age")
                        .dataType(PaimonDataType.builder().type("INT").build())
                        .comment("")
                        .isPk(false)
                        .defaultValue("0")
                        .build();
        TableColumn createTime =
                TableColumn.builder()
                        .field("create_time")
                        .dataType(PaimonDataType.builder().type("STRING").build())
                        .comment("partition key")
                        .isPk(true)
                        .defaultValue("0")
                        .build();

        tableColumns.add(id);
        tableColumns.add(name);
        tableColumns.add(age);
        tableColumns.add(createTime);

        List<String> partitionKey = Lists.newArrayList("create_time");
        Map<String, String> tableOptions = ImmutableMap.of("bucket", "2");
        TableDTO table =
                TableDTO.builder()
                        .catalogName(catalogName)
                        .databaseName(databaseName)
                        .name(tableName)
                        .tableColumns(tableColumns)
                        .partitionKey(partitionKey)
                        .tableOptions(tableOptions)
                        .build();

        mockMvc.perform(
                        MockMvcRequestBuilders.post(tablePath + "/create")
                                .cookie(cookie)
                                .content(ObjectMapperUtils.toJSON(table))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @AfterEach
    public void after() throws Exception {
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
    public void testGetSchemaInfo() throws Exception {
        MetadataDTO metadata = new MetadataDTO();
        metadata.setCatalogId(catalogId);
        metadata.setDatabaseName(databaseName);
        metadata.setTableName(tableName);

        String response =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(METADATA_PATH + "/schema")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(metadata))
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
        MetadataDTO metadata = new MetadataDTO();
        metadata.setCatalogId(catalogId);
        metadata.setDatabaseName(databaseName);
        metadata.setTableName(tableName);

        String response =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(METADATA_PATH + "/manifest")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(metadata))
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
        MetadataDTO metadata = new MetadataDTO();
        metadata.setCatalogId(catalogId);
        metadata.setDatabaseName(databaseName);
        metadata.setTableName(tableName);

        String response =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(METADATA_PATH + "/dataFile")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(metadata))
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
        MetadataDTO metadata = new MetadataDTO();
        metadata.setCatalogId(catalogId);
        metadata.setDatabaseName(databaseName);
        metadata.setTableName(tableName);

        String response =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(METADATA_PATH + "/snapshot")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(metadata))
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
    public void testGetOptionInfo() throws Exception {
        MetadataDTO metadata = new MetadataDTO();
        metadata.setCatalogId(catalogId);
        metadata.setDatabaseName(databaseName);
        metadata.setTableName(tableName);

        String response =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(METADATA_PATH + "/options")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(metadata))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<List<OptionVO>> result =
                ObjectMapperUtils.fromJSON(response, new TypeReference<R<List<OptionVO>>>() {});

        assertEquals(200, result.getCode());
        List<OptionVO> expected =
                Arrays.asList(
                        new OptionVO("bucket", "2"),
                        new OptionVO("FIELDS.create_time.default-value", "0"),
                        new OptionVO("FIELDS.age.default-value", "0"));
        assertEquals(expected, result.getData());
    }
}
