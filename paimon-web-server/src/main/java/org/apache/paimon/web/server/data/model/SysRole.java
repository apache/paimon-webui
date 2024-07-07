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

package org.apache.paimon.web.server.data.model;

import org.apache.paimon.web.server.constant.Constants;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import java.util.Set;

/** sys_role. */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysRole extends BaseModel {
    /** role name. */
    @NotBlank(message = "invalid.roleName")
    private String roleName;

    /** role key. */
    @NotBlank(message = "invalid.roleKey")
    private String roleKey;

    /** sort. */
    private Integer sort;

    /** is enable. */
    private Boolean enabled;

    /** is delete. */
    @TableLogic private Boolean isDelete;

    /** remark. */
    private String remark;

    /** Does the user have this role identity. Default false. */
    @TableField(exist = false)
    private boolean flag = false;

    /** menu ids. */
    @TableField(exist = false)
    @NotEmpty(message = "invalid.menuIds")
    private Integer[] menuIds;

    /** Role menu permissions. */
    @TableField(exist = false)
    private Set<String> permissions;

    public boolean isAdmin() {
        return isAdmin(this.getId());
    }

    public static boolean isAdmin(Integer roleId) {
        return roleId != null && Constants.ADMIN_ID == roleId;
    }

    private static final long serialVersionUID = 1L;
}
