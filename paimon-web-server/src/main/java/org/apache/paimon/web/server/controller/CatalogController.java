/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.paimon.web.server.controller;

import org.apache.paimon.web.api.catalog.PaimonServiceFactory;
import org.apache.paimon.web.server.data.enums.CatalogMode;
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.service.CatalogService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
     * Create a catalog.
     *
     * @param catalogInfo The catalogInfo for the catalog.
     * @return The created catalog.
     */
    @PostMapping("/create")
    public R<Void> createCatalog(@RequestBody CatalogInfo catalogInfo) {
        if (!catalogService.checkCatalogNameUnique(catalogInfo)) {
            return R.failed(Status.CATALOG_NAME_IS_EXIST, catalogInfo.getCatalogName());
        }

        try {
            if (catalogInfo.getCatalogType().equalsIgnoreCase(CatalogMode.FILESYSTEM.getMode())) {
                PaimonServiceFactory.createFileSystemCatalogService(
                        catalogInfo.getCatalogName(), catalogInfo.getWarehouse());
            } else if (catalogInfo.getCatalogType().equalsIgnoreCase(CatalogMode.HIVE.getMode())) {
                if (StringUtils.isNotBlank(catalogInfo.getHiveConfDir())) {
                    PaimonServiceFactory.createHiveCatalogService(
                            catalogInfo.getCatalogName(),
                            catalogInfo.getWarehouse(),
                            catalogInfo.getHiveUri(),
                            catalogInfo.getHiveConfDir());
                } else {
                    PaimonServiceFactory.createHiveCatalogService(
                            catalogInfo.getCatalogName(),
                            catalogInfo.getWarehouse(),
                            catalogInfo.getHiveUri(),
                            null);
                }
            }
            return catalogService.save(catalogInfo) ? R.succeed() : R.failed();
        } catch (Exception e) {
            log.error("Exception with creating catalog.", e);
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

    /**
     * Removes a catalog with given catalog name.
     *
     * @param catalogName The catalog name.
     * @return A response indicating the success or failure of the operation.
     */
    @DeleteMapping("/remove/{catalogName}")
    public R<Void> removeCatalog(@PathVariable String catalogName) {
        QueryWrapper<CatalogInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("catalog_name", catalogName);
        return catalogService.remove(queryWrapper)
                ? R.succeed()
                : R.failed(Status.CATALOG_REMOVE_ERROR);
    }
}
