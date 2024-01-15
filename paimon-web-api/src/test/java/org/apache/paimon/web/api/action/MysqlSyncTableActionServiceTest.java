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

package org.apache.paimon.web.api.action;

import org.apache.paimon.web.api.action.context.MysqlSyncTableActionContext;
import org.apache.paimon.web.api.action.service.MysqlSyncTableActionService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;

/** The test class of mysql sync table action service in {@link MysqlSyncTableActionService}. */
public class MysqlSyncTableActionServiceTest {

    @TempDir private Path flinkHomePath;

    @TempDir private Path actionPath;

    @BeforeEach
    public void setUp() {
        System.setProperty("FLINK_HOME", getFlinkHome());
        System.setProperty("ACTION_PATH", getActionPath());
    }

    @AfterEach
    public void tearDown() {
        System.clearProperty("FLINK_HOME");
        System.clearProperty("ACTION_PATH");
    }

    @Test
    public void testGetName() {
        assertEquals("mysql_sync_table", new MysqlSyncTableActionService().name());
    }

    private String getActionPath() {
        return actionPath.toUri().toString();
    }

    private String getFlinkHome() {
        return flinkHomePath.toUri().toString();
    }

    @Test
    public void testBuildCommand() {
        MysqlSyncTableActionContext actionContext =
                MysqlSyncTableActionContext.builder()
                        .warehouse("warehouse")
                        .table("table")
                        .database("database")
                        .build();
        List<String> commands = new MysqlSyncTableActionService().getCommand(actionContext);
        List<String> expectedCommands =
                Arrays.asList(
                        "${FLINK_HOME}/bin/flink",
                        "run",
                        getActionPath(),
                        "mysql_sync_table",
                        "--warehouse",
                        "warehouse",
                        "--database",
                        "database",
                        "--table",
                        "table");
        assertLinesMatch(expectedCommands, commands);
    }
}
