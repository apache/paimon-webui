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

package org.apache.paimon.web.server.service.impl;

import org.apache.paimon.web.api.catalog.PaimonService;
import org.apache.paimon.web.server.data.dto.DatabaseDTO;
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.vo.DatabaseVO;
import org.apache.paimon.web.server.mapper.DatabaseMapper;
import org.apache.paimon.web.server.service.CatalogService;
import org.apache.paimon.web.server.service.DatabaseService;
import org.apache.paimon.web.server.util.PaimonServiceUtils;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/** The implementation of {@link DatabaseService}. */
@Service
public class DatabaseServiceImpl extends ServiceImpl<DatabaseMapper, DatabaseVO>
        implements DatabaseService {

    private final CatalogService catalogService;

    public DatabaseServiceImpl(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Override
    public boolean databaseExists(DatabaseDTO databaseDTO) {
        CatalogInfo catalogInfo = getCatalogInfo(databaseDTO);
        PaimonService service = PaimonServiceUtils.getPaimonService(catalogInfo);
        return service.databaseExists(databaseDTO.getName());
    }

    @Override
    public boolean createDatabase(DatabaseDTO databaseDTO) {
        try {
            CatalogInfo catalogInfo = getCatalogInfo(databaseDTO);
            PaimonService service = PaimonServiceUtils.getPaimonService(catalogInfo);
            service.createDatabase(
                    databaseDTO.getName(),
                    BooleanUtils.toBooleanDefaultIfNull(databaseDTO.isIgnoreIfExists(), false));
            return true;
        } catch (Exception e) {
            log.error("Exception with creating database.", e);
            return false;
        }
    }

    @Override
    public List<DatabaseVO> listDatabases(Integer catalogId) {
        List<DatabaseVO> resultList = new LinkedList<>();
        if (Objects.nonNull(catalogId)) {
            CatalogInfo catalog = catalogService.getById(catalogId);
            PaimonService service = PaimonServiceUtils.getPaimonService(catalog);
            List<String> databases = service.listDatabases();
            databases.forEach(
                    databaseName -> {
                        DatabaseVO database = new DatabaseVO();
                        database.setName(databaseName);
                        database.setCatalogId(catalog.getId());
                        database.setCatalogName(catalog.getCatalogName());
                        database.setDescription("");
                        resultList.add(database);
                    });
            return resultList;
        } else {
            List<CatalogInfo> catalogInfoList = catalogService.list();
            if (!CollectionUtils.isEmpty(catalogInfoList)) {
                catalogInfoList.forEach(
                        item -> {
                            PaimonService service = PaimonServiceUtils.getPaimonService(item);
                            List<String> list = service.listDatabases();
                            list.forEach(
                                    databaseName -> {
                                        DatabaseVO info =
                                                DatabaseVO.builder()
                                                        .name(databaseName)
                                                        .catalogId(item.getId())
                                                        .catalogName(item.getCatalogName())
                                                        .description("")
                                                        .build();
                                        resultList.add(info);
                                    });
                        });
            }
            return resultList;
        }
    }

    @Override
    public boolean dropDatabase(DatabaseDTO databaseDTO) {
        try {
            CatalogInfo catalogInfo = getCatalogInfo(databaseDTO);
            PaimonService service = PaimonServiceUtils.getPaimonService(catalogInfo);
            service.dropDatabase(
                    databaseDTO.getName(),
                    BooleanUtils.toBooleanDefaultIfNull(databaseDTO.isIgnoreIfExists(), false),
                    BooleanUtils.toBooleanDefaultIfNull(databaseDTO.isCascade(), true));
            return true;
        } catch (Exception e) {
            log.error("Exception with dropping database.", e);
            return false;
        }
    }

    /**
     * Retrieves the associated CatalogInfo object based on the given catalog id.
     *
     * @param databaseDTO The database DTO.
     * @return The associated CatalogInfo object, or null if it doesn't exist.
     */
    private CatalogInfo getCatalogInfo(DatabaseDTO databaseDTO) {
        CatalogInfo catalogInfo;
        if (databaseDTO.getCatalogId() != null) {
            catalogInfo =
                    catalogService.getOne(
                            Wrappers.lambdaQuery(CatalogInfo.class)
                                    .eq(CatalogInfo::getId, databaseDTO.getCatalogId()));
        } else {
            catalogInfo =
                    catalogService.getOne(
                            Wrappers.lambdaQuery(CatalogInfo.class)
                                    .eq(CatalogInfo::getCatalogName, databaseDTO.getCatalogName()));
        }
        Objects.requireNonNull(
                catalogInfo,
                String.format("Catalog: [%s] is not found.", databaseDTO.getCatalogName()));
        return catalogInfo;
    }
}
