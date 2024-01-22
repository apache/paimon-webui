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

package org.apache.paimon.web.api.action.service;

import org.apache.paimon.web.api.action.context.ActionContext;
import org.apache.paimon.web.api.exception.ActionException;
import org.apache.paimon.web.api.shell.ShellService;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/** An abstract Action service that executes actions through the shell. */
@Slf4j
public abstract class AbstractCdcActionService implements ActionService {

    public List<String> getCommand(ActionContext actionContext) {
        List<String> commandList = new ArrayList<>();
        commandList.add("${FLINK_HOME}/bin/flink");
        commandList.add("run");
        commandList.add(getActionPath());
        commandList.add(name());
        commandList.addAll(actionContext.getCommand());
        return commandList;
    }

    public void execute(ActionContext actionContext) throws Exception {
        String flinkHome = getFlinkHome();
        try {
            Process process = new ShellService(flinkHome, getCommand(actionContext)).execute();
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            catchException(exception);
        }
    }

    protected String getFlinkHome() {
        String flinkHome = System.getenv("FLINK_HOME");
        if (StringUtils.isBlank(flinkHome)) {
            flinkHome = System.getProperty("FLINK_HOME");
        }
        if (StringUtils.isBlank(flinkHome)) {
            throw new ActionException("FLINK_HOME is null");
        }
        return flinkHome;
    }

    protected String getActionPath() {
        String actionPath = System.getenv("ACTION_PATH");
        if (StringUtils.isBlank(actionPath)) {
            actionPath = System.getProperty("ACTION_PATH");
        }
        if (StringUtils.isBlank(actionPath)) {
            throw new ActionException("ACTION_PATH is null");
        }
        return actionPath;
    }

    public void beforeExecute(ActionContext actionContext, List<String> commandList) {}

    public void afterExecute(Process process, ActionContext actionContext) {}

    public void catchException(Exception exception) throws Exception {}
}
