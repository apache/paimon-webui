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

package org.apache.paimon.web.spark.executor;

import org.apache.paimon.web.common.executor.Executor;
import org.apache.paimon.web.common.executor.ExecutorFactory;

import org.apache.spark.sql.SparkSession;

/** Factory class for creating executors that interface with the Spark. */
public class SparkExecutorFactory implements ExecutorFactory {

    @Override
    public Executor createExecutor() throws Exception {
        // TODO other mode.
        SparkSession sparkSession =
                SparkSession.builder().appName("SparkExecutor").master("local[*]").getOrCreate();

        return new SparkExecutor(sparkSession);
    }
}
