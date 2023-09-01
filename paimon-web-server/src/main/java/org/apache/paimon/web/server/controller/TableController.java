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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Table api controller. */
@Slf4j
@RestController
@RequestMapping("/api/table")
public class TableController {

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
            e.printStackTrace();
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
            for (TableColumn tableColumn : tableColumns) {
                AlterTableEntity alterTableEntity =
                        AlterTableEntity.builder()
                                .columnName(tableColumn.getField())
                                .type(DataTypeConvertUtils.convert(tableColumn.getDataType()))
                                .comment(tableColumn.getComment())
                                .kind(OperatorKind.ADD_COLUMN)
                                .build();
                entityList.add(alterTableEntity);
            }
            TableManager.alterTable(
                    catalog, tableInfo.getDatabaseName(), tableInfo.getTableName(), entityList);
            return R.succeed();
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed(Status.TABLE_ADD_COLUMN_ERROR);
        }
    }

    /**
     * Drops a column from a table.
     *
     * @param tableInfo The information of the table.
     * @return The result of the operation.
     */
    @PostMapping("/dropColumn")
    public R<Void> dropColumn(@RequestBody TableInfo tableInfo) {
        try {
            Catalog catalog = CatalogUtils.getCatalog(getCatalogInfo(tableInfo.getCatalogName()));
            List<TableColumn> tableColumns = tableInfo.getTableColumns();
            List<AlterTableEntity> entityList = new ArrayList<>();
            for (TableColumn tableColumn : tableColumns) {
                AlterTableEntity alterTableEntity =
                        AlterTableEntity.builder()
                                .columnName(tableColumn.getField())
                                .kind(OperatorKind.DROP_COLUMN)
                                .build();
                entityList.add(alterTableEntity);
            }
            TableManager.alterTable(
                    catalog, tableInfo.getDatabaseName(), tableInfo.getTableName(), entityList);
            return R.succeed();
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed(Status.TABLE_DROP_COLUMN_ERROR);
        }
    }

    /**
     * Renames a column in a table.
     *
     * @param tableInfo The information of the table.
     * @return The result of the operation.
     */
    @PostMapping("/renameColumn")
    public R<Void> renameColumn(@RequestBody TableInfo tableInfo) {
        try {
            Catalog catalog = CatalogUtils.getCatalog(getCatalogInfo(tableInfo.getCatalogName()));
            List<TableColumn> tableColumns = tableInfo.getTableColumns();

            if (tableColumns.size() != 2) {
                throw new IllegalArgumentException("Expected exactly 2 TableColumn objects");
            }

            String columnName = tableColumns.get(0).getField();
            String newColumnName = tableColumns.get(1).getField();

            AlterTableEntity alterTableEntity =
                    AlterTableEntity.builder()
                            .columnName(columnName)
                            .newColumn(newColumnName)
                            .kind(OperatorKind.RENAME_COLUMN)
                            .build();

            List<AlterTableEntity> entityList = new ArrayList<>();
            entityList.add(alterTableEntity);

            TableManager.alterTable(
                    catalog, tableInfo.getDatabaseName(), tableInfo.getTableName(), entityList);
            return R.succeed();
        } catch (Exception e) {
            e.printStackTrace();
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
                                .type(DataTypeConvertUtils.convert(tableColumn.getDataType()))
                                .kind(OperatorKind.UPDATE_COLUMN_TYPE)
                                .build();
                entityList.add(alterTableEntity);
            }
            TableManager.alterTable(
                    catalog, tableInfo.getDatabaseName(), tableInfo.getTableName(), entityList);
            return R.succeed();
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
            return R.failed(Status.TABLE_REMOVE_OPTION_ERROR);
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
        if (catalogInfoList.size() > 0) {
            for (CatalogInfo item : catalogInfoList) {
                Catalog catalog = CatalogUtils.getCatalog(item);
                List<String> databaseList = DatabaseManager.listDatabase(catalog);
                if (databaseList.size() > 0) {
                    for (String db : databaseList) {
                        try {
                            List<String> tables = TableManager.listTables(catalog, db);
                            if (tables.size() > 0) {
                                for (String t : tables) {
                                    try {
                                        Table table = TableManager.getTable(catalog, db, t);
                                        if (table != null) {
                                            List<String> primaryKeys = table.primaryKeys();
                                            List<DataField> fields = table.rowType().getFields();
                                            List<TableColumn> tableColumns = new ArrayList<>();
                                            if (fields.size() > 0) {
                                                for (DataField field : fields) {
                                                    TableColumn.TableColumnBuilder builder =
                                                            TableColumn.builder()
                                                                    .field(field.name())
                                                                    .dataType(
                                                                            DataTypeConvertUtils
                                                                                    .fromPaimonType(
                                                                                            field
                                                                                                    .type()))
                                                                    .comment(field.description());
                                                    if (primaryKeys.size() > 0
                                                            && primaryKeys.contains(field.name())) {
                                                        builder.isPK(true);
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
        if (tableColumns != null && tableColumns.size() > 0) {
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
        if (tableColumns != null && tableColumns.size() > 0) {
            tableColumns.forEach(
                    item -> {
                        ColumnMetadata columnMetadata =
                                new ColumnMetadata(
                                        item.getField(),
                                        DataTypeConvertUtils.convert(item.getDataType()),
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
