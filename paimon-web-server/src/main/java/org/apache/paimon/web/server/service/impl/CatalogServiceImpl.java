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

package org.apache.paimon.web.server.service.impl;

import org.apache.paimon.web.api.catalog.PaimonServiceFactory;
import org.apache.paimon.web.server.data.dto.CatalogDto;
import org.apache.paimon.web.server.data.enums.CatalogMode;
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.mapper.CatalogMapper;
import org.apache.paimon.web.server.service.CatalogService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

/** CatalogServiceImpl. */
@Service
public class CatalogServiceImpl extends ServiceImpl<CatalogMapper, CatalogInfo>
        implements CatalogService {
    @Override
    public boolean checkCatalogNameUnique(CatalogDto catalogDto) {
        CatalogInfo info =
                this.lambdaQuery()
                        .eq(CatalogInfo::getCatalogName, catalogDto.getCatalogName())
                        .one();
        return Objects.nonNull(info);
    }

    @Override
    public R<Void> createCatalog(CatalogDto catalogDto) {
        if (checkCatalogNameUnique(catalogDto)) {
            return R.failed(Status.CATALOG_NAME_IS_EXIST, catalogDto.getCatalogName());
        }

        if (catalogDto.getCatalogType().equalsIgnoreCase(CatalogMode.FILESYSTEM.getMode())) {
            PaimonServiceFactory.createFileSystemCatalogService(
                    catalogDto.getCatalogName(), catalogDto.getWarehouse());
        } else if (catalogDto.getCatalogType().equalsIgnoreCase(CatalogMode.HIVE.getMode())) {
            if (StringUtils.isNotBlank(catalogDto.getHiveConfDir())) {
                PaimonServiceFactory.createHiveCatalogService(
                        catalogDto.getCatalogName(),
                        catalogDto.getWarehouse(),
                        catalogDto.getHiveUri(),
                        catalogDto.getHiveConfDir());
            } else {
                PaimonServiceFactory.createHiveCatalogService(
                        catalogDto.getCatalogName(),
                        catalogDto.getWarehouse(),
                        catalogDto.getHiveUri(),
                        null);
            }
        }

        CatalogInfo catalogInfo =
                CatalogInfo.builder()
                        .catalogName(catalogDto.getCatalogName())
                        .catalogType(catalogDto.getCatalogType())
                        .hiveUri(catalogDto.getHiveUri())
                        .warehouse(catalogDto.getWarehouse())
                        .isDelete(false)
                        .build();

        return this.save(catalogInfo) ? R.succeed() : R.failed();
    }
}
