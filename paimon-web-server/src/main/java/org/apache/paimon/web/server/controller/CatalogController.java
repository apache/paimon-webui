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

import org.apache.paimon.web.server.data.dto.CatalogDto;
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.service.CatalogService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * @param catalogDto The catalogDto for the catalog.
     * @return The created catalog.
     */
    @PostMapping("/create")
    public R<Void> createCatalog(@RequestBody CatalogDto catalogDto) {
        try {
            return catalogService.createCatalog(catalogDto);
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
     * @param catalogId The catalog id.
     * @return A response indicating the success or failure of the operation.
     */
    @DeleteMapping("/remove/{catalogId}")
    public R<Void> removeCatalog(@PathVariable String catalogId) {
        LambdaQueryWrapper<CatalogInfo> lambadaQueryWrapper =
                Wrappers.lambdaQuery(CatalogInfo.class).eq(CatalogInfo::getId, catalogId);
        return catalogService.remove(lambadaQueryWrapper)
                ? R.succeed()
                : R.failed(Status.CATALOG_REMOVE_ERROR);
    }
}
