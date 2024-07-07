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

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import javax.validation.constraints.NotBlank;

import java.util.Map;

/** DTO of catalog. */
@Data
public class CatalogDTO {

    private Integer id;

    @NotBlank(message = "invalid.catalogType")
    private String type;

    @NotBlank(message = "invalid.catalogName")
    private String name;

    @NotBlank(message = "invalid.warehouseDir")
    private String warehouse;

    private Map<String, String> options;

    @TableLogic private boolean isDelete;

    public String getHiveConfDir() {
        if (options == null) {
            return null;
        }
        return options.get("hiveConfDir");
    }

    public String getHiveUri() {
        if (options == null) {
            return null;
        }
        return options.get("hiveUri");
    }
}
