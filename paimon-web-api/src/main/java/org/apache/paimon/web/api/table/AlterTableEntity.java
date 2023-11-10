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

package org.apache.paimon.web.api.table;

import org.apache.paimon.schema.SchemaChange;
import org.apache.paimon.types.DataType;
import org.apache.paimon.web.api.common.OperatorKind;

import javax.annotation.Nullable;

/** alter table entity. */
public class AlterTableEntity {

    private final String columnName;
    private final DataType type;
    private final String comment;
    private final String newColumn;
    private final boolean isNullable;
    private final SchemaChange.Move move;
    private final OperatorKind kind;

    public AlterTableEntity(
            String columnName,
            DataType type,
            String comment,
            String newColumn,
            boolean isNullable,
            SchemaChange.Move move,
            OperatorKind kind) {
        this.columnName = columnName;
        this.type = type;
        this.comment = comment;
        this.newColumn = newColumn;
        this.isNullable = isNullable;
        this.move = move;
        this.kind = kind;
    }

    public String getColumnName() {
        return columnName;
    }

    public DataType getType() {
        return type;
    }

    public String getComment() {
        return comment;
    }

    public String getNewColumn() {
        return newColumn;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public SchemaChange.Move getMove() {
        return move;
    }

    public OperatorKind getKind() {
        return kind;
    }

    public static AlterTableEntity.Builder builder() {
        return new Builder();
    }

    /** The builder for AlterTableEntity. */
    public static class Builder {
        private String columnName;
        @Nullable private DataType type;
        @Nullable private String comment;
        @Nullable private String newColumn;
        @Nullable private boolean isNullable;
        @Nullable private SchemaChange.Move move;
        private OperatorKind kind;

        public Builder columnName(String columnName) {
            this.columnName = columnName;
            return this;
        }

        public Builder type(DataType type) {
            this.type = type;
            return this;
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder newColumn(String newColumn) {
            this.newColumn = newColumn;
            return this;
        }

        public Builder nullable(boolean isNullable) {
            this.isNullable = isNullable;
            return this;
        }

        public Builder move(SchemaChange.Move move) {
            this.move = move;
            return this;
        }

        public Builder kind(OperatorKind kind) {
            this.kind = kind;
            return this;
        }

        public AlterTableEntity build() {
            return new AlterTableEntity(
                    columnName, type, comment, newColumn, isNullable, move, kind);
        }
    }
}
