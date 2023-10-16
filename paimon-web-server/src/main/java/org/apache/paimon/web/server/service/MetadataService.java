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

package org.apache.paimon.web.server.service;

import org.apache.paimon.web.server.data.dto.QueryMetadataInfoDto;
import org.apache.paimon.web.server.data.vo.DataFileInfoVo;
import org.apache.paimon.web.server.data.vo.ManifestsInfoVo;
import org.apache.paimon.web.server.data.vo.SchemaInfoVo;
import org.apache.paimon.web.server.data.vo.SnapshotInfoVo;

import java.util.List;

public interface MetadataService {

    /**
     * Retrieve a list of Metadata schema info
     *
     * @param dto query metadata info
     * @return a list of DatabaseInfo objects
     */
    List<SchemaInfoVo> getSchemaInfo(QueryMetadataInfoDto dto);

    /**
     * Retrieve a list of Metadata snapshot info
     *
     * @param dto query metadata info
     * @return a list of snapshot objects
     */
    List<SnapshotInfoVo> getSnapshotInfo(QueryMetadataInfoDto dto);

    /**
     * Retrieve a list of Metadata manifest info
     *
     * @param dto query metadata info
     * @return a list of manifest info objects
     */
    List<ManifestsInfoVo> getManifestInfo(QueryMetadataInfoDto dto);

    /**
     * Retrieve a list of Metadata data file info
     *
     * @param dto query metadata info
     * @return a list of data file objects
     */
    List<DataFileInfoVo> getDataFileInfo(QueryMetadataInfoDto dto);



}
