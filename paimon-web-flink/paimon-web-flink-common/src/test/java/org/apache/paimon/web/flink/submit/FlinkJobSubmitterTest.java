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

import org.apache.paimon.web.flink.common.ExecutionMode;
import org.apache.paimon.web.flink.config.FlinkJobConfiguration;
import org.apache.paimon.web.flink.job.FlinkJobResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/** This class tests the functionality of the FlinkJobSubmitter class. */
public class FlinkJobSubmitterTest {

    private FlinkJobSubmitter submitter;

    @BeforeEach
    public void setup() {
        FlinkJobConfiguration jobConfig = new FlinkJobConfiguration();
        Map<String, String> config = new HashMap<>();
        config.put("parallelism.default", "2");
        jobConfig.setTaskConfig(config);
        jobConfig.setLocalMode(true);
        jobConfig.setExecutionMode(ExecutionMode.STREAMING);
        submitter = new FlinkJobSubmitter(jobConfig);
    }

    @Test
    public void testSubmitJob() {
        String statement =
                "CREATE TABLE IF NOT EXISTS t_order(\n"
                        + "    --订单id\n"
                        + "    `order_id` BIGINT,\n"
                        + "    --产品\n"
                        + "    `product` BIGINT,\n"
                        + "    --金额\n"
                        + "    `amount` BIGINT,\n"
                        + "    --支付时间\n"
                        + "    `order_time` as CAST(CURRENT_TIMESTAMP AS TIMESTAMP(3)),\n"
                        + "    --WATERMARK\n"
                        + "    WATERMARK FOR order_time AS order_time-INTERVAL '2' SECOND\n"
                        + ") WITH(\n"
                        + "    'connector' = 'datagen',\n"
                        + "    'rows-per-second' = '1',\n"
                        + "    'fields.order_id.min' = '1',\n"
                        + "    'fields.order_id.max' = '2',\n"
                        + "    'fields.amount.min' = '1',\n"
                        + "    'fields.amount.max' = '10',\n"
                        + "    'fields.product.min' = '1',\n"
                        + "    'fields.product.max' = '2'\n"
                        + ");\n"
                        + "SELECT * from t_order limit 10;";

        FlinkJobResult result = submitter.submitJob(statement);
        System.out.println(result.getJobId());
        System.out.println(result.getStatus());
        assertThat(result).isNotNull();
        assertThat(result.getData()).isEmpty();
    }
}
