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

import org.apache.paimon.web.server.data.dto.DatabaseDTO;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.vo.DatabaseVO;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/** Database Service. */
public interface DatabaseService extends IService<DatabaseVO> {

    /**
     * Verify if the database name is unique.
     *
     * @param databaseVO database info
     * @return result
     */
    boolean checkCatalogNameUnique(DatabaseVO databaseVO);

    /**
     * Creates a new database based on the provided DatabaseInfo.
     *
     * @param databaseDTO The DatabaseInfo object containing the details of the new database.
     * @return void indicating the result of the operation.
     */
    R<Void> createDatabase(DatabaseDTO databaseDTO);

    /**
     * Get all database information.
     *
     * @return The list of all databases.
     */
    R<List<DatabaseVO>> getAllDatabases();

    /**
     * Get database information by catalog id.
     *
     * @return The list of databases.
     */
    R<List<DatabaseVO>> getDatabaseById(Integer catalogId);

    /**
     * Remove a database by its name.
     *
     * @param databaseDTO The drop database DTO.
     * @return A response indicating the success or failure of the removal operation.
     * @throws RuntimeException if the database is not found, or it is not empty.
     */
    R<Void> dropDatabase(DatabaseDTO databaseDTO);
}
