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

package org.apache.paimon.web.server.service;

import org.apache.paimon.web.server.data.dto.DatabaseDTO;
import org.apache.paimon.web.server.data.vo.DatabaseVO;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/** Database Service. */
public interface DatabaseService extends IService<DatabaseVO> {

    /**
     * Checks if the specified database exists.
     *
     * @param databaseDTO The database to check
     * @return true if the database exists, false otherwise
     */
    boolean databaseExists(DatabaseDTO databaseDTO);

    /**
     * Creates a new database given {@link DatabaseDTO}.
     *
     * @param databaseDTO The {@link DatabaseDTO} object that contains the detail of the created
     *     database
     * @return true if the operation is successful, false otherwise
     */
    boolean createDatabase(DatabaseDTO databaseDTO);

    /**
     * Lists databases given catalog id.
     *
     * @return The list of databases of given catalog
     */
    List<DatabaseVO> listDatabases(Integer catalogId);

    /**
     * Drops database given database name.
     *
     * @param databaseDTO The dropping database
     * @return true if the operation is successful, false otherwise
     */
    boolean dropDatabase(DatabaseDTO databaseDTO);
}
