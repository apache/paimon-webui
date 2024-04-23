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

import org.apache.paimon.web.server.data.dto.AlterTableDTO;
import org.apache.paimon.web.server.data.dto.CatalogDTO;
import org.apache.paimon.web.server.data.dto.DatabaseDTO;
import org.apache.paimon.web.server.data.dto.TableDTO;
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.model.TableColumn;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.vo.TableVO;
import org.apache.paimon.web.server.util.ObjectMapperUtils;
import org.apache.paimon.web.server.util.PaimonDataType;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Test for {@link TableController}. */
public class TableControllerTest extends ControllerTestBase {

    private static final String catalogPath = "/api/catalog";
    private static final String databasePath = "/api/database";
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
    public void testAddColumn() throws Exception {
        List<TableColumn> tableColumns = new ArrayList<>();
        TableColumn address =
                TableColumn.builder()
                        .field("address")
                        .dataType(PaimonDataType.builder().type("STRING").build())
                        .comment("")
                        .isPk(false)
                        .defaultValue("")
                        .build();
        tableColumns.add(address);
        TableDTO table =
                TableDTO.builder()
                        .catalogName(catalogName)
                        .databaseName(databaseName)
                        .name(tableName)
                        .tableColumns(tableColumns)
                        .partitionKey(Lists.newArrayList())
                        .tableOptions(Maps.newHashMap())
                        .build();

        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(tablePath + "/column/add")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(table))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        R<Void> r = ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<Void>>() {});
        assertEquals(200, r.getCode());
        assertEquals(5, getColumns().size());

        List<String> actualColumnNames =
                getColumns().stream().map(TableColumn::getField).collect(Collectors.toList());
        List<String> expectedColumnNamesList =
                Arrays.asList("id", "name", "age", "create_time", "address");
        assertEquals(expectedColumnNamesList, actualColumnNames);
    }

    @Test
    public void testDropColumn() throws Exception {
        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.delete(
                                                tablePath
                                                        + "/column/drop/"
                                                        + catalogName
                                                        + "/"
                                                        + databaseName
                                                        + "/"
                                                        + tableName
                                                        + "/"
                                                        + "name")
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<Void> r = ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<Void>>() {});
        assertEquals(200, r.getCode());
        assertEquals(3, getColumns().size());

        List<String> actualColumnNames =
                getColumns().stream().map(TableColumn::getField).collect(Collectors.toList());
        List<String> expectedColumnNamesList = Arrays.asList("id", "age", "create_time");
        assertEquals(expectedColumnNamesList, actualColumnNames);

        // drop primary key.
        String responsePkStr =
                mockMvc.perform(
                                MockMvcRequestBuilders.delete(
                                                tablePath
                                                        + "/column/drop/"
                                                        + catalogName
                                                        + "/"
                                                        + databaseName
                                                        + "/"
                                                        + tableName
                                                        + "/"
                                                        + "id")
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .locale(Locale.US)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        R<Void> pkRes = ObjectMapperUtils.fromJSON(responsePkStr, new TypeReference<R<Void>>() {});
        assertEquals(10506, pkRes.getCode());
        assertEquals("Exception calling Paimon Catalog API to drop a column.", pkRes.getMsg());

        // drop partition key.
        String responsePartitionKeyStr =
                mockMvc.perform(
                                MockMvcRequestBuilders.delete(
                                                tablePath
                                                        + "/column/drop/"
                                                        + catalogName
                                                        + "/"
                                                        + databaseName
                                                        + "/"
                                                        + tableName
                                                        + "/"
                                                        + "create_time")
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .locale(Locale.US)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        R<Void> partitionKeyRes =
                ObjectMapperUtils.fromJSON(
                        responsePartitionKeyStr, new TypeReference<R<Void>>() {});
        assertEquals(10506, partitionKeyRes.getCode());
        assertEquals(
                "Exception calling Paimon Catalog API to drop a column.", partitionKeyRes.getMsg());
    }

    @Test
    public void testAlterTable() throws Exception {
        // before modification.
        List<TableColumn> columns = getColumns();
        assertEquals(4, columns.size());
        List<String> actualColumnNames =
                columns.stream().map(TableColumn::getField).collect(Collectors.toList());
        List<String> expectedColumnNamesList = Arrays.asList("id", "name", "age", "create_time");
        assertEquals(expectedColumnNamesList, actualColumnNames);

        List<TableColumn> tableColumns = new ArrayList<>();
        TableColumn id =
                TableColumn.builder()
                        .id(0)
                        .field("id")
                        .dataType(PaimonDataType.builder().type("INT").build())
                        .comment("pk")
                        .isPk(true)
                        .defaultValue("")
                        .sort(2)
                        .build();
        TableColumn name =
                TableColumn.builder()
                        .id(1)
                        .field("name")
                        .dataType(PaimonDataType.builder().type("STRING").build())
                        .comment("")
                        .isPk(false)
                        .defaultValue("")
                        .sort(3)
                        .build();
        TableColumn age =
                TableColumn.builder()
                        .id(2)
                        .field("age1")
                        .dataType(PaimonDataType.builder().type("BIGINT").build())
                        .comment("")
                        .isPk(false)
                        .defaultValue("0")
                        .sort(0)
                        .build();
        TableColumn createTime =
                TableColumn.builder()
                        .id(3)
                        .field("create_time")
                        .dataType(PaimonDataType.builder().type("STRING").build())
                        .comment("partition key")
                        .isPk(true)
                        .defaultValue("1970-01-01 00:00:00")
                        .sort(1)
                        .build();
        tableColumns.add(id);
        tableColumns.add(name);
        tableColumns.add(age);
        tableColumns.add(createTime);

        AlterTableDTO alterTableDTO =
                AlterTableDTO.builder()
                        .catalogName(catalogName)
                        .databaseName(databaseName)
                        .tableName(tableName)
                        .tableColumns(tableColumns)
                        .build();

        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(tablePath + "/alter")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(alterTableDTO))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<Void> r = ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<Void>>() {});
        assertEquals(200, r.getCode());

        // after modification.
        columns = getColumns();
        List<String> afterActualColumnNames =
                columns.stream().map(TableColumn::getField).collect(Collectors.toList());
        List<String> afterExpectedColumnNamesList =
                Arrays.asList("age1", "create_time", "id", "name");
        assertEquals(afterExpectedColumnNamesList, afterActualColumnNames);

        TableColumn age1Column =
                columns.stream()
                        .filter(column -> "age1".equals(column.getField()))
                        .findFirst()
                        .orElse(null);
        assert age1Column != null;
        assertEquals("BIGINT", age1Column.getDataType().getType());

        TableColumn createTimeColumn =
                columns.stream()
                        .filter(column -> "create_time".equals(column.getField()))
                        .findFirst()
                        .orElse(null);
        assert createTimeColumn != null;
        assertEquals("1970-01-01 00:00:00", createTimeColumn.getDefaultValue());
    }

    @Test
    public void testAddOption() throws Exception {
        Map<String, String> option = new HashMap<>();
        option.put("bucket", "4");

        TableDTO table =
                TableDTO.builder()
                        .catalogName(catalogName)
                        .databaseName(databaseName)
                        .name(tableName)
                        .tableColumns(Lists.newArrayList())
                        .partitionKey(Lists.newArrayList())
                        .tableOptions(option)
                        .build();

        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(tablePath + "/option/add")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(table))
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
    public void testRemoveOption() throws Exception {
        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(tablePath + "/option/remove")
                                        .cookie(cookie)
                                        .param("catalogName", catalogName)
                                        .param("databaseName", databaseName)
                                        .param("tableName", tableName)
                                        .param("key", "bucket")
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
    public void testRenameTable() throws Exception {
        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(tablePath + "/rename")
                                        .cookie(cookie)
                                        .param("catalogName", catalogName)
                                        .param("databaseName", databaseName)
                                        .param("fromTableName", tableName)
                                        .param("toTableName", "test_table_01")
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<Void> r = ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<Void>>() {});
        assertEquals(200, r.getCode());
        assertEquals("test_table_01", getTables().get(0).getName());

        mockMvc.perform(
                MockMvcRequestBuilders.delete(
                                tablePath
                                        + "/drop/"
                                        + catalogName
                                        + "/"
                                        + databaseName
                                        + "/"
                                        + "test_table_01")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void testListTables() throws Exception {
        TableDTO table = new TableDTO();
        table.setCatalogId(catalogId);
        table.setDatabaseName(databaseName);
        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(tablePath + "/list")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(table))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<List<TableVO>> r =
                ObjectMapperUtils.fromJSON(
                        responseString, new TypeReference<R<List<TableVO>>>() {});
        assertEquals(200, r.getCode());
        assertEquals(1, r.getData().size());
        assertEquals(tableName, r.getData().get(0).getName());
    }

    @Test
    public void testListColumns() throws Exception {
        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(tablePath + "/column/list")
                                        .cookie(cookie)
                                        .param("catalogName", catalogName)
                                        .param("databaseName", databaseName)
                                        .param("tableName", tableName)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<TableVO> r =
                ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<TableVO>>() {});
        assertEquals(200, r.getCode());
        assertEquals(4, r.getData().getColumns().size());
    }

    private List<TableColumn> getColumns() throws Exception {
        String contentAsString =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(tablePath + "/column/list")
                                        .cookie(cookie)
                                        .param("catalogName", catalogName)
                                        .param("databaseName", databaseName)
                                        .param("tableName", tableName)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        R<TableVO> columnRes =
                ObjectMapperUtils.fromJSON(contentAsString, new TypeReference<R<TableVO>>() {});
        return columnRes.getData().getColumns();
    }

    private List<TableVO> getTables() throws Exception {
        TableDTO table = new TableDTO();
        table.setCatalogId(catalogId);
        table.setDatabaseName(databaseName);
        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(tablePath + "/list")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(table))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<List<TableVO>> r =
                ObjectMapperUtils.fromJSON(
                        responseString, new TypeReference<R<List<TableVO>>>() {});
        return r.getData();
    }
}
