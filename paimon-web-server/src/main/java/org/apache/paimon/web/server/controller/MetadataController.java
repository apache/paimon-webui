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

package org.apache.paimon.web.server.controller;

import org.apache.paimon.web.server.data.dto.QueryMetadataInfoDto;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.vo.DataFileInfoVo;
import org.apache.paimon.web.server.data.vo.ManifestsInfoVo;
import org.apache.paimon.web.server.data.vo.SchemaInfoVo;
import org.apache.paimon.web.server.data.vo.SnapshotInfoVo;
import org.apache.paimon.web.server.service.MetadataService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** metadata api controller. */
@Slf4j
@RestController
@RequestMapping("/api/metadata")
public class MetadataController {

    private final MetadataService metadataService;

    public MetadataController(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @PostMapping("/querySchemaInfo")
    public R<List<SchemaInfoVo>> getSchemaInfo(@RequestBody QueryMetadataInfoDto dto) {
        return R.succeed(metadataService.getSchemaInfo(dto));
    }

    @PostMapping("/querySnapshotInfo")
    public R<List<SnapshotInfoVo>> getSnapshotInfo(@RequestBody QueryMetadataInfoDto dto) {
        return R.succeed(metadataService.getSnapshotInfo(dto));
    }

    @PostMapping("/queryManifestInfo")
    public R<List<ManifestsInfoVo>> getManifestInfo(@RequestBody QueryMetadataInfoDto dto) {
        return R.succeed(metadataService.getManifestInfo(dto));
    }

    @PostMapping("/queryDataFileInfo")
    public R<List<DataFileInfoVo>> getDataFileInfo(@RequestBody QueryMetadataInfoDto dto) {
        return R.succeed(metadataService.getDataFileInfo(dto));
    }
}
