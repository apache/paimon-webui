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

/** This factory is defined to create an {@link Executor} instance. */
public interface ExecutorFactory {

    /**
     * Creates and returns a new instance of an {@link Executor}. Implementations should provide
     * specific details about the type of executor being created and any initialization details.
     *
     * @return A new instance of {@link Executor}.
     * @throws Exception if there is an error creating the executor instance.
     */
    Executor createExecutor() throws Exception;
}
