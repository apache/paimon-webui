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

import java.time.LocalDateTime;

/** snapshot table metadata. */
public class SnapshotTableMetadata {

    private final Long snapshotId;
    private final String snapshotPath;
    private final Long schemaId;
    private final String commitUser;
    private final Long commitIdentifier;
    private final String commitKind;
    private final LocalDateTime commitTime;
    private final Long totalRecordCount;
    private final Long deltaRecordCount;
    private final Long changelogRecordCount;
    private final Long watermark;

    public SnapshotTableMetadata(
            Long snapshotId,
            String snapshotPath,
            Long schemaId,
            String commitUser,
            Long commitIdentifier,
            String commitKind,
            LocalDateTime commitTime,
            Long totalRecordCount,
            Long deltaRecordCount,
            Long changelogRecordCount,
            Long watermark) {
        this.snapshotId = snapshotId;
        this.snapshotPath = snapshotPath;
        this.schemaId = schemaId;
        this.commitUser = commitUser;
        this.commitIdentifier = commitIdentifier;
        this.commitKind = commitKind;
        this.commitTime = commitTime;
        this.totalRecordCount = totalRecordCount;
        this.deltaRecordCount = deltaRecordCount;
        this.changelogRecordCount = changelogRecordCount;
        this.watermark = watermark;
    }

    public Long getSnapshotId() {
        return snapshotId;
    }

    public String getSnapshotPath() {
        return snapshotPath;
    }

    public Long getSchemaId() {
        return schemaId;
    }

    public String getCommitUser() {
        return commitUser;
    }

    public Long getCommitIdentifier() {
        return commitIdentifier;
    }

    public String getCommitKind() {
        return commitKind;
    }

    public LocalDateTime getCommitTime() {
        return commitTime;
    }

    public Long getTotalRecordCount() {
        return totalRecordCount;
    }

    public Long getDeltaRecordCount() {
        return deltaRecordCount;
    }

    public Long getChangelogRecordCount() {
        return changelogRecordCount;
    }

    public Long getWatermark() {
        return watermark;
    }

    public static SnapshotTableMetadata.Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long snapshotId;
        private String snapshotPath;
        private Long schemaId;
        private String commitUser;
        private Long commitIdentifier;
        private String commitKind;
        private LocalDateTime commitTime;
        private Long totalRecordCount;
        private Long deltaRecordCount;
        private Long changelogRecordCount;
        private Long watermark;

        public Builder snapshotId(Long snapshotId) {
            this.snapshotId = snapshotId;
            return this;
        }

        public Builder snapshotPath(String snapshotPath) {
            this.snapshotPath = snapshotPath;
            return this;
        }

        public Builder schemaId(Long schemaId) {
            this.schemaId = schemaId;
            return this;
        }

        public Builder commitUser(String commitUser) {
            this.commitUser = commitUser;
            return this;
        }

        public Builder commitIdentifier(Long commitIdentifier) {
            this.commitIdentifier = commitIdentifier;
            return this;
        }

        public Builder commitKind(String commitKind) {
            this.commitKind = commitKind;
            return this;
        }

        public Builder commitTime(LocalDateTime commitTime) {
            this.commitTime = commitTime;
            return this;
        }

        public Builder totalRecordCount(Long totalRecordCount) {
            this.totalRecordCount = totalRecordCount;
            return this;
        }

        public Builder deltaRecordCount(Long deltaRecordCount) {
            this.deltaRecordCount = deltaRecordCount;
            return this;
        }

        public Builder changelogRecordCount(Long changelogRecordCount) {
            this.changelogRecordCount = changelogRecordCount;
            return this;
        }

        public Builder watermark(Long watermark) {
            this.watermark = watermark;
            return this;
        }

        public SnapshotTableMetadata build() {
            return new SnapshotTableMetadata(
                    snapshotId,
                    snapshotPath,
                    schemaId,
                    commitUser,
                    commitIdentifier,
                    commitKind,
                    commitTime,
                    totalRecordCount,
                    deltaRecordCount,
                    changelogRecordCount,
                    watermark);
        }
    }
}
