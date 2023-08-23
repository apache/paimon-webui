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

package org.apache.paimon.web.server.util;

import org.apache.paimon.catalog.Catalog;
import org.apache.paimon.web.api.catalog.CatalogCreator;
import org.apache.paimon.web.server.data.model.CatalogInfo;

/** catalog util. */
public class CatalogUtils {

    /**
     * Get a Catalog based on the provided CatalogInfo.
     *
     * @param catalogInfo The CatalogInfo object containing the catalog details.
     * @return The created Catalog object.
     */
    public static Catalog getCatalog(CatalogInfo catalogInfo) {
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
