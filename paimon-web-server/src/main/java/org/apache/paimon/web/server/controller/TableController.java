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

import org.apache.paimon.table.Table;
import org.apache.paimon.types.DataField;
import org.apache.paimon.web.api.catalog.PaimonService;
import org.apache.paimon.web.api.table.ColumnMetadata;
import org.apache.paimon.web.api.table.TableChange;
import org.apache.paimon.web.api.table.TableMetadata;
import org.apache.paimon.web.server.data.dto.TableDTO;
import org.apache.paimon.web.server.data.model.AlterTableRequest;
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.model.TableColumn;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.service.CatalogService;
import org.apache.paimon.web.server.util.DataTypeConvertUtils;
import org.apache.paimon.web.server.util.PaimonDataType;
import org.apache.paimon.web.server.util.PaimonServiceUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Table api controller. */
@Slf4j
@RestController
@RequestMapping("/api/table")
public class TableController {

    private static final String FIELDS_PREFIX = "FIELDS";
    private static final String DEFAULT_VALUE_SUFFIX = "default-value";

    @Autowired private CatalogService catalogService;

    /**
     * Creates a table in the database based on the provided TableInfo.
     *
     * @param tableDTO The tableDTO object containing information about the table.
     * @return R<Void/> indicating the success or failure of the operation.
     */
    @PostMapping("/create")
    public R<Void> createTable(@RequestBody TableDTO tableDTO) {
        try {
            PaimonService service =
                    PaimonServiceUtils.getPaimonService(getCatalogInfo(tableDTO.getCatalogName()));
            List<String> partitionKeys = tableDTO.getPartitionKey();

            Map<String, String> tableOptions = tableDTO.getTableOptions();
            List<TableColumn> tableColumns = tableDTO.getTableColumns();
            if (!CollectionUtils.isEmpty(tableColumns)) {
                for (TableColumn tableColumn : tableColumns) {
                    if (tableColumn.getDefaultValue() != null
                            && !tableColumn.getDefaultValue().equals("")) {
                        tableOptions.put(
                                FIELDS_PREFIX
                                        + "."
                                        + tableColumn.getField()
                                        + "."
                                        + DEFAULT_VALUE_SUFFIX,
                                tableColumn.getDefaultValue());
                    }
                }
            }

            TableMetadata tableMetadata =
                    TableMetadata.builder()
                            .columns(buildColumns(tableDTO))
                            .partitionKeys(partitionKeys)
                            .primaryKeys(buildPrimaryKeys(tableDTO))
                            .options(tableOptions)
                            .comment(tableDTO.getDescription())
                            .build();
            if (service.tableExists(tableDTO.getDatabaseName(), tableDTO.getTableName())) {
                return R.failed(Status.TABLE_NAME_IS_EXIST, tableDTO.getTableName());
            }
            service.createTable(tableDTO.getDatabaseName(), tableDTO.getTableName(), tableMetadata);
            return R.succeed();
        } catch (Exception e) {
            log.error("Exception with creating table.", e);
            return R.failed(Status.TABLE_CREATE_ERROR);
        }
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
        try {
            PaimonService service =
                    PaimonServiceUtils.getPaimonService(getCatalogInfo(tableDTO.getCatalogName()));
            List<TableColumn> tableColumns = tableDTO.getTableColumns();
            List<TableChange> tableChanges = new ArrayList<>();
            Map<String, String> options = new HashMap<>();
            for (TableColumn tableColumn : tableColumns) {
                if (tableColumn.getDefaultValue() != null
                        && !tableColumn.getDefaultValue().equals("")) {
                    options.put(
                            FIELDS_PREFIX
                                    + "."
                                    + tableColumn.getField()
                                    + "."
                                    + DEFAULT_VALUE_SUFFIX,
                            tableColumn.getDefaultValue());
                }
                ColumnMetadata columnMetadata =
                        new ColumnMetadata(
                                tableColumn.getField(),
                                DataTypeConvertUtils.convert(
                                        new PaimonDataType(
                                                tableColumn.getDataType().getType(),
                                                true,
                                                tableColumn.getDataType().getPrecision(),
                                                tableColumn.getDataType().getScale())),
                                tableColumn.getComment());
                TableChange.AddColumn add = TableChange.add(columnMetadata);
                tableChanges.add(add);
            }

            if (options.size() > 0) {
                for (Map.Entry<String, String> entry : options.entrySet()) {
                    TableChange.SetOption setOption =
                            TableChange.set(entry.getKey(), entry.getValue());
                    tableChanges.add(setOption);
                }
            }
            service.alterTable(tableDTO.getDatabaseName(), tableDTO.getTableName(), tableChanges);
            return R.succeed();
        } catch (Exception e) {
            log.error("Exception with adding column.", e);
            return R.failed(Status.TABLE_ADD_COLUMN_ERROR);
        }
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
        try {
            PaimonService service =
                    PaimonServiceUtils.getPaimonService(getCatalogInfo(catalogName));
            List<TableChange> tableChanges = new ArrayList<>();
            TableChange.DropColumn dropColumn = TableChange.dropColumn(columnName);
            tableChanges.add(dropColumn);
            service.alterTable(databaseName, tableName, tableChanges);
            return R.succeed();
        } catch (Exception e) {
            log.error("Exception with dropping column.", e);
            return R.failed(Status.TABLE_DROP_COLUMN_ERROR);
        }
    }

    /**
     * Modify a column in a table.
     *
     * @param catalogName The name of the catalog.
     * @param databaseName The name of the database.
     * @param tableName The name of the table.
     * @param alterTableRequest The param of the alter table request.
     * @return A response indicating the success or failure of the operation.
     */
    @PostMapping("/alter")
    public R<Void> alterTable(
            @RequestParam String catalogName,
            @RequestParam String databaseName,
            @RequestParam String tableName,
            @RequestBody AlterTableRequest alterTableRequest) {
        try {
            PaimonService service =
                    PaimonServiceUtils.getPaimonService(getCatalogInfo(catalogName));

            TableColumn oldColumn = alterTableRequest.getOldColumn();
            TableColumn newColumn = alterTableRequest.getNewColumn();

            List<TableChange> tableChanges = createTableChanges(oldColumn, newColumn);

            if (!Objects.equals(newColumn.getField(), oldColumn.getField())) {
                ColumnMetadata columnMetadata =
                        new ColumnMetadata(
                                oldColumn.getField(),
                                DataTypeConvertUtils.convert(oldColumn.getDataType()),
                                oldColumn.getComment());

                TableChange.ModifyColumnName modifyColumnName =
                        TableChange.modifyColumnName(columnMetadata, newColumn.getField());
                List<TableChange> modifyNameTableChanges = new ArrayList<>();
                modifyNameTableChanges.add(modifyColumnName);
                service.alterTable(databaseName, tableName, modifyNameTableChanges);
            }

            service.alterTable(databaseName, tableName, tableChanges);
            return R.succeed();
        } catch (Exception e) {
            log.error("Exception with altering table.", e);
            return R.failed(Status.TABLE_AlTER_COLUMN_ERROR);
        }
    }

    private List<TableChange> createTableChanges(TableColumn oldColumn, TableColumn newColumn) {
        ColumnMetadata columnMetadata =
                new ColumnMetadata(
                        newColumn.getField(),
                        DataTypeConvertUtils.convert(oldColumn.getDataType()),
                        oldColumn.getComment());

        TableChange.ModifyColumnType modifyColumnType =
                TableChange.modifyColumnType(
                        columnMetadata, DataTypeConvertUtils.convert(newColumn.getDataType()));

        TableChange.ModifyColumnComment modifyColumnComment =
                TableChange.modifyColumnComment(columnMetadata, newColumn.getComment());

        List<TableChange> tableChanges = new ArrayList<>();
        tableChanges.add(modifyColumnType);
        tableChanges.add(modifyColumnComment);

        return tableChanges;
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
        List<TableChange> tableChanges = new ArrayList<>();
        try {
            PaimonService service =
                    PaimonServiceUtils.getPaimonService(getCatalogInfo(tableDTO.getCatalogName()));
            Map<String, String> tableOptions = tableDTO.getTableOptions();
            for (Map.Entry<String, String> entry : tableOptions.entrySet()) {
                TableChange.SetOption setOption = TableChange.set(entry.getKey(), entry.getValue());
                tableChanges.add(setOption);
            }
            service.alterTable(tableDTO.getDatabaseName(), tableDTO.getTableName(), tableChanges);
            return R.succeed();
        } catch (Exception e) {
            log.error("Exception with adding option.", e);
            return R.failed(Status.TABLE_ADD_OPTION_ERROR);
        }
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
        List<TableChange> tableChanges = new ArrayList<>();
        try {
            PaimonService service =
                    PaimonServiceUtils.getPaimonService(getCatalogInfo(catalogName));
            TableChange.RemoveOption removeOption = TableChange.remove(key);
            tableChanges.add(removeOption);
            service.alterTable(databaseName, tableName, tableChanges);
            return R.succeed();
        } catch (Exception e) {
            log.error("Exception with removing option.", e);
            return R.failed(Status.TABLE_REMOVE_OPTION_ERROR);
        }
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
        try {
            PaimonService service =
                    PaimonServiceUtils.getPaimonService(getCatalogInfo(catalogName));
            service.dropTable(databaseName, tableName);
            return R.succeed();
        } catch (Exception e) {
            log.error("Exception with dropping table.", e);
            return R.failed(Status.TABLE_DROP_ERROR);
        }
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
        try {
            PaimonService service =
                    PaimonServiceUtils.getPaimonService(getCatalogInfo(catalogName));
            service.renameTable(databaseName, fromTableName, toTableName);
            return R.succeed();
        } catch (Exception e) {
            log.error("Exception with renaming table.", e);
            return R.failed(Status.TABLE_RENAME_ERROR);
        }
    }

    /**
     * Handler method for the "/getAllTables" endpoint. Retrieves information about all tables and
     * returns a response containing the table details.
     *
     * @return Response object containing a list of {@link TableDTO} representing the tables.
     */
    @GetMapping("/getAllTables")
    public R<List<TableDTO>> getAllTables() {
        List<TableDTO> tableDTOList = new ArrayList<>();
        List<CatalogInfo> catalogInfoList = catalogService.list();
        if (!CollectionUtils.isEmpty(catalogInfoList)) {
            for (CatalogInfo item : catalogInfoList) {
                PaimonService service = PaimonServiceUtils.getPaimonService(item);
                List<String> databaseList = service.listDatabases();
                if (!CollectionUtils.isEmpty(databaseList)) {
                    for (String db : databaseList) {
                        try {
                            List<String> tables = service.listTables(db);
                            if (!CollectionUtils.isEmpty(tables)) {
                                for (String t : tables) {
                                    try {
                                        Table table = service.getTable(db, t);
                                        if (table != null) {
                                            List<String> primaryKeys = table.primaryKeys();
                                            List<DataField> fields = table.rowType().getFields();
                                            List<TableColumn> tableColumns = new ArrayList<>();
                                            Map<String, String> options = table.options();
                                            if (!CollectionUtils.isEmpty(fields)) {
                                                for (DataField field : fields) {
                                                    String key =
                                                            FIELDS_PREFIX
                                                                    + "."
                                                                    + field.name()
                                                                    + "."
                                                                    + DEFAULT_VALUE_SUFFIX;
                                                    PaimonDataType dataType =
                                                            DataTypeConvertUtils.fromPaimonType(
                                                                    field.type());
                                                    TableColumn.TableColumnBuilder builder =
                                                            TableColumn.builder()
                                                                    .field(field.name())
                                                                    .dataType(dataType)
                                                                    .comment(field.description());
                                                    if (primaryKeys.size() > 0
                                                            && primaryKeys.contains(field.name())) {
                                                        builder.isPk(true);
                                                    }
                                                    if (options.get(key) != null) {
                                                        builder.defaultValue(options.get(key));
                                                    }
                                                    tableColumns.add(builder.build());
                                                }
                                            }
                                            TableDTO tableDTO =
                                                    TableDTO.builder()
                                                            .catalogName(item.getCatalogName())
                                                            .databaseName(db)
                                                            .tableName(table.name())
                                                            .partitionKey(table.partitionKeys())
                                                            .tableOptions(table.options())
                                                            .tableColumns(tableColumns)
                                                            .build();
                                            tableDTOList.add(tableDTO);
                                        }
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        return R.succeed(tableDTOList);
    }

    /**
     * Builds a list of primary keys for the given table.
     *
     * @param tableDTO The TableInfo object representing the table.
     * @return A list of primary keys as strings.
     */
    private List<String> buildPrimaryKeys(TableDTO tableDTO) {
        List<String> primaryKeys = new ArrayList<>();
        List<TableColumn> tableColumns = tableDTO.getTableColumns();
        if (!CollectionUtils.isEmpty(tableColumns)) {
            tableColumns.forEach(
                    item -> {
                        if (item.isPk()) {
                            primaryKeys.add(item.getField());
                        }
                    });
        }
        return primaryKeys;
    }

    /**
     * Builds a list of ColumnMetadata objects for the given table.
     *
     * @param tableDTO The TableInfo object representing the table.
     * @return A list of ColumnMetadata objects.
     */
    private List<ColumnMetadata> buildColumns(TableDTO tableDTO) {
        List<ColumnMetadata> columns = new ArrayList<>();
        List<TableColumn> tableColumns = tableDTO.getTableColumns();
        if (!CollectionUtils.isEmpty(tableColumns)) {
            tableColumns.forEach(
                    item -> {
                        ColumnMetadata columnMetadata =
                                new ColumnMetadata(
                                        item.getField(),
                                        DataTypeConvertUtils.convert(
                                                new PaimonDataType(
                                                        item.getDataType().getType(),
                                                        item.getDataType().isNullable(),
                                                        item.getDataType().getPrecision(),
                                                        item.getDataType().getScale())),
                                        item.getComment() != null ? item.getComment() : null);
                        columns.add(columnMetadata);
                    });
        }
        return columns;
    }

    /**
     * Retrieves the associated CatalogInfo object based on the given catalog name.
     *
     * @param catalogName The name of the catalog for which to retrieve the associated CatalogInfo.
     * @return The associated CatalogInfo object, or null if it doesn't exist.
     */
    private CatalogInfo getCatalogInfo(String catalogName) {
        LambdaQueryWrapper<CatalogInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CatalogInfo::getCatalogName, catalogName);
        return catalogService.getOne(queryWrapper);
    }
}
