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

package org.apache.paimon.web.server.service;

import org.apache.paimon.web.server.data.dto.CatalogDTO;
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.result.R;

import com.baomidou.mybatisplus.extension.service.IService;

/** Catalog Service. */
public interface CatalogService extends IService<CatalogInfo> {

    /**
     * Verify if the catalog name is unique.
     *
     * @param catalog catalog info
     * @return result
     */
    boolean checkCatalogNameUnique(CatalogDTO catalog);

    /**
     * Create a catalog.
     *
     * @param catalogDTO catalog for the catalog.
     * @return The created catalog.
     */
    R<Void> createCatalog(CatalogDTO catalogDTO);
}
