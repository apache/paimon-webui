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

package org.apache.paimon.web.api.common;

import javax.annotation.Nullable;

/** catalog entity. */
public class CatalogEntity {

    private final Long catalogId;

    private final String warehouse;

    private final String metastoreType;

    private final String uri;

    private final String hiveConfDir;

    public CatalogEntity(
            Long catalogId,
            String warehouse,
            String metastoreType,
            String uri,
            String hiveConfDir) {
        this.catalogId = catalogId;
        this.warehouse = warehouse;
        this.metastoreType = metastoreType;
        this.uri = uri;
        this.hiveConfDir = hiveConfDir;
    }

    public Long getCatalogId() {
        return catalogId;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public String getMetastoreType() {
        return metastoreType;
    }

    public String getUri() {
        return uri;
    }

    public String getHiveConfDir() {
        return hiveConfDir;
    }

    public static CatalogEntity.Builder builder() {
        return new Builder();
    }

    /** The builder for CatalogEntity. */
    public static final class Builder {
        private Long catalogId;
        private String warehouse;
        private String metastoreType;
        @Nullable private String uri;
        @Nullable private String hiveConfDir;

        public Builder catalogId(Long catalogId) {
            this.catalogId = catalogId;
            return this;
        }

        public Builder warehouse(String warehouse) {
            this.warehouse = warehouse;
            return this;
        }

        public Builder metastoreType(String metastoreType) {
            this.metastoreType = metastoreType;
            return this;
        }

        public Builder uri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder hiveConfDir(String hiveConfDir) {
            this.hiveConfDir = hiveConfDir;
            return this;
        }

        public CatalogEntity build() {
            return new CatalogEntity(catalogId, warehouse, metastoreType, uri, hiveConfDir);
        }
    }
}
