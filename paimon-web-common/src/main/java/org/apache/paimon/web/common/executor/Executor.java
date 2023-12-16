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

package org.apache.paimon.web.common.executor;

import org.apache.paimon.web.common.result.FetchResultParams;
import org.apache.paimon.web.common.result.SubmissionResult;

/**
 * The Executor interface provides methods to submit SQL statements for execution,
 * fetch results, and stop jobs.
 */
public interface Executor {

    /**
     * Executes an SQL statement.
     *
     * @param statement The SQL statement to be executed.
     * @return SubmitResult containing information about the execution result.
     * @throws Exception if there is an error executing the SQL statement.
     */
    SubmissionResult executeSql(String statement) throws Exception;

    /**
     * Fetches the results of a previously submitted SQL statement execution.
     *
     * @param params The parameters defining how results should be fetched.
     * @return SubmitResult containing the execution results.
     * @throws Exception if there is an error fetching the results.
     */
    SubmissionResult fetchResults(FetchResultParams params) throws Exception;

    /**
     * Stops the execution of a job with the given job ID. The method can accept
     * additional options to customize the stopping behavior based on the
     * underlying execution framework.
     *
     * <p>For Flink, you can pass additional boolean flags:
     * <ul>
     *   <li>{@code withSavepoint}: Set to {@code true} to create a savepoint before stopping.</li>
     *   <li>{@code withDrain}: Set to {@code true} to perform a drain before stopping.</li>
     * </ul>
     * </p>
     *
     * <p>For Spark, no additional options are required, and any supplied options will be ignored.</p>
     *
     * @param jobId The unique ID of the job to stop.
     * @param options Optional parameters to customize the stop behavior. The first boolean
     *                is treated as {@code withSavepoint}, and the second boolean (if present)
     *                is treated as {@code withDrain}.
     * @return {@code true} if the job was stopped successfully, {@code false} otherwise.
     * @throws Exception if there is an error stopping the job.
     */
    boolean stop(String jobId, Object... options) throws Exception;
}
