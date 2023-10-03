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

/** The type of flink job submitted. */
public enum SubmitType {
    YARN_APPLICATION("yarn-application", new YarnApplicationSubmit());

    private final String value;

    private FlinkJobSubmit flinkJobSubmit;

    SubmitType(String value, FlinkJobSubmit flinkJobSubmit) {
        this.value = value;
        this.flinkJobSubmit = flinkJobSubmit;
    }

    public String getValue() {
        return value;
    }

    public FlinkJobSubmit getFlinkJobSubmit() {
        return flinkJobSubmit;
    }

    public void setFlinkJobSubmit(FlinkJobSubmit flinkJobSubmit) {
        this.flinkJobSubmit = flinkJobSubmit;
    }

    public static SubmitType get(String value) {
        for (SubmitType type : SubmitType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return SubmitType.YARN_APPLICATION;
    }
}
