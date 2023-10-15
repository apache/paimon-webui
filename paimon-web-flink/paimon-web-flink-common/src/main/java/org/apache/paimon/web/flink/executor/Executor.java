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

import org.apache.flink.api.dag.Transformation;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.bridge.java.internal.StreamTableEnvironmentImpl;
import org.apache.flink.table.delegation.Parser;
import org.apache.flink.table.delegation.Planner;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/** The Executor interface. */
public interface Executor {

    TableResult executeSql(String statement);

    TableResult executeStatementSet(List<String> statements);

    JobGraph getJobGraph(List<String> statements);

    default StreamGraph getStreamGraph(StreamExecutionEnvironment env, StreamTableEnvironment tableEnv, List<String> statements) {
        try {
            Method getParserMethod = StreamTableEnvironment.class.getDeclaredMethod("getParser");
            getParserMethod.setAccessible(true);
            Parser parser = (Parser) getParserMethod.invoke(tableEnv);

            List<ModifyOperation> modifyOperations = new ArrayList<>();
            for (String statement : statements) {
                List<Operation> operations = parser.parse(statement);
                if (operations.size() != 1) {
                    throw new TableException("Single statement required.");
                } else {
                    Operation operation = operations.get(0);
                    if (operation instanceof ModifyOperation) {
                        modifyOperations.add((ModifyOperation) operation);
                    } else {
                        throw new TableException("INSERT statement required.");
                    }
                }
            }

            Planner planner = ((StreamTableEnvironmentImpl) tableEnv).getPlanner();
            List<Transformation<?>> transformations = planner.translate(modifyOperations);
            for (Transformation<?> transformation : transformations) {
                env.addOperator(transformation);
            }

            StreamGraph streamGraph = env.getStreamGraph();
            if (tableEnv.getConfig().getConfiguration().containsKey(PipelineOptions.NAME.key())) {
                streamGraph.setJobName(tableEnv.getConfig().getConfiguration().getString(PipelineOptions.NAME));
            }

            return streamGraph;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
