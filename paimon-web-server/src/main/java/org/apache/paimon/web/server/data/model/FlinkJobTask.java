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

package org.apache.paimon.web.server.data.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/** flink_job_task table class. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("flink_job_task")
public class FlinkJobTask extends BaseModel {
    /** Flink job name. */
    @NotEmpty(message = "jobName cannot be empty")
    private String jobName;

    /** Execution runtime mode: STREAMING, BATCH, AUTOMATIC. */
    @NotEmpty(message = "Execution runtime mode cannot be empty")
    private String executionRuntimeMode;

    /**
     * Flink job submission type, eg: yarn-application, yarn-session, yarn-per-job, local,
     * kubernetes-session, kubernetes-application.
     */
    @NotEmpty(message = "executionTarget cannot be empty！")
    private String executionTarget;

    /** Job memory. */
    private String jobMemory;

    /** Task memory. */
    private String taskMemory;

    /** Job parallelism. */
    private Integer parallelism;

    /** flink version. */
    private String flinkVersion;

    /** Flink deployment config directory path, eg: /opt/soft/flink-1.17.0/conf. */
    private String flinkConfigPath;

    /** Flink deployment config directory path, eg: /opt/soft/hadoop-3.3.3/etc/hadoop/conf. */
    private String hadoopConfigPath;

    /** Flink checkpoints directory path, eg: hdfs://hacluster/flink_meta/flink-checkpoints. */
    private String checkpointPath;
    /** flink checkpoint interval, in milliseconds.Default 10 minutes. */
    private String checkpointInterval;

    /** Flink savepoints directory path, eg: hdfs://hacluster/flink_meta/flink-savepoints. */
    private String savepointPath;

    /** User JAR path, eg: hdfs://hacluster/usr/xxx.jar. */
    private String userJarPath;

    /** Flink start class name. */
    private String userJarMainAppClass;

    /** Flink Lib path, eg: hdfs://hacluster/flink_lib. */
    private String flinkLibPath;
    /** Job ID. */
    private String jobId;

    /** Application ID. */
    private String applicationId;

    /** Flink job task web URL. */
    private String flinkWebUrl;

    /** Flink job status: INITIALIZING, RUNNING, FAILED, FINISHED. */
    private String jobStatus;

    /** Job admin user. */
    @NotEmpty(message = "adminUser cannot be empty")
    private String adminUser;

    /** Other parameters required for the flink job task to run, in JSON string format. */
    private String otherParams;

    /** flink sql. Multiple SQL statements are used `\n;` Separated. */
    @NotEmpty(message = "flinkSql cannot be empty")
    private String flinkSql;
}
