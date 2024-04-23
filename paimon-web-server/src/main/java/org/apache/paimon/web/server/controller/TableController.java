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
     * @param tableDTO The TableDTO object containing information about the table.
     * @return R<Void/> indicating the success or failure of the operation.
     */
    @PostMapping("/create")
    public R<Void> createTable(@RequestBody TableDTO tableDTO) {
        return tableService.createTable(tableDTO);
    }

    /**
     * Adds a column to the table.
     *
     * @param tableDTO The information of the table, including the catalog name, database name,
     *     table name, and table columns.
     * @return A response indicating the success or failure of the operation.
     */
    @PostMapping("/column/add")
    public R<Void> addColumn(@RequestBody TableDTO tableDTO) {
        return tableService.addColumn(tableDTO);
    }

    /**
     * Lists columns given a table.
     *
     * @param catalogName The name of the catalog.
     * @param databaseName The name of the database.
     * @param tableName The name of the table.
     * @return Response object containing {@link TableVO} representing the table.
     */
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
     * @param catalogName The name of the catalog.
     * @param databaseName The name of the database.
     * @param tableName The name of the table.
     * @param columnName The name of the column to be dropped.
     * @return The result indicating the success or failure of the operation.
     */
    @DeleteMapping("/column/drop/{catalogName}/{databaseName}/{tableName}/{columnName}")
    public R<Void> dropColumn(
            @PathVariable String catalogName,
            @PathVariable String databaseName,
            @PathVariable String tableName,
            @PathVariable String columnName) {
        return tableService.dropColumn(catalogName, databaseName, tableName, columnName);
    }

    /**
     * Modify a column in a table.
     *
     * @param alterTableDTO the DTO containing alteration details.
     * @return A response indicating the success or failure of the operation.
     */
    @PostMapping("/alter")
    public R<Void> alterTable(@RequestBody AlterTableDTO alterTableDTO) {
        return tableService.alterTable(alterTableDTO);
    }

    /**
     * Adds options to a table.
     *
     * @param tableDTO An object containing table information.
     * @return If the options are successfully added, returns a successful result object. If an
     *     exception occurs, returns a result object with an error status.
     */
    @PostMapping("/option/add")
    public R<Void> addOption(@RequestBody TableDTO tableDTO) {
        return tableService.addOption(tableDTO);
    }

    /**
     * Removes an option from a table.
     *
     * @param catalogName The name of the catalog.
     * @param databaseName The name of the database.
     * @param tableName The name of the table.
     * @param key The key of the option to be removed.
     * @return Returns a {@link R} object indicating the success or failure of the operation. If the
     *     option is successfully removed, the result will be a successful response with no data. If
     *     an error occurs during the operation, the result will be a failed response with an error
     *     code. Possible error codes: {@link Status#TABLE_REMOVE_OPTION_ERROR}.
     */
    @PostMapping("/option/remove")
    public R<Void> removeOption(
            @RequestParam String catalogName,
            @RequestParam String databaseName,
            @RequestParam String tableName,
            @RequestParam String key) {
        return tableService.removeOption(catalogName, databaseName, tableName, key);
    }

    /**
     * Drops a table from the specified database in the given catalog.
     *
     * @param catalogName The name of the catalog from which the table will be dropped.
     * @param databaseName The name of the database from which the table will be dropped.
     * @param tableName The name of the table to be dropped.
     * @return A Response object indicating the success or failure of the operation. If the
     *     operation is successful, the response will be R.succeed(). If the operation fails, the
     *     response will be R.failed() with Status.TABLE_DROP_ERROR.
     * @throws RuntimeException If there is an error during the operation, a RuntimeException is
     *     thrown with the error message.
     */
    @DeleteMapping("/drop/{catalogName}/{databaseName}/{tableName}")
    public R<Void> dropTable(
            @PathVariable String catalogName,
            @PathVariable String databaseName,
            @PathVariable String tableName) {
        return tableService.dropTable(catalogName, databaseName, tableName);
    }

    /**
     * Renames a table in the specified database of the given catalog.
     *
     * @param catalogName The name of the catalog where the table resides.
     * @param databaseName The name of the database where the table resides.
     * @param fromTableName The current name of the table to be renamed.
     * @param toTableName The new name for the table.
     * @return A Response object indicating the success or failure of the operation. If the
     *     operation is successful, the response will be R.succeed(). If the operation fails, the
     *     response will be R.failed() with Status.TABLE_RENAME_ERROR.
     * @throws RuntimeException If there is an error during the operation, a RuntimeException is
     *     thrown with the error message.
     */
    @PostMapping("/rename")
    public R<Void> renameTable(
            @RequestParam String catalogName,
            @RequestParam String databaseName,
            @RequestParam String fromTableName,
            @RequestParam String toTableName) {
        return tableService.renameTable(catalogName, databaseName, fromTableName, toTableName);
    }

    /**
     * Lists tables given {@link TableDTO} condition.
     *
     * @return Response object containing a list of {@link TableVO} representing the tables.
     */
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
