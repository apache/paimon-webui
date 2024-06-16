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

package org.apache.paimon.web.server.context.logTool;

/** log status */
public enum LogStatus {
  INITIALIZING("INITIALIZING"),
  RUNNING("RUNNING"),
  FAILED("FAILED"),
  CANCELED("CANCELED"),
  FINISHED("FINISHED"),
  UNKNOWN("UNKNOWN");

  private String value;

  LogStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static LogStatus get(String value) {
    for (LogStatus type : LogStatus.values()) {
      if (type.getValue().equalsIgnoreCase(value)) {
        return type;
      }
    }
    return LogStatus.UNKNOWN;
  }

  public boolean equalsValue(String type) {
    if (value.equalsIgnoreCase(type)) {
      return true;
    }
    return false;
  }

  public boolean isActiveStatus() {
    switch (this) {
      case INITIALIZING:
      case RUNNING:
        return true;
      default:
        return false;
    }
  }
}
