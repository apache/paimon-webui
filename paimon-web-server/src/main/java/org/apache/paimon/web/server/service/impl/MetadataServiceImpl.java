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

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.paimon.catalog.Catalog;
import org.apache.paimon.data.InternalRow;
import org.apache.paimon.reader.RecordReader;
import org.apache.paimon.table.Table;
import org.apache.paimon.table.source.ReadBuilder;
import org.apache.paimon.web.api.table.TableManager;
import org.apache.paimon.web.server.constant.MetadataConstant;
import org.apache.paimon.web.server.data.dto.QueryMetadataInfoDto;
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.model.MetadataFieldsModel;
import org.apache.paimon.web.server.data.model.MetadataOptionModel;
import org.apache.paimon.web.server.data.vo.DataFileInfoVo;
import org.apache.paimon.web.server.data.vo.ManifestsInfoVo;
import org.apache.paimon.web.server.data.vo.SchemaInfoVo;
import org.apache.paimon.web.server.data.vo.SnapshotInfoVo;
import org.apache.paimon.web.server.service.CatalogService;
import org.apache.paimon.web.server.service.MetadataService;
import org.apache.paimon.web.server.util.PaimonServiceUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MetadataServiceImpl implements MetadataService {

    private final CatalogService catalogService;

    public MetadataServiceImpl(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    private RecordReader<InternalRow> reader;

    @Override
    public List<SchemaInfoVo> getSchemaInfo(QueryMetadataInfoDto dto) {

        initEnvironment(dto, MetadataConstant.SCHEMAS);

        List<SchemaInfoVo> result = new LinkedList<>();
        try {
            reader.forEachRemaining(internalRow -> {
                SchemaInfoVo schemaInfoVo = SchemaInfoVo.builder()
                        .setSchemaId(internalRow.getLong(0))
                        .setFields(JSONObject.parseArray(internalRow.getString(1).toString(), MetadataFieldsModel.class))
                        .setPartitionKeys(internalRow.getString(2).toString())
                        .setPrimaryKeys(internalRow.getString(3).toString())
                        .setOption(formatOptions(internalRow.getString(4).toString()))
                        .setComment(internalRow.getString(5).toString())
                        .setUpdateTime(internalRow.getTimestamp(6, 3).toLocalDateTime())
                        .build();
                result.add(schemaInfoVo);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public List<SnapshotInfoVo> getSnapshotInfo(QueryMetadataInfoDto dto) {

        initEnvironment(dto, MetadataConstant.SNAPSHOTS);

        List<SnapshotInfoVo> result = new LinkedList<>();

        try {
            reader.forEachRemaining(internalRow -> {
                SnapshotInfoVo build = SnapshotInfoVo.builder()
                        .setSnapshotId(internalRow.getLong(0))
                        .setSnapshotId(internalRow.getLong(1))
                        .setCommitIdentifier(internalRow.getLong(3))
                        .setCommitTime(internalRow.getTimestamp(5, 3).toLocalDateTime())
                        .build();
                result.add(build);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public List<ManifestsInfoVo> getManifestInfo(QueryMetadataInfoDto dto) {
        initEnvironment(dto, MetadataConstant.MANIFESTS);

        List<ManifestsInfoVo> result = new LinkedList<>();

        try {
            reader.forEachRemaining(internalRow -> {
                ManifestsInfoVo manifestsInfoVo = ManifestsInfoVo.builder()
                        .setFileName(internalRow.getString(0).toString())
                        .setFileSize(internalRow.getLong(1))
                        .setNumAddedFiles(internalRow.getLong(2))
                        .build();
                result.add(manifestsInfoVo);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public List<DataFileInfoVo> getDataFileInfo(QueryMetadataInfoDto dto) {

        initEnvironment(dto, MetadataConstant.FILES);

        List<DataFileInfoVo> result = new LinkedList<>();

        try {
            reader.forEachRemaining(internalRow -> {
                DataFileInfoVo dataFileInfoVo = DataFileInfoVo.builder()
                        .setPartition(internalRow.getString(0).toString())
                        .setBucket(internalRow.getLong(1))
                        .setFilePath(internalRow.getString(2).toString())
                        .setFileFormat(internalRow.getString(3).toString())
                        .builder();
                result.add(dataFileInfoVo);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private void initEnvironment(QueryMetadataInfoDto dto, String metadataConstantType) {
        dto.setTableName(String.format(MetadataConstant.METADATA_TABLE_FORMAT, dto.getTableName(), metadataConstantType));
        CatalogInfo catalogInfo = catalogService.getOne(Wrappers.lambdaQuery(CatalogInfo.class).eq(CatalogInfo::getCatalogName, dto.getCatalogName()).select(i -> true));
        Catalog catalog = PaimonServiceUtils.getPaimonService(catalogInfo).catalog();
        try {
            Table table = TableManager.getTable(catalog, dto.getDatabaseName(), dto.getTableName());
            this.reader = getReader(table);
        } catch (Catalog.TableNotExistException e) {
            throw new RuntimeException(String.format("Table [%s] not exists", dto.getTableName()), e);
        }
    }

    private List<MetadataOptionModel> formatOptions(String jsonOption) {
        JSONObject jsonObject = JSONObject.parseObject(jsonOption);
        List<MetadataOptionModel> result = new LinkedList<>();
        Iterator<Map.Entry<String, Object>> entryIterator = jsonObject.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, Object> map = entryIterator.next();
            result.add(new MetadataOptionModel(map.getKey(), map.getValue()));
        }
        return result;
    }

    private static RecordReader<InternalRow> getReader(Table table) {
        ReadBuilder readBuilder = table.newReadBuilder();
        try (RecordReader<InternalRow> reader = readBuilder.newRead().createReader(readBuilder.newScan().plan())) {
            return reader;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
