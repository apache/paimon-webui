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

package org.apache.paimon.web.server.controller;

import org.apache.paimon.web.api.catalog.PaimonService;
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.model.DatabaseInfo;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.service.CatalogService;
import org.apache.paimon.web.server.util.PaimonServiceUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/** Database api controller. */
@Slf4j
@RestController
@RequestMapping("/api/database")
public class DatabaseController {

    @Autowired private CatalogService catalogService;

    /**
     * Creates a new database based on the provided DatabaseInfo.
     *
     * @param databaseInfo The DatabaseInfo object containing the details of the new database.
     * @return R<Void/> indicating the result of the operation.
     */
    @PostMapping("/create")
    public R<Void> createDatabase(@RequestBody DatabaseInfo databaseInfo) {
        try {
            CatalogInfo catalogInfo = getCatalogInfo(databaseInfo.getCatalogId());
            PaimonService service = PaimonServiceUtils.getPaimonService(catalogInfo);
            if (service.databaseExists(databaseInfo.getDatabaseName())) {
                return R.failed(Status.DATABASE_NAME_IS_EXIST, databaseInfo.getDatabaseName());
            }
            service.createDatabase(databaseInfo.getDatabaseName());
            return R.succeed();
        } catch (Exception e) {
            log.error("Exception with creating database.", e);
            return R.failed(Status.DATABASE_CREATE_ERROR);
        }
    }

    /**
     * /** Get all database information.
     *
     * @return The list of all databases.
     */
    @GetMapping("/getAllDatabases")
    public R<List<DatabaseInfo>> getAllDatabases() {
        List<DatabaseInfo> databaseInfoList = new ArrayList<>();
        List<CatalogInfo> catalogInfoList = catalogService.list();
        if (!CollectionUtils.isEmpty(catalogInfoList)) {
            catalogInfoList.forEach(
                    item -> {
                        PaimonService service = PaimonServiceUtils.getPaimonService(item);
                        List<String> list = service.listDatabases();
                        list.forEach(
                                databaseName -> {
                                    DatabaseInfo info =
                                            DatabaseInfo.builder()
                                                    .databaseName(databaseName)
                                                    .catalogId(item.getId())
                                                    .catalogName(item.getCatalogName())
                                                    .description("")
                                                    .build();
                                    databaseInfoList.add(info);
                                });
                    });
        }
        return R.succeed(databaseInfoList);
    }

    /**
     * Removes a database by its name.
     *
     * @param databaseName The database to be removed.
     * @param catalogId The catalog to which the database to be removed belongs.
     * @return A response indicating the success or failure of the removal operation.
     * @throws RuntimeException if the database is not found or it is not empty.
     */
    @DeleteMapping("/drop/{databaseName}/{catalogId}")
    public R<Void> dropDatabase(@PathVariable String databaseName, @PathVariable String catalogId) {
        try {
            CatalogInfo catalogInfo = getCatalogInfo(catalogId);
            PaimonService service = PaimonServiceUtils.getPaimonService(catalogInfo);
            service.dropDatabase(databaseName);
            return R.succeed();
        } catch (Exception e) {
            log.error("Exception with dropping database.", e);
            return R.failed(Status.DATABASE_DROP_ERROR);
        }
    }

    /**
     * Retrieves the associated CatalogInfo object based on the given catalog id.
     *
     * @param catalogId The catalog id.
     * @return The associated CatalogInfo object, or null if it doesn't exist.
     */
    private CatalogInfo getCatalogInfo(String catalogId) {
        LambdaQueryWrapper<CatalogInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CatalogInfo::getId, catalogId);
        return catalogService.getOne(queryWrapper);
    }
}
