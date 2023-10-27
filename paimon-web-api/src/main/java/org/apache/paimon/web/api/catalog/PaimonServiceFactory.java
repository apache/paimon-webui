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
package org.apache.paimon.web.api.catalog;

import org.apache.paimon.catalog.CatalogContext;
import org.apache.paimon.catalog.CatalogFactory;
import org.apache.paimon.options.Options;
import org.apache.paimon.web.api.common.CatalogProperties;
import org.apache.paimon.web.api.common.MetastoreType;

import org.apache.commons.lang3.StringUtils;

/** Paimon service factory. */
public class PaimonServiceFactory {

    public static PaimonService createFileSystemCatalogService(String name, String warehouse) {
        Options options = new Options();
        options.set(CatalogProperties.WAREHOUSE, warehouse + "/" + name);

        CatalogContext context = CatalogContext.create(options);

        return new PaimonService(CatalogFactory.createCatalog(context), name);
    }

    public static PaimonService createHiveCatalogService(
            String name, String warehouse, String uri, String hiveConfDir) {
        Options options = new Options();
        options.set(CatalogProperties.WAREHOUSE, warehouse + "/" + name);

        options.set(CatalogProperties.METASTORE, MetastoreType.HIVE.toString());
        options.set(CatalogProperties.URI, uri);
        if (StringUtils.isNotBlank(hiveConfDir)) {
            options.set(CatalogProperties.HIVE_CONF_DIR, hiveConfDir);
        }
        CatalogContext context = CatalogContext.create(options);

        return new PaimonService(CatalogFactory.createCatalog(context), name);
    }
}
