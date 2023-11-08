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

package org.apache.paimon.web.server.service;

import org.apache.paimon.web.server.data.dto.QueryMetadataDTO;
import org.apache.paimon.web.server.data.vo.DataFileVO;
import org.apache.paimon.web.server.data.vo.ManifestsVO;
import org.apache.paimon.web.server.data.vo.SchemaVO;
import org.apache.paimon.web.server.data.vo.SnapshotVO;

import java.util.List;

/** Metadata service includes the service interfaces of metadata. */
public interface MetadataService {

    /**
     * Retrieves a list of Metadata schema.
     *
     * @param dto query metadata info
     * @return a list of DatabaseInfo objects
     */
    List<SchemaVO> getSchema(QueryMetadataDTO dto);

    /**
     * Retrieves a list of Metadata snapshot.
     *
     * @param dto query metadata info
     * @return a list of snapshot objects
     */
    List<SnapshotVO> getSnapshot(QueryMetadataDTO dto);

    /**
     * Retrieves a list of Metadata manifest.
     *
     * @param dto query metadata info
     * @return a list of manifest info objects
     */
    List<ManifestsVO> getManifest(QueryMetadataDTO dto);

    /**
     * Retrieves a list of Metadata data file.
     *
     * @param dto query metadata info
     * @return a list of data file objects
     */
    List<DataFileVO> getDataFile(QueryMetadataDTO dto);
}
