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

package org.apache.paimon.web.api.action.context;

import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

import static org.apache.paimon.web.api.action.context.ActionContextUtil.addConf;
import static org.apache.paimon.web.api.action.context.ActionContextUtil.addConfList;

/** Mysql sync table action context. */
@Builder
public class MysqlSyncTableActionContext implements ActionContext {

    private String warehouse;

    private String database;

    private String table;

    private String partitionKeys;

    private String primaryKeys;

    private List<String> mysqlConfList;

    private List<String> catalogConfList;

    private List<String> tableConfList;

    private MysqlSyncTableActionContext() {}

    @Override
    public List<String> getCommand() {
        List<String> command = new ArrayList<>();
        addConf(command, "warehouse", warehouse);
        addConf(command, "database", database);
        addConf(command, "table", table);
        addConf(command, "partition_keys", partitionKeys);
        addConf(command, "primary_keys", primaryKeys);
        addConfList(command, "mysql_conf", mysqlConfList);
        addConfList(command, "catalog_conf", catalogConfList);
        addConfList(command, "table_conf", tableConfList);
        return command;
    }
}
