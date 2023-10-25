/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.paimon.web.api.table;

/** manifest table metadata. */
public class ManifestTableMetadata {

    private final String fileName;
    private final Long fileSize;
    private final Long numAddedFiles;
    private final Long numDeletedFiles;
    private final Long schemaId;

    public ManifestTableMetadata(
            String fileName,
            Long fileSize,
            Long numAddedFiles,
            Long numDeletedFiles,
            Long schemaId) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.numAddedFiles = numAddedFiles;
        this.numDeletedFiles = numDeletedFiles;
        this.schemaId = schemaId;
    }

    public String getFileName() {
        return fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public Long getNumAddedFiles() {
        return numAddedFiles;
    }

    public Long getNumDeletedFiles() {
        return numDeletedFiles;
    }

    public Long getSchemaId() {
        return schemaId;
    }

    public static ManifestTableMetadata.Builder builder() {
        return new Builder();
    }

    /** The builder for ManifestTableMetadata. */
    public static final class Builder {
        private String fileName;
        private Long fileSize;
        private Long numAddedFiles;
        private Long numDeletedFiles;
        private Long schemaId;

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder fileSize(Long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public Builder numAddedFiles(Long numAddedFiles) {
            this.numAddedFiles = numAddedFiles;
            return this;
        }

        public Builder numDeletedFiles(Long numDeletedFiles) {
            this.numDeletedFiles = numDeletedFiles;
            return this;
        }

        public Builder schemaId(Long schemaId) {
            this.schemaId = schemaId;
            return this;
        }

        public ManifestTableMetadata build() {
            return new ManifestTableMetadata(
                    fileName, fileSize, numAddedFiles, numDeletedFiles, schemaId);
        }
    }
}
