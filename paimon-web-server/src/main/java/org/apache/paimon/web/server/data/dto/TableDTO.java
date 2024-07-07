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

package org.apache.paimon.web.server.data.dto;

import org.apache.paimon.web.server.data.model.TableColumn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

/** DTO of table. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableDTO {
    @NotNull(message = "invalid.catalogId")
    private Integer catalogId;

    private String catalogName;

    @NotBlank(message = "invalid.databaseName")
    private String databaseName;

    @NotBlank(message = "invalid.tableName")
    private String name;

    private String description;

    private List<TableColumn> tableColumns;

    private List<String> partitionKey;

    private Map<String, String> tableOptions;
}
