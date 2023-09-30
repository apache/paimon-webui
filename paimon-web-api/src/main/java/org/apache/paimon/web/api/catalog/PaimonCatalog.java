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

import org.apache.paimon.catalog.Catalog;
import org.apache.paimon.catalog.Identifier;
import org.apache.paimon.schema.Schema;
import org.apache.paimon.schema.SchemaChange;
import org.apache.paimon.schema.SchemaManager;
import org.apache.paimon.table.Table;
import org.apache.paimon.types.DataType;
import org.apache.paimon.web.api.exception.ColumnException;
import org.apache.paimon.web.api.exception.DatabaseException;
import org.apache.paimon.web.api.exception.TableException;
import org.apache.paimon.web.api.table.ColumnMetadata;
import org.apache.paimon.web.api.table.TableChange;
import org.apache.paimon.web.api.table.TableMetadata;
import org.apache.paimon.web.common.utils.ParameterValidationUtil;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Paimon catalog. */
public class PaimonCatalog {
    private final Catalog catalog;

    private final String name;

    public PaimonCatalog(Catalog catalog, String name) {
        this.catalog = catalog;
        this.name = name;
    }

    public Catalog catalog() {
        return catalog;
    }

    public List<String> listDatabases() {
        return catalog.listDatabases();
    }

    public boolean databaseExists(String databaseName) {
        return catalog.databaseExists(databaseName);
    }

    public void createDatabase(String databaseName) {
        Preconditions.checkNotNull(databaseName, "Database name cannot be null.");

        try {
            catalog.createDatabase(databaseName, false);
        } catch (Catalog.DatabaseAlreadyExistException e) {
            throw new DatabaseException.DatabaseAlreadyExistsException(
                    String.format(
                            "The database '%s' already exists in the catalog.", databaseName));
        }
    }

    public void dropDatabase(String databaseName) {
        Preconditions.checkNotNull(databaseName, "Database name cannot be null.");
        try {
            catalog.dropDatabase(databaseName, false, true);
        } catch (Catalog.DatabaseNotExistException e) {
            throw new DatabaseException.DatabaseNotExistException(
                    String.format(
                            "The database '%s' does not exist in the catalog.", databaseName));
        } catch (Catalog.DatabaseNotEmptyException e) {
            throw new DatabaseException.DatabaseNotEmptyException(
                    String.format("The database '%s' is not empty.", databaseName));
        }
    }

    public List<String> listTables(String databaseName) {
        Preconditions.checkNotNull(databaseName, "Database name cannot be null.");
        try {
            return catalog.listTables(databaseName);
        } catch (Catalog.DatabaseNotExistException e) {
            throw new DatabaseException.DatabaseNotExistException(
                    String.format(
                            "The database '%s' does not exist in the catalog.", databaseName));
        }
    }

    public boolean tableExists(String databaseName, String tableName) {
        ParameterValidationUtil.checkNotNull(
                Pair.of(databaseName, "Database name"), Pair.of(tableName, "Table name"));

        Identifier identifier = Identifier.create(databaseName, tableName);
        return catalog.tableExists(identifier);
    }

    public void createTable(String databaseName, String tableName, TableMetadata tableMetadata) {
        ParameterValidationUtil.checkNotNull(
                Pair.of(databaseName, "Database name"), Pair.of(tableName, "Table name"));

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
                        .options(tableMetadata.options());

        for (ColumnMetadata column : tableMetadata.columns()) {
            schemaBuilder.column(column.name(), column.type(), column.description());
        }

        Schema schema = schemaBuilder.build();

        Identifier identifier = Identifier.create(databaseName, tableName);

        try {
            catalog.createTable(identifier, schema, false);
        } catch (Catalog.TableAlreadyExistException e) {
            throw new TableException.TableAlreadyExistException(
                    String.format("The table '%s' already exists in the database.", tableName));
        } catch (Catalog.DatabaseNotExistException e) {
            throw new DatabaseException.DatabaseNotExistException(
                    String.format(
                            "The database '%s' does not exist in the catalog.", databaseName));
        }
    }

    public Table getTable(String databaseName, String tableName) {
        ParameterValidationUtil.checkNotNull(
                Pair.of(databaseName, "Database name"), Pair.of(tableName, "Table name"));

        Identifier identifier = Identifier.create(databaseName, tableName);
        try {
            return catalog.getTable(identifier);
        } catch (Catalog.TableNotExistException e) {
            throw new TableException.TableNotExistException(
                    String.format("The table '%s' does not exist in the database.", tableName));
        }
    }

    public void dropTable(String databaseName, String tableName) {
        ParameterValidationUtil.checkNotNull(
                Pair.of(databaseName, "Database name"), Pair.of(tableName, "Table name"));

        Identifier identifier = Identifier.create(databaseName, tableName);
        try {
            catalog.dropTable(identifier, false);
        } catch (Catalog.TableNotExistException e) {
            throw new TableException.TableNotExistException(
                    String.format("The table '%s' does not exist in the database.", tableName));
        }
    }

    public void renameTable(String databaseName, String fromTable, String toTable) {
        ParameterValidationUtil.checkNotNull(
                Pair.of(databaseName, "Database name"),
                Pair.of(fromTable, "From table"),
                Pair.of(fromTable, "To table"));

        Identifier fromTableIdentifier = Identifier.create(databaseName, fromTable);
        Identifier toTableIdentifier = Identifier.create(databaseName, toTable);
        try {
            catalog.renameTable(fromTableIdentifier, toTableIdentifier, false);
        } catch (Catalog.TableNotExistException e) {
            throw new TableException.TableNotExistException(
                    String.format("The table '%s' does not exist in the database.", fromTable));
        } catch (Catalog.TableAlreadyExistException e) {
            throw new TableException.TableAlreadyExistException(
                    String.format("The table '%s' already exists in the database.", toTable));
        }
    }

    public void alterTable(String databaseName, String tableName, List<TableChange> tableChanges) {
        if (!tableExists(databaseName, tableName)) {
            return;
        }

        Identifier identifier = Identifier.create(databaseName, tableName);

        List<SchemaChange> changes = new ArrayList<>();
        if (null != tableChanges) {
            List<SchemaChange> schemaChanges =
                    tableChanges.stream()
                            .flatMap(tableChange -> toSchemaChange(tableChange).stream())
                            .collect(Collectors.toList());
            changes.addAll(schemaChanges);
        }

        try {
            catalog.alterTable(identifier, changes, false);
        } catch (Catalog.ColumnAlreadyExistException e) {
            throw new ColumnException.ColumnAlreadyExistException(e.getMessage());
        } catch (Catalog.TableNotExistException e) {
            throw new TableException.TableNotExistException(
                    String.format("The table '%s' does not exist in the database.", tableName));
        } catch (Catalog.ColumnNotExistException e) {
            throw new ColumnException.ColumnNotExistException(e.getMessage());
        }
    }

    private List<SchemaChange> toSchemaChange(TableChange change) {
        List<SchemaChange> schemaChanges = new ArrayList<>();
        if (change instanceof TableChange.AddColumn) {
            TableChange.AddColumn add = (TableChange.AddColumn) change;
            String comment = add.getColumn().description();
            SchemaChange.Move move = getMove(add.getPosition(), add.getColumn().name());
            schemaChanges.add(
                    SchemaChange.addColumn(
                            add.getColumn().name(), add.getColumn().type(), comment, move));
            return schemaChanges;
        } else if (change instanceof TableChange.DropColumn) {
            TableChange.DropColumn drop = (TableChange.DropColumn) change;
            schemaChanges.add(SchemaChange.dropColumn(drop.getColumnName()));
            return schemaChanges;
        } else if (change instanceof TableChange.ModifyColumnName) {
            TableChange.ModifyColumnName modify = (TableChange.ModifyColumnName) change;
            schemaChanges.add(
                    SchemaChange.renameColumn(
                            modify.getOldColumnName(), modify.getNewColumnName()));
            return schemaChanges;
        } else if (change instanceof TableChange.ModifyColumnType) {
            TableChange.ModifyColumnType modify = (TableChange.ModifyColumnType) change;
            DataType newColumnType = modify.getNewType();
            DataType oldColumnType = modify.getOldColumn().type();
            if (newColumnType.isNullable() != oldColumnType.isNullable()) {
                schemaChanges.add(
                        SchemaChange.updateColumnNullability(
                                modify.getNewColumn().name(), newColumnType.isNullable()));
            }
            schemaChanges.add(
                    SchemaChange.updateColumnType(modify.getOldColumn().name(), newColumnType));
            return schemaChanges;
        } else if (change instanceof TableChange.ModifyColumnPosition) {
            TableChange.ModifyColumnPosition modify = (TableChange.ModifyColumnPosition) change;
            SchemaChange.Move move = getMove(modify.getNewPosition(), modify.getNewColumn().name());
            schemaChanges.add(SchemaChange.updateColumnPosition(move));
            return schemaChanges;
        } else if (change instanceof TableChange.ModifyColumnComment) {
            TableChange.ModifyColumnComment modify = (TableChange.ModifyColumnComment) change;
            schemaChanges.add(
                    SchemaChange.updateColumnComment(
                            modify.getNewColumn().name(), modify.getNewComment()));
            return schemaChanges;
        } else if (change instanceof TableChange.SetOption) {
            TableChange.SetOption setOption = (TableChange.SetOption) change;
            String key = setOption.getKey();
            String value = setOption.getValue();

            SchemaManager.checkAlterTablePath(key);

            schemaChanges.add(SchemaChange.setOption(key, value));
            return schemaChanges;
        } else if (change instanceof TableChange.RemoveOption) {
            TableChange.RemoveOption removeOption = (TableChange.RemoveOption) change;
            schemaChanges.add(SchemaChange.removeOption(removeOption.getKey()));
            return schemaChanges;
        } else {
            throw new UnsupportedOperationException(
                    "Change is not supported: " + change.getClass());
        }
    }

    private SchemaChange.Move getMove(TableChange.ColumnPosition columnPosition, String fieldName) {
        SchemaChange.Move move = null;
        if (columnPosition instanceof TableChange.First) {
            move = SchemaChange.Move.first(fieldName);
        } else if (columnPosition instanceof TableChange.After) {
            move =
                    SchemaChange.Move.after(
                            fieldName, ((TableChange.After) columnPosition).column());
        }
        return move;
    }
}
