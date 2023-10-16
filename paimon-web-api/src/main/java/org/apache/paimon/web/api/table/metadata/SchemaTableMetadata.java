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
import java.util.Date;

/** schema table metadata. */
public class SchemaTableMetadata {

    private final Long schemaId;
    private final String fields;
    private final String partitionKeys;
    private final String primaryKeys;
    private final String options;
    private final String comment;

    public SchemaTableMetadata(
            Long schemaId,
            String fields,
            String partitionKeys,
            String primaryKeys,
            String options,
            String comment) {
        this.schemaId = schemaId;
        this.fields = fields;
        this.partitionKeys = partitionKeys;
        this.primaryKeys = primaryKeys;
        this.options = options;
        this.comment = comment;
    }

    public Long getSchemaId() {
        return schemaId;
    }

    public String getFields() {
        return fields;
    }

    public String getPartitionKeys() {
        return partitionKeys;
    }

    public String getPrimaryKeys() {
        return primaryKeys;
    }

    public String getOptions() {
        return options;
    }

    public String getComment() {
        return comment;
    }

    public static SchemaTableMetadata.Builder builder() {
        return new Builder();
    }

    /** The builder for SchemaTableMetadata. */
    public static final class Builder {
        private Long schemaId;
        private String fields;
        private String partitionKeys;
        private String primaryKeys;
        private String options;
        @Nullable private String comment;

        public Builder schemaId(Long schemaId) {
            this.schemaId = schemaId;
            return this;
        }

        public Builder fields(String fields) {
            this.fields = fields;
            return this;
        }

        public Builder partitionKeys(String partitionKeys) {
            this.partitionKeys = partitionKeys;
            return this;
        }

        public Builder primaryKeys(String primaryKeys) {
            this.primaryKeys = primaryKeys;
            return this;
        }

        public Builder options(String options) {
            this.options = options;
            return this;
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public SchemaTableMetadata build() {
            return new SchemaTableMetadata(
                    schemaId, fields, partitionKeys, primaryKeys, options, comment);
        }
    }
}
