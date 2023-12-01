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

import org.apache.paimon.web.common.executor.Executor;
import org.apache.paimon.web.common.executor.ExecutorFactory;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.internal.StreamTableEnvironmentImpl;

/** Factory class for creating executors that interface with the Flink Table API. */
public class FlinkExecutorFactory implements ExecutorFactory {

    private final RuntimeExecutionMode mode;

    private final Configuration configuration;

    public FlinkExecutorFactory(RuntimeExecutionMode mode, Configuration configuration) {
        this.mode = mode;
        this.configuration = configuration;
    }

    @Override
    public Executor createExecutor() {
        EnvironmentSettings environmentSettings = getEnvironmentSettings(mode);
        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment(configuration);
        TableEnvironment tableEvn = StreamTableEnvironmentImpl.create(env, environmentSettings);
        return new FlinkExecutor(env, tableEvn);
    }

    private EnvironmentSettings getEnvironmentSettings(RuntimeExecutionMode mode) {
        return mode == RuntimeExecutionMode.BATCH
                ? EnvironmentSettings.newInstance().inBatchMode().build()
                : EnvironmentSettings.newInstance().inStreamingMode().build();
    }
}
