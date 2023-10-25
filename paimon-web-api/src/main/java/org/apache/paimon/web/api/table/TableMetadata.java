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

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** table metadata. */
public class TableMetadata {

    private final List<ColumnMetadata> columns;

    private final List<String> partitionKeys;

    private final List<String> primaryKeys;

    private final Map<String, String> options;

    private final String comment;

    public TableMetadata(
            List<ColumnMetadata> columns,
            List<String> partitionKeys,
            List<String> primaryKeys,
            Map<String, String> options,
            String comment) {
        this.columns = normalizeFields(columns, primaryKeys, partitionKeys);
        this.partitionKeys = partitionKeys;
        this.primaryKeys = primaryKeys;
        this.options = new HashMap<>(options);
        this.comment = comment;
    }

    public List<ColumnMetadata> columns() {
        return columns;
    }

    public List<String> partitionKeys() {
        return partitionKeys;
    }

    public List<String> primaryKeys() {
        return primaryKeys;
    }

    public Map<String, String> options() {
        return options;
    }

    public String comment() {
        return comment;
    }

    private static List<ColumnMetadata> normalizeFields(
            List<ColumnMetadata> columns, List<String> primaryKeys, List<String> partitionKeys) {
        List<String> fieldNames =
                columns.stream().map(ColumnMetadata::name).collect(Collectors.toList());

        Set<String> duplicateColumns = duplicate(fieldNames);
        Preconditions.checkState(
                duplicateColumns.isEmpty(),
                "Table column %s must not contain duplicate fields. Found: %s",
                fieldNames,
                duplicateColumns);

        Set<String> allFields = new HashSet<>(fieldNames);

        duplicateColumns = duplicate(partitionKeys);
        Preconditions.checkState(
                duplicateColumns.isEmpty(),
                "Partition key constraint %s must not contain duplicate columns. Found: %s",
                partitionKeys,
                duplicateColumns);
        Preconditions.checkState(
                allFields.containsAll(partitionKeys),
                "Table column %s should include all partition fields %s",
                fieldNames,
                partitionKeys);

        if (primaryKeys.isEmpty()) {
            return columns;
        }
        duplicateColumns = duplicate(primaryKeys);
        Preconditions.checkState(
                duplicateColumns.isEmpty(),
                "Primary key constraint %s must not contain duplicate columns. Found: %s",
                primaryKeys,
                duplicateColumns);
        Preconditions.checkState(
                allFields.containsAll(primaryKeys),
                "Table column %s should include all primary key constraint %s",
                fieldNames,
                primaryKeys);
        Set<String> pkSet = new HashSet<>(primaryKeys);
        Preconditions.checkState(
                pkSet.containsAll(partitionKeys),
                "Primary key constraint %s should include all partition fields %s",
                primaryKeys,
                partitionKeys);

        // primary key should not nullable
        List<ColumnMetadata> newFields = new ArrayList<>();
        for (ColumnMetadata field : columns) {
            if (pkSet.contains(field.name()) && field.type().isNullable()) {
                newFields.add(
                        new ColumnMetadata(
                                field.name(), field.type().copy(false), field.description()));
            } else {
                newFields.add(field);
            }
        }
        return newFields;
    }

    private static Set<String> duplicate(List<String> names) {
        return names.stream()
                .filter(name -> Collections.frequency(names, name) > 1)
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "TableMetadata{"
                + "columns="
                + columns
                + ", partitionKeys="
                + partitionKeys
                + ", primaryKeys="
                + primaryKeys
                + ", options="
                + options
                + ", comment='"
                + comment
                + '\''
                + '}';
    }

    public static TableMetadata.Builder builder() {
        return new Builder();
    }

    /** The builder for TableMetadata. */
    public static final class Builder {
        private List<ColumnMetadata> columns = new ArrayList<>();

        private List<String> partitionKeys = new ArrayList<>();

        private List<String> primaryKeys = new ArrayList<>();

        @Nullable private Map<String, String> options = new HashMap<>();

        @Nullable private String comment;

        public Builder columns(List<ColumnMetadata> columns) {
            this.columns = new ArrayList<>(columns);
            return this;
        }

        public Builder partitionKeys(List<String> partitionKeys) {
            this.partitionKeys = new ArrayList<>(partitionKeys);
            return this;
        }

        public Builder primaryKeys(List<String> primaryKeys) {
            this.primaryKeys = new ArrayList<>(primaryKeys);
            return this;
        }

        public Builder options(Map<String, String> options) {
            this.options.putAll(options);
            return this;
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public TableMetadata build() {
            return new TableMetadata(columns, partitionKeys, primaryKeys, options, comment);
        }
    }
}
