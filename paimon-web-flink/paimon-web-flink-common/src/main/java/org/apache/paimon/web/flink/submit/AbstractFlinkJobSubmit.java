/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.paimon.web.flink.submit;

import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.yarn.configuration.YarnConfigOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** flink job task submit abstract class. */
public abstract class AbstractFlinkJobSubmit implements FlinkJobSubmit {
    protected Configuration configuration;
    /** configuration info. */
    protected Map<String, Object> config = new HashMap<>();
    /** flink configuration others info. */
    protected Map<String, String> flinkConfigMap = new HashMap<>();

    @Override
    public void buildConf(Map<String, Object> config, Map<String, String> flinkConfigMap) {
        configuration =
                GlobalConfiguration.loadConfiguration(config.get("flinkConfigPath").toString());
        if (flinkConfigMap != null) {
            flinkConfigMap.forEach(
                    (K, v) -> {
                        if (K != null
                                && K.trim().length() > 0
                                && v != null
                                && v.trim().length() > 0) {
                            configuration.setString(K, v);
                        }
                    });
        }
        configuration.set(DeploymentOptions.TARGET, config.get("executionTarget").toString());
        if (config.get("savepointPath") != null
                && !"".equals(config.get("savepointPath").toString())) {
            configuration.setString(
                    SavepointConfigOptions.SAVEPOINT_PATH,
                    config.get("savepointPath").toString().trim());
        }

        if (config.get("checkpointPath") != null
                && !"".equals(config.get("checkpointPath").toString())) {
            // enable Checkpoint
            configuration.setString("execution.checkpointing.enabled", "true");
            // Set the Checkpoint interval to 10 minute
            configuration.setString(
                    "execution.checkpointing.interval",
                    config.get("checkpointInterval").toString());
            configuration.setString(
                    CheckpointingOptions.CHECKPOINTS_DIRECTORY.key(),
                    config.get("checkpointPath").toString());
        }
        configuration.set(
                YarnConfigOptions.PROVIDED_LIB_DIRS,
                Collections.singletonList(config.get("flinkLibPath").toString()));
        configuration.set(YarnConfigOptions.APPLICATION_NAME, config.get("jobName").toString());

        String hadoopConfigPath = config.get("hadoopConfigPath").toString();
        configuration.setString("fs.hdfs.hadoopconf", hadoopConfigPath);

        this.config.putAll(config);
        this.flinkConfigMap.putAll(flinkConfigMap);
    }
}
