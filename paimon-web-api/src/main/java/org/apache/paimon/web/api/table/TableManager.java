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
import org.apache.paimon.catalog.Identifier;
import org.apache.paimon.schema.Schema;
import org.apache.paimon.table.Table;

import com.google.common.collect.ImmutableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** paimon table manager. */
public class TableManager {

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

    public static Table getTable(Catalog catalog, String dbName, String tableName)
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
