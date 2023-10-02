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

package org.apache.paimon.web.flink.submit;

import org.apache.paimon.web.flink.config.FlinkJobConfiguration;
import org.apache.paimon.web.flink.context.ExecutorContext;
import org.apache.paimon.web.flink.context.LocalExecutorContext;
import org.apache.paimon.web.flink.context.RemoteExecutorContext;
import org.apache.paimon.web.flink.context.params.RemoteParams;
import org.apache.paimon.web.flink.executor.Executor;
import org.apache.paimon.web.flink.executor.ExecutorFactory;
import org.apache.paimon.web.flink.executor.LocalExecutorFactory;
import org.apache.paimon.web.flink.executor.RemoteExecutorFactory;
import org.apache.paimon.web.flink.handler.SqlTaskHandler;
import org.apache.paimon.web.flink.job.FlinkJobResult;

import org.apache.flink.configuration.Configuration;

import java.util.Map;

/**
 * FlinkJobSubmitter is a class that creates an Executor and a SqlTaskHandler and submits a Flink
 * job. It decides whether to create a LocalExecutor or a RemoteExecutor, and whether to use
 * streaming mode or batch mode based on the FlinkJobConfiguration.
 */
public class FlinkJobSubmitter {

    private final SqlTaskHandler sqlTaskHandler;

    public FlinkJobSubmitter(FlinkJobConfiguration jobConfig) {
        ExecutorFactory executorFactory;
        ExecutorContext context;

        Configuration configuration = new Configuration();
        if (jobConfig.getTaskConfig() != null) {
            for (Map.Entry<String, String> entry : jobConfig.getTaskConfig().entrySet()) {
                configuration.setString(entry.getKey(), entry.getValue());
            }
        }

        if (jobConfig.isLocalMode()) {
            executorFactory = new LocalExecutorFactory();
            context = new LocalExecutorContext(configuration, jobConfig.getExecutionMode());
        } else {
            executorFactory = new RemoteExecutorFactory();
            RemoteParams remoteParams =
                    new RemoteParams(
                            jobConfig.getHost(), jobConfig.getPort(), jobConfig.getJarFilePath());
            context =
                    new RemoteExecutorContext(
                            remoteParams, configuration, jobConfig.getExecutionMode());
        }

        Executor executor = executorFactory.createExecutor(context);

        sqlTaskHandler = new SqlTaskHandler(executor);
    }

    /**
     * Submit a Flink job with the given SQL statement.
     *
     * @param sql the SQL statement of the job.
     * @return the result of the job.
     */
    public FlinkJobResult submitJob(String sql) {
        return sqlTaskHandler.handle(sql);
    }
}
