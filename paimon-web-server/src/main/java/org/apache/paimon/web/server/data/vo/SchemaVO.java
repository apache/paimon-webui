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

package org.apache.paimon.web.server.data.vo;

import org.apache.paimon.web.server.data.model.MetadataFieldsModel;
import org.apache.paimon.web.server.data.model.MetadataOptionModel;

import javax.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/** VO of metadata schema. */
public class SchemaVO {

    private final Long schemaId;
    private final List<MetadataFieldsModel> fields;
    private final String partitionKeys;
    private final String primaryKeys;
    private final String comment;
    private final List<MetadataOptionModel> option;
    private final LocalDateTime updateTime;

    public SchemaVO(
            Long schemaId,
            List<MetadataFieldsModel> fields,
            String partitionKeys,
            String primaryKeys,
            String comment,
            List<MetadataOptionModel> option,
            LocalDateTime updateTime) {
        this.schemaId = schemaId;
        this.fields = fields;
        this.partitionKeys = partitionKeys;
        this.primaryKeys = primaryKeys;
        this.comment = comment;
        this.option = option;
        this.updateTime = updateTime;
    }

    public Long getSchemaId() {
        return schemaId;
    }

    public List<MetadataFieldsModel> getFields() {
        return fields;
    }

    public String getPartitionKeys() {
        return partitionKeys;
    }

    public String getPrimaryKeys() {
        return primaryKeys;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public List<MetadataOptionModel> getOption() {
        return option;
    }

    public static SchemaVO.Builder builder() {
        return new Builder();
    }

    /** Builder for SchemaInfoVo. */
    public static class Builder {
        private Long schemaId;
        private List<MetadataFieldsModel> fields;
        private String partitionKeys;
        private String primaryKeys;
        @Nullable private String comment;
        @Nullable private List<MetadataOptionModel> option;
        private LocalDateTime updateTime;

        public Builder setSchemaId(Long schemaId) {
            this.schemaId = schemaId;
            return this;
        }

        public Builder setFields(List<MetadataFieldsModel> fields) {
            this.fields = fields;
            return this;
        }

        public Builder setPartitionKeys(String partitionKeys) {
            this.partitionKeys = partitionKeys;
            return this;
        }

        public Builder setPrimaryKeys(String primaryKeys) {
            this.primaryKeys = primaryKeys;
            return this;
        }

        public Builder setComment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder setOption(List<MetadataOptionModel> option) {
            this.option = option;
            return this;
        }

        public Builder setUpdateTime(LocalDateTime updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public SchemaVO build() {
            return new SchemaVO(
                    schemaId, fields, partitionKeys, primaryKeys, comment, option, updateTime);
        }
    }
}
