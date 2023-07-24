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
import org.apache.paimon.table.Table;
import org.apache.paimon.table.sink.*;
import org.apache.paimon.table.source.ReadBuilder;
import org.apache.paimon.table.source.Split;
import org.apache.paimon.table.source.TableRead;
import org.apache.paimon.utils.SnapshotManager;
import org.apache.paimon.web.api.common.CatalogEntity;
import org.apache.paimon.web.api.common.CatalogProperties;
import org.apache.paimon.web.api.common.MetastoreType;
import org.apache.paimon.web.api.common.WriteMode;
import org.apache.paimon.web.common.annotation.VisibleForTesting;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
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
        Schema.Builder schemaBuilder =
                Schema.newBuilder()
                        .partitionKeys(
                                tableMetadata.primaryKeys() == null
                                        ? ImmutableList.of()
                                        : ImmutableList.copyOf(tableMetadata.primaryKeys()))
                        .partitionKeys(
                                tableMetadata.partitionKeys() == null
                                        ? ImmutableList.of()
                                        : ImmutableList.copyOf(tableMetadata.partitionKeys()))
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
        Identifier identifier = Identifier.create(dbName, tableName);
        return catalog.tableExists(identifier);
    }

    public static Table GetTable(Catalog catalog, String dbName, String tableName)
            throws Catalog.TableNotExistException {
        Identifier identifier = Identifier.create(dbName, tableName);
        return catalog.getTable(identifier);
    }

    public static List<String> listTables(Catalog catalog, String dbName)
            throws Catalog.DatabaseNotExistException {
        return catalog.listTables(dbName);
    }

    public static void dropTable(Catalog catalog, String dbName, String tableName)
            throws Catalog.TableNotExistException {
        Identifier identifier = Identifier.create(dbName, tableName);
        catalog.dropTable(identifier, false);
    }

    public static void renameTable(Catalog catalog, String dbName, String fromTable, String toTable)
            throws Catalog.TableAlreadyExistException, Catalog.TableNotExistException {
        Identifier fromTableIdentifier = Identifier.create(dbName, fromTable);
        Identifier toTableIdentifier = Identifier.create(dbName, toTable);
        catalog.renameTable(fromTableIdentifier, toTableIdentifier, false);
    }

    public static List<SnapshotTableMetadata> listSnapshots(
            Catalog catalog, CatalogEntity catalogEntity, String dbName, String tableName)
            throws Catalog.TableNotExistException, IOException {
        List<SnapshotTableMetadata> snapshots = new ArrayList<>();

        Table table = GetTable(catalog, dbName, "`" + tableName + "$" + SNAPSHOTS + "`");

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
        List<SchemaTableMetadata> schemas = new ArrayList<>();

        Table table = GetTable(catalog, dbName, "`" + tableName + "$" + SCHEMAS + "`");

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
        List<OptionTableMetadata> options = new ArrayList<>();

        Table table = GetTable(catalog, dbName, "`" + tableName + "$" + OPTIONS + "`");

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
        List<ManifestTableMetadata> manifests = new ArrayList<>();

        Table table = GetTable(catalog, dbName, "`" + tableName + "$" + MANIFESTS + "`");

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
        List<FileTableMetadata> files = new ArrayList<>();

        Table table = GetTable(catalog, dbName, "`" + tableName + "$" + FILES + "`");

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
        List<ConsumerTableMetadata> consumers = new ArrayList<>();

        Table table = GetTable(catalog, dbName, "`" + tableName + "$" + CONSUMER + "`");

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
        List<TagTableMetadata> tags = new ArrayList<>();

        Table table = GetTable(catalog, dbName, "`" + tableName + "$" + TAGS + "`");

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
        BatchWriteBuilder writeBuilder =
                (BatchWriteBuilder)
                        getWriteBuilder(
                                GetTable(catalog, dbName, tableName),
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
