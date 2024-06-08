#!/bin/bash
#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

PAIMON_UI_HOME="${PAIMON_UI_HOME:-$(pwd)}"
source ${PAIMON_UI_HOME}/bin/env.sh

if [[ "$DOCKER" == "true" ]]; then
  JAVA_OPTS="${JAVA_OPTS} -XX:-UseContainerSupport"
fi

echo "JAVA_HOME=${JAVA_HOME}"
echo "JAVA_OPTS=${JAVA_OPTS}"
echo "PAIMON_UI_HOME=${PAIMON_UI_HOME}"
echo "FLINK_HOME=${PAIMON_UI_HOME}"
echo "ACTION_JAR_PATH=${PAIMON_UI_HOME}"

if [ -z "$PAIMON_UI_HOME" ]; then
    echo "PAIMON_UI_HOME is null, exit..."
    exit 1
fi
if [ -z "$JAVA_HOME" ]; then
      echo "JAVA_HOME is null, exit..."
      exit 1
fi
if [ -z "$FLINK_HOME" ]; then
    echo "FLINK_HOME is null, CDC cannot be used normally!"
fi
if [ -z "$ACTION_JAR_PATH" ]; then
    echo "ACTION_JAR_PATH is null, CDC cannot be used normally!"
fi

$JAVA_HOME/bin/java $JAVA_OPTS \
  -cp "$PAIMON_UI_HOME/conf":"$PAIMON_UI_HOME/libs/*" \
   org.apache.paimon.web.server.PaimonWebServerApplication