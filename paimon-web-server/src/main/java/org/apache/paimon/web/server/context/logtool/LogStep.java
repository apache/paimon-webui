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

import java.time.LocalDateTime;

/** Record the current log write to step. */
public class LogStep {
    private LogStatus stepStatus;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long time;
    private StringBuilder info = new StringBuilder();
    private StringBuilder error = new StringBuilder();
    private boolean isError = false;

    public LogStep() {}

    public LogStep(LogStatus stepStatus, LocalDateTime startTime) {
        this(stepStatus, startTime, null, 0, new StringBuilder(), new StringBuilder());
    }

    public LogStep(
            LogStatus stepStatus,
            LocalDateTime startTime,
            LocalDateTime endTime,
            long time,
            StringBuilder info,
            StringBuilder error) {
        this.stepStatus = stepStatus;
        this.startTime = startTime;
        this.endTime = endTime;
        this.time = time;
        this.info = info;
        this.error = error;
    }

    public static LogStep init() {
        return new LogStep(LogStatus.INITIALIZING, LocalDateTime.now());
    }

    public static LogStep run() {
        return new LogStep(LogStatus.RUNNING, LocalDateTime.now());
    }

    public void appendInfo(String str) {
        info.append(str);
    }

    public void appendError(String str) {
        error.append(str);
        isError = true;
    }

    public LogStatus getStepStatus() {
        return stepStatus;
    }

    public void setStepStatus(LogStatus stepStatus) {
        this.stepStatus = stepStatus;
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
        this.time = endTime.compareTo(startTime);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public StringBuilder getInfo() {
        return info;
    }

    public void setInfo(StringBuilder info) {
        this.info = info;
    }

    public StringBuilder getError() {
        return error;
    }

    public void setError(StringBuilder error) {
        this.error = error;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }
}
