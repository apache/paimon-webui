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

package org.apache.paimon.web.server.data.vo;

import java.time.LocalDateTime;

/** VO of metadata snapshot. */
public class SnapshotVO {
    private final Long snapshotId;
    private final Long schemaId;
    private final Long commitIdentifier;
    private final LocalDateTime commitTime;

    public SnapshotVO(
            Long snapshotId, Long schemaId, Long commitIdentifier, LocalDateTime commitTime) {
        this.snapshotId = snapshotId;
        this.schemaId = schemaId;
        this.commitIdentifier = commitIdentifier;
        this.commitTime = commitTime;
    }

    public Long getSnapshotId() {
        return snapshotId;
    }

    public Long getSchemaId() {
        return schemaId;
    }

    public Long getCommitIdentifier() {
        return commitIdentifier;
    }

    public LocalDateTime getCommitTime() {
        return commitTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Builder for SnapshotInfoVo. */
    public static class Builder {
        private Long snapshotId;
        private Long schemaId;
        private Long commitIdentifier;
        private LocalDateTime commitTime;

        public Builder setSnapshotId(Long snapshotId) {
            this.snapshotId = snapshotId;
            return this;
        }

        public Builder setSchemaId(Long schemaId) {
            this.schemaId = schemaId;
            return this;
        }

        public Builder setCommitIdentifier(Long commitIdentifier) {
            this.commitIdentifier = commitIdentifier;
            return this;
        }

        public Builder setCommitTime(LocalDateTime commitTime) {
            this.commitTime = commitTime;
            return this;
        }

        public SnapshotVO build() {
            return new SnapshotVO(snapshotId, schemaId, commitIdentifier, commitTime);
        }
    }
}
