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

import org.apache.paimon.catalog.Catalog;
import org.apache.paimon.data.InternalRow;
import org.apache.paimon.reader.RecordReader;
import org.apache.paimon.table.Table;
import org.apache.paimon.table.source.ReadBuilder;
import org.apache.paimon.web.api.table.TableManager;
import org.apache.paimon.web.server.constant.MetadataConstant;
import org.apache.paimon.web.server.data.dto.QueryMetadataDTO;
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.model.MetadataFieldsModel;
import org.apache.paimon.web.server.data.model.MetadataOptionModel;
import org.apache.paimon.web.server.data.vo.DataFileVO;
import org.apache.paimon.web.server.data.vo.ManifestsVO;
import org.apache.paimon.web.server.data.vo.SchemaVO;
import org.apache.paimon.web.server.data.vo.SnapshotVO;
import org.apache.paimon.web.server.service.CatalogService;
import org.apache.paimon.web.server.service.MetadataService;
import org.apache.paimon.web.server.util.PaimonServiceUtils;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** The implementation of {@link MetadataService} */
@Service
@Slf4j
public class MetadataServiceImpl implements MetadataService {

    private final CatalogService catalogService;

    public MetadataServiceImpl(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    private RecordReader<InternalRow> reader;

    @Override
    public List<SchemaVO> getSchema(QueryMetadataDTO dto) {

        initEnvironment(dto, MetadataConstant.SCHEMAS);

        List<SchemaVO> result = new LinkedList<>();
        try {
            reader.forEachRemaining(
                    internalRow -> {
                        SchemaVO schemaVo =
                                SchemaVO.builder()
                                        .setSchemaId(internalRow.getLong(0))
                                        .setFields(
                                                new Gson()
                                                        .fromJson(
                                                                internalRow.getString(1).toString(),
                                                                new TypeToken<
                                                                        LinkedList<
                                                                                MetadataFieldsModel>>() {}))
                                        .setPartitionKeys(internalRow.getString(2).toString())
                                        .setPrimaryKeys(internalRow.getString(3).toString())
                                        .setOption(
                                                formatOptions(internalRow.getString(4).toString()))
                                        .setComment(internalRow.getString(5).toString())
                                        .setUpdateTime(
                                                internalRow.getTimestamp(6, 3).toLocalDateTime())
                                        .build();
                        result.add(schemaVo);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public List<SnapshotVO> getSnapshot(QueryMetadataDTO dto) {

        initEnvironment(dto, MetadataConstant.SNAPSHOTS);

        List<SnapshotVO> result = new LinkedList<>();

        try {
            reader.forEachRemaining(
                    internalRow -> {
                        SnapshotVO build =
                                SnapshotVO.builder()
                                        .setSnapshotId(internalRow.getLong(0))
                                        .setSnapshotId(internalRow.getLong(1))
                                        .setCommitIdentifier(internalRow.getLong(3))
                                        .setCommitTime(
                                                internalRow.getTimestamp(5, 3).toLocalDateTime())
                                        .build();
                        result.add(build);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public List<ManifestsVO> getManifest(QueryMetadataDTO dto) {
        initEnvironment(dto, MetadataConstant.MANIFESTS);

        List<ManifestsVO> result = new LinkedList<>();

        try {
            reader.forEachRemaining(
                    internalRow -> {
                        ManifestsVO manifestsVo =
                                ManifestsVO.builder()
                                        .setFileName(internalRow.getString(0).toString())
                                        .setFileSize(internalRow.getLong(1))
                                        .setNumAddedFiles(internalRow.getLong(2))
                                        .build();
                        result.add(manifestsVo);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public List<DataFileVO> getDataFile(QueryMetadataDTO dto) {

        initEnvironment(dto, MetadataConstant.FILES);

        List<DataFileVO> result = new LinkedList<>();

        try {
            reader.forEachRemaining(
                    internalRow -> {
                        DataFileVO dataFileVo = new DataFileVO();
                        dataFileVo.setPartition(internalRow.getString(0).toString());
                        dataFileVo.setBucket(internalRow.getLong(1));
                        dataFileVo.setFilePath(internalRow.getString(2).toString());
                        dataFileVo.setFileFormat(internalRow.getString(3).toString());
                        result.add(dataFileVo);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private void initEnvironment(QueryMetadataDTO dto, String metadataConstantType) {
        dto.setTableName(
                String.format(
                        MetadataConstant.METADATA_TABLE_FORMAT,
                        dto.getTableName(),
                        metadataConstantType));
        CatalogInfo catalogInfo =
                catalogService.getOne(
                        Wrappers.lambdaQuery(CatalogInfo.class)
                                .eq(CatalogInfo::getCatalogName, dto.getCatalogName())
                                .select(i -> true));
        Catalog catalog = PaimonServiceUtils.getPaimonService(catalogInfo).catalog();
        try {
            Table table = TableManager.getTable(catalog, dto.getDatabaseName(), dto.getTableName());
            this.reader = getReader(table);
        } catch (Catalog.TableNotExistException e) {
            throw new RuntimeException(
                    String.format("Table [%s] not exists", dto.getTableName()), e);
        }
    }

    private List<MetadataOptionModel> formatOptions(String jsonOption) {
        Gson gson = new Gson();
        Map<String, Object> map =
                gson.fromJson(jsonOption, new TypeToken<Map<String, Object>>() {});
        List<MetadataOptionModel> result = new LinkedList<>();
        for (Object key : map.keySet()) {
            result.add(new MetadataOptionModel(key.toString(), map.get(key)));
        }
        return result;
    }

    private static RecordReader<InternalRow> getReader(Table table) {
        ReadBuilder readBuilder = table.newReadBuilder();
        try (RecordReader<InternalRow> reader =
                readBuilder.newRead().createReader(readBuilder.newScan().plan())) {
            return reader;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
