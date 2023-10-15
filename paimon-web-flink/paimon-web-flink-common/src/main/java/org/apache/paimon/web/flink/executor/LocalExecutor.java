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

import org.apache.calcite.sql.SqlInsert;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.dag.Transformation;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.api.bridge.java.internal.StreamTableEnvironmentImpl;
import org.apache.flink.table.api.internal.TableEnvironmentImpl;
import org.apache.flink.table.delegation.Parser;
import org.apache.flink.table.delegation.Planner;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.types.Row;
import org.apache.paimon.web.flink.context.ExecutorContext;
import org.apache.paimon.web.flink.context.LocalExecutorContext;

import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamStatementSet;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * The LocalExecutor class is an implementation of the Executor interface for local execution
 * environments.
 */
public class LocalExecutor implements Executor {

    private final StreamTableEnvironment tableEnv;
    private final ExecutorContext context;

    public LocalExecutor(LocalExecutorContext context) {
        tableEnv = context.getTableEnvironment();
        this.context = context;
    }

    @Override
    public TableResult executeSql(String statement) {
        return tableEnv.executeSql(statement);
    }

    @Override
    public TableResult executeStatementSet(List<String> statements) {
        StreamStatementSet statementSet = tableEnv.createStatementSet();
        statements.forEach(statementSet::addInsertSql);
        return statementSet.execute();
    }

    @Override
    public JobGraph getJobGraph(List<String> statements) {
        return getStreamGraph(context.getEnvironment(), tableEnv, statements).getJobGraph();
    }
}
