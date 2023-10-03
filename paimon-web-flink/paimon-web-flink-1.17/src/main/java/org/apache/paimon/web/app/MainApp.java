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

package org.apache.paimon.web.app;

import org.apache.paimon.web.flink.client.db.DBConfig;
import org.apache.paimon.web.flink.client.util.FlinkJobConfUtil;
import org.apache.paimon.web.flink.common.ExecutionMode;
import org.apache.paimon.web.flink.config.FlinkJobConfiguration;
import org.apache.paimon.web.flink.submit.FlinkJobSubmitter;

import cn.hutool.json.JSONUtil;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.configuration.CheckpointingOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/** User jar package startup class. */
public class MainApp {
    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) {
        InputStream inputStream =
                MainApp.class.getClassLoader().getResourceAsStream("application.properties");
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
            DBConfig dbConfig = DBConfig.build(properties);
            Integer id = Integer.valueOf(args[0]);
            Map<String, String> taskConfig = FlinkJobConfUtil.getJobTaskConfig(id, dbConfig);
            if (taskConfig == null || taskConfig.isEmpty()) {
                logger.error(
                        "get flink job task info error! job info is Empty! Check whether the flink job task with the id {} exists",
                        id);
                return;
            }
            FlinkJobConfiguration jobConfig = new FlinkJobConfiguration();
            Map<String, String> config = new HashMap<>();
            String jobName = taskConfig.get("job_name");
            String checkpointPath = taskConfig.get("checkpoint_path");
            if (checkpointPath != null && !"".equals(checkpointPath.trim())) {
                // enable Checkpoint
                config.put("execution.checkpointing.enabled", "true");
                // Set the default Checkpoint interval to 10 minute
                String interval = taskConfig.get("checkpoint_interval");
                if (interval == null || "".equals(interval.trim())) {
                    interval = "600000";
                }
                config.put("execution.checkpointing.interval", interval);
                config.put(CheckpointingOptions.CHECKPOINTS_DIRECTORY.key(), checkpointPath);
            }
            String savepointPath = taskConfig.get("savepoint_path");
            if (savepointPath != null && !"".equals(savepointPath.trim())) {
                config.put(CheckpointingOptions.SAVEPOINT_DIRECTORY.key(), savepointPath);
            }
            String parallelism = taskConfig.get("parallelism");
            if (parallelism == null || "".equals(parallelism.trim())) {
                parallelism = "1";
            }
            config.put("parallelism.default", parallelism);
            String runtimeMode = taskConfig.get("execution_runtime_mode");
            if (runtimeMode != null
                    && runtimeMode.equalsIgnoreCase(RuntimeExecutionMode.BATCH.toString())) {
                jobConfig.setExecutionMode(ExecutionMode.BATCH);
            } else {
                jobConfig.setExecutionMode(ExecutionMode.STREAMING);
            }
            jobConfig.setLocalMode(true);
            String otherParams = taskConfig.get("other_params");
            if (otherParams != null && !"".equals(otherParams.trim())) {
                Map<String, String> otherParamsMap = JSONUtil.parse(otherParams).toBean(Map.class);
                jobConfig.setTaskConfig(otherParamsMap);
            }
            FlinkJobSubmitter submitter = new FlinkJobSubmitter(jobConfig);
            submitter.submitJob(taskConfig.get("flink_sql"));
            submitter.getEnvironment().execute(jobName);
        } catch (Exception e) {
            logger.error("org.apache.paimon.web.app.MainApp run error:", e);
        }
    }
}
