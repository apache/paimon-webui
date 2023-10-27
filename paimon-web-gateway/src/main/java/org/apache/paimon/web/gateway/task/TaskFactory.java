/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import org.apache.paimon.web.task.SubmitTask;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.internal.StreamTableEnvironmentImpl;
import org.apache.spark.sql.SparkSession;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class TaskFactory {
    /** List of available connectors */
    public static final Map<TaskType, SubmitTask> AVAILABLE_TASK_LIST = new LinkedHashMap<>();
    /** Flink(local mode) default port */
    private static final int FLINK_DEFAULT_PORT = 8081;

    /** Initialization and check connector status */
    public static void init() {
        initFlink();
        initSpark();
        initFlinkSqlGateway();
    }

    public static SubmitTask getTask(String taskType) {
        TaskType type = TaskType.valueOf(taskType.toUpperCase());
        Assert.notNull(type, "TaskType:{} is not exists!", taskType);
        return getTask(type);
    }

    public static SubmitTask getTask(TaskType taskType) {
        SubmitTask submitTask = AVAILABLE_TASK_LIST.get(taskType);
        Assert.notNull(submitTask, "TaskType:{} is initialization failed!", taskType);
        return submitTask;
    }

    /**
     * Determine whether the IP is a local IP
     *
     * @param ip ip
     * @return is local ip
     */
    private static boolean isLocalIp(String ip) {
        for (String localIp : NetUtil.localIps()) {
            if (StrUtil.equals(localIp, ip)) {
                return true;
            }
        }
        return false;
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
        SysEnv.Config flinkConf = SysEnv.INSTANCE.getFlinkConf();
        Configuration configuration = new Configuration().set(RestOptions.PORT, FLINK_DEFAULT_PORT);
        if (flinkConf == null) {
            env = initFlinkLocalEnv(configuration);
        } else {
            Configuration newConfiguration =
                    Opt.ofNullable(flinkConf)
                            .map(SysEnv.Config::getConfPath)
                            .map(FileUtil::file)
                            .map(
                                    x ->
                                            GlobalConfiguration.loadConfiguration(
                                                    x.getAbsolutePath(), configuration))
                            .orElse(configuration);
            env =
                    Opt.ofNullable(flinkConf)
                            .map(SysEnv.Config::getRemoteAddr)
                            .map(x -> StrUtil.split(x, ":"))
                            .map(
                                    x -> {
                                        String ip = x.get(0);
                                        String port = x.get(1);
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
        SysEnv.Config sparkConf = SysEnv.INSTANCE.getSparkConf();
        if (sparkConf == null) {
            builder.master("local[*]");
        } else {
            Opt.ofNullable(FileUtil.file(sparkConf.getConfPath()))
                    .ifPresent(
                            file ->
                                    new Props(file)
                                            .forEach(
                                                    (key, value) ->
                                                            builder.config(
                                                                    key.toString(),
                                                                    value.toString())));
            Opt.ofNullable(sparkConf.getRemoteAddr()).ifPresent(builder::master);
        }
        SparkSession spark = builder.master("local[*]").getOrCreate();
        SparkTask sparkTask = new SparkTask(spark);
        if (sparkTask.checkStatus()) {
            AVAILABLE_TASK_LIST.put(TaskType.SPARK, sparkTask);
        }
    }

    /** Initialize flink sql gateway */
    protected static void initFlinkSqlGateway() {
        SysEnv.Config flinkSqlGatewayConf = SysEnv.INSTANCE.getFlinkSqlGatewayConf();
        Opt.ofNullable(flinkSqlGatewayConf)
                .map(SysEnv.Config::getRemoteAddr)
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
