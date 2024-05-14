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

import org.apache.paimon.web.server.data.dto.CatalogDTO;
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.service.CatalogService;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    /**
     * Create a catalog.
     *
     * @param catalogDTO The catalogDTO for the catalog
     * @return A response indicating the success or failure of the operation
     */
    @PostMapping("/create")
    public R<Void> createCatalog(@RequestBody CatalogDTO catalogDTO) {
        try {
            if (catalogService.checkCatalogNameUnique(catalogDTO)) {
                return R.failed(Status.CATALOG_NAME_IS_EXIST, catalogDTO.getName());
            }
            return catalogService.createCatalog(catalogDTO) ? R.succeed() : R.failed();
        } catch (Exception e) {
            log.error("Exception with creating catalog.", e);
            return R.failed(Status.CATALOG_CREATE_ERROR);
        }
    }

    /**
     * Get all catalog information.
     *
     * @return The list of all catalogs
     */
    @GetMapping("/list")
    public R<List<CatalogInfo>> getCatalog() {
        List<CatalogInfo> catalogs = catalogService.list();
        return R.succeed(catalogs);
    }

    /**
     * Removes a catalog with given catalog name or catalog id.
     *
     * @param catalogDTO Given the catalog name or catalog id to remove catalog
     * @return A response indicating the success or failure of the operation
     */
    @PostMapping("/remove")
    public R<Void> removeCatalog(@RequestBody CatalogDTO catalogDTO) {
        boolean remove;
        if (StringUtils.isNotBlank(catalogDTO.getName())) {
            remove =
                    catalogService.remove(
                            Wrappers.lambdaQuery(CatalogInfo.class)
                                    .eq(CatalogInfo::getCatalogName, catalogDTO.getName()));
        } else {
            remove =
                    catalogService.remove(
                            Wrappers.lambdaQuery(CatalogInfo.class)
                                    .eq(CatalogInfo::getId, catalogDTO.getId()));
        }
        return remove ? R.succeed() : R.failed(Status.CATALOG_REMOVE_ERROR);
    }
}
