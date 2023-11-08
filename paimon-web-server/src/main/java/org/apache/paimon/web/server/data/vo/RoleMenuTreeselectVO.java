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

import org.apache.paimon.web.server.data.tree.TreeSelect;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

/** Role Menu List Tree Vo. */
@Getter
public class RoleMenuTreeselectVO {
    private final List<Integer> checkedKeys;
    private final List<TreeSelect> menus;

    public RoleMenuTreeselectVO(
            @JsonProperty("checkedKeys") List<Integer> checkedKeys,
            @JsonProperty("menus") List<TreeSelect> menus) {
        this.checkedKeys = checkedKeys;
        this.menus = menus;
    }
}
