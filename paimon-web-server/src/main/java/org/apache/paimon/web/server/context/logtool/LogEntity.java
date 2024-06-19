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

import org.apache.paimon.web.server.util.LocalDateTimeUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** log entity. */
public class LogEntity {
    private String pid;
    private String name;
    private Integer taskId;
    private LogType type;
    private LogStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long time;
    private int stepIndex = 0;
    private List<LogStep> steps;
    private String userId;

    public static final LogEntity NULL_PROCESS = new LogEntity();

    public LogEntity() {}

    public LogEntity(String pid, String name, Integer taskId, LogType type, String userId) {
        this.pid = pid;
        this.name = name;
        this.taskId = taskId;
        this.type = type;
        this.userId = userId;
    }

    public LogEntity(
            String name,
            Integer taskId,
            LogType type,
            LogStatus status,
            LocalDateTime startTime,
            LocalDateTime endTime,
            long time,
            List<LogStep> steps,
            String userId) {
        this.name = name;
        this.taskId = taskId;
        this.type = type;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.time = time;
        this.steps = steps;
        this.userId = userId;
    }

    public static LogEntity init(LogType type, String submitId) {
        return init(type.getValue() + "_TEMP", null, type, submitId);
    }

    public static LogEntity init(Integer taskId, LogType type, String submitId) {
        return init(type.getValue() + taskId, taskId, type, submitId);
    }

    public static LogEntity init(String name, Integer taskId, LogType type, String submitId) {
        LogEntity process =
                new LogEntity(UUID.randomUUID().toString(), name, taskId, type, submitId);
        process.setStatus(LogStatus.INITIALIZING);
        process.setStartTime(LocalDateTime.now());
        process.setSteps(new ArrayList<>());
        process.getSteps().add(LogStep.init());
        process.nextStep();
        return process;
    }

    public void start() {
        if (isNullProcess()) {
            return;
        }
        steps.get(stepIndex - 1).setEndTime(LocalDateTime.now());
        setStatus(LogStatus.RUNNING);
        steps.add(LogStep.run());
        nextStep();
    }

    public void finish() {
        if (isNullProcess()) {
            return;
        }
        steps.get(stepIndex - 1).setEndTime(LocalDateTime.now());
        setStatus(LogStatus.FINISHED);
        setEndTime(LocalDateTime.now());
        setTime(getEndTime().compareTo(getStartTime()));
    }

    public void finish(String str) {
        if (isNullProcess()) {
            return;
        }
        steps.get(stepIndex - 1).setEndTime(LocalDateTime.now());
        String message =
                String.format(
                        "\n[%s] INFO: %s",
                        LocalDateTimeUtil.getFormattedDateTime(LocalDateTime.now()), str);
        steps.get(stepIndex - 1).appendInfo(message);
        setStatus(LogStatus.FINISHED);
        setEndTime(LocalDateTime.now());
        setTime(getEndTime().compareTo(getStartTime()));
        LogReadPool.write(message, userId);
    }

    public void config(String str) {
        if (isNullProcess()) {
            return;
        }
        String message =
                String.format(
                        "\n[%s] CONFIG: %s",
                        LocalDateTimeUtil.getFormattedDateTime(LocalDateTime.now()), str);
        steps.get(stepIndex - 1).appendInfo(message);
        LogReadPool.write(message, userId);
    }

    public void info(String str) {
        if (isNullProcess()) {
            return;
        }
        String message =
                String.format(
                        "\n[%s] INFO: %s",
                        LocalDateTimeUtil.getFormattedDateTime(LocalDateTime.now()), str);
        steps.get(stepIndex - 1).appendInfo(message);
        LogReadPool.write(message, userId);
    }

    public void infoSuccess() {
        if (isNullProcess()) {
            return;
        }
        steps.get(stepIndex - 1).appendInfo("Success.");
        LogReadPool.write("Success.", userId);
    }

    public void infoFail() {
        if (isNullProcess()) {
            return;
        }
        steps.get(stepIndex - 1).appendInfo("Fail.");
        LogReadPool.write("Fail.", userId);
    }

    public void error(String str) {
        if (isNullProcess()) {
            return;
        }
        String message =
                String.format(
                        "\n[%s] ERROR: %s",
                        LocalDateTimeUtil.getFormattedDateTime(LocalDateTime.now()), str);
        steps.get(stepIndex - 1).appendInfo(message);
        steps.get(stepIndex - 1).appendError(message);
        LogReadPool.write(message, userId);
    }

    public void nextStep() {
        if (isNullProcess()) {
            return;
        }
        stepIndex++;
    }

    public boolean isNullProcess() {
        return pid == null;
    }

    public boolean isActiveProcess() {
        return status.isActiveStatus();
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public LogType getType() {
        return type;
    }

    public void setType(LogType type) {
        this.type = type;
    }

    public LogStatus getStatus() {
        return status;
    }

    public void setStatus(LogStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public List<LogStep> getSteps() {
        return steps;
    }

    public void setSteps(List<LogStep> steps) {
        this.steps = steps;
    }

    public int getStepIndex() {
        return stepIndex;
    }

    public void setStepIndex(int stepIndex) {
        this.stepIndex = stepIndex;
    }
}
