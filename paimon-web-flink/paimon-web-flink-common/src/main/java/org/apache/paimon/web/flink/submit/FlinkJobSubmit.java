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

import org.apache.paimon.web.flink.submit.result.SubmitResult;

import java.util.Map;

/**
 * flink job FlinkJobSubmit.
 *
 * <p>The function of this interface is to submit the flink SQL submitted by the user to the backend
 * for execution, which can be a yarn cluster or a flink cluster.
 */
public interface FlinkJobSubmit {

    /**
     * init configuration info.
     *
     * @param config configuration info
     * @param flinkConfigMap flink configuration others info
     */
    void buildConf(Map<String, Object> config, Map<String, String> flinkConfigMap);

    /**
     * submit flink sql task.
     *
     * @return submit result
     */
    SubmitResult submitFlinkSql();
}
