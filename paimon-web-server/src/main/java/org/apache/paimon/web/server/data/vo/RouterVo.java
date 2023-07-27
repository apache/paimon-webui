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

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** route config info. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouterVo {
    /** route name. */
    private String name;

    /** route path. */
    private String path;

    /**
     * Do you want to hide the route? When true is set, the route will no longer appear in the
     * sidebar.
     */
    private boolean hidden;

    /**
     * Redirect address. When setting noRedirect, the route cannot be clicked in Breadcrumb
     * navigation.
     */
    private String redirect;

    /** component path. */
    private String component;

    /** route query params：eg. {"id": 1, "name": "xx"}. */
    private String query;

    /**
     * When there are more than one routes declared by children under a route, it will
     * automatically. become nested mode - such as component pages
     */
    private Boolean alwaysShow;

    /** other meta info. */
    private MetaVo meta;

    /** children route. */
    private List<RouterVo> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean getHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Boolean getAlwaysShow() {
        return alwaysShow;
    }

    public void setAlwaysShow(Boolean alwaysShow) {
        this.alwaysShow = alwaysShow;
    }

    public MetaVo getMeta() {
        return meta;
    }

    public void setMeta(MetaVo meta) {
        this.meta = meta;
    }

    public List<RouterVo> getChildren() {
        return children;
    }

    public void setChildren(List<RouterVo> children) {
        this.children = children;
    }
}
