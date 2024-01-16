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

package org.apache.paimon.web.server.service.impl;

import org.apache.paimon.table.Table;
import org.apache.paimon.types.DataField;
import org.apache.paimon.web.api.catalog.PaimonService;
import org.apache.paimon.web.api.table.TableChange;
import org.apache.paimon.web.api.table.metadata.ColumnMetadata;
import org.apache.paimon.web.api.table.metadata.TableMetadata;
import org.apache.paimon.web.server.data.dto.AlterTableDTO;
import org.apache.paimon.web.server.data.dto.TableDTO;
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.model.TableColumn;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.data.vo.TableVO;
import org.apache.paimon.web.server.service.CatalogService;
import org.apache.paimon.web.server.service.TableService;
import org.apache.paimon.web.server.util.DataTypeConvertUtils;
import org.apache.paimon.web.server.util.PaimonDataType;
import org.apache.paimon.web.server.util.PaimonServiceUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/** The implementation of {@link TableService}. */
@Slf4j
@Service
public class TableServiceImpl implements TableService {

    private static final String FIELDS_PREFIX = "FIELDS";

    private static final String DEFAULT_VALUE_SUFFIX = "default-value";

    private final CatalogService catalogService;

    public TableServiceImpl(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Override
    public R<Void> createTable(TableDTO tableDTO) {
        try {
            PaimonService service =
                    PaimonServiceUtils.getPaimonService(getCatalogInfo(tableDTO.getCatalogName()));
            List<String> partitionKeys = tableDTO.getPartitionKey();

            Map<String, String> tableOptions = tableDTO.getTableOptions();
            List<TableColumn> tableColumns = tableDTO.getTableColumns();
            if (!CollectionUtils.isEmpty(tableColumns)) {
                for (TableColumn tableColumn : tableColumns) {
                    if (tableColumn.getDefaultValue() != null
                            && !tableColumn.getDefaultValue().isEmpty()) {
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
            if (service.tableExists(tableDTO.getDatabaseName(), tableDTO.getName())) {
                return R.failed(Status.TABLE_NAME_IS_EXIST, tableDTO.getName());
            }
            service.createTable(tableDTO.getDatabaseName(), tableDTO.getName(), tableMetadata);
            return R.succeed();
        } catch (Exception e) {
            log.error("Exception with creating table.", e);
            return R.failed(Status.TABLE_CREATE_ERROR);
        }
    }

    @Override
    public R<Void> addColumn(TableDTO tableDTO) {
        try {
            PaimonService service =
                    PaimonServiceUtils.getPaimonService(getCatalogInfo(tableDTO.getCatalogName()));
            List<TableColumn> tableColumns = tableDTO.getTableColumns();
            List<TableChange> tableChanges = new ArrayList<>();
            Map<String, String> options = new HashMap<>();
            for (TableColumn tableColumn : tableColumns) {
                if (tableColumn.getDefaultValue() != null
                        && !tableColumn.getDefaultValue().isEmpty()) {
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

            if (!options.isEmpty()) {
                for (Map.Entry<String, String> entry : options.entrySet()) {
                    TableChange.SetOption setOption =
                            TableChange.set(entry.getKey(), entry.getValue());
                    tableChanges.add(setOption);
                }
            }
            service.alterTable(tableDTO.getDatabaseName(), tableDTO.getName(), tableChanges);
            return R.succeed();
        } catch (Exception e) {
            log.error("Exception with adding column.", e);
            return R.failed(Status.TABLE_ADD_COLUMN_ERROR);
        }
    }

    @Override
    public R<Void> dropColumn(
            String catalogName, String databaseName, String tableName, String columnName) {
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

    @Override
    public R<Void> alterTable(AlterTableDTO alterTableDTO) {
        try {
            String databaseName = alterTableDTO.getDatabaseName();
            String tableName = alterTableDTO.getTableName();
            PaimonService service =
                    PaimonServiceUtils.getPaimonService(
                            getCatalogInfo(alterTableDTO.getCatalogName()));

            Table table = service.getTable(databaseName, tableName);
            Map<String, String> options = table.options();
            Map<Integer, DataField> dataFieldMap = getFields(table);

            DataField dataField;
            List<TableChange> tableChanges = new ArrayList<>();
            List<TableColumn> tableColumns = alterTableDTO.getTableColumns();
            for (TableColumn tableColumn : tableColumns) {
                dataField = dataFieldMap.get(tableColumn.getId());
                addTableChanges(tableColumn, dataField, options, tableChanges);
            }
            if (tableChanges.size() > 0) {
                service.alterTable(databaseName, tableName, tableChanges);
            }
            return R.succeed();
        } catch (Exception e) {
            log.error("Exception with altering table.", e);
            return R.failed(Status.TABLE_AlTER_COLUMN_ERROR);
        }
    }

    private Map<Integer, DataField> getFields(Table table) {
        List<DataField> fields = table.rowType().getFields();
        return fields.stream().collect(Collectors.toMap(DataField::id, Function.identity()));
    }

    @Override
    public R<Void> addOption(TableDTO tableDTO) {
        List<TableChange> tableChanges = new ArrayList<>();
        try {
            PaimonService service =
                    PaimonServiceUtils.getPaimonService(getCatalogInfo(tableDTO.getCatalogName()));
            Map<String, String> tableOptions = tableDTO.getTableOptions();
            for (Map.Entry<String, String> entry : tableOptions.entrySet()) {
                TableChange.SetOption setOption = TableChange.set(entry.getKey(), entry.getValue());
                tableChanges.add(setOption);
            }
            service.alterTable(tableDTO.getDatabaseName(), tableDTO.getName(), tableChanges);
            return R.succeed();
        } catch (Exception e) {
            log.error("Exception with adding option.", e);
            return R.failed(Status.TABLE_ADD_OPTION_ERROR);
        }
    }

    @Override
    public R<Void> removeOption(
            String catalogName, String databaseName, String tableName, String key) {
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

    @Override
    public R<Void> dropTable(String catalogName, String databaseName, String tableName) {
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

    @Override
    public R<Void> renameTable(
            String catalogName, String databaseName, String fromTableName, String toTableName) {
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

    @Override
    public List<TableVO> listTables(TableDTO tableDTO) {
        List<TableVO> resultList = new LinkedList<>();
        List<CatalogInfo> catalogInfoList = catalogService.list();
        PaimonService paimonService;
        for (CatalogInfo catalog : catalogInfoList) {
            if (Objects.nonNull(tableDTO.getCatalogId())
                    && Objects.nonNull(tableDTO.getDatabaseName())
                    && catalog.getId().equals(tableDTO.getCatalogId())) {
                paimonService = PaimonServiceUtils.getPaimonService(catalog);
                List<String> tables = paimonService.listTables(tableDTO.getDatabaseName());
                tables.forEach(
                        name -> {
                            TableVO table = new TableVO();
                            table.setCatalogId(catalog.getId());
                            table.setCatalogName(catalog.getCatalogName());
                            table.setName(name);
                            table.setDatabaseName(tableDTO.getDatabaseName());
                            resultList.add(table);
                        });
                break;
            }
            if (Objects.nonNull(tableDTO.getName())) {
                paimonService = PaimonServiceUtils.getPaimonService(catalog);
                List<String> databaseList = paimonService.listDatabases();
                for (String database : databaseList) {
                    List<String> tables = paimonService.listTables(database);
                    tables.forEach(
                            tableName -> {
                                if (tableName.contains(tableDTO.getName())) {
                                    TableVO table = new TableVO();
                                    table.setCatalogId(catalog.getId());
                                    table.setCatalogName(catalog.getCatalogName());
                                    table.setDatabaseName(database);
                                    table.setName(tableName);
                                    resultList.add(table);
                                }
                            });
                }
            }
        }

        return resultList;
    }

    @Override
    public TableVO listColumns(String catalogName, String databaseName, String tableName) {
        PaimonService service = PaimonServiceUtils.getPaimonService(getCatalogInfo(catalogName));
        Table table = service.getTable(databaseName, tableName);
        TableVO.TableVOBuilder builder =
                TableVO.builder()
                        .catalogName(catalogName)
                        .databaseName(databaseName)
                        .name(tableName);
        if (Objects.nonNull(table)) {
            List<String> primaryKeys = table.primaryKeys();
            List<DataField> fields = table.rowType().getFields();
            List<TableColumn> tableColumns = new ArrayList<>();
            Map<String, String> options = table.options();
            if (CollectionUtils.isNotEmpty(fields)) {
                for (DataField field : fields) {
                    String key = FIELDS_PREFIX + "." + field.name() + "." + DEFAULT_VALUE_SUFFIX;
                    TableColumn.TableColumnBuilder columnBuilder =
                            TableColumn.builder()
                                    .id(field.id())
                                    .field(field.name())
                                    .sort(field.id() + 1)
                                    .dataType(DataTypeConvertUtils.fromPaimonType(field.type()))
                                    .comment(field.description());
                    if (CollectionUtils.isNotEmpty(primaryKeys)
                            && primaryKeys.contains(field.name())) {
                        columnBuilder.isPk(true);
                    }
                    if (options.get(key) != null) {
                        columnBuilder.defaultValue(options.get(key));
                    }
                    tableColumns.add(columnBuilder.build());
                }
            }
            builder.columns(tableColumns).partitionKey(table.partitionKeys());
        }
        return builder.build();
    }

    private void addTableChanges(
            TableColumn tableColumn,
            DataField dataField,
            Map<String, String> options,
            List<TableChange> tableChanges) {
        if (!Objects.equals(tableColumn.getField(), dataField.name())) {
            ColumnMetadata columnMetadata =
                    new ColumnMetadata(dataField.name(), dataField.type(), dataField.description());

            TableChange.ModifyColumnName modifyColumnName =
                    TableChange.modifyColumnName(columnMetadata, tableColumn.getField());
            tableChanges.add(modifyColumnName);
        }

        ColumnMetadata columnMetadata =
                new ColumnMetadata(
                        tableColumn.getField(), dataField.type(), dataField.description());

        if (!DataTypeConvertUtils.convert(tableColumn.getDataType()).equals(dataField.type())) {
            TableChange.ModifyColumnType modifyColumnType =
                    TableChange.modifyColumnType(
                            columnMetadata,
                            DataTypeConvertUtils.convert(tableColumn.getDataType()));
            tableChanges.add(modifyColumnType);
        }

        if (!Objects.equals(tableColumn.getComment(), dataField.description())) {
            TableChange.ModifyColumnComment modifyColumnComment =
                    TableChange.modifyColumnComment(columnMetadata, tableColumn.getComment());
            tableChanges.add(modifyColumnComment);
        }

        String key = FIELDS_PREFIX + "." + tableColumn.getField() + "." + DEFAULT_VALUE_SUFFIX;
        if (options.get(key) != null) {
            String defaultValue = options.get(key);
            if (!Objects.equals(tableColumn.getDefaultValue(), defaultValue)) {
                TableChange.SetOption setOption =
                        TableChange.set(
                                FIELDS_PREFIX
                                        + "."
                                        + tableColumn.getField()
                                        + "."
                                        + DEFAULT_VALUE_SUFFIX,
                                tableColumn.getDefaultValue());
                tableChanges.add(setOption);
            }
        }
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
