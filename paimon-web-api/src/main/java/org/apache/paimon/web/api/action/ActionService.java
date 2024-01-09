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

package org.apache.paimon.web.api.action;

import org.apache.paimon.web.api.action.context.ActionContext;
import org.apache.paimon.web.api.exception.ActionException;
import org.apache.paimon.web.api.shell.ShellService;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/** Action service. */
public interface ActionService {

    default String getFlinkHome() {
        String flinkHome = System.getenv("FLINK_HOME");
        if (StringUtils.isBlank(flinkHome)) {
            flinkHome = System.getProperty("FLINK_HOME");
        }
        if (StringUtils.isBlank(flinkHome)) {
            throw new ActionException("FLINK_HOME is null");
        }
        return flinkHome;
    }

    default String getActionPath() {
        String actionPath = System.getenv("ACTION_PATH");
        if (StringUtils.isBlank(actionPath)) {
            actionPath = System.getProperty("ACTION_PATH");
        }
        if (StringUtils.isBlank(actionPath)) {
            throw new ActionException("ACTION_PATH is null");
        }
        return actionPath;
    }

    String name();

    default void execute(ActionContext actionContext) throws Exception {
        String flinkHome = getFlinkHome();
        beforeExecute(actionContext);
        try {
            Process process = new ShellService(flinkHome, getCommand(actionContext)).execute();
            afterExecute(process, actionContext);
        } catch (Exception exception) {
            catchException(exception);
        }
    }

    List<String> getCommand(ActionContext actionContext);

    default void beforeExecute(ActionContext actionContext) {}

    default void afterExecute(Process process, ActionContext actionContext) {}

    default void catchException(Exception exception) throws Exception {}
}
