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

import org.apache.paimon.catalog.Catalog;
import org.apache.paimon.web.api.catalog.CatalogCreator;
import org.apache.paimon.web.api.database.DatabaseManager;
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.model.DatabaseInfo;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.service.CatalogService;
import org.apache.paimon.web.server.service.DatabaseService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Database api controller. */
@Slf4j
@RestController
@RequestMapping("/api/database")
public class DatabaseController {

    @Autowired private DatabaseService databaseService;

    @Autowired private CatalogService catalogService;

    @PostMapping("/createDatabase")
    public R<Void> createFilesystemCatalog(@RequestBody DatabaseInfo databaseInfo) {
        if (!databaseService.checkCatalogNameUnique(databaseInfo)) {
            return R.failed(Status.CATALOG_NAME_IS_EXIST, databaseInfo.getDatabaseName());
        }

        try {
            LambdaQueryWrapper<CatalogInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CatalogInfo::getId, databaseInfo.getCatalogId());
            CatalogInfo catalogInfo = catalogService.getOne(queryWrapper);
            DatabaseManager.createDatabase(getCatalog(catalogInfo), databaseInfo.getDatabaseName());
            return databaseService.save(databaseInfo) ? R.succeed() : R.failed();
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed(Status.CATALOG_CREATE_ERROR);
        }
    }

    private Catalog getCatalog(CatalogInfo catalogInfo) {
        if ("filesystem".equals(catalogInfo.getCatalogType())) {
            return CatalogCreator.createFilesystemCatalog(catalogInfo.getWarehouse());
        } else {
            return CatalogCreator.createHiveCatalog(
                    catalogInfo.getWarehouse(),
                    catalogInfo.getHiveUri(),
                    catalogInfo.getHiveConfDir());
        }
    }
}
