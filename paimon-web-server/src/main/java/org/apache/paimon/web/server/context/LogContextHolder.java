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

package org.apache.paimon.web.server.context;

import org.apache.paimon.web.server.context.logtool.LogEntity;
import org.apache.paimon.web.server.context.logtool.LogWritePool;

/** Use Thread local cache log. */
public class LogContextHolder {
    private static final ThreadLocal<LogEntity> PROCESS_CONTEXT = new ThreadLocal<>();

    public static void setProcess(LogEntity process) {
        PROCESS_CONTEXT.set(process);
    }

    public static LogEntity getProcess() {
        if (PROCESS_CONTEXT.get() == null) {
            return LogEntity.NULL_PROCESS;
        }
        return PROCESS_CONTEXT.get();
    }

    public static LogEntity registerProcess(LogEntity process) {
        setProcess(process);
        LogWritePool.getInstance().push(process.getName(), process);
        return process;
    }
}
