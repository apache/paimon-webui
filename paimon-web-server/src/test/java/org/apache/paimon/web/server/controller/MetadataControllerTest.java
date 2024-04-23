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
import org.apache.paimon.table.sink.CommitMessageImpl;
import org.apache.paimon.web.api.catalog.PaimonService;
import org.apache.paimon.web.server.data.dto.CatalogDTO;
import org.apache.paimon.web.server.data.dto.DatabaseDTO;
import org.apache.paimon.web.server.data.dto.MetadataDTO;
import org.apache.paimon.web.server.data.dto.TableDTO;
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.model.MetadataFieldsModel;
import org.apache.paimon.web.server.data.model.MetadataOptionModel;
import org.apache.paimon.web.server.data.model.TableColumn;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.vo.DataFileVO;
import org.apache.paimon.web.server.data.vo.ManifestsVO;
import org.apache.paimon.web.server.data.vo.OptionVO;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    private List<CommitMessage> messages;

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

        messages = write.prepareCommit();
        BatchTableCommit commit = writeBuilder.newCommit();
        commit.commit(messages);

        write.close();
        commit.close();
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
        assertEquals(1, schemaVOS.size());

        // Make assertions on each field of the SchemaVO class.
        SchemaVO schemaVO = schemaVOS.get(0);
        assertNotNull(schemaVO.getUpdateTime());

        assertEquals(0, schemaVO.getSchemaId());
        assertEquals("[\"id\",\"create_time\"]", schemaVO.getPrimaryKeys());
        assertEquals("[\"create_time\"]", schemaVO.getPartitionKeys());
        assertEquals("", schemaVO.getComment());

        assertEquals(4, schemaVO.getFields().size());
        ArrayList<MetadataFieldsModel> expectedFields =
                Lists.newArrayList(
                        new MetadataFieldsModel(0, "id", "INT NOT NULL", null),
                        new MetadataFieldsModel(1, "name", "STRING NOT NULL", null),
                        new MetadataFieldsModel(2, "age", "INT NOT NULL", null),
                        new MetadataFieldsModel(3, "create_time", "STRING NOT NULL", null));
        assertEquals(expectedFields, schemaVO.getFields());

        assertEquals(3, schemaVO.getOption().size());
        ArrayList<MetadataOptionModel> expectedOptions =
                Lists.newArrayList(
                        new MetadataOptionModel("bucket", "2"),
                        new MetadataOptionModel("FIELDS.create_time.default-value", "0"),
                        new MetadataOptionModel("FIELDS.age.default-value", "0"));
        assertEquals(expectedOptions, schemaVO.getOption());
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
        assertTrue(manifestsVO.getFileSize() > 0);
        assertEquals(3, manifestsVO.getNumAddedFiles());
        assertEquals(0, manifestsVO.getNumDeletedFiles());
        assertEquals(0, manifestsVO.getSchemaId());
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
        assertEquals(3, dataFileVOS.size());

        // Make assertions on each field of the DataFileVO class.
        DataFileVO dataFileVO = dataFileVOS.get(0);
        CommitMessageImpl commitMessage = (CommitMessageImpl) messages.get(0);
        assertEquals("[2023-12-04 00:00:00]", dataFileVO.getPartition());
        assertEquals(0, dataFileVO.getBucket());
        assertNotNull(dataFileVO.getFilePath());
        assertEquals("orc", dataFileVO.getFileFormat());
        assertEquals(0, dataFileVO.getLevel());
        assertEquals(1, dataFileVO.getRecordCount());
        assertEquals(
                commitMessage.newFilesIncrement().newFiles().get(0).fileSize(),
                dataFileVO.getFileSizeInBytes());
        assertEquals("[1]", dataFileVO.getMinKey());
        assertEquals("[1]", dataFileVO.getMaxKey());
        assertEquals("{age=0, create_time=0, id=0, name=0}", dataFileVO.getNullValueCounts());
        assertEquals(
                "{age=24, create_time=2023-12-04 00:00, id=1, name=Alice}",
                dataFileVO.getMinValueStats());
        assertEquals(
                "{age=24, create_time=2023-12-04 00:01, id=1, name=Alice}",
                dataFileVO.getMaxValueStats());
        assertEquals(0, dataFileVO.getMinSequenceNumber());
        assertEquals(0, dataFileVO.getMaxSequenceNumber());
        assertEquals(
                commitMessage.newFilesIncrement().newFiles().get(0).creationTimeEpochMillis(),
                dataFileVO
                        .getCreationTime()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli());

        List<String> actualPartitions = new ArrayList<>();
        List<String> expectedPartitions =
                Lists.newArrayList(
                        "[2023-12-04 00:00:00]", "[2023-10-11 00:00:00]", "[2023-10-04 00:00:00]");
        dataFileVOS.forEach(item -> actualPartitions.add(item.getPartition()));
        assertEquals(actualPartitions, expectedPartitions);

        List<String> actualFilePaths = new ArrayList<>();
        dataFileVOS.forEach(item -> actualFilePaths.add(item.getFilePath()));
        List<String> expectedFilePaths = new ArrayList<>();
        messages.forEach(
                item -> {
                    CommitMessageImpl message = (CommitMessageImpl) item;
                    expectedFilePaths.add(message.newFilesIncrement().newFiles().get(0).fileName());
                });
        assertEquals(actualFilePaths, expectedFilePaths);
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
        assertEquals(1, snapshotVOS.size());

        // Make assertions on each field of the SnapshotVO class.
        SnapshotVO snapshotVO = snapshotVOS.get(0);
        assertEquals(1, snapshotVO.getSnapshotId());
        assertEquals(0, snapshotVO.getSchemaId());
        assertFalse(snapshotVO.getCommitUser().isEmpty());
        assertEquals("OVERWRITE", snapshotVO.getCommitKind());
        assertTrue(snapshotVO.getCommitIdentifier() > 0);
        assertTrue(snapshotVO.getCommitTime().isBefore(LocalDateTime.now()));
        assertEquals(0, snapshotVO.getSchemaId());
        assertFalse(snapshotVO.getDeltaManifestList().isEmpty());
        assertFalse(snapshotVO.getBaseManifestList().isEmpty());
        assertEquals("", snapshotVO.getChangelogManifestList());
        assertEquals(3, snapshotVO.getTotalRecordCount());
        assertEquals(3, snapshotVO.getDeltaRecordCount());
        assertEquals(0, snapshotVO.getChangelogRecordCount());
        assertEquals(3, snapshotVO.getAddedFileCount());
        assertEquals(0, snapshotVO.getDeletedFileCount());
        assertNull(snapshotVO.getWatermark());
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
