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

package org.apache.paimon.web.api.catalog;

import org.apache.paimon.table.Table;
import org.apache.paimon.types.DataType;
import org.apache.paimon.types.DataTypes;
import org.apache.paimon.web.api.exception.DatabaseException;
import org.apache.paimon.web.api.exception.TableException;
import org.apache.paimon.web.api.table.ColumnMetadata;
import org.apache.paimon.web.api.table.TableChange;
import org.apache.paimon.web.api.table.TableMetadata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/** The test class of catalog creator in {@link PaimonCatalog}. */
public class PaimonCatalogTest {

    private String warehouse;

    private PaimonCatalog catalog;

    @TempDir java.nio.file.Path tempFile;

    private final String db = "test_default_db";

    @BeforeEach
    public void before() {
        warehouse = tempFile.toUri().toString();
        catalog = PaimonCatalogFactory.createFileSystemCatalog("paimon", warehouse);
        catalog.createDatabase(db);
    }

    @Test
    public void testDatabaseExists() {
        boolean exists = catalog.databaseExists(db);
        assertThat(exists).isTrue();
    }

    @Test
    public void testListDatabase() {
        catalog.createDatabase("db1");
        List<String> databases = catalog.listDatabases();
        assertThat(databases).contains("test_default_db", "db1");
    }

    @Test
    public void testCreateDatabase() {
        catalog.createDatabase("test_db");
        boolean exists = catalog.databaseExists("test_db");
        assertThat(exists).isTrue();

        assertThatExceptionOfType(DatabaseException.DatabaseAlreadyExistsException.class)
                .isThrownBy(() -> catalog.createDatabase("test_db"))
                .withMessage("The database 'test_db' already exists in the catalog.");
    }

    @Test
    public void testDropDatabase() {
        catalog.createDatabase("db2");
        boolean exists = catalog.databaseExists("db2");
        assertThat(exists).isTrue();
        catalog.dropDatabase("db2");
        boolean notExist = catalog.databaseExists("db2");
        assertThat(notExist).isFalse();
    }

    @Test
    public void testTableExists() {
        createTable(db, "tb1");
        boolean exists = catalog.tableExists(db, "tb1");
        boolean notExists = catalog.tableExists(db, "tb_not");
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    public void testListTables() {
        createTable(db, "tb1");
        createTable(db, "tb2");
        List<String> tables = catalog.listTables(db);
        assertThat(tables).contains("tb1", "tb2");
    }

    @Test
    public void testCreateTable() {
        createTable(db, "tb1");
        boolean exists = catalog.tableExists(db, "tb1");
        assertThat(exists).isTrue();

        assertThatExceptionOfType(TableException.TableAlreadyExistException.class)
                .isThrownBy(() -> createTable(db, "tb1"))
                .withMessage("The table 'tb1' already exists in the database.");

        assertThatExceptionOfType(DatabaseException.DatabaseNotExistException.class)
                .isThrownBy(() -> createTable("db1", "tb1"))
                .withMessage("The database 'db1' does not exist in the catalog.");
    }

    @Test
    public void testGetTable() {
        createTable(db, "tb1");
        Table tb1 = catalog.getTable(db, "tb1");
        assertThat(tb1).isInstanceOf(Table.class);
        assertThat(tb1.name()).isEqualTo("tb1");

        assertThatExceptionOfType(TableException.TableNotExistException.class)
                .isThrownBy(() -> catalog.getTable(db, "tb2"))
                .withMessage("The table 'tb2' does not exist in the database.");
    }

    @Test
    public void testRenameTable() {
        createTable(db, "tb1");
        createTable(db, "tb3");
        createTable(db, "tb4");
        createTable(db, "tb6");
        assertThat(catalog.tableExists(db, "tb1")).isTrue();
        catalog.renameTable(db, "tb1", "tb2");
        assertThat(catalog.tableExists(db, "tb1")).isFalse();
        assertThat(catalog.tableExists(db, "tb2")).isTrue();

        assertThatExceptionOfType(TableException.TableAlreadyExistException.class)
                .isThrownBy(() -> catalog.renameTable(db, "tb3", "tb4"))
                .withMessage("The table 'tb4' already exists in the database.");

        assertThatExceptionOfType(TableException.TableNotExistException.class)
                .isThrownBy(() -> catalog.renameTable(db, "tb5", "tb7"))
                .withMessage("The table 'tb5' does not exist in the database.");
    }

    @Test
    public void testDropTable() {
        createTable(db, "tb1");
        assertThat(catalog.tableExists(db, "tb1")).isTrue();
        catalog.dropTable(db, "tb1");
        assertThat(catalog.tableExists(db, "tb1")).isFalse();

        assertThatExceptionOfType(TableException.TableNotExistException.class)
                .isThrownBy(() -> catalog.dropTable(db, "tb5"))
                .withMessage("The table 'tb5' does not exist in the database.");
    }

    @Test
    public void testAddColumn() {
        createTable(db, "tb1");
        ColumnMetadata age = new ColumnMetadata("age", DataTypes.INT());
        TableChange.ColumnPosition columnPosition = TableChange.ColumnPosition.after("id");
        TableChange.AddColumn add = TableChange.add(age, columnPosition);
        List<TableChange> tableChanges = new ArrayList<>();
        tableChanges.add(add);
        catalog.alterTable(db, "tb1", tableChanges);
        Table tb1 = catalog.getTable(db, "tb1");
        List<String> fieldNames = tb1.rowType().getFieldNames();
        assertThat(fieldNames).contains("id", "age", "name");
    }

    @Test
    public void testModifyColumnType() {
        createTable(db, "tb1");
        ColumnMetadata id = new ColumnMetadata("id", DataTypes.INT());
        TableChange.ModifyColumnType modifyColumnType =
                TableChange.modifyColumnType(id, DataTypes.BIGINT());
        List<TableChange> tableChanges = new ArrayList<>();
        tableChanges.add(modifyColumnType);
        catalog.alterTable(db, "tb1", tableChanges);
        Table tb1 = catalog.getTable(db, "tb1");
        List<DataType> fieldTypes = tb1.rowType().getFieldTypes();
        assertThat(fieldTypes).contains(DataTypes.BIGINT(), DataTypes.STRING());
    }

    @Test
    public void testModifyColumnName() {
        createTable(db, "tb1");
        ColumnMetadata id = new ColumnMetadata("id", DataTypes.INT());
        TableChange.ModifyColumnName modifyColumnName = TableChange.modifyColumnName(id, "age");
        List<TableChange> tableChanges = new ArrayList<>();
        tableChanges.add(modifyColumnName);
        catalog.alterTable(db, "tb1", tableChanges);
        Table tb1 = catalog.getTable(db, "tb1");
        List<String> fieldNames = tb1.rowType().getFieldNames();
        assertThat(fieldNames).contains("age", "name");
    }

    @Test
    public void testModifyColumnComment() {
        createTable(db, "tb1");
        ColumnMetadata id = new ColumnMetadata("id", DataTypes.INT());
        TableChange.ModifyColumnComment modifyColumnComment =
                TableChange.modifyColumnComment(id, "id");
        List<TableChange> tableChanges = new ArrayList<>();
        tableChanges.add(modifyColumnComment);
        catalog.alterTable(db, "tb1", tableChanges);
        Table tb1 = catalog.getTable(db, "tb1");
        String description = tb1.rowType().getFields().get(0).description();
        assertThat(description).isEqualTo("id");
    }

    @Test
    public void testModifyColumnPosition() {
        createTable(db, "tb1");
        ColumnMetadata name = new ColumnMetadata("name", DataTypes.STRING());
        TableChange.ColumnPosition columnPosition = TableChange.ColumnPosition.first();
        TableChange.ModifyColumnPosition modifyColumnPosition =
                TableChange.modifyColumnPosition(name, columnPosition);
        List<TableChange> tableChanges = new ArrayList<>();
        tableChanges.add(modifyColumnPosition);
        catalog.alterTable(db, "tb1", tableChanges);
        Table tb1 = catalog.getTable(db, "tb1");
        String columnName = tb1.rowType().getFields().get(0).name();
        assertThat(columnName).isEqualTo("name");
    }

    @Test
    public void testDropColumn() {
        createTable(db, "tb1");
        TableChange.DropColumn dropColumn = TableChange.dropColumn("id");
        List<TableChange> tableChanges = new ArrayList<>();
        tableChanges.add(dropColumn);
        catalog.alterTable(db, "tb1", tableChanges);
        Table tb1 = catalog.getTable(db, "tb1");
        List<String> fieldNames = tb1.rowType().getFieldNames();
        assertThat(fieldNames).contains("name");
        assertThat(fieldNames).doesNotContain("id");
    }

    @Test
    public void testSetOption() {
        createTable(db, "tb1");
        TableChange.SetOption setOption = TableChange.set("bucket", "2");
        List<TableChange> tableChanges = new ArrayList<>();
        tableChanges.add(setOption);
        catalog.alterTable(db, "tb1", tableChanges);
        Table tb1 = catalog.getTable(db, "tb1");
        String bucket = tb1.options().get("bucket");
        assertThat(bucket).isEqualTo("2");
    }

    @Test
    public void testRemoveOption() {
        createTable(db, "tb1");
        TableChange.SetOption setOption = TableChange.set("bucket", "2");
        List<TableChange> tableChanges = new ArrayList<>();
        tableChanges.add(setOption);
        catalog.alterTable(db, "tb1", tableChanges);
        Table tb1 = catalog.getTable(db, "tb1");
        String bucket = tb1.options().get("bucket");
        assertThat(bucket).isEqualTo("2");

        TableChange.RemoveOption resetOption = TableChange.remove("bucket");
        List<TableChange> changes = new ArrayList<>();
        changes.add(resetOption);
        catalog.alterTable(db, "tb1", changes);
        Table tb = catalog.getTable(db, "tb1");
        assertThat(tb.options().get("bucket")).isEqualTo(null);
    }

    private void createTable(String databaseName, String tableName) {
        List<ColumnMetadata> columns = new ArrayList<>();
        ColumnMetadata id = new ColumnMetadata("id", DataTypes.INT());
        ColumnMetadata name = new ColumnMetadata("name", DataTypes.STRING());
        columns.add(id);
        columns.add(name);
        TableMetadata tableMetadata = TableMetadata.builder().columns(columns).build();
        catalog.createTable(databaseName, tableName, tableMetadata);
    }
}
