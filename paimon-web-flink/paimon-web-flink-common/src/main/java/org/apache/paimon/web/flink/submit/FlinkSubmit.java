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

import org.apache.paimon.web.flink.submit.request.SubmitRequest;
import org.apache.paimon.web.flink.submit.result.SubmitResult;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.yarn.configuration.YarnConfigOptions;

import java.util.Collections;

/** This abstract class provides a base implementation for submitting Flink jobs. */
public abstract class FlinkSubmit {

    protected Configuration configuration;

    protected SubmitRequest request;

    public FlinkSubmit(SubmitRequest request) {
        this.request = request;
        buildConf();
    }

    public SubmitResult submit() {
        init();
        return doSubmit();
    }

    protected abstract void init();

    protected abstract SubmitResult doSubmit();

    protected abstract void buildPlatformSpecificConf();

    protected void buildConf() {
        configuration = GlobalConfiguration.loadConfiguration(request.getFlinkConfigPath());
        if (MapUtils.isNotEmpty(request.getFlinkConfigMap())) {
            request.getFlinkConfigMap()
                    .forEach(
                            (K, v) -> {
                                if (K != null
                                        && K.trim().length() > 0
                                        && v != null
                                        && v.trim().length() > 0) {
                                    configuration.setString(K, v);
                                }
                            });
        }

        configuration.set(DeploymentOptions.TARGET, request.getExecutionTarget());
        if (StringUtils.isNotBlank(request.getSavepointPath())) {
            configuration.setString(
                    SavepointConfigOptions.SAVEPOINT_PATH, request.getSavepointPath().trim());
        }

        if (StringUtils.isNotBlank(request.getCheckpointPath())) {
            // enable Checkpoint
            configuration.setString("execution.checkpointing.enabled", "true");
            // Set the Checkpoint interval to 10 minute
            configuration.setString(
                    "execution.checkpointing.interval", request.getCheckpointInterval());
            configuration.setString(
                    CheckpointingOptions.CHECKPOINTS_DIRECTORY.key(), request.getCheckpointPath());
        }

        configuration.set(
                YarnConfigOptions.PROVIDED_LIB_DIRS,
                Collections.singletonList(request.getFlinkLibPath()));
        configuration.set(YarnConfigOptions.APPLICATION_NAME, request.getJobName());

        buildPlatformSpecificConf();
    }
}
