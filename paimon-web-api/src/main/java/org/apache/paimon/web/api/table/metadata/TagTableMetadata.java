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

package org.apache.paimon.web.api.table.metadata;

import javax.annotation.Nullable;

import java.time.LocalDateTime;

/** file table metadata. */
public class TagTableMetadata {

    private final String tagName;
    private final Long snapshotId;
    private final Long schemaId;
    private final LocalDateTime createTime;
    private final Long recordCount;

    public TagTableMetadata(
            String tagName,
            Long snapshotId,
            Long schemaId,
            LocalDateTime createTime,
            Long recordCount) {
        this.tagName = tagName;
        this.snapshotId = snapshotId;
        this.schemaId = schemaId;
        this.createTime = createTime;
        this.recordCount = recordCount;
    }

    public String getTagName() {
        return tagName;
    }

    public Long getSnapshotId() {
        return snapshotId;
    }

    public Long getSchemaId() {
        return schemaId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public Long getRecordCount() {
        return recordCount;
    }

    public static TagTableMetadata.Builder builder() {
        return new Builder();
    }

    /** The builder for TagTableMetadata. */
    public static final class Builder {
        private String tagName;
        private Long snapshotId;
        private Long schemaId;
        private LocalDateTime createTime;
        @Nullable private Long recordCount;

        public Builder tagName(String tagName) {
            this.tagName = tagName;
            return this;
        }

        public Builder snapshotId(Long snapshotId) {
            this.snapshotId = snapshotId;
            return this;
        }

        public Builder schemaId(Long schemaId) {
            this.schemaId = schemaId;
            return this;
        }

        public Builder createTime(LocalDateTime createTime) {
            this.createTime = createTime;
            return this;
        }

        public Builder recordCount(Long recordCount) {
            this.recordCount = recordCount;
            return this;
        }

        public TagTableMetadata build() {
            return new TagTableMetadata(tagName, snapshotId, schemaId, createTime, recordCount);
        }
    }
}
