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

package org.apache.paimon.web.flink.executor;

import org.apache.paimon.web.flink.TestBase;
import org.apache.paimon.web.flink.context.LocalExecutorContext;
import org.apache.paimon.web.flink.operation.FlinkSqlOperationType;
import org.apache.paimon.web.flink.operation.SqlCategory;
import org.apache.paimon.web.flink.parser.StatementParser;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.table.api.TableResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/** The test of {@link LocalExecutor}. */
public class LocalExecutorTest extends TestBase {

    private Executor executor;

    @BeforeEach
    public void before() {
        Configuration configuration = new Configuration();
        LocalExecutorContext context =
                new LocalExecutorContext(configuration, RuntimeExecutionMode.STREAMING);
        executor = new LocalExecutorFactory().createExecutor(context);
    }

    @Test
    public void testExecuteSql() {
        TableResult result = executor.executeSql(createStatement);
        assertThat(result).isNotNull();
    }

    @Test
    public void testExecuteStatementSet() {
        String[] statements = StatementParser.parse(statementSetSql);
        List<String> insertStatements = new ArrayList<>();
        for (String statement : statements) {
            FlinkSqlOperationType operationType = FlinkSqlOperationType.getOperationType(statement);
            if (operationType.getCategory() == SqlCategory.DDL) {
                executor.executeSql(statement);
            } else if (Objects.equals(
                    operationType.getType(), FlinkSqlOperationType.INSERT.getType())) {
                insertStatements.add(statement);
            }
        }
        TableResult result = executor.executeStatementSet(insertStatements);
        assertThat(result).isNotNull();
        assertThat(result.getJobClient()).isNotNull();
    }

    @Test
    public void testGetJobGraph() {
        String[] statements = StatementParser.parse(statementSetSql);
        List<String> insertStatements = new ArrayList<>();
        for (String statement : statements) {
            FlinkSqlOperationType operationType = FlinkSqlOperationType.getOperationType(statement);
            if (operationType.getCategory() == SqlCategory.DDL) {
                executor.executeSql(statement);
            } else if (Objects.equals(
                    operationType.getType(), FlinkSqlOperationType.INSERT.getType())) {
                insertStatements.add(statement);
            }
        }
        JobGraph jobGraph = executor.getJobGraph(insertStatements);
        assertThat(jobGraph).isNotNull();
    }
}
