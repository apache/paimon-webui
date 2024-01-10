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

import org.apache.paimon.web.api.exception.ActionException;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/** Mysql sync table action context. */
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

    public static MysqlSyncTableActionContext builder() {
        return new MysqlSyncTableActionContext();
    }

    public MysqlSyncTableActionContext warehouse(String warehouse) {
        this.warehouse = warehouse;
        return this;
    }

    public MysqlSyncTableActionContext database(String database) {
        this.database = database;
        return this;
    }

    public MysqlSyncTableActionContext table(String table) {
        this.table = table;
        return this;
    }

    public MysqlSyncTableActionContext partitionKeys(String partitionKeys) {
        this.partitionKeys = partitionKeys;
        return this;
    }

    public MysqlSyncTableActionContext primaryKeys(String primaryKeys) {
        this.primaryKeys = primaryKeys;
        return this;
    }

    public MysqlSyncTableActionContext mysqlConf(String mysqlConf) {
        if (mysqlConfList == null) {
            mysqlConfList = new ArrayList<>();
        }
        mysqlConfList.add(mysqlConf);
        return this;
    }

    public MysqlSyncTableActionContext catalogConf(String catalogConf) {
        if (catalogConfList == null) {
            catalogConfList = new ArrayList<>();
        }
        catalogConfList.add(catalogConf);
        return this;
    }

    public MysqlSyncTableActionContext tableConf(String tableConf) {
        if (tableConfList == null) {
            tableConfList = new ArrayList<>();
        }
        tableConfList.add(tableConf);
        return this;
    }

    @Override
    public List<String> getCommand() {
        List<String> command = new ArrayList<>();
        if (StringUtils.isAnyBlank(warehouse, database, table)) {
            throw new ActionException("warehouse、database、table can not be null");
        }
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
