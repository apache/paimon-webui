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
import org.apache.paimon.web.api.action.context.ActionExecutionResult;
import org.apache.paimon.web.api.action.context.FlinkActionContext;
import org.apache.paimon.web.api.enums.FlinkJobType;
import org.apache.paimon.web.api.exception.ActionException;
import org.apache.paimon.web.api.shell.ShellService;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** An abstract Action service that executes actions through the shell. */
@Slf4j
public class FlinkCdcActionService implements ActionService {
    private static final ExecutorService shellExecutor = Executors.newFixedThreadPool(5);

    private List<String> getCommand(FlinkActionContext actionContext) {
        List<String> commandList = new ArrayList<>();
        commandList.add("bin/flink");
        commandList.add("run");
        if (actionContext.getFlinkJobType() != FlinkJobType.SESSION) {
            throw new ActionException("Only support session job now.");
        }
        String sessionUrl = actionContext.getSessionUrl();
        if (StringUtils.isNotBlank(sessionUrl)) {
            commandList.add("-m");
            commandList.add(sessionUrl);
        }
        commandList.add(actionContext.getJarPath());
        commandList.addAll(actionContext.getArguments());
        return commandList;
    }

    public ActionExecutionResult execute(ActionContext actionContext) throws Exception {
        String flinkHome = getFlinkHome();
        FlinkActionContext flinkActionContext;
        if (!(actionContext instanceof FlinkActionContext)) {
            throw new ActionException("Only support FlinkActionContext. ");
        }
        flinkActionContext = (FlinkActionContext) actionContext;
        ActionExecutionResult result;
        try {
            List<String> command = getCommand(flinkActionContext);
            Process process = new ShellService(flinkHome, command).execute();
            shellExecutor.execute(
                    () -> {
                        try (InputStream inputStream = process.getInputStream();
                                InputStream errorStream = process.getErrorStream(); ) {
                            List<String> logLines =
                                    IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
                            for (String logLine : logLines) {
                                log.info(logLine);
                            }
                            List<String> errorLines =
                                    IOUtils.readLines(errorStream, StandardCharsets.UTF_8);
                            for (String logLine : errorLines) {
                                log.error(logLine);
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    });
            result = ActionExecutionResult.success();
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            result = ActionExecutionResult.fail(exception.getMessage());
        }
        return result;
    }

    private String getFlinkHome() {
        String flinkHome = System.getenv("FLINK_HOME");
        if (StringUtils.isBlank(flinkHome)) {
            flinkHome = System.getProperty("FLINK_HOME");
        }
        if (StringUtils.isBlank(flinkHome)) {
            throw new ActionException("FLINK_HOME is null");
        }
        return flinkHome;
    }
}
