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

package org.apache.paimon.web.engine.flink.common.executor;

import org.apache.paimon.web.engine.flink.common.result.ExecutionResult;
import org.apache.paimon.web.engine.flink.common.result.FetchResultParams;

/**
 * The Executor interface provides methods to submit SQL statements for execution, fetch results,
 * and stop jobs.
 */
public interface Executor {

    /**
     * Executes an SQL statement.
     *
     * @param statement The SQL statement to be executed.
     * @param maxRows The maximum number of rows to return in the result set.
     * @return SubmitResult containing information about the execution result.
     * @throws Exception if there is an error executing the SQL statement.
     */
    ExecutionResult executeSql(String statement, int maxRows) throws Exception;

    /**
     * Fetches the results of a previously submitted SQL statement execution.
     *
     * @param params The parameters defining how results should be fetched.
     * @return SubmitResult containing the execution results.
     * @throws Exception if there is an error fetching the results.
     */
    ExecutionResult fetchResults(FetchResultParams params) throws Exception;

    /**
     * Attempts to stop a running job identified by its jobId.
     *
     * @param jobId The unique identifier of the job to stop.
     * @param withSavepoint If true, the job will create a savepoint before stopping.
     * @throws Exception if the job cannot be stopped or savepoint cannot be created.
     */
    void stop(String jobId, boolean withSavepoint) throws Exception;
}
