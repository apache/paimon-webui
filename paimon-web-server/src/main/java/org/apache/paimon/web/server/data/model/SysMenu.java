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

package org.apache.paimon.web.server.data.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/** sys_menu table. */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysMenu extends BaseModel {
    /** menu name. */
    private String menuName;

    /** parent id. */
    private Integer parentId;

    /** sort. */
    private Integer sort;

    /** route path. */
    private String path;

    /** route params. */
    private String query;

    /** is cache（0:cache 1:no_cache）. */
    private Integer isCache;

    /** menu type（M:directory C:menu F:button）. */
    private String type;

    /** is visible（0:display 1:hide）. */
    private String visible;

    /** component path. */
    private String component;

    /** is frame. */
    private Integer isFrame;

    /** is enable. */
    private Boolean enabled;

    /** is delete. */
    private Boolean isDelete;

    /** menu perms. */
    private String perms;

    /** menu icon. */
    private String icon;

    /** remark. */
    private String remark;

    /** children menu. */
    private List<SysMenu> children = new ArrayList<SysMenu>();

    private static final long serialVersionUID = 1L;
}
