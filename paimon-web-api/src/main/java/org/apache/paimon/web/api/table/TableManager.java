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

package org.apache.paimon.web.api.table;

import org.apache.paimon.catalog.Catalog;
import org.apache.paimon.catalog.CatalogContext;
import org.apache.paimon.catalog.FileSystemCatalog;
import org.apache.paimon.catalog.Identifier;
import org.apache.paimon.data.GenericRow;
import org.apache.paimon.data.InternalRow;
import org.apache.paimon.fs.FileIO;
import org.apache.paimon.fs.Path;
import org.apache.paimon.hive.HiveCatalog;
import org.apache.paimon.options.Options;
import org.apache.paimon.reader.RecordReader;
import org.apache.paimon.schema.Schema;
import org.apache.paimon.schema.SchemaChange;
import org.apache.paimon.table.Table;
import org.apache.paimon.table.sink.BatchTableCommit;
import org.apache.paimon.table.sink.BatchTableWrite;
import org.apache.paimon.table.sink.BatchWriteBuilder;
import org.apache.paimon.table.sink.CommitMessage;
import org.apache.paimon.table.sink.StreamWriteBuilder;
import org.apache.paimon.table.sink.TableWrite;
import org.apache.paimon.table.sink.WriteBuilder;
import org.apache.paimon.table.source.ReadBuilder;
import org.apache.paimon.table.source.Split;
import org.apache.paimon.table.source.TableRead;
import org.apache.paimon.utils.SnapshotManager;
import org.apache.paimon.web.api.common.CatalogEntity;
import org.apache.paimon.web.api.common.CatalogProperties;
import org.apache.paimon.web.api.common.MetastoreType;
import org.apache.paimon.web.api.common.OperatorKind;
import org.apache.paimon.web.api.common.WriteMode;
import org.apache.paimon.web.api.table.metadata.ColumnMetadata;
import org.apache.paimon.web.api.table.metadata.ConsumerTableMetadata;
import org.apache.paimon.web.api.table.metadata.FileTableMetadata;
import org.apache.paimon.web.api.table.metadata.ManifestTableMetadata;
import org.apache.paimon.web.api.table.metadata.OptionTableMetadata;
import org.apache.paimon.web.api.table.metadata.SchemaTableMetadata;
import org.apache.paimon.web.api.table.metadata.SnapshotTableMetadata;
import org.apache.paimon.web.api.table.metadata.TableMetadata;
import org.apache.paimon.web.api.table.metadata.TagTableMetadata;
import org.apache.paimon.web.common.annotation.VisibleForTesting;
import org.apache.paimon.web.common.utils.ParameterValidationUtil;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** paimon table manager. */
public class TableManager {

    private static final String SNAPSHOTS = "snapshots";
    private static final String SCHEMAS = "schemas";
    private static final String OPTIONS = "options";
    private static final String MANIFESTS = "manifests";
    private static final String FILES = "files";
    private static final String CONSUMER = "consumers";
    private static final String TAGS = "tags";

    public static void createTable(
            Catalog catalog, String dbName, String tableName, TableMetadata tableMetadata)
            throws Catalog.TableAlreadyExistException, Catalog.DatabaseNotExistException {
        checkNotNull(catalog, dbName, tableName);

        Schema.Builder schemaBuilder =
                Schema.newBuilder()
                        .partitionKeys(
                                tableMetadata.partitionKeys() == null
                                        ? ImmutableList.of()
                                        : ImmutableList.copyOf(tableMetadata.partitionKeys()))
                        .primaryKey(
                                tableMetadata.primaryKeys() == null
                                        ? ImmutableList.of()
                                        : ImmutableList.copyOf(tableMetadata.primaryKeys()))
                        .comment(tableMetadata.comment() == null ? "" : tableMetadata.comment())
                        .options(handleOptions(tableMetadata.options()));

        for (ColumnMetadata column : tableMetadata.columns()) {
            schemaBuilder.column(column.name(), column.type(), column.description());
        }

        Schema schema = schemaBuilder.build();

        Identifier identifier = Identifier.create(dbName, tableName);

        catalog.createTable(identifier, schema, false);
    }

    public static boolean tableExists(Catalog catalog, String dbName, String tableName) {
        checkNotNull(catalog, dbName, tableName);

        Identifier identifier = Identifier.create(dbName, tableName);
        return catalog.tableExists(identifier);
    }

    public static Table getTable(Catalog catalog, String dbName, String tableName)
            throws Catalog.TableNotExistException {
        checkNotNull(catalog, dbName, tableName);

        Identifier identifier = Identifier.create(dbName, tableName);
        return catalog.getTable(identifier);
    }

    public static List<String> listTables(Catalog catalog, String dbName)
            throws Catalog.DatabaseNotExistException {
        ParameterValidationUtil.checkNotNull(
                new SimpleEntry<>(catalog, () -> "Catalog"),
                new SimpleEntry<>(dbName, () -> "Database name"));
        return catalog.listTables(dbName);
    }

    public static void dropTable(Catalog catalog, String dbName, String tableName)
            throws Catalog.TableNotExistException {
        checkNotNull(catalog, dbName, tableName);

        Identifier identifier = Identifier.create(dbName, tableName);
        catalog.dropTable(identifier, false);
    }

    public static void renameTable(Catalog catalog, String dbName, String fromTable, String toTable)
            throws Catalog.TableAlreadyExistException, Catalog.TableNotExistException {
        ParameterValidationUtil.checkNotNull(
                new SimpleEntry<>(catalog, () -> "Catalog"),
                new SimpleEntry<>(dbName, () -> "Database name"),
                new SimpleEntry<>(fromTable, () -> "From table name"),
                new SimpleEntry<>(toTable, () -> "To table name"));

        Identifier fromTableIdentifier = Identifier.create(dbName, fromTable);
        Identifier toTableIdentifier = Identifier.create(dbName, toTable);
        catalog.renameTable(fromTableIdentifier, toTableIdentifier, false);
    }

    public static void setOptions(
            Catalog catalog, String dbName, String tableName, Map<String, String> options)
            throws Catalog.ColumnAlreadyExistException, Catalog.TableNotExistException,
                    Catalog.ColumnNotExistException {
        checkNotNull(catalog, dbName, tableName);

        Identifier identifier = Identifier.create(dbName, tableName);

        List<SchemaChange> schemaChanges = new ArrayList<>();

        Map<String, String> filteredOptions = handleOptions(options);
        for (String key : filteredOptions.keySet()) {
            SchemaChange addOption = SchemaChange.setOption(key, filteredOptions.get(key));
            schemaChanges.add(addOption);
        }

        catalog.alterTable(identifier, schemaChanges, false);
    }

    public static void removeOptions(
            Catalog catalog, String dbName, String tableName, Map<String, String> options)
            throws Catalog.ColumnAlreadyExistException, Catalog.TableNotExistException,
                    Catalog.ColumnNotExistException {
        checkNotNull(catalog, dbName, tableName);

        Identifier identifier = Identifier.create(dbName, tableName);

        List<SchemaChange> schemaChanges = new ArrayList<>();

        Map<String, String> filteredOptions = handleOptions(options);
        for (String key : filteredOptions.keySet()) {
            SchemaChange addOption = SchemaChange.removeOption(key);
            schemaChanges.add(addOption);
        }

        catalog.alterTable(identifier, schemaChanges, false);
    }

    private static SchemaChange addColumn(AlterTableEntity entity) {
        ParameterValidationUtil.checkNotNull(
                new SimpleEntry<>(entity.getColumnName(), () -> "Column name"),
                new SimpleEntry<>(entity.getType(), () -> "Column type"));
        return SchemaChange.addColumn(
                entity.getColumnName(), entity.getType(), entity.getComment());
    }

    private static SchemaChange renameColumn(
            Catalog catalog, String dbName, String tableName, AlterTableEntity entity)
            throws Catalog.TableNotExistException, IOException {
        ParameterValidationUtil.checkNotNull(
                new SimpleEntry<>(entity.getColumnName(), () -> "Column name"),
                new SimpleEntry<>(entity.getNewColumn(), () -> "New column name"),
                new SimpleEntry<>(catalog, () -> "Catalog"),
                new SimpleEntry<>(dbName, () -> "Database name"),
                new SimpleEntry<>(tableName, () -> "Table name"));
        validateColumnExistence(catalog, dbName, tableName, entity.getColumnName());
        return SchemaChange.renameColumn(entity.getColumnName(), entity.getNewColumn());
    }

    private static SchemaChange dropColumn(
            Catalog catalog, String dbName, String tableName, AlterTableEntity entity)
            throws Catalog.TableNotExistException, IOException {
        ParameterValidationUtil.checkNotNull(
                new SimpleEntry<>(entity.getColumnName(), () -> "Column name"),
                new SimpleEntry<>(catalog, () -> "Catalog"),
                new SimpleEntry<>(dbName, () -> "Database name"),
                new SimpleEntry<>(tableName, () -> "Table name"));
        validateColumnExistence(catalog, dbName, tableName, entity.getColumnName());
        return SchemaChange.dropColumn(entity.getColumnName());
    }

    private static SchemaChange updateColumnComment(
            Catalog catalog, String dbName, String tableName, AlterTableEntity entity)
            throws Catalog.TableNotExistException, IOException {
        ParameterValidationUtil.checkNotNull(
                new SimpleEntry<>(entity.getColumnName(), () -> "Column name"),
                new SimpleEntry<>(catalog, () -> "Catalog"),
                new SimpleEntry<>(dbName, () -> "Database name"),
                new SimpleEntry<>(tableName, () -> "Table name"));
        validateColumnExistence(catalog, dbName, tableName, entity.getColumnName());
        return SchemaChange.updateColumnComment(entity.getColumnName(), entity.getComment());
    }

    private static SchemaChange updateColumnType(
            Catalog catalog, String dbName, String tableName, AlterTableEntity entity)
            throws Catalog.TableNotExistException, IOException {
        ParameterValidationUtil.checkNotNull(
                new SimpleEntry<>(entity.getColumnName(), () -> "Column name"),
                new SimpleEntry<>(entity.getType(), () -> "Column type"),
                new SimpleEntry<>(catalog, () -> "Catalog"),
                new SimpleEntry<>(dbName, () -> "Database name"),
                new SimpleEntry<>(tableName, () -> "Table name"));
        validateColumnExistence(catalog, dbName, tableName, entity.getColumnName());
        return SchemaChange.updateColumnType(entity.getColumnName(), entity.getType());
    }

    private static SchemaChange updateColumnPosition(AlterTableEntity entity) {
        ParameterValidationUtil.checkNotNull(new SimpleEntry<>(entity.getMove(), () -> "Move"));
        return SchemaChange.updateColumnPosition(entity.getMove());
    }

    private static SchemaChange updateColumnNullability(
            Catalog catalog, String dbName, String tableName, AlterTableEntity entity)
            throws Catalog.TableNotExistException, IOException {
        ParameterValidationUtil.checkNotNull(
                new SimpleEntry<>(entity.getColumnName(), () -> "Column name"),
                new SimpleEntry<>(catalog, () -> "Catalog"),
                new SimpleEntry<>(dbName, () -> "Database name"),
                new SimpleEntry<>(tableName, () -> "Table name"));
        validateColumnExistence(catalog, dbName, tableName, entity.getColumnName());
        return SchemaChange.updateColumnNullability(entity.getColumnName(), entity.isNullable());
    }

    private static SchemaChange performAlterTableAction(
            Catalog catalog, String dbName, String tableName, AlterTableEntity entity)
            throws Catalog.TableNotExistException, IOException {
        OperatorKind kind = entity.getKind();

        switch (kind) {
            case ADD_COLUMN:
                return addColumn(entity);
            case RENAME_COLUMN:
                return renameColumn(catalog, dbName, tableName, entity);
            case DROP_COLUMN:
                return dropColumn(catalog, dbName, tableName, entity);
            case UPDATE_COLUMN_COMMENT:
                return updateColumnComment(catalog, dbName, tableName, entity);
            case UPDATE_COLUMN_TYPE:
                return updateColumnType(catalog, dbName, tableName, entity);
            case UPDATE_COLUMN_POSITION:
                return updateColumnPosition(entity);
            case UPDATE_COLUMN_NULLABILITY:
                return updateColumnNullability(catalog, dbName, tableName, entity);
            default:
                return null;
        }
    }

    public static void alterTable(
            Catalog catalog, String dbName, String tableName, List<AlterTableEntity> entities)
            throws Catalog.TableNotExistException, IOException, Catalog.ColumnAlreadyExistException,
                    Catalog.ColumnNotExistException {
        checkNotNull(catalog, dbName, tableName);

        Identifier identifier = Identifier.create(dbName, tableName);

        List<SchemaChange> schemaChanges = new ArrayList<>();

        for (AlterTableEntity entity : entities) {
            SchemaChange schemaChange = performAlterTableAction(catalog, dbName, tableName, entity);
            schemaChanges.add(schemaChange);
        }

        catalog.alterTable(identifier, schemaChanges, false);
    }

    @VisibleForTesting
    private static SchemaTableMetadata getLatestSchema(
            Catalog catalog, String dbName, String tableName)
            throws Catalog.TableNotExistException, IOException {
        List<SchemaTableMetadata> schemas = listSchemas(catalog, dbName, tableName);
        return schemas.stream()
                .max(Comparator.comparingLong(SchemaTableMetadata::getSchemaId))
                .orElse(null);
    }

    public static List<SnapshotTableMetadata> listSnapshots(
            Catalog catalog, CatalogEntity catalogEntity, String dbName, String tableName)
            throws Catalog.TableNotExistException, IOException {
        checkNotNull(catalog, dbName, tableName);

        List<SnapshotTableMetadata> snapshots = new ArrayList<>();

        Table table = getTable(catalog, dbName, "`" + tableName + "$" + SNAPSHOTS + "`");

        SnapshotManager snapshotManager =
                getSnapshotManager(catalog, catalogEntity, dbName, tableName);

        RecordReader<InternalRow> reader = getReader(table);
        reader.forEachRemaining(
                row -> {
                    SnapshotTableMetadata snapshotTableMetadata =
                            SnapshotTableMetadata.builder()
                                    .snapshotId(row.getLong(1))
                                    .schemaId(row.getLong(2))
                                    .commitUser(row.getString(3).toString())
                                    .commitIdentifier(row.getLong(4))
                                    .commitKind(row.getString(5).toString())
                                    .commitTime(row.getTimestamp(6, 3).toLocalDateTime())
                                    .totalRecordCount(row.getLong(7))
                                    .deltaRecordCount(row.getLong(8))
                                    .changelogRecordCount(row.getLong(9))
                                    .watermark(row.getLong(10))
                                    .snapshotPath(
                                            snapshotManager.snapshotPath(row.getLong(1)).toString())
                                    .build();
                    snapshots.add(snapshotTableMetadata);
                });

        return snapshots;
    }

    public static List<SchemaTableMetadata> listSchemas(
            Catalog catalog, String dbName, String tableName)
            throws Catalog.TableNotExistException, IOException {
        checkNotNull(catalog, dbName, tableName);

        List<SchemaTableMetadata> schemas = new ArrayList<>();

        Table table = getTable(catalog, dbName, "`" + tableName + "$" + SCHEMAS + "`");

        RecordReader<InternalRow> reader = getReader(table);
        reader.forEachRemaining(
                row -> {
                    SchemaTableMetadata schemaTableMetadata =
                            SchemaTableMetadata.builder()
                                    .schemaId(row.getLong(1))
                                    .fields(row.getString(2).toString())
                                    .partitionKeys(row.getString(3).toString())
                                    .primaryKeys(row.getString(4).toString())
                                    .options(row.getString(5).toString())
                                    .comment(row.getString(6).toString())
                                    .build();
                    schemas.add(schemaTableMetadata);
                });

        return schemas;
    }

    public static List<OptionTableMetadata> listOptions(
            Catalog catalog, String dbName, String tableName)
            throws Catalog.TableNotExistException, IOException {
        checkNotNull(catalog, dbName, tableName);

        List<OptionTableMetadata> options = new ArrayList<>();

        Table table = getTable(catalog, dbName, "`" + tableName + "$" + OPTIONS + "`");

        RecordReader<InternalRow> reader = getReader(table);
        reader.forEachRemaining(
                row -> {
                    OptionTableMetadata optionsTableMetadata =
                            new OptionTableMetadata(
                                    row.getString(1).toString(), row.getString(2).toString());
                    options.add(optionsTableMetadata);
                });

        return options;
    }

    public static List<ManifestTableMetadata> listManifests(
            Catalog catalog, String dbName, String tableName)
            throws Catalog.TableNotExistException, IOException {
        checkNotNull(catalog, dbName, tableName);

        List<ManifestTableMetadata> manifests = new ArrayList<>();

        Table table = getTable(catalog, dbName, "`" + tableName + "$" + MANIFESTS + "`");

        RecordReader<InternalRow> reader = getReader(table);
        reader.forEachRemaining(
                row -> {
                    ManifestTableMetadata manifestTableMetadata =
                            ManifestTableMetadata.builder()
                                    .fileName(row.getString(1).toString())
                                    .fileSize(row.getLong(2))
                                    .numAddedFiles(row.getLong(3))
                                    .numDeletedFiles(row.getLong(4))
                                    .schemaId(row.getLong(5))
                                    .build();
                    manifests.add(manifestTableMetadata);
                });

        return manifests;
    }

    public static List<FileTableMetadata> listFiles(
            Catalog catalog, String dbName, String tableName)
            throws Catalog.TableNotExistException, IOException {
        checkNotNull(catalog, dbName, tableName);

        List<FileTableMetadata> files = new ArrayList<>();

        Table table = getTable(catalog, dbName, "`" + tableName + "$" + FILES + "`");

        RecordReader<InternalRow> reader = getReader(table);
        reader.forEachRemaining(
                row -> {
                    FileTableMetadata fileTableMetadata =
                            FileTableMetadata.builder()
                                    .partition(row.getString(1).toString())
                                    .bucket(row.getInt(2))
                                    .filePath(row.getString(3).toString())
                                    .fileFormat(row.getString(4).toString())
                                    .schemaId(row.getLong(5))
                                    .level(row.getInt(6))
                                    .fileSizeInBytes(row.getLong(7))
                                    .minKey(row.getString(8).toString())
                                    .maxKey(row.getString(9).toString())
                                    .nullValueCounts(row.getString(10).toString())
                                    .minValueStats(row.getString(11).toString())
                                    .maxValueStats(row.getString(12).toString())
                                    .creationTime(row.getTimestamp(13, 6).toLocalDateTime())
                                    .build();
                    files.add(fileTableMetadata);
                });

        return files;
    }

    public static List<ConsumerTableMetadata> listConsumers(
            Catalog catalog, String dbName, String tableName)
            throws Catalog.TableNotExistException, IOException {
        checkNotNull(catalog, dbName, tableName);

        List<ConsumerTableMetadata> consumers = new ArrayList<>();

        Table table = getTable(catalog, dbName, "`" + tableName + "$" + CONSUMER + "`");

        RecordReader<InternalRow> reader = getReader(table);

        reader.forEachRemaining(
                row -> {
                    ConsumerTableMetadata consumerTableMetadata =
                            new ConsumerTableMetadata(row.getString(1).toString(), row.getLong(2));
                    consumers.add(consumerTableMetadata);
                });
        return consumers;
    }

    public static List<TagTableMetadata> listTags(Catalog catalog, String dbName, String tableName)
            throws Catalog.TableNotExistException, IOException {
        checkNotNull(catalog, dbName, tableName);

        List<TagTableMetadata> tags = new ArrayList<>();

        Table table = getTable(catalog, dbName, "`" + tableName + "$" + TAGS + "`");

        RecordReader<InternalRow> reader = getReader(table);
        reader.forEachRemaining(
                row -> {
                    TagTableMetadata tagTableMetadata =
                            TagTableMetadata.builder()
                                    .tagName(row.getString(1).toString())
                                    .snapshotId(row.getLong(2))
                                    .schemaId(row.getLong(3))
                                    .createTime(row.getTimestamp(4, 3).toLocalDateTime())
                                    .recordCount(row.getLong(5))
                                    .build();
                    tags.add(tagTableMetadata);
                });

        return tags;
    }

    @VisibleForTesting
    private static SnapshotManager getSnapshotManager(
            Catalog catalog, CatalogEntity catalogEntity, String dbName, String tableName)
            throws IOException {
        String warehouse = catalogEntity.getWarehouse();

        FileIO fileIO =
                FileIO.get(
                        new Path(warehouse),
                        CatalogContext.create(buildOptions(catalog, catalogEntity)));

        String tablePath = warehouse + "/" + dbName + ".db" + "/" + tableName;
        return new SnapshotManager(fileIO, new Path(tablePath));
    }

    private static Options buildOptions(Catalog catalog, CatalogEntity catalogEntity) {
        Options options = new Options();
        if (catalog instanceof FileSystemCatalog) {
            options.set(CatalogProperties.WAREHOUSE, catalogEntity.getWarehouse());
        } else if (catalog instanceof HiveCatalog) {
            options.set(CatalogProperties.WAREHOUSE, catalogEntity.getWarehouse());
            options.set(CatalogProperties.METASTORE, MetastoreType.HIVE.toString());
            options.set(CatalogProperties.URI, catalogEntity.getUri());
            options.set(CatalogProperties.HIVE_CONF_DIR, catalogEntity.getHiveConfDir());
        }
        return options;
    }

    @VisibleForTesting
    private static RecordReader<InternalRow> getReader(Table table) {
        ReadBuilder readBuilder = table.newReadBuilder();
        List<Split> splits = readBuilder.newScan().plan().splits();
        TableRead tableRead = readBuilder.newRead();
        try {
            return tableRead.createReader(splits);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @VisibleForTesting
    private static WriteBuilder getWriteBuilder(
            Table table, String writeMode, @Nullable Map<String, String> staticPartition) {
        if (writeMode.equals(WriteMode.BATCH.getValue())) {
            return table.newBatchWriteBuilder().withOverwrite(staticPartition);
        } else {
            return table.newStreamWriteBuilder();
        }
    }

    public static TableWrite getBatchTableWriter(
            Table table, @Nullable Map<String, String> staticPartition) {
        BatchWriteBuilder writeBuilder =
                (BatchWriteBuilder)
                        getWriteBuilder(table, WriteMode.BATCH.getValue(), staticPartition);
        return writeBuilder.newWrite();
    }

    public static TableWrite getStreamTableWriter(Table table) {
        StreamWriteBuilder writeBuilder =
                (StreamWriteBuilder) getWriteBuilder(table, WriteMode.STREAM.getValue(), null);
        return writeBuilder.newWrite();
    }

    public static void batchWrite(
            List<GenericRow> records,
            Catalog catalog,
            String dbName,
            String tableName,
            @Nullable Map<String, String> staticPartition)
            throws Exception {
        checkNotNull(catalog, dbName, tableName);

        BatchWriteBuilder writeBuilder =
                (BatchWriteBuilder)
                        getWriteBuilder(
                                getTable(catalog, dbName, tableName),
                                WriteMode.BATCH.getValue(),
                                staticPartition);

        List<CommitMessage> commitMessages;
        try (BatchTableWrite writer = writeBuilder.newWrite()) {

            for (GenericRow record : records) {
                writer.write(record);
            }

            commitMessages = writer.prepareCommit();
        }

        try (BatchTableCommit commit = writeBuilder.newCommit()) {
            commit.commit(commitMessages);
        }
    }

    private static void checkNotNull(Catalog catalog, String dbName, String tableName) {
        ParameterValidationUtil.checkNotNull(
                new SimpleEntry<>(catalog, () -> "Catalog"),
                new SimpleEntry<>(dbName, () -> "Database name"),
                new SimpleEntry<>(tableName, () -> "Table name"));
    }

    private static void validateColumnExistence(
            Catalog catalog, String dbName, String tableName, String columnName)
            throws Catalog.TableNotExistException, IOException {
        SchemaTableMetadata latestSchema = getLatestSchema(catalog, dbName, tableName);
        if (!latestSchema.getFields().contains(columnName)) {
            throw new RuntimeException("Column not found: " + columnName);
        }
    }

    private static Map<String, String> handleOptions(Map<String, String> options) {
        List<String> keys = TableOptionExtractor.keys();
        Map<String, String> filteredOptions = new HashMap<>();

        for (String key : options.keySet()) {
            if (keys.contains(key)) {
                filteredOptions.put(key, options.get(key));
            }
        }

        return filteredOptions;
    }
}
