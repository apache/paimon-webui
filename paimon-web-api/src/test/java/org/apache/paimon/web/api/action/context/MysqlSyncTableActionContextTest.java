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

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** The test class of mysql sync table action context in {@link MysqlSyncTableActionContext}. */
public class MysqlSyncTableActionContextTest {

    private static final String warehouse = "warehouse";

    private static final String database = "database";

    private static final String table = "table";

    @Test
    public void testBuild() throws IOException {
        List<String> command =
                MysqlSyncTableActionContext.builder()
                        .warehouse(warehouse)
                        .database(database)
                        .table(table)
                        .build()
                        .getCommand();
        assertEquals(3, command.size());
        assertEquals(command.get(0), "--warehouse " + warehouse);
        assertEquals(command.get(1), "--database " + database);
        assertEquals(command.get(2), "--table " + table);
    }

    @Test
    public void testBuildConf() throws IOException {
        List<String> command =
                MysqlSyncTableActionContext.builder()
                        .warehouse(warehouse)
                        .database(database)
                        .table(table)
                        .partitionKeys("pt")
                        .primaryKeys("pt,uid")
                        .mysqlConf("table-name='source_table'")
                        .mysqlConf("database-name='source_db.+'")
                        .mysqlConf("password=123456")
                        .catalogConf("metastore=hive")
                        .catalogConf("uri=thrift://hive-metastore:9083")
                        .tableConf("bucket=4")
                        .tableConf("changelog-producer=input")
                        .build()
                        .getCommand();
        assertEquals(12, command.size());
        assertEquals("--warehouse " + warehouse, command.get(0));
        assertEquals("--database " + database, command.get(1));
        assertEquals("--table " + table, command.get(2));
        assertEquals("--partition_keys pt", command.get(3));
        assertEquals("--primary_keys pt,uid", command.get(4));
        assertEquals("--mysql_conf table-name='source_table'", command.get(5));
        assertEquals("--mysql_conf database-name='source_db.+'", command.get(6));
        assertEquals("--mysql_conf password=123456", command.get(7));
        assertEquals("--catalog_conf metastore=hive", command.get(8));
        assertEquals("--catalog_conf uri=thrift://hive-metastore:9083", command.get(9));
        assertEquals("--table_conf bucket=4", command.get(10));
        assertEquals("--table_conf changelog-producer=input", command.get(11));
    }

    @Test
    public void testBuildError() throws IOException {
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
