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

import org.apache.paimon.web.server.data.dto.TableDTO;
import org.apache.paimon.web.server.data.model.AlterTableRequest;
import org.apache.paimon.web.server.data.model.TableColumn;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.util.ObjectMapperUtils;
import org.apache.paimon.web.server.util.PaimonDataType;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Test for TableController. */
public class TableControllerTest extends ControllerTestBase {

    private static final String tablePath = "/api/table";

    private static final Integer catalogId = 1;

    private static final String catalogName = "paimon_catalog";

    private static final String databaseName = "paimon_database";

    private static final String tableName = "paimon_table";

    @Test
    public void testCreateTable() throws Exception {
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

        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(tablePath + "/create")
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

        mockMvc.perform(
                MockMvcRequestBuilders.delete(
                                tablePath
                                        + "/drop/"
                                        + catalogName
                                        + "/"
                                        + databaseName
                                        + "/"
                                        + "test_table")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void testAddColumn() throws Exception {
        List<TableColumn> tableColumns = new ArrayList<>();
        TableColumn age =
                new TableColumn(
                        "age",
                        PaimonDataType.builder().type("INT").isNullable(true).build(),
                        "",
                        false,
                        "0");
        tableColumns.add(age);
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
    }

    @Test
    public void testDropColumn() throws Exception {
        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.delete(
                                                tablePath
                                                        + "/drop/"
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
    }

    @Test
    public void testAlterTable() throws Exception {
        TableColumn oldColumn =
                new TableColumn("id", PaimonDataType.builder().type("INT").build(), "", false, "0");

        TableColumn newColumn =
                new TableColumn(
                        "age", PaimonDataType.builder().type("BIGINT").build(), "", false, "0");

        AlterTableRequest alterTableRequest = new AlterTableRequest();
        alterTableRequest.setOldColumn(oldColumn);
        alterTableRequest.setNewColumn(newColumn);

        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(tablePath + "/alter")
                                        .cookie(cookie)
                                        .param("catalogName", catalogName)
                                        .param("databaseName", databaseName)
                                        .param("tableName", tableName)
                                        .content(ObjectMapperUtils.toJSON(alterTableRequest))
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
    public void testAddOption() throws Exception {
        Map<String, String> option = new HashMap<>();
        option.put("bucket", "2");

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
                        .name("test_table_01")
                        .tableColumns(tableColumns)
                        .partitionKey(Lists.newArrayList())
                        .tableOptions(Maps.newHashMap())
                        .build();

        mockMvc.perform(
                        MockMvcRequestBuilders.post(tablePath + "/create")
                                .cookie(cookie)
                                .content(ObjectMapperUtils.toJSON(table))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(tablePath + "/rename")
                                        .cookie(cookie)
                                        .param("catalogName", catalogName)
                                        .param("databaseName", databaseName)
                                        .param("fromTableName", "test_table_01")
                                        .param("toTableName", "test_table_02")
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

        R<Void> r = ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<Void>>() {});
        assertEquals(200, r.getCode());
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

        R<Void> r = ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<Void>>() {});
        assertEquals(200, r.getCode());
    }
}
