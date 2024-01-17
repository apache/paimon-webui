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

import org.apache.paimon.data.BinaryString;
import org.apache.paimon.data.GenericRow;
import org.apache.paimon.table.Table;
import org.apache.paimon.table.sink.BatchTableCommit;
import org.apache.paimon.table.sink.BatchTableWrite;
import org.apache.paimon.table.sink.BatchWriteBuilder;
import org.apache.paimon.table.sink.CommitMessage;
import org.apache.paimon.web.api.catalog.PaimonService;
import org.apache.paimon.web.server.data.dto.CatalogDTO;
import org.apache.paimon.web.server.data.dto.DatabaseDTO;
import org.apache.paimon.web.server.data.dto.MetadataDTO;
import org.apache.paimon.web.server.data.dto.TableDTO;
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.model.TableColumn;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.vo.DataFileVO;
import org.apache.paimon.web.server.data.vo.ManifestsVO;
import org.apache.paimon.web.server.data.vo.SchemaVO;
import org.apache.paimon.web.server.data.vo.SnapshotVO;
import org.apache.paimon.web.server.util.ObjectMapperUtils;
import org.apache.paimon.web.server.util.PaimonDataType;
import org.apache.paimon.web.server.util.PaimonServiceUtils;

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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
                new TableColumn("id", PaimonDataType.builder().type("INT").build(), "pk", true, "");
        TableColumn name =
                new TableColumn(
                        "name", PaimonDataType.builder().type("STRING").build(), "", false, "");
        TableColumn age =
                new TableColumn(
                        "age", PaimonDataType.builder().type("INT").build(), "", false, "0");
        TableColumn createTime =
                new TableColumn(
                        "create_time",
                        PaimonDataType.builder().type("STRING").build(),
                        "partition key",
                        true,
                        "");
        tableColumns.add(id);
        tableColumns.add(name);
        tableColumns.add(age);
        tableColumns.add(createTime);

        List<String> partitionKey = Lists.newArrayList("create_time");
        Map<String, String> tableOptions = ImmutableMap.of("bucket", "2");
        TableDTO tableDTO =
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
                                .content(ObjectMapperUtils.toJSON(tableDTO))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // insert
        CatalogInfo catalogInfo =
                CatalogInfo.builder()
                        .catalogName(catalog.getName())
                        .catalogType(catalog.getType())
                        .warehouse(catalog.getWarehouse())
                        .build();
        PaimonService paimonService = PaimonServiceUtils.getPaimonService(catalogInfo);
        Table table = paimonService.getTable(databaseName, tableName);
        BatchWriteBuilder writeBuilder = table.newBatchWriteBuilder().withOverwrite();
        BatchTableWrite write = writeBuilder.newWrite();

        GenericRow record1 =
                GenericRow.of(
                        1,
                        BinaryString.fromString("Alice"),
                        24,
                        BinaryString.fromString("2023-12-04 00:00:00"));
        GenericRow record2 =
                GenericRow.of(
                        2,
                        BinaryString.fromString("Bob"),
                        28,
                        BinaryString.fromString("2023-10-11 00:00:00"));
        GenericRow record3 =
                GenericRow.of(
                        3,
                        BinaryString.fromString("Emily"),
                        32,
                        BinaryString.fromString("2023-10-04 00:00:00"));

        write.write(record1);
        write.write(record2);
        write.write(record3);

        List<CommitMessage> messages = write.prepareCommit();
        BatchTableCommit commit = writeBuilder.newCommit();
        commit.commit(messages);
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

        R<List<SchemaVO>> result =
                ObjectMapperUtils.fromJSON(response, new TypeReference<R<List<SchemaVO>>>() {});
        List<SchemaVO> schemaVOS = result.getData();
        assertEquals(200, result.getCode());
        assertFalse(schemaVOS.isEmpty());

        // Make assertions on each field of the SchemaVO class.
        SchemaVO schemaVO = schemaVOS.get(0);
        assertNotNull(schemaVO.getSchemaId());
        assertNotNull(schemaVO.getFields());
        assertNotNull(schemaVO.getPartitionKeys());
        assertNotNull(schemaVO.getPrimaryKeys());
        assertNotNull(schemaVO.getComment());
        assertNotNull(schemaVO.getOption());
        assertNotNull(schemaVO.getUpdateTime());
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

        R<List<ManifestsVO>> result =
                ObjectMapperUtils.fromJSON(response, new TypeReference<R<List<ManifestsVO>>>() {});
        assertEquals(200, result.getCode());
        List<ManifestsVO> manifestsVOS = result.getData();
        assertFalse(manifestsVOS.isEmpty());

        // Make assertions on each field of the ManifestsVO class.
        ManifestsVO manifestsVO = manifestsVOS.get(0);
        assertNotNull(manifestsVO.getFileName());
        assertNotNull(manifestsVO.getFileSize());
        assertNotNull(manifestsVO.getNumAddedFiles());
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

        R<List<DataFileVO>> result =
                ObjectMapperUtils.fromJSON(response, new TypeReference<R<List<DataFileVO>>>() {});
        assertEquals(200, result.getCode());
        List<DataFileVO> dataFileVOS = result.getData();
        assertFalse(dataFileVOS.isEmpty());

        // Make assertions on each field of the DataFileVO class.
        DataFileVO dataFileVO = dataFileVOS.get(0);
        assertNotNull(dataFileVO.getPartition());
        assertNotNull(dataFileVO.getBucket());
        assertNotNull(dataFileVO.getFilePath());
        assertNotNull(dataFileVO.getFileFormat());
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

        R<List<SnapshotVO>> result =
                ObjectMapperUtils.fromJSON(response, new TypeReference<R<List<SnapshotVO>>>() {});
        assertEquals(200, result.getCode());
        List<SnapshotVO> snapshotVOS = result.getData();
        assertFalse(snapshotVOS.isEmpty());

        // Make assertions on each field of the SnapshotVO class.
        SnapshotVO snapshotVO = snapshotVOS.get(0);
        assertNotNull(snapshotVO.getSnapshotId());
        assertNotNull(snapshotVO.getSchemaId());
        assertNotNull(snapshotVO.getCommitIdentifier());
        assertNotNull(snapshotVO.getCommitTime());
    }
}
