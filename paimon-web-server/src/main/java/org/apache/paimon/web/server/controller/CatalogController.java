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

import lombok.extern.slf4j.Slf4j;
import org.apache.paimon.catalog.Catalog;
import org.apache.paimon.web.api.catalog.CatalogCreator;
import org.apache.paimon.web.server.data.result.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    /**
     * Create a filesystem catalog.
     *
     * @param fileSystemCatalogInfo The fileSystemCatalogInfo for the filesystem catalog.
     * @return The created catalog.
     */
    @PostMapping("/createFilesystemCatalog")
    public R<Catalog> createFilesystemCatalog(@RequestBody FileSystemCatalogInfo fileSystemCatalogInfo) {
        return R.succeed(CatalogCreator.createFilesystemCatalog(fileSystemCatalogInfo.path));
    }

    /**
     * Create a hive catalog.
     *
     * @param catalogInfo The information for the hive catalog.
     * @return The created catalog.
     */
    @PostMapping("/createHiveCatalog")
    public Catalog createHiveCatalog(@RequestBody HiveCatalogInfo catalogInfo) {
        return CatalogCreator.createHiveCatalog(catalogInfo.getWarehouse(), catalogInfo.getUri(), catalogInfo.getHiveConfDir());
    }

    /**
     * A class to hold the information for the filesystem catalog.
     */
    public static class FileSystemCatalogInfo {
        private String catalogName;
        private String catalogType;
        private String path;
    }

    /**
     * A class to hold the information for the hive catalog.
     */
    public static class HiveCatalogInfo {
        private String warehouse;
        private String uri;
        private String hiveConfDir;

        public String getWarehouse() {
            return warehouse;
        }

        public void setWarehouse(String warehouse) {
            this.warehouse = warehouse;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getHiveConfDir() {
            return hiveConfDir;
        }

        public void setHiveConfDir(String hiveConfDir) {
            this.hiveConfDir = hiveConfDir;
        }
    }
}
