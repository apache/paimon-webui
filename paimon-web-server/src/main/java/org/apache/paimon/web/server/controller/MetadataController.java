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

package org.apache.paimon.web.server.controller;

import org.apache.paimon.web.server.data.dto.MetadataDTO;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.vo.DataFileVO;
import org.apache.paimon.web.server.data.vo.ManifestsVO;
import org.apache.paimon.web.server.data.vo.SchemaVO;
import org.apache.paimon.web.server.data.vo.SnapshotVO;
import org.apache.paimon.web.server.service.MetadataService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Metadata api controller. */
@Slf4j
@RestController
@RequestMapping("/api/metadata/query")
public class MetadataController {

    private final MetadataService metadataService;

    public MetadataController(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @PostMapping("/schema")
    public R<List<SchemaVO>> getSchemaInfo(@RequestBody MetadataDTO dto) {
        return R.succeed(metadataService.getSchema(dto));
    }

    @PostMapping("/snapshot")
    public R<List<SnapshotVO>> getSnapshotInfo(@RequestBody MetadataDTO dto) {
        return R.succeed(metadataService.getSnapshot(dto));
    }

    @PostMapping("/manifest")
    public R<List<ManifestsVO>> getManifestInfo(@RequestBody MetadataDTO dto) {
        return R.succeed(metadataService.getManifest(dto));
    }

    @PostMapping("/dataFile")
    public R<List<DataFileVO>> getDataFileInfo(@RequestBody MetadataDTO dto) {
        return R.succeed(metadataService.getDataFile(dto));
    }
}
