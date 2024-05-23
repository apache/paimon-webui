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

import org.apache.paimon.data.InternalRow;
import org.apache.paimon.reader.RecordReader;
import org.apache.paimon.table.Table;
import org.apache.paimon.table.source.ReadBuilder;
import org.apache.paimon.web.api.catalog.PaimonService;
import org.apache.paimon.web.server.constant.MetadataConstant;
import org.apache.paimon.web.server.data.dto.MetadataDTO;
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.model.MetadataFieldsModel;
import org.apache.paimon.web.server.data.model.MetadataOptionModel;
import org.apache.paimon.web.server.data.vo.DataFileVO;
import org.apache.paimon.web.server.data.vo.ManifestsVO;
import org.apache.paimon.web.server.data.vo.OptionVO;
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
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** The implementation of {@link MetadataService}. */
@Service
@Slf4j
public class MetadataServiceImpl implements MetadataService {

    private final CatalogService catalogService;

    public MetadataServiceImpl(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    private RecordReader<InternalRow> reader;

    @Override
    public List<SchemaVO> getSchema(MetadataDTO dto) {

        initEnvironment(dto, MetadataConstant.SCHEMAS);

        List<SchemaVO> result = new LinkedList<>();
        try {
            reader.forEachRemaining(
                    internalRow -> {
                        SchemaVO schemaVo =
                                SchemaVO.builder()
                                        .schemaId(internalRow.getLong(0))
                                        .fields(
                                                new Gson()
                                                        .fromJson(
                                                                internalRow.getString(1).toString(),
                                                                new TypeToken<
                                                                        LinkedList<
                                                                                MetadataFieldsModel>>() {}))
                                        .partitionKeys(getSafeString(internalRow, 2))
                                        .primaryKeys(getSafeString(internalRow, 3))
                                        .option(formatOptions(getSafeString(internalRow, 4)))
                                        .comment(getSafeString(internalRow, 5))
                                        .updateTime(getSafeLocalDateTime(internalRow, 6))
                                        .build();
                        result.add(schemaVo);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public List<SnapshotVO> getSnapshot(MetadataDTO dto) {

        initEnvironment(dto, MetadataConstant.SNAPSHOTS);

        List<SnapshotVO> result = new LinkedList<>();

        try {
            reader.forEachRemaining(
                    internalRow -> {
                        SnapshotVO build =
                                SnapshotVO.builder()
                                        .snapshotId(internalRow.getLong(0))
                                        .schemaId(internalRow.getLong(1))
                                        .commitUser(getSafeString(internalRow, 2))
                                        .commitIdentifier(internalRow.getLong(3))
                                        .commitKind(getSafeString(internalRow, 4))
                                        .commitTime(getSafeLocalDateTime(internalRow, 5))
                                        .baseManifestList(getSafeString(internalRow, 6))
                                        .deltaManifestList(getSafeString(internalRow, 7))
                                        .changelogManifestList(getSafeString(internalRow, 8))
                                        .totalRecordCount(internalRow.getLong(9))
                                        .deltaRecordCount(internalRow.getLong(10))
                                        .changelogRecordCount(internalRow.getLong(11))
                                        .addedFileCount(internalRow.getLong(12))
                                        .deletedFileCount(internalRow.getInt(13))
                                        .watermark(getSafeLong(internalRow, 14))
                                        .build();
                        result.add(build);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public List<ManifestsVO> getManifest(MetadataDTO dto) {
        initEnvironment(dto, MetadataConstant.MANIFESTS);

        List<ManifestsVO> result = new LinkedList<>();

        try {
            reader.forEachRemaining(
                    internalRow -> {
                        ManifestsVO manifestsVo =
                                ManifestsVO.builder()
                                        .fileName(getSafeString(internalRow, 0))
                                        .fileSize(internalRow.getLong(1))
                                        .numAddedFiles(internalRow.getLong(2))
                                        .numDeletedFiles(internalRow.getLong(3))
                                        .schemaId(internalRow.getLong(4))
                                        .build();
                        result.add(manifestsVo);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public List<DataFileVO> getDataFile(MetadataDTO dto) {

        initEnvironment(dto, MetadataConstant.FILES);

        List<DataFileVO> result = new LinkedList<>();

        try {
            reader.forEachRemaining(
                    internalRow -> {
                        DataFileVO dataFileVO =
                                DataFileVO.builder()
                                        .partition(getSafeString(internalRow, 0))
                                        .bucket(internalRow.getInt(1))
                                        .filePath(getSafeString(internalRow, 2))
                                        .fileFormat(getSafeString(internalRow, 3))
                                        .schemaId(internalRow.getLong(4))
                                        .level(internalRow.getInt(5))
                                        .recordCount(internalRow.getLong(6))
                                        .fileSizeInBytes(internalRow.getLong(7))
                                        .minKey(getSafeString(internalRow, 8))
                                        .maxKey(getSafeString(internalRow, 9))
                                        .nullValueCounts(getSafeString(internalRow, 10))
                                        .minValueStats(getSafeString(internalRow, 11))
                                        .maxValueStats(getSafeString(internalRow, 12))
                                        .minSequenceNumber(internalRow.getLong(13))
                                        .maxSequenceNumber(internalRow.getLong(14))
                                        .creationTime(getSafeLocalDateTime(internalRow, 15))
                                        .build();
                        result.add(dataFileVO);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public List<OptionVO> getOption(MetadataDTO dto) {
        initEnvironment(dto, MetadataConstant.OPTIONS);

        List<OptionVO> result = new LinkedList<>();

        try {
            reader.forEachRemaining(
                    internalRow -> {
                        OptionVO optionVo = new OptionVO();
                        optionVo.setKey(internalRow.getString(0).toString());
                        optionVo.setValue(internalRow.getString(1).toString());
                        result.add(optionVo);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private void initEnvironment(MetadataDTO dto, String metadataConstantType) {
        dto.setTableName(
                String.format(
                        MetadataConstant.METADATA_TABLE_FORMAT,
                        dto.getTableName(),
                        metadataConstantType));
        CatalogInfo catalogInfo =
                catalogService.getOne(
                        Wrappers.lambdaQuery(CatalogInfo.class)
                                .eq(CatalogInfo::getId, dto.getCatalogId())
                                .select(i -> true));
        PaimonService paimonService = PaimonServiceUtils.getPaimonService(catalogInfo);
        Table table = paimonService.getTable(dto.getDatabaseName(), dto.getTableName());
        this.reader = getReader(table);
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

    private String getSafeString(InternalRow internalRow, int index) {
        return internalRow.isNullAt(index) ? "" : internalRow.getString(index).toString();
    }

    private Long getSafeLong(InternalRow internalRow, int index) {
        return internalRow.isNullAt(index) ? null : internalRow.getLong(14);
    }

    private LocalDateTime getSafeLocalDateTime(InternalRow internalRow, int index) {
        return internalRow.isNullAt(index)
                ? null
                : internalRow.getTimestamp(index, 3).toLocalDateTime();
    }
}
