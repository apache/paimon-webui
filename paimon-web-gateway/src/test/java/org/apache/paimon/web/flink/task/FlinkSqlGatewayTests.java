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

package org.apache.paimon.web.flink.task;

import org.apache.paimon.web.common.utils.ReflectUtil;
import org.apache.paimon.web.flink.sql.gateway.FlinkSqlGatewayTask;
import org.apache.paimon.web.task.BaseTaskTests;

import com.google.common.io.Resources;
import org.apache.commons.io.FileUtils;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.runtime.minicluster.MiniCluster;
import org.apache.flink.runtime.minicluster.MiniClusterConfiguration;
import org.apache.flink.runtime.minicluster.RpcServiceSharing;
import org.apache.flink.table.gateway.SqlGateway;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;
import java.util.Properties;

public class FlinkSqlGatewayTests extends BaseTaskTests {

    @Test
    public void taskExecutorTest() throws Exception {
        String localAddress = "127.0.0.1";
        int sqlGatewayPort = 8083;

        Configuration configuration =
                new Configuration()
                        .set(RestOptions.ADDRESS, localAddress)
                        .set(RestOptions.PORT, 8081);

        // start flink mini cluster
        MiniClusterConfiguration miniClusterConfiguration =
                new MiniClusterConfiguration.Builder()
                        .setConfiguration(configuration)
                        .setNumTaskManagers(1)
                        .setRpcServiceSharing(RpcServiceSharing.SHARED)
                        .setNumSlotsPerTaskManager(1)
                        .build();
        MiniCluster miniCluster = new MiniCluster(miniClusterConfiguration);
        miniCluster.start();

        // start sql gateway
        File confFile = new File(Resources.getResource("flink-conf.yaml").getFile());
        FileUtils.forceMkdirParent(confFile);
        getSysEnv()
                .put(
                        ConfigConstants.ENV_FLINK_CONF_DIR,
                        confFile.getParentFile().getAbsolutePath());
        configuration.setInteger("sql-gateway.endpoint.rest.port", sqlGatewayPort);
        configuration.setString("sql-gateway.endpoint.rest.address", localAddress);
        Properties dynamicConfig = new Properties();
        configuration.addAllToProperties(dynamicConfig);
        SqlGateway sqlGateway = new SqlGateway(dynamicConfig);
        sqlGateway.start();

        // task test
        FlinkSqlGatewayTask flinkSqlGatewayTask =
                new FlinkSqlGatewayTask(localAddress, sqlGatewayPort);
        assertTask(flinkSqlGatewayTask);
        sqlGateway.stop();
        miniCluster.close();
    }

    private static Map<String, String> getSysEnv() throws Exception {
        return ReflectUtil.getStaticFieldValue(
                Class.forName("java.lang.ProcessEnvironment"), "theCaseInsensitiveEnvironment");
    }
}
