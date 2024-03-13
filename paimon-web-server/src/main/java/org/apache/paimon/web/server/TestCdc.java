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

package org.apache.paimon.web.server;

import org.apache.paimon.web.api.action.context.ActionContext;
import org.apache.paimon.web.api.action.context.MysqlSyncTableActionContext;
import org.apache.paimon.web.api.action.service.ActionService;
import org.apache.paimon.web.api.action.service.FlinkCdcActionService;
import org.apache.paimon.web.api.enums.FlinkJobType;

import java.util.Arrays;

public class TestCdc {
    public static void main(String[] args) throws Exception {
        ActionService actionService = new FlinkCdcActionService();
        ActionContext actionContext =
                MysqlSyncTableActionContext.builder()
                        .sessionUrl("192.168.0.101:8081")
                        .flinkJobType(FlinkJobType.SESSION)
                        .actionPath(
                                "/Users/zhongyangyang/Applications/soft/flink/flink-1.17.1/paimon/jars/paimon-flink-action-0.7-20240108.002126-47.jar")
                        .warehouse("s3://paimon/cdc")
                        .database("paimonweb")
                        .table("catalog")
                        .primaryKeys("id")
                        .mysqlConfList(
                                Arrays.asList(
                                        "hostname=192.168.0.101",
                                        "port=30001",
                                        "username=root",
                                        "password=ZYYroot456@=",
                                        "database-name=paimon",
                                        "table-name=catalog",
                                        "server-time-zone=Asia/Shanghai"))
                        .catalogConfList(
                                Arrays.asList(
                                        "s3.endpoint=http://192.168.0.101:32308",
                                        "s3.access-key=BNA9YT5dm2asa2MRFfnc",
                                        "s3.secret-key=g0AWWthulyMA3qsnwFypNB01iq1wtiQe74vmKxEv"))
                        .tableConfList(
                                Arrays.asList(
                                        "bucket=4",
                                        "changelog-producer=input",
                                        "sink.parallelism=4"))
                        .build();
        actionService.execute(actionContext);
        String errorMessage = actionContext.getErrorMessage();
        System.out.println(errorMessage);
    }
}
