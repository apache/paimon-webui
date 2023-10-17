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

import org.apache.paimon.web.flink.TestBase;
import org.apache.paimon.web.flink.common.ExecutionMode;
import org.apache.paimon.web.flink.config.FlinkJobConfiguration;
import org.apache.paimon.web.flink.handler.FlinkSubmitHandler;
import org.apache.paimon.web.flink.job.FlinkJobResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** The test of {@link FlinkSubmitHandler}. */
public class FlinkSubmitHandlerTest extends TestBase {

    private FlinkSubmitHandler handler;

    @BeforeEach
    public void setup() {
        FlinkJobConfiguration.Builder builder = FlinkJobConfiguration.builder();
        FlinkJobConfiguration jobConfig =
                builder.executionTarget("local").executionMode(ExecutionMode.STREAMING).build();
        handler = FlinkSubmitHandler.build(jobConfig);
        handler.init();
    }

    @Test
    public void testSubmitJob() {
        FlinkJobResult result = handler.handle(handleStatement);
        System.out.println(result.getJobId());
        System.out.println(result.getStatus());
        assertThat(result).isNotNull();
        assertThat(result.getJobId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo("RUNNING");
    }
}
