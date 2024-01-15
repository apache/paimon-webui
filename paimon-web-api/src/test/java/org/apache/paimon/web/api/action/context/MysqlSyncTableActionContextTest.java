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

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** The test class of mysql sync table action context in {@link MysqlSyncTableActionContext}. */
public class MysqlSyncTableActionContextTest {

    private static final String warehouse = "warehouse";

    private static final String database = "database";

    private static final String table = "table";

    @Test
    public void testBuild() {
        List<String> commands =
                MysqlSyncTableActionContext.builder()
                        .warehouse(warehouse)
                        .database(database)
                        .table(table)
                        .build()
                        .getCommand();
        List<String> expectedCommands =
                Arrays.asList("--warehouse", warehouse, "--database", database, "--table", table);
        assertLinesMatch(expectedCommands, commands);
    }

    @Test
    public void testBuildConf() {
        List<String> commands =
                MysqlSyncTableActionContext.builder()
                        .warehouse(warehouse)
                        .database(database)
                        .table(table)
                        .partitionKeys("pt")
                        .mysqlConf("table-name='source_table'")
                        .mysqlConf("database-name='source_db'")
                        .mysqlConf("password=123456")
                        .catalogConf("metastore=hive")
                        .catalogConf("uri=thrift://hive-metastore:9083")
                        .tableConf("bucket=4")
                        .tableConf("changelog-producer=input")
                        .build()
                        .getCommand();
        List<String> expectedCommands =
                Arrays.asList(
                        "--warehouse",
                        warehouse,
                        "--database",
                        database,
                        "--table",
                        table,
                        "--partition_keys",
                        "pt",
                        "--mysql_conf",
                        "table-name='source_table'",
                        "--mysql_conf",
                        "database-name='source_db'",
                        "--mysql_conf",
                        "password=123456",
                        "--catalog_conf",
                        "metastore=hive",
                        "--catalog_conf",
                        "uri=thrift://hive-metastore:9083",
                        "--table_conf",
                        "bucket=4",
                        "--table_conf",
                        "changelog-producer=input");
        assertLinesMatch(expectedCommands, commands);
    }

    @Test
    public void testBuildError() {
        MysqlSyncTableActionContext mysqlSyncTableActionContext =
                MysqlSyncTableActionContext.builder()
                        .warehouse(warehouse)
                        .database(database)
                        .partitionKeys("pt")
                        .primaryKeys("pt,uid")
                        .mysqlConf("table-name='source_table'")
                        .mysqlConf("database-name='source_db.+'")
                        .mysqlConf("password=123456")
                        .catalogConf("metastore=hive")
                        .catalogConf("uri=thrift://hive-metastore:9083")
                        .tableConf("bucket=4")
                        .tableConf("changelog-producer=input")
                        .build();
        ActionException actionException =
                assertThrows(ActionException.class, mysqlSyncTableActionContext::getCommand);
        assertEquals("warehouse、database、table can not be null", actionException.getMessage());
    }
}
