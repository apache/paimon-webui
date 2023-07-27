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
import org.apache.paimon.table.Table;
import org.apache.paimon.types.DataTypes;
import org.apache.paimon.web.api.catalog.CatalogCreator;
import org.apache.paimon.web.api.database.DatabaseManager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/** The test class of table manager in {@link TableManager}. */
public class TableManagerTest {

    @TempDir java.nio.file.Path tempFile;
    Catalog catalog;
    private static final String DB_NAME = "test_db";
    private static final String TABLE_NAME = "test_table";
    private static final String DEFAULT_TABLE = "default_table";

    @BeforeEach
    public void setUp() throws Exception {
        String warehouse = tempFile.toUri().toString();
        catalog = CatalogCreator.createFilesystemCatalog(warehouse);
        DatabaseManager.createDatabase(catalog, DB_NAME);
        TableManager.createTable(
                catalog,
                DB_NAME,
                DEFAULT_TABLE,
                new TableMetadata(
                        Lists.newArrayList(new ColumnMetadata("id", DataTypes.STRING(), "")),
                        ImmutableList.of(),
                        ImmutableList.of(),
                        ImmutableMap.of(),
                        ""));
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (catalog != null) {
            catalog.close();
        }
    }

    @Test
    public void testTableExists() {
        boolean exists = TableManager.tableExists(catalog, DB_NAME, TABLE_NAME);
        assertThat(exists).isFalse();
        assertThat(TableManager.tableExists(catalog, DB_NAME, DEFAULT_TABLE)).isTrue();
    }

    @Test
    public void testCreateTable()
            throws Catalog.TableAlreadyExistException, Catalog.DatabaseNotExistException {
        ArrayList<ColumnMetadata> columns =
                Lists.newArrayList(
                        new ColumnMetadata("id", DataTypes.BIGINT()),
                        new ColumnMetadata("name", DataTypes.STRING()),
                        new ColumnMetadata("age", DataTypes.INT()),
                        new ColumnMetadata("birth", DataTypes.DATE()));

        ArrayList<String> primaryKeys = Lists.newArrayList("id", "birth");

        ArrayList<String> partitionKeys = Lists.newArrayList("birth");

        Map<String, String> options =
                ImmutableMap.of(
                        "bucket", "4",
                        "changelog-producer", "input");

        TableMetadata tableMetadata =
                TableMetadata.builder()
                        .columns(columns)
                        .partitionKeys(partitionKeys)
                        .primaryKeys(primaryKeys)
                        .options(options)
                        .build();

        TableManager.createTable(catalog, DB_NAME, TABLE_NAME, tableMetadata);
        boolean exists = TableManager.tableExists(catalog, DB_NAME, TABLE_NAME);
        assertThat(exists).isTrue();

        // Create table throws Exception when table is system table
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(
                        () ->
                                TableManager.createTable(
                                        catalog, DB_NAME, "$system_table", tableMetadata))
                .withMessage(
                        "Cannot 'createTable' for system table 'Identifier{database='test_db', table='$system_table'}', please use data table.");

        // Create table throws DatabaseNotExistException when database does not exist
        assertThatExceptionOfType(Catalog.DatabaseNotExistException.class)
                .isThrownBy(
                        () -> TableManager.createTable(catalog, "db_01", TABLE_NAME, tableMetadata))
                .withMessage("Database db_01 does not exist.");

        // Create table throws TableAlreadyExistException when table already exists and
        // ignoreIfExists is false
        assertThatExceptionOfType(Catalog.TableAlreadyExistException.class)
                .isThrownBy(
                        () -> TableManager.createTable(catalog, DB_NAME, TABLE_NAME, tableMetadata))
                .withMessage("Table test_db.test_table already exists.");
    }

    @Test
    public void testGetTable() throws Catalog.TableNotExistException {
        Table table = TableManager.getTable(catalog, DB_NAME, DEFAULT_TABLE);
        assertThat(table).isInstanceOf(Table.class);

        // Get table throws TableNotExistException when table does not exist
        assertThatExceptionOfType(Catalog.TableNotExistException.class)
                .isThrownBy(() -> TableManager.getTable(catalog, DB_NAME, TABLE_NAME))
                .withMessage("Table test_db.test_table does not exist.");
    }

    @Test
    public void testListTables() throws Catalog.DatabaseNotExistException {
        List<String> tables = TableManager.listTables(catalog, DB_NAME);
        assertThat(tables).contains("default_table");

        // List tables throws DatabaseNotExistException when database does not exist
        assertThatExceptionOfType(Catalog.DatabaseNotExistException.class)
                .isThrownBy(() -> TableManager.listTables(catalog, "db_01"))
                .withMessage("Database db_01 does not exist.");
    }

    @Test
    public void testDropTable() throws Exception {
        TableManager.createTable(
                catalog,
                DB_NAME,
                "tb_01",
                new TableMetadata(
                        Lists.newArrayList(new ColumnMetadata("id", DataTypes.STRING(), "")),
                        ImmutableList.of(),
                        ImmutableList.of(),
                        ImmutableMap.of(),
                        ""));
        assertThat(TableManager.tableExists(catalog, DB_NAME, "tb_01")).isTrue();

        TableManager.dropTable(catalog, DB_NAME, "tb_01");
        assertThat(TableManager.tableExists(catalog, DB_NAME, "tb_01")).isFalse();

        // Drop table throws TableNotExistException when table does not exist
        assertThatExceptionOfType(Catalog.TableNotExistException.class)
                .isThrownBy(() -> TableManager.dropTable(catalog, DB_NAME, "tb_02"))
                .withMessage("Table test_db.tb_02 does not exist.");
    }

    @Test
    public void testRenameTable() throws Exception {
        TableManager.createTable(
                catalog,
                DB_NAME,
                "tb_01",
                new TableMetadata(
                        Lists.newArrayList(new ColumnMetadata("id", DataTypes.STRING(), "")),
                        ImmutableList.of(),
                        ImmutableList.of(),
                        ImmutableMap.of(),
                        ""));

        TableManager.renameTable(catalog, DB_NAME, "tb_01", "tb_02");
        assertThat(TableManager.tableExists(catalog, DB_NAME, "tb_01")).isFalse();
        assertThat(TableManager.tableExists(catalog, DB_NAME, "tb_02")).isTrue();

        // Rename table throws TableNotExistException when fromTable does not exist
        assertThatExceptionOfType(Catalog.TableNotExistException.class)
                .isThrownBy(() -> TableManager.renameTable(catalog, DB_NAME, "tb_01", "table_04"))
                .withMessage("Table test_db.tb_01 does not exist.");

        // Rename table throws TableAlreadyExistException when toTable already exists
        TableManager.createTable(
                catalog,
                DB_NAME,
                "tb_03",
                new TableMetadata(
                        Lists.newArrayList(new ColumnMetadata("id", DataTypes.STRING(), "")),
                        ImmutableList.of(),
                        ImmutableList.of(),
                        ImmutableMap.of(),
                        ""));

        assertThatExceptionOfType(Catalog.TableAlreadyExistException.class)
                .isThrownBy(
                        () -> TableManager.renameTable(catalog, DB_NAME, "tb_03", "default_table"))
                .withMessage("Table test_db.default_table already exists.");
    }
}
