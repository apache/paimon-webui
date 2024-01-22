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

package org.apache.paimon.web.engine.flink.sql.gataway;

import org.apache.commons.io.FileUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.testutils.CommonTestUtils;
import org.apache.flink.table.gateway.api.SqlGatewayService;
import org.apache.flink.table.gateway.api.endpoint.SqlGatewayEndpointFactoryUtils;
import org.apache.flink.table.gateway.rest.SqlGatewayRestEndpoint;
import org.apache.flink.table.gateway.rest.util.SqlGatewayRestOptions;
import org.apache.flink.table.gateway.service.SqlGatewayServiceImpl;
import org.apache.flink.table.gateway.service.context.DefaultContext;
import org.apache.flink.table.gateway.service.session.SessionManager;
import org.apache.flink.test.junit5.MiniClusterExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.rules.TemporaryFolder;

import javax.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.apache.flink.configuration.ConfigConstants.ENV_FLINK_CONF_DIR;
import static org.apache.flink.table.gateway.api.endpoint.SqlGatewayEndpointFactoryUtils.getEndpointConfig;
import static org.apache.flink.table.gateway.api.endpoint.SqlGatewayEndpointFactoryUtils.getSqlGatewayOptionPrefix;
import static org.apache.flink.table.gateway.rest.SqlGatewayRestEndpointFactory.IDENTIFIER;
import static org.apache.flink.table.gateway.rest.SqlGatewayRestEndpointFactory.rebuildRestEndpointOptions;
import static org.apache.flink.util.Preconditions.checkNotNull;

/** The base class for sql gateway test. */
public class TestBase {

    @RegisterExtension
    @Order(1)
    private static final MiniClusterExtension MINI_CLUSTER = new MiniClusterExtension();

    @RegisterExtension
    @Order(2)
    protected static final SqlGatewayServiceExtension SQL_GATEWAY_SERVICE_EXTENSION =
            new SqlGatewayServiceExtension(MINI_CLUSTER::getClientConfiguration);

    @Nullable protected static String targetAddress = null;
    @Nullable private static SqlGatewayRestEndpoint sqlGatewayRestEndpoint = null;

    protected static int port = 0;

    @BeforeAll
    static void start() throws Exception {
        final String address = InetAddress.getLoopbackAddress().getHostAddress();
        Configuration config = getBaseConfig(getFlinkConfig(address, address, "0"));
        sqlGatewayRestEndpoint =
                new SqlGatewayRestEndpoint(config, SQL_GATEWAY_SERVICE_EXTENSION.getService());
        sqlGatewayRestEndpoint.start();
        InetSocketAddress serverAddress = checkNotNull(sqlGatewayRestEndpoint.getServerAddress());
        targetAddress = serverAddress.getHostName();
        port = serverAddress.getPort();
    }

    @AfterAll
    static void stop() throws Exception {
        checkNotNull(sqlGatewayRestEndpoint);
        sqlGatewayRestEndpoint.close();
    }

    private static Configuration getBaseConfig(Configuration flinkConf) {
        SqlGatewayEndpointFactoryUtils.DefaultEndpointFactoryContext context =
                new SqlGatewayEndpointFactoryUtils.DefaultEndpointFactoryContext(
                        null, flinkConf, getEndpointConfig(flinkConf, IDENTIFIER));

        return rebuildRestEndpointOptions(context.getEndpointOptions());
    }

    private static Configuration getFlinkConfig(
            String address, String bindAddress, String portRange) {
        final Configuration config = new Configuration();
        if (address != null) {
            config.setString(
                    getSqlGatewayRestOptionFullName(SqlGatewayRestOptions.ADDRESS.key()), address);
        }
        if (bindAddress != null) {
            config.setString(
                    getSqlGatewayRestOptionFullName(SqlGatewayRestOptions.BIND_ADDRESS.key()),
                    bindAddress);
        }
        if (portRange != null) {
            config.setString(
                    getSqlGatewayRestOptionFullName(SqlGatewayRestOptions.PORT.key()), portRange);
        }
        return config;
    }

    private static String getSqlGatewayRestOptionFullName(String key) {
        return getSqlGatewayOptionPrefix(IDENTIFIER) + key;
    }

    /** A simple {@link Extension} to be used by tests that require a {@link SqlGatewayService}. */
    static class SqlGatewayServiceExtension implements BeforeAllCallback, AfterAllCallback {

        private SqlGatewayService service;
        private SessionManager sessionManager;
        private TemporaryFolder temporaryFolder;
        private final Supplier<Configuration> configSupplier;
        private final Function<DefaultContext, SessionManager> sessionManagerCreator;

        public SqlGatewayServiceExtension(Supplier<Configuration> configSupplier) {
            this(configSupplier, SessionManager::create);
        }

        public SqlGatewayServiceExtension(
                Supplier<Configuration> configSupplier,
                Function<DefaultContext, SessionManager> sessionManagerCreator) {
            this.configSupplier = configSupplier;
            this.sessionManagerCreator = sessionManagerCreator;
        }

        @Override
        public void beforeAll(ExtensionContext context) throws Exception {
            final Map<String, String> originalEnv = System.getenv();
            try {
                // prepare conf dir
                temporaryFolder = new TemporaryFolder();
                temporaryFolder.create();
                File confFolder = temporaryFolder.newFolder("conf");
                File confYaml = new File(confFolder, "flink-conf.yaml");
                if (!confYaml.createNewFile()) {
                    throw new IOException("Can't create testing flink-conf.yaml file.");
                }

                FileUtils.write(
                        confYaml,
                        getFlinkConfContent(configSupplier.get().toMap()),
                        StandardCharsets.UTF_8);

                // adjust the test environment for the purposes of this test
                Map<String, String> map = new HashMap<>(System.getenv());
                map.put(ENV_FLINK_CONF_DIR, confFolder.getAbsolutePath());
                CommonTestUtils.setEnv(map);

                sessionManager =
                        sessionManagerCreator.apply(
                                DefaultContext.load(
                                        new Configuration(), Collections.emptyList(), true, false));
            } finally {
                CommonTestUtils.setEnv(originalEnv);
            }

            service = new SqlGatewayServiceImpl(sessionManager);
            sessionManager.start();
        }

        @Override
        public void afterAll(ExtensionContext context) throws Exception {
            if (sessionManager != null) {
                sessionManager.stop();
            }
            temporaryFolder.delete();
        }

        public SqlGatewayService getService() {
            return service;
        }

        private String getFlinkConfContent(Map<String, String> flinkConf) {
            StringBuilder sb = new StringBuilder();
            flinkConf.forEach((k, v) -> sb.append(k).append(": ").append(v).append("\n"));
            return sb.toString();
        }
    }
}
