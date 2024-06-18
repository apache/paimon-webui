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

package org.apache.paimon.web.server.context.logtool;

import org.apache.paimon.web.server.context.LogContextHolder;
import org.apache.paimon.web.server.util.LogUtil;

/** log exception. */
public class LogException extends RuntimeException {

    public LogException() {}

    public LogException(String message) {
        super(message);
        LogContextHolder.getProcess().error(message);
    }

    public LogException(String message, Throwable cause) {
        super(message, cause);
        LogContextHolder.getProcess().error(LogUtil.getError(cause));
    }

    public LogException(Throwable cause) {
        super(cause);
        LogContextHolder.getProcess().error(cause.toString());
    }

    public LogException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        LogContextHolder.getProcess().error(LogUtil.getError(cause));
    }
}
