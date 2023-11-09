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

package org.apache.paimon.web.gateway.task;

import org.apache.paimon.web.common.data.propertis.SysEnv;
import org.apache.paimon.web.flink.sql.gateway.FlinkSqlGatewayTask;
import org.apache.paimon.web.flink.task.FlinkTask;
import org.apache.paimon.web.flink.task.SparkTask;
import org.apache.paimon.web.server.data.enums.TaskType;
import org.apache.paimon.web.task.SubmitJob;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.internal.StreamTableEnvironmentImpl;
import org.apache.spark.sql.SparkSession;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class TaskFactory {
    /** List of available connectors */
    public static final Map<TaskType, SubmitJob> AVAILABLE_TASK_LIST = new LinkedHashMap<>();
    /** Flink(local mode) default port */
    private static final int FLINK_DEFAULT_PORT = 8081;

    /** Initialization and check connector status */
    public static void init() {
        initFlink();
        initSpark();
        initFlinkSqlGateway();
    }

    public static SubmitJob getTask(String taskType) {
        TaskType type = TaskType.valueOf(taskType.toUpperCase());
        Validate.notNull(type, "TaskType:{} is not exists!", taskType);
        return getTask(type);
    }

    public static SubmitJob getTask(TaskType taskType) {
        SubmitJob submitJob = AVAILABLE_TASK_LIST.get(taskType);
        Validate.notNull(submitJob, "TaskType:{} is initialization failed!", taskType);
        return submitJob;
    }

    /**
     * Determine whether the IP is a local IP
     *
     * @param ip ip
     * @return is local ip
     */
    private static boolean isLocalIp(String ip) {
        return StringUtils.containsAnyIgnoreCase(ip, "127.0.0.1", "localhost");
    }

    /**
     * Initialize flink local mode
     *
     * @param configuration configuration
     * @return StreamExecutionEnvironment
     */
    private static StreamExecutionEnvironment initFlinkLocalEnv(Configuration configuration) {
        return StreamExecutionEnvironment.getExecutionEnvironment(configuration);
    }

    /** Initialize flink */
    protected static void initFlink() {
        StreamExecutionEnvironment env;
        SysEnv.JobProperties flinkConf = SysEnv.INSTANCE.getFlinkConf();
        Configuration configuration = new Configuration().set(RestOptions.PORT, FLINK_DEFAULT_PORT);
        if (flinkConf == null) {
            env = initFlinkLocalEnv(configuration);
        } else {
            Configuration newConfiguration =
                    Optional.of(flinkConf)
                            .map(SysEnv.JobProperties::getConfPath)
                            .map(File::new)
                            .map(
                                    x ->
                                            GlobalConfiguration.loadConfiguration(
                                                    x.getAbsolutePath(), configuration))
                            .orElse(configuration);
            env =
                    Optional.of(flinkConf)
                            .map(SysEnv.JobProperties::getRemoteAddr)
                            .map(x -> StringUtils.split(x, ":"))
                            .map(
                                    x -> {
                                        String ip = x[0];
                                        String port = x[1];
                                        newConfiguration.set(RestOptions.ADDRESS, ip);
                                        newConfiguration.set(
                                                RestOptions.PORT, Integer.parseInt(port));
                                        if (isLocalIp(ip)) {
                                            return initFlinkLocalEnv(newConfiguration);
                                        } else {
                                            return StreamExecutionEnvironment
                                                    .createRemoteEnvironment(
                                                            ip,
                                                            Integer.parseInt(port),
                                                            newConfiguration);
                                        }
                                    })
                            .orElse(initFlinkLocalEnv(newConfiguration));
        }
        TableEnvironment tEnv =
                StreamTableEnvironmentImpl.create(env, EnvironmentSettings.inStreamingMode());
        FlinkTask flinkTask = new FlinkTask(env, tEnv);
        if (flinkTask.checkStatus()) {
            AVAILABLE_TASK_LIST.put(TaskType.FLINK, flinkTask);
        }
    }

    /** Initialize spark */
    protected static void initSpark() {
        SparkSession.Builder builder = SparkSession.builder();
        SysEnv.JobProperties sparkConf = SysEnv.INSTANCE.getSparkConf();
        if (sparkConf == null) {
            builder.master("local[*]");
        } else {
            Optional.of(new File(sparkConf.getConfPath()))
                    .ifPresent(
                            file -> {
                                try {
                                    PropertiesConfiguration propertiesConfiguration =
                                            new PropertiesConfiguration(file);
                                    Lists.newArrayList(propertiesConfiguration.getKeys())
                                            .forEach(
                                                    key ->
                                                            builder.config(
                                                                    key.toString(),
                                                                    propertiesConfiguration
                                                                            .getString(
                                                                                    key
                                                                                            .toString())));
                                } catch (ConfigurationException e) {
                                    throw new RuntimeException(e);
                                }
                            });
            Optional.ofNullable(sparkConf.getRemoteAddr()).ifPresent(builder::master);
        }
        SparkSession spark = builder.master("local[*]").getOrCreate();
        SparkTask sparkTask = new SparkTask(spark);
        if (sparkTask.checkStatus()) {
            AVAILABLE_TASK_LIST.put(TaskType.SPARK, sparkTask);
        }
    }

    /** Initialize flink sql gateway */
    protected static void initFlinkSqlGateway() {
        SysEnv.JobProperties flinkSqlGatewayConf = SysEnv.INSTANCE.getFlinkSqlGatewayConf();
        Optional.ofNullable(flinkSqlGatewayConf)
                .map(SysEnv.JobProperties::getRemoteAddr)
                .ifPresent(
                        addr -> {
                            try {
                                String[] split = addr.split(":");
                                String ip = split[0];
                                String port = split[1];
                                FlinkSqlGatewayTask flinkSqlGatewayTask =
                                        new FlinkSqlGatewayTask(ip, Integer.parseInt(port));
                                if (flinkSqlGatewayTask.checkStatus()) {
                                    AVAILABLE_TASK_LIST.put(TaskType.SPARK, flinkSqlGatewayTask);
                                }
                            } catch (Exception e) {
                                log.error("", e);
                            }
                        });
    }
}
