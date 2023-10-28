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

package org.apache.paimon.web.server.service;

import org.apache.paimon.web.server.data.dto.TableDTO;
import org.apache.paimon.web.server.data.model.AlterTableRequest;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.data.vo.TableVO;

import java.util.List;

/** Table service. */
public interface TableService {

    /**
     * Creates a table in the database based on the provided TableInfo.
     *
     * @param tableDTO The TableDTO object containing information about the table.
     * @return R<Void/> indicating the success or failure of the operation.
     */
    R<Void> createTable(TableDTO tableDTO);

    /**
     * Adds a column to the table.
     *
     * @param tableDTO The information of the table, including the catalog name, database name,
     *     table name, and table columns.
     * @return A response indicating the success or failure of the operation.
     */
    R<Void> addColumn(TableDTO tableDTO);

    /**
     * Drops a column from a table.
     *
     * @param catalogName The name of the catalog.
     * @param databaseName The name of the database.
     * @param tableName The name of the table.
     * @param columnName The name of the column to be dropped.
     * @return The result indicating the success or failure of the operation.
     */
    R<Void> dropColumn(
            String catalogName, String databaseName, String tableName, String columnName);

    /**
     * Modify a column in a table.
     *
     * @param catalogName The name of the catalog.
     * @param databaseName The name of the database.
     * @param tableName The name of the table.
     * @param alterTableRequest The param of the alter table request.
     * @return A response indicating the success or failure of the operation.
     */
    R<Void> alterTable(
            String catalogName,
            String databaseName,
            String tableName,
            AlterTableRequest alterTableRequest);

    /**
     * Adds options to a table.
     *
     * @param tableDTO An object containing table information.
     * @return If the options are successfully added, returns a successful result object. If an
     *     exception occurs, returns a result object with an error status.
     */
    R<Void> addOption(TableDTO tableDTO);

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
    R<Void> removeOption(String catalogName, String databaseName, String tableName, String key);

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
    R<Void> dropTable(String catalogName, String databaseName, String tableName);

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
    R<Void> renameTable(
            String catalogName, String databaseName, String fromTableName, String toTableName);

    /**
     * Handler method for the "/getAllTables" endpoint. Retrieves information about all tables and
     * returns a response containing the table details.
     *
     * @return Response object containing a list of {@link TableDTO} representing the tables.
     */
    R<List<TableDTO>> getAllTables();

    /**
     * Query tables by catalog id and database name.
     *
     * @return Response object containing a list of {@link TableVO} representing the tables.
     */
    R<List<TableVO>> getTablesByCondition(Integer catalogId, String databaseName);

    /**
     * Query all tables by name fuzzy.
     *
     * @return Response object containing a list of {@link TableVO} representing the tables.
     */
    R<List<TableVO>> queryTablesByCondition(String name);
}
