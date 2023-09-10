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

import org.apache.paimon.catalog.Catalog;
import org.apache.paimon.table.Table;
import org.apache.paimon.types.DataField;
import org.apache.paimon.web.api.common.OperatorKind;
import org.apache.paimon.web.api.database.DatabaseManager;
import org.apache.paimon.web.api.table.AlterTableEntity;
import org.apache.paimon.web.api.table.ColumnMetadata;
import org.apache.paimon.web.api.table.TableManager;
import org.apache.paimon.web.api.table.TableMetadata;
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.model.TableColumn;
import org.apache.paimon.web.server.data.model.TableInfo;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.service.CatalogService;
import org.apache.paimon.web.server.util.CatalogUtils;
import org.apache.paimon.web.server.util.DataTypeConvertUtils;
import org.apache.paimon.web.server.util.PaimonDataType;

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

/** Table api controller. */
@Slf4j
@RestController
@RequestMapping("/api/table")
public class TableController {

    private final String FIELDS_PREFIX = "fields";
    private final String DEFAULT_VALUE_SUFFIX = "default-value";

    @Autowired private CatalogService catalogService;

    /**
     * Creates a table in the database based on the provided TableInfo.
     *
     * @param tableInfo The TableInfo object containing information about the table.
     * @return R<Void/> indicating the success or failure of the operation.
     */
    @PostMapping("/createTable")
    public R<Void> createTable(@RequestBody TableInfo tableInfo) {
        try {
            Catalog catalog = CatalogUtils.getCatalog(getCatalogInfo(tableInfo.getCatalogName()));
            List<String> partitionKeys = tableInfo.getPartitionKey();

            Map<String, String> tableOptions = tableInfo.getTableOptions();
            List<TableColumn> tableColumns = tableInfo.getTableColumns();
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
                            .columns(buildColumns(tableInfo))
                            .partitionKeys(partitionKeys)
                            .primaryKeys(buildPrimaryKeys(tableInfo))
                            .options(tableOptions)
                            .comment(tableInfo.getDescription())
                            .build();
            if (TableManager.tableExists(
                    catalog, tableInfo.getDatabaseName(), tableInfo.getTableName())) {
                return R.failed(Status.TABLE_NAME_IS_EXIST, tableInfo.getTableName());
            }
            TableManager.createTable(
                    catalog, tableInfo.getDatabaseName(), tableInfo.getTableName(), tableMetadata);
            return R.succeed();
        } catch (Exception e) {
            log.error(e.getMessage());
            return R.failed(Status.TABLE_CREATE_ERROR);
        }
    }

    /**
     * Adds a column to the table.
     *
     * @param tableInfo The information of the table, including the catalog name, database name,
     *     table name, and table columns.
     * @return A response indicating the success or failure of the operation.
     */
    @PostMapping("/addColumn")
    public R<Void> addColumn(@RequestBody TableInfo tableInfo) {
        try {
            Catalog catalog = CatalogUtils.getCatalog(getCatalogInfo(tableInfo.getCatalogName()));
            List<TableColumn> tableColumns = tableInfo.getTableColumns();
            List<AlterTableEntity> entityList = new ArrayList<>();
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
                AlterTableEntity alterTableEntity =
                        AlterTableEntity.builder()
                                .columnName(tableColumn.getField())
                                .type(
                                        DataTypeConvertUtils.convert(
                                                new PaimonDataType(
                                                        tableColumn.getDataType(),
                                                        tableColumn.isNullable(),
                                                        tableColumn.getLength0(),
                                                        tableColumn.getLength1())))
                                .comment(tableColumn.getComment())
                                .kind(OperatorKind.ADD_COLUMN)
                                .build();
                entityList.add(alterTableEntity);
            }
            TableManager.alterTable(
                    catalog, tableInfo.getDatabaseName(), tableInfo.getTableName(), entityList);
            if (options.size() > 0) {
                TableManager.setOptions(
                        catalog, tableInfo.getDatabaseName(), tableInfo.getTableName(), options);
            }
            return R.succeed();
        } catch (Exception e) {
            log.error(e.getMessage());
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
    @DeleteMapping("/dropColumn/{catalogName}/{databaseName}/{tableName}/{columnName}")
    public R<Void> dropColumn(
            @PathVariable String catalogName,
            @PathVariable String databaseName,
            @PathVariable String tableName,
            @PathVariable String columnName) {
        try {
            Catalog catalog = CatalogUtils.getCatalog(getCatalogInfo(catalogName));
            List<AlterTableEntity> entityList = new ArrayList<>();
            AlterTableEntity alterTableEntity =
                    AlterTableEntity.builder()
                            .columnName(columnName)
                            .kind(OperatorKind.DROP_COLUMN)
                            .build();
            entityList.add(alterTableEntity);
            TableManager.alterTable(catalog, databaseName, tableName, entityList);
            return R.succeed();
        } catch (Exception e) {
            log.error(e.getMessage());
            return R.failed(Status.TABLE_DROP_COLUMN_ERROR);
        }
    }

    /**
     * Renames a column in a table.
     *
     * @param catalogName The name of the catalog.
     * @param databaseName The name of the database.
     * @param tableName The name of the table.
     * @param fromColumnName The current name of the column.
     * @param toColumnName The new name for the column.
     * @return The result indicating the success or failure of the operation.
     */
    @PostMapping("/renameColumn")
    public R<Void> renameColumn(
            @RequestParam String catalogName,
            @RequestParam String databaseName,
            @RequestParam String tableName,
            @RequestParam String fromColumnName,
            @RequestParam String toColumnName) {
        try {
            Catalog catalog = CatalogUtils.getCatalog(getCatalogInfo(catalogName));

            AlterTableEntity alterTableEntity =
                    AlterTableEntity.builder()
                            .columnName(fromColumnName)
                            .newColumn(toColumnName)
                            .kind(OperatorKind.RENAME_COLUMN)
                            .build();

            List<AlterTableEntity> entityList = new ArrayList<>();
            entityList.add(alterTableEntity);

            TableManager.alterTable(catalog, databaseName, tableName, entityList);
            return R.succeed();
        } catch (Exception e) {
            log.error(e.getMessage());
            return R.failed(Status.TABLE_RENAME_COLUMN_ERROR);
        }
    }

    /**
     * Updates the data type of columns in a table.
     *
     * @param tableInfo The information of the table.
     * @return The result of the operation.
     */
    @PostMapping("/updateColumnType")
    public R<Void> updateColumnType(@RequestBody TableInfo tableInfo) {
        try {
            Catalog catalog = CatalogUtils.getCatalog(getCatalogInfo(tableInfo.getCatalogName()));
            List<TableColumn> tableColumns = tableInfo.getTableColumns();
            List<AlterTableEntity> entityList = new ArrayList<>();
            for (TableColumn tableColumn : tableColumns) {
                AlterTableEntity alterTableEntity =
                        AlterTableEntity.builder()
                                .columnName(tableColumn.getField())
                                .type(
                                        DataTypeConvertUtils.convert(
                                                new PaimonDataType(
                                                        tableColumn.getDataType(),
                                                        tableColumn.isNullable(),
                                                        tableColumn.getLength0(),
                                                        tableColumn.getLength1())))
                                .kind(OperatorKind.UPDATE_COLUMN_TYPE)
                                .build();
                entityList.add(alterTableEntity);
            }
            TableManager.alterTable(
                    catalog, tableInfo.getDatabaseName(), tableInfo.getTableName(), entityList);
            return R.succeed();
        } catch (Exception e) {
            log.error(e.getMessage());
            return R.failed(Status.TABLE_UPDATE_COLUMN_TYPE_ERROR);
        }
    }

    /**
     * Updates the comments of columns in a table.
     *
     * @param tableInfo The information of the table.
     * @return The result of the operation.
     */
    @PostMapping("/updateColumnComment")
    public R<Void> updateColumnComment(@RequestBody TableInfo tableInfo) {
        try {
            Catalog catalog = CatalogUtils.getCatalog(getCatalogInfo(tableInfo.getCatalogName()));
            List<TableColumn> tableColumns = tableInfo.getTableColumns();
            List<AlterTableEntity> entityList = new ArrayList<>();
            for (TableColumn tableColumn : tableColumns) {
                AlterTableEntity alterTableEntity =
                        AlterTableEntity.builder()
                                .columnName(tableColumn.getField())
                                .comment(tableColumn.getComment())
                                .kind(OperatorKind.UPDATE_COLUMN_COMMENT)
                                .build();
                entityList.add(alterTableEntity);
            }
            TableManager.alterTable(
                    catalog, tableInfo.getDatabaseName(), tableInfo.getTableName(), entityList);
            return R.succeed();
        } catch (Exception e) {
            log.error(e.getMessage());
            return R.failed(Status.TABLE_UPDATE_COLUMN_COMMENT_ERROR);
        }
    }

    /**
     * Adds options to a table.
     *
     * @param tableInfo An object containing table information.
     * @return If the options are successfully added, returns a successful result object. If an
     *     exception occurs, returns a result object with an error status.
     */
    @PostMapping("/addOption")
    public R<Void> addOption(@RequestBody TableInfo tableInfo) {
        try {
            Catalog catalog = CatalogUtils.getCatalog(getCatalogInfo(tableInfo.getCatalogName()));
            Map<String, String> tableOptions = tableInfo.getTableOptions();
            TableManager.setOptions(
                    catalog, tableInfo.getDatabaseName(), tableInfo.getTableName(), tableOptions);
            return R.succeed();
        } catch (Exception e) {
            log.error(e.getMessage());
            return R.failed(Status.TABLE_ADD_OPTION_ERROR);
        }
    }

    /**
     * Endpoint for deleting a table option.
     *
     * @param tableInfo The table information containing the catalog, database, table name, and
     *     table options.
     * @return A response indicating the success or failure of the operation.
     */
    @PostMapping("/removeOption")
    public R<Void> deleteOption(@RequestBody TableInfo tableInfo) {
        try {
            Catalog catalog = CatalogUtils.getCatalog(getCatalogInfo(tableInfo.getCatalogName()));
            Map<String, String> tableOptions = tableInfo.getTableOptions();
            TableManager.removeOptions(
                    catalog, tableInfo.getDatabaseName(), tableInfo.getTableName(), tableOptions);
            return R.succeed();
        } catch (Exception e) {
            log.error(e.getMessage());
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
    @DeleteMapping("/delete/{catalogName}/{databaseName}/{tableName}")
    public R<Void> dropTable(
            @PathVariable String catalogName,
            @PathVariable String databaseName,
            @PathVariable String tableName) {
        try {
            Catalog catalog = CatalogUtils.getCatalog(getCatalogInfo(catalogName));
            TableManager.dropTable(catalog, databaseName, tableName);
            return R.succeed();
        } catch (Exception e) {
            log.error(e.getMessage());
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
    @PostMapping("/renameTable")
    public R<Void> renameTable(
            @RequestParam String catalogName,
            @RequestParam String databaseName,
            @RequestParam String fromTableName,
            @RequestParam String toTableName) {
        try {
            Catalog catalog = CatalogUtils.getCatalog(getCatalogInfo(catalogName));
            TableManager.renameTable(catalog, databaseName, fromTableName, toTableName);
            return R.succeed();
        } catch (Exception e) {
            log.error(e.getMessage());
            return R.failed(Status.TABLE_RENAME_ERROR);
        }
    }

    /**
     * Handler method for the "/getAllTables" endpoint. Retrieves information about all tables and
     * returns a response containing the table details.
     *
     * @return Response object containing a list of {@link TableInfo} representing the tables.
     */
    @GetMapping("/getAllTables")
    public R<List<TableInfo>> getAllTables() {
        List<TableInfo> tableInfoList = new ArrayList<>();
        List<CatalogInfo> catalogInfoList = catalogService.list();
        if (!CollectionUtils.isEmpty(catalogInfoList)) {
            for (CatalogInfo item : catalogInfoList) {
                Catalog catalog = CatalogUtils.getCatalog(item);
                List<String> databaseList = DatabaseManager.listDatabase(catalog);
                if (!CollectionUtils.isEmpty(databaseList)) {
                    for (String db : databaseList) {
                        try {
                            List<String> tables = TableManager.listTables(catalog, db);
                            if (!CollectionUtils.isEmpty(tables)) {
                                for (String t : tables) {
                                    try {
                                        Table table = TableManager.getTable(catalog, db, t);
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
                                                    TableColumn.TableColumnBuilder builder =
                                                            TableColumn.builder()
                                                                    .field(field.name())
                                                                    .dataType(
                                                                            DataTypeConvertUtils
                                                                                    .fromPaimonType(
                                                                                            field
                                                                                                    .type())
                                                                                    .getType())
                                                                    .comment(field.description())
                                                                    .isNullable(
                                                                            field.type()
                                                                                    .isNullable())
                                                                    .length0(
                                                                            DataTypeConvertUtils
                                                                                    .fromPaimonType(
                                                                                            field
                                                                                                    .type())
                                                                                    .getLength0())
                                                                    .length1(
                                                                            DataTypeConvertUtils
                                                                                    .fromPaimonType(
                                                                                            field
                                                                                                    .type())
                                                                                    .getLength1());
                                                    if (primaryKeys.size() > 0
                                                            && primaryKeys.contains(field.name())) {
                                                        builder.isPK(true);
                                                    }
                                                    if (options.get(key) != null) {
                                                        builder.defaultValue(options.get(key));
                                                    }
                                                    tableColumns.add(builder.build());
                                                }
                                            }
                                            TableInfo tableInfo =
                                                    TableInfo.builder()
                                                            .catalogName(item.getCatalogName())
                                                            .databaseName(db)
                                                            .tableName(table.name())
                                                            .partitionKey(table.partitionKeys())
                                                            .tableOptions(table.options())
                                                            .tableColumns(tableColumns)
                                                            .build();
                                            tableInfoList.add(tableInfo);
                                        }
                                    } catch (Catalog.TableNotExistException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        } catch (Catalog.DatabaseNotExistException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        return R.succeed(tableInfoList);
    }

    /**
     * Builds a list of primary keys for the given table.
     *
     * @param tableInfo The TableInfo object representing the table.
     * @return A list of primary keys as strings.
     */
    private List<String> buildPrimaryKeys(TableInfo tableInfo) {
        List<String> primaryKeys = new ArrayList<>();
        List<TableColumn> tableColumns = tableInfo.getTableColumns();
        if (!CollectionUtils.isEmpty(tableColumns)) {
            tableColumns.forEach(
                    item -> {
                        if (item.isPK()) {
                            primaryKeys.add(item.getField());
                        }
                    });
        }
        return primaryKeys;
    }

    /**
     * Builds a list of ColumnMetadata objects for the given table.
     *
     * @param tableInfo The TableInfo object representing the table.
     * @return A list of ColumnMetadata objects.
     */
    private List<ColumnMetadata> buildColumns(TableInfo tableInfo) {
        List<ColumnMetadata> columns = new ArrayList<>();
        List<TableColumn> tableColumns = tableInfo.getTableColumns();
        if (!CollectionUtils.isEmpty(tableColumns)) {
            tableColumns.forEach(
                    item -> {
                        ColumnMetadata columnMetadata =
                                new ColumnMetadata(
                                        item.getField(),
                                        DataTypeConvertUtils.convert(
                                                new PaimonDataType(
                                                        item.getDataType(),
                                                        item.isNullable(),
                                                        item.getLength0(),
                                                        item.getLength1())),
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
