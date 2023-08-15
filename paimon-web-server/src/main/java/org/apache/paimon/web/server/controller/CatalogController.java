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

import org.apache.paimon.web.api.catalog.CatalogCreator;
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.service.CatalogService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Catalog api controller. */
@Slf4j
@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    @Autowired private CatalogService catalogService;

    /**
     * Create a filesystem catalog.
     *
     * @param catalogInfo The catalogInfo for the filesystem catalog.
     * @return The created catalog.
     */
    @PostMapping("/createFilesystemCatalog")
    public R<Void> createFilesystemCatalog(@RequestBody CatalogInfo catalogInfo) {
        if (!catalogService.checkCatalogNameUnique(catalogInfo)) {
            return R.failed(Status.CATALOG_NAME_IS_EXIST, catalogInfo.getCatalogName());
        }

        try {
            CatalogCreator.createFilesystemCatalog(catalogInfo.getWarehouse());
            return catalogService.save(catalogInfo) ? R.succeed() : R.failed();
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed(Status.CATALOG_CREATE_ERROR);
        }
    }

    /**
     * Create a hive catalog.
     *
     * @param catalogInfo The information for the hive catalog.
     * @return The created catalog.
     */
    @PostMapping("/createHiveCatalog")
    public R<Void> createHiveCatalog(@RequestBody CatalogInfo catalogInfo) {
        if (!catalogService.checkCatalogNameUnique(catalogInfo)) {
            return R.failed(Status.CATALOG_NAME_IS_EXIST, catalogInfo.getCatalogName());
        }

        try {
            CatalogCreator.createHiveCatalog(
                    catalogInfo.getWarehouse(),
                    catalogInfo.getHiveUri(),
                    catalogInfo.getHiveConfDir());
            return catalogService.save(catalogInfo) ? R.succeed() : R.failed();
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed(Status.CATALOG_CREATE_ERROR);
        }
    }

    /**
     * Get all catalog information.
     *
     * @return The list of all catalogs.
     */
    @GetMapping("/getAllCatalogs")
    public R<List<CatalogInfo>> getCatalog() {
        List<CatalogInfo> catalogs = catalogService.list();
        return R.succeed(catalogs);
    }
}
