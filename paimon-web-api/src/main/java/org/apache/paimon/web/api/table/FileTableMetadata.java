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

package org.apache.paimon.web.api.table;

import javax.annotation.Nullable;

import java.time.LocalDateTime;

/** file table metadata. */
public class FileTableMetadata {

    private final String partition;
    private final Integer bucket;
    private final String filePath;
    private final String fileFormat;
    private final Long schemaId;
    private final Integer level;
    private final Long recordCount;
    private final Long fileSizeInBytes;
    private final String minKey;
    private final String maxKey;
    private final String nullValueCounts;
    private final String minValueStats;
    private final String maxValueStats;
    private final LocalDateTime creationTime;

    public FileTableMetadata(
            String partition,
            Integer bucket,
            String filePath,
            String fileFormat,
            Long schemaId,
            Integer level,
            Long recordCount,
            Long fileSizeInBytes,
            String minKey,
            String maxKey,
            String nullValueCounts,
            String minValueStats,
            String maxValueStats,
            LocalDateTime creationTime) {
        this.partition = partition;
        this.bucket = bucket;
        this.filePath = filePath;
        this.fileFormat = fileFormat;
        this.schemaId = schemaId;
        this.level = level;
        this.recordCount = recordCount;
        this.fileSizeInBytes = fileSizeInBytes;
        this.minKey = minKey;
        this.maxKey = maxKey;
        this.nullValueCounts = nullValueCounts;
        this.minValueStats = minValueStats;
        this.maxValueStats = maxValueStats;
        this.creationTime = creationTime;
    }

    public String getPartition() {
        return partition;
    }

    public Integer getBucket() {
        return bucket;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public Long getSchemaId() {
        return schemaId;
    }

    public Integer getLevel() {
        return level;
    }

    public Long getRecordCount() {
        return recordCount;
    }

    public Long getFileSizeInBytes() {
        return fileSizeInBytes;
    }

    public String getMinKey() {
        return minKey;
    }

    public String getMaxKey() {
        return maxKey;
    }

    public String getNullValueCounts() {
        return nullValueCounts;
    }

    public String getMinValueStats() {
        return minValueStats;
    }

    public String getMaxValueStats() {
        return maxValueStats;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public static FileTableMetadata.Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        @Nullable private String partition;
        private Integer bucket;
        private String filePath;
        private String fileFormat;
        private Long schemaId;
        private Integer level;
        private Long recordCount;
        private Long fileSizeInBytes;
        private String minKey;
        private String maxKey;
        private String nullValueCounts;
        @Nullable private String minValueStats;
        @Nullable private String maxValueStats;
        private LocalDateTime creationTime;

        public Builder partition(String partition) {
            this.partition = partition;
            return this;
        }

        public Builder bucket(Integer bucket) {
            this.bucket = bucket;
            return this;
        }

        public Builder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder fileFormat(String fileFormat) {
            this.fileFormat = fileFormat;
            return this;
        }

        public Builder schemaId(Long schemaId) {
            this.schemaId = schemaId;
            return this;
        }

        public Builder level(Integer level) {
            this.level = level;
            return this;
        }

        public Builder recordCount(Long recordCount) {
            this.recordCount = recordCount;
            return this;
        }

        public Builder fileSizeInBytes(Long fileSizeInBytes) {
            this.fileSizeInBytes = fileSizeInBytes;
            return this;
        }

        public Builder minKey(String minKey) {
            this.minKey = minKey;
            return this;
        }

        public Builder maxKey(String maxKey) {
            this.maxKey = maxKey;
            return this;
        }

        public Builder nullValueCounts(String nullValueCounts) {
            this.nullValueCounts = nullValueCounts;
            return this;
        }

        public Builder minValueStats(String minValueStats) {
            this.minValueStats = minValueStats;
            return this;
        }

        public Builder maxValueStats(String maxValueStats) {
            this.maxValueStats = maxValueStats;
            return this;
        }

        public Builder creationTime(LocalDateTime creationTime) {
            this.creationTime = creationTime;
            return this;
        }

        public FileTableMetadata build() {
            return new FileTableMetadata(
                    partition,
                    bucket,
                    filePath,
                    fileFormat,
                    schemaId,
                    level,
                    recordCount,
                    fileSizeInBytes,
                    minKey,
                    maxKey,
                    nullValueCounts,
                    minValueStats,
                    maxValueStats,
                    creationTime);
        }
    }
}
