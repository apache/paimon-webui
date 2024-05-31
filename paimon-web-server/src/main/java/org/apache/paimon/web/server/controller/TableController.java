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
import org.apache.paimon.web.server.data.dto.TableDTO;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.data.vo.TableVO;
import org.apache.paimon.web.server.service.TableService;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** Table api controller. */
@Slf4j
@RestController
@RequestMapping("/api/table")
public class TableController {

    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    /**
     * Creates a table in the database based on the provided TableInfo.
     *
     * @param tableDTO The TableDTO object containing information about the table
     * @return a {@code R<Void>} response indicating success or failure
     */
    @SaCheckPermission("metadata:table:create")
    @PostMapping("/create")
    public R<Void> createTable(@RequestBody TableDTO tableDTO) {
        if (tableService.tableExists(tableDTO)) {
            return R.failed(Status.TABLE_NAME_IS_EXIST, tableDTO.getName());
        }
        return tableService.createTable(tableDTO)
                ? R.succeed()
                : R.failed(Status.TABLE_CREATE_ERROR);
    }

    /**
     * Adds a column to the table.
     *
     * @param tableDTO The TableDTO object containing information about the table
     * @return a {@code R<Void>} response indicating success or failure
     */
    @SaCheckPermission("metadata:column:add")
    @PostMapping("/column/add")
    public R<Void> addColumn(@RequestBody TableDTO tableDTO) {
        return tableService.addColumn(tableDTO)
                ? R.succeed()
                : R.failed(Status.TABLE_ADD_COLUMN_ERROR);
    }

    /**
     * Lists columns given a table.
     *
     * @param catalogName The name of the catalog
     * @param databaseName The name of the database
     * @param tableName The name of the table
     * @return Response object containing {@link TableVO} representing the table
     */
    @SaCheckPermission("metadata:column:list")
    @GetMapping("/column/list")
    public R<TableVO> listColumns(
            @RequestParam String catalogName,
            @RequestParam String databaseName,
            @RequestParam String tableName) {
        return R.succeed(tableService.listColumns(catalogName, databaseName, tableName));
    }

    /**
     * Drops a column from a table.
     *
     * @param catalogName The name of the catalog
     * @param databaseName The name of the database
     * @param tableName The name of the table
     * @param columnName The name of the column to be dropped
     * @return a {@code R<Void>} response indicating success or failure
     */
    @SaCheckPermission("metadata:column:drop")
    @DeleteMapping("/column/drop/{catalogName}/{databaseName}/{tableName}/{columnName}")
    public R<Void> dropColumn(
            @PathVariable String catalogName,
            @PathVariable String databaseName,
            @PathVariable String tableName,
            @PathVariable String columnName) {
        return tableService.dropColumn(catalogName, databaseName, tableName, columnName)
                ? R.succeed()
                : R.failed(Status.TABLE_DROP_COLUMN_ERROR);
    }

    /**
     * Modify a column in a table.
     *
     * @param alterTableDTO the DTO containing alteration details
     * @return a {@code R<Void>} response indicating success or failure
     */
    @SaCheckPermission("metadata:table:update")
    @PostMapping("/alter")
    public R<Void> alterTable(@RequestBody AlterTableDTO alterTableDTO) {
        return tableService.alterTable(alterTableDTO)
                ? R.succeed()
                : R.failed(Status.TABLE_AlTER_COLUMN_ERROR);
    }

    /**
     * Adds options to a table.
     *
     * @param tableDTO The TableDTO object containing information about the table
     * @return a {@code R<Void>} response indicating success or failure
     */
    @SaCheckPermission("metadata:option:add")
    @PostMapping("/option/add")
    public R<Void> addOption(@RequestBody TableDTO tableDTO) {
        return tableService.addOption(tableDTO)
                ? R.succeed()
                : R.failed(Status.TABLE_ADD_OPTION_ERROR);
    }

    /**
     * Removes an option from a table.
     *
     * @param catalogName The name of the catalog
     * @param databaseName The name of the database
     * @param tableName The name of the table
     * @param key The key of the option to be removed
     * @return a {@code R<Void>} response indicating success or failure
     */
    @SaCheckPermission("metadata:option:remove")
    @PostMapping("/option/remove")
    public R<Void> removeOption(
            @RequestParam String catalogName,
            @RequestParam String databaseName,
            @RequestParam String tableName,
            @RequestParam String key) {
        return tableService.removeOption(catalogName, databaseName, tableName, key)
                ? R.succeed()
                : R.failed(Status.TABLE_REMOVE_OPTION_ERROR);
    }

    /**
     * Drops a table from the specified database in the given catalog.
     *
     * @param catalogName The name of the catalog from which the table will be dropped
     * @param databaseName The name of the database from which the table will be dropped
     * @param tableName The name of the table to be dropped
     * @return a {@code R<Void>} response indicating success or failure
     */
    @SaCheckPermission("metadata:table:drop")
    @DeleteMapping("/drop/{catalogName}/{databaseName}/{tableName}")
    public R<Void> dropTable(
            @PathVariable String catalogName,
            @PathVariable String databaseName,
            @PathVariable String tableName) {
        return tableService.dropTable(catalogName, databaseName, tableName)
                ? R.succeed()
                : R.failed(Status.TABLE_DROP_ERROR);
    }

    /**
     * Renames a table in the specified database of the given catalog.
     *
     * @param catalogName The name of the catalog where the table resides
     * @param databaseName The name of the database where the table resides
     * @param fromTableName The current name of the table to be renamed
     * @param toTableName The new name for the table
     * @return a {@code R<Void>} response indicating success or failure
     */
    @SaCheckPermission("metadata:table:update")
    @PostMapping("/rename")
    public R<Void> renameTable(
            @RequestParam String catalogName,
            @RequestParam String databaseName,
            @RequestParam String fromTableName,
            @RequestParam String toTableName) {
        return tableService.renameTable(catalogName, databaseName, fromTableName, toTableName)
                ? R.succeed()
                : R.failed(Status.TABLE_RENAME_ERROR);
    }

    /**
     * Lists tables given {@link TableDTO} condition.
     *
     * @return Response object containing a list of {@link TableVO} representing the tables
     */
    @SaCheckPermission("metadata:table:list")
    @PostMapping("/list")
    public R<Object> listTables(@RequestBody TableDTO tableDTO) {
        List<TableVO> tables = tableService.listTables(tableDTO);
        if (Objects.nonNull(tableDTO.getCatalogId())
                && Objects.nonNull(tableDTO.getDatabaseName())) {
            return R.succeed(tables);
        } else {
            TreeMap<Integer, Map<String, List<TableVO>>> collect =
                    tables.stream()
                            .collect(
                                    Collectors.groupingBy(
                                            TableVO::getCatalogId,
                                            TreeMap::new,
                                            Collectors.groupingBy(TableVO::getDatabaseName)));
            return R.succeed(collect);
        }
    }
}
