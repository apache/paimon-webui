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

package org.apache.paimon.web.flink.context;

import org.apache.paimon.web.flink.common.ExecutionMode;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * The ApplicationExecutorContext class is an extension of the ExecutorContext, designed to work
 * with Flink applications running in YARN or Kubernetes application mode.
 *
 * <p>In this mode, Flink automatically identifies the runtime environment and returns the
 * appropriate StreamExecutionEnvironment instance.
 */
public class ApplicationExecutorContext extends ExecutorContext {

    public ApplicationExecutorContext(Configuration configuration, ExecutionMode mode) {
        super(configuration, mode);
    }

    @Override
    protected StreamExecutionEnvironment createEnvironment() {
        return StreamExecutionEnvironment.getExecutionEnvironment();
    }
}
