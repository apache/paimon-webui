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

import org.apache.paimon.web.api.catalog.PaimonService;
import org.apache.paimon.web.server.data.dto.DatabaseDTO;
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/** DatabaseServiceImpl. */
@Service
public class DatabaseServiceImpl extends ServiceImpl<DatabaseMapper, DatabaseVO>
        implements DatabaseService {

    private final CatalogService catalogService;

    public DatabaseServiceImpl(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Override
    public boolean checkCatalogNameUnique(DatabaseVO databaseVO) {
        return false;
    }

    @Override
    public R<Void> createDatabase(DatabaseDTO databaseDTO) {
        try {
            CatalogInfo catalogInfo = getCatalogInfo(databaseDTO);
            PaimonService service = PaimonServiceUtils.getPaimonService(catalogInfo);
            if (service.databaseExists(databaseDTO.getName())) {
                return R.failed(Status.DATABASE_NAME_IS_EXIST, databaseDTO.getName());
            }
            service.createDatabase(
                    databaseDTO.getName(),
                    BooleanUtils.toBooleanDefaultIfNull(databaseDTO.isIgnoreIfExists(), false));
            return R.succeed();
        } catch (Exception e) {
            log.error("Exception with creating database.", e);
            return R.failed(Status.DATABASE_CREATE_ERROR);
        }
    }

    @Override
    public R<List<DatabaseVO>> listDatabases() {
        List<DatabaseVO> databaseVOList = new ArrayList<>();
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
                                    databaseVOList.add(info);
                                });
                    });
        }
        return R.succeed(databaseVOList);
    }

    @Override
    public R<List<DatabaseVO>> getDatabasesByCatalogId(Integer id) {
        List<DatabaseVO> resultList = new LinkedList<>();
        CatalogInfo catalog = catalogService.getById(id);
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
        return R.succeed(resultList);
    }

    @Override
    public R<Void> dropDatabase(DatabaseDTO databaseDTO) {
        try {
            CatalogInfo catalogInfo = getCatalogInfo(databaseDTO);
            PaimonService service = PaimonServiceUtils.getPaimonService(catalogInfo);
            service.dropDatabase(
                    databaseDTO.getName(),
                    BooleanUtils.toBooleanDefaultIfNull(databaseDTO.isIgnoreIfExists(), false),
                    BooleanUtils.toBooleanDefaultIfNull(databaseDTO.isCascade(), true));
            return R.succeed();
        } catch (Exception e) {
            log.error("Exception with dropping database.", e);
            return R.failed(Status.DATABASE_DROP_ERROR);
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
                String.format("CatalogName: [%s] not found.", databaseDTO.getCatalogName()));
        return catalogInfo;
    }
}
