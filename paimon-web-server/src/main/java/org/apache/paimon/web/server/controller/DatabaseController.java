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

import org.apache.paimon.web.api.catalog.PaimonService;
import org.apache.paimon.web.server.data.dto.DatabaseDTO;
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.model.DatabaseInfo;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.service.CatalogService;
import org.apache.paimon.web.server.util.PaimonServiceUtils;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Database api controller. */
@Slf4j
@RestController
@RequestMapping("/api/database")
public class DatabaseController {

    private final CatalogService catalogService;

    public DatabaseController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    /**
     * Creates a new database based on the provided DatabaseInfo.
     *
     * @param databaseDTO The DatabaseInfo object containing the details of the new database.
     * @return R<Void/> indicating the result of the operation.
     */
    @PostMapping("/create")
    public R<Void> createDatabase(@RequestBody DatabaseDTO databaseDTO) {
        try {
            CatalogInfo catalogInfo = getCatalogInfo(databaseDTO);
            PaimonService service = PaimonServiceUtils.getPaimonService(catalogInfo);
            if (service.databaseExists(databaseDTO.getName())) {
                return R.failed(Status.DATABASE_NAME_IS_EXIST, databaseDTO.getName());
            }
            service.createDatabase(
                    databaseDTO.getName(),
                    BooleanUtils.toBooleanDefaultIfNull(databaseDTO.isIgnoreIfExists(), false));
            return R.succeed();
        } catch (Exception e) {
            log.error("Exception with creating database.", e);
            return R.failed(Status.DATABASE_CREATE_ERROR);
        }
    }

    /**
     * Get all database information.
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
     * @param databaseDTO The drop database DTO.
     * @return A response indicating the success or failure of the removal operation.
     * @throws RuntimeException if the database is not found, or it is not empty.
     */
    @PostMapping("/drop")
    public R<Void> dropDatabase(@RequestBody DatabaseDTO databaseDTO) {
        try {
            CatalogInfo catalogInfo = getCatalogInfo(databaseDTO);
            PaimonService service = PaimonServiceUtils.getPaimonService(catalogInfo);
            service.dropDatabase(
                    databaseDTO.getName(),
                    BooleanUtils.toBooleanDefaultIfNull(databaseDTO.isIgnoreIfExists(), false),
                    BooleanUtils.toBooleanDefaultIfNull(databaseDTO.isCascade(), true));
            return R.succeed();
        } catch (Exception e) {
            log.error("Exception with dropping database.", e);
            return R.failed(Status.DATABASE_DROP_ERROR);
        }
    }

    /**
     * Retrieves the associated CatalogInfo object based on the given catalog id.
     *
     * @param databaseDTO The database DTO.
     * @return The associated CatalogInfo object, or null if it doesn't exist.
     */
    private CatalogInfo getCatalogInfo(DatabaseDTO databaseDTO) {
        CatalogInfo catalogInfo;
        if (StringUtils.isNotBlank(databaseDTO.getCatalogId())) {
            catalogInfo =
                    catalogService.getOne(
                            Wrappers.lambdaQuery(CatalogInfo.class)
                                    .eq(CatalogInfo::getId, databaseDTO.getCatalogId()));
        } else {
            catalogInfo =
                    catalogService.getOne(
                            Wrappers.lambdaQuery(CatalogInfo.class)
                                    .eq(CatalogInfo::getCatalogName, databaseDTO.getCatalogName()));
        }
        Objects.requireNonNull(
                catalogInfo,
                String.format("CatalogName: [%s] not found.", databaseDTO.getCatalogName()));
        return catalogInfo;
    }
}
