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

package org.apache.paimon.web.server.controller;

import org.apache.paimon.web.server.data.dto.DatabaseDTO;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.vo.DatabaseVO;
import org.apache.paimon.web.server.service.DatabaseService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Database api controller. */
@Slf4j
@RestController
@RequestMapping("/api/database")
public class DatabaseController {

    private final DatabaseService databaseService;

    public DatabaseController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * Creates a new database based on the provided DatabaseInfo.
     *
     * @param databaseDTO The DatabaseInfo object containing the details of the new database.
     * @return R<Void/> indicating the result of the operation.
     */
    @PostMapping("/create")
    public R<Void> createDatabase(@RequestBody DatabaseDTO databaseDTO) {
        return databaseService.createDatabase(databaseDTO);
    }

    /**
     * Lists databases given catalog id.
     *
     * @return The list of databases of given catalog id.
     */
    @GetMapping("/list")
    public R<List<DatabaseVO>> listDatabases(
            @RequestParam(value = "catalogId", required = false) Integer catalogId) {
        return databaseService.listDatabases(catalogId);
    }

    /**
     * Removes a database by its name.
     *
     * @param databaseDTO The drop database DTO.
     * @return A response indicating the success or failure of the removal operation.
     * @throws RuntimeException if the database is not found, or it is not empty.
     */
    @PostMapping("/drop")
    public R<Void> dropDatabase(@RequestBody DatabaseDTO databaseDTO) {
        return databaseService.dropDatabase(databaseDTO);
    }
}
