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

package org.apache.paimon.web.flink.submit;

import org.apache.paimon.web.flink.submit.yarn.YarnApplicationSubmit;

/** The mode of flink job submitted. */
public enum SubmitMode {

    LOCAL(0, "local"),
    REMOTE(1, "remote"),
    YARN_PRE_JOB(2, "yarn-pre-job"),
    YARN_SESSION(3, "yarn-session"),
    APPLICATION(4, "yarn-application"),
    KUBERNETES_NATIVE_SESSION(5, "kubernetes-session"),
    KUBERNETES_NATIVE_APPLICATION(6, "kubernetes-application");

    private final Integer mode;
    private final String name;

    SubmitMode(Integer mode, String name) {
        this.mode = mode;
        this.name = name;
    }

    public int getMode() {
        return mode;
    }

    public String getName() {
        return name;
    }

    public static SubmitMode of(Integer value) {
        for (SubmitMode executionMode : values()) {
            if (executionMode.mode.equals(value)) {
                return executionMode;
            }
        }
        return null;
    }

    public static SubmitMode of(String name) {
        for (SubmitMode executionMode : values()) {
            if (executionMode.name.equals(name)) {
                return executionMode;
            }
        }
        return null;
    }
}
