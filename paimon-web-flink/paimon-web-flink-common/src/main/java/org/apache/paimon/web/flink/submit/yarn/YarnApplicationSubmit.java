/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.paimon.web.flink.submit.yarn;

import org.apache.paimon.web.flink.submit.AbstractFlinkJobSubmit;
import org.apache.paimon.web.flink.submit.result.SubmitResult;

import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.runtime.client.JobStatusMessage;
import org.apache.flink.yarn.YarnClientYarnClusterInformationRetriever;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnLogConfigUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * yarn-application submit flink job implement.
 *
 * <p>This class implements the function of submitting Flink SQL to the YARN cluster for execution
 * by calling the Flink-Yarn API.
 */
public class YarnApplicationSubmit extends AbstractFlinkJobSubmit {
    private static final Logger log = LoggerFactory.getLogger(YarnApplicationSubmit.class);
    /** yarn config info. */
    protected YarnConfiguration yarnConfiguration;
    /** yarn client. */
    protected YarnClient yarnClient;

    @Override
    public void buildConf(Map<String, Object> config, Map<String, String> flinkConfigMap) {
        super.buildConf(config, flinkConfigMap);
    }

    @Override
    public SubmitResult submitFlinkSql() {
        if (yarnClient == null) {
            init();
        }
        // Configure user jar, job jobMemory, taskMemory and other information
        configuration.set(
                PipelineOptions.JARS,
                Collections.singletonList(config.get("userJarPath").toString()));
        String[] userJarParams = config.get("userJarParams").toString().split(" ");
        ApplicationConfiguration applicationConfiguration =
                new ApplicationConfiguration(
                        userJarParams, config.get("userJarMainAppClass").toString());
        YarnClusterDescriptor yarnClusterDescriptor =
                new YarnClusterDescriptor(
                        configuration,
                        yarnConfiguration,
                        yarnClient,
                        YarnClientYarnClusterInformationRetriever.create(yarnClient),
                        true);

        String jobMemory =
                config.get("jobMemory") == null ? "1g" : config.get("jobMemory").toString();
        String taskMemory =
                config.get("taskMemory") == null ? "1g" : config.get("taskMemory").toString();

        ClusterSpecification clusterSpecification =
                new ClusterSpecification.ClusterSpecificationBuilder()
                        .setMasterMemoryMB(MemorySize.parse(jobMemory).getMebiBytes())
                        .setTaskManagerMemoryMB(MemorySize.parse(taskMemory).getMebiBytes())
                        .setSlotsPerTaskManager(1)
                        .createClusterSpecification();

        // Execute jobs submitted to the cluster
        return executeSubmit(applicationConfiguration, yarnClusterDescriptor, clusterSpecification);
    }

    private SubmitResult executeSubmit(
            ApplicationConfiguration applicationConfiguration,
            YarnClusterDescriptor yarnClusterDescriptor,
            ClusterSpecification clusterSpecification) {
        try {
            ClusterClientProvider<ApplicationId> clusterClientProvider =
                    yarnClusterDescriptor.deployApplicationCluster(
                            clusterSpecification, applicationConfiguration);
            ClusterClient<ApplicationId> clusterClient = clusterClientProvider.getClusterClient();
            Collection<JobStatusMessage> jobStatusMessages = clusterClient.listJobs().get();
            while (jobStatusMessages.size() == 0) {
                jobStatusMessages = clusterClient.listJobs().get();
                if (jobStatusMessages.size() > 0) {
                    break;
                }
            }
            List<String> jobIds = new ArrayList<>();
            for (JobStatusMessage jobStatusMessage : jobStatusMessages) {
                jobIds.add(jobStatusMessage.getJobId().toHexString());
            }
            ApplicationId applicationId = clusterClient.getClusterId();
            return SubmitResult.builder()
                    .appId(applicationId.toString())
                    .jobIds(jobIds)
                    .webUrl(clusterClient.getWebInterfaceURL())
                    .isSuccess(true)
                    .build();
        } catch (Exception e) {
            log.error("flink sql is committed to the yarn cluster exception:", e);
            return SubmitResult.builder().isSuccess(false).msg(e.getMessage()).build();
        } finally {
            try {
                yarnClusterDescriptor.close();
                yarnClient.close();
            } catch (IOException e) {
                log.error("yarnClient.close error:", e);
            }
        }
    }

    /** init configuration and yarnClient. */
    public void init() {
        YarnLogConfigUtil.setLogConfigFileInConfig(
                configuration, config.get("flinkConfigPath").toString());
        yarnConfiguration = new YarnConfiguration();
        String hadoopConfigPath = config.get("hadoopConfigPath").toString();
        yarnConfiguration.addResource(new Path(URI.create(hadoopConfigPath + "/yarn-site.xml")));
        yarnConfiguration.addResource(new Path(URI.create(hadoopConfigPath + "/core-site.xml")));
        yarnConfiguration.addResource(new Path(URI.create(hadoopConfigPath + "/hdfs-site.xml")));
        yarnClient = YarnClient.createYarnClient();
        yarnClient.init(yarnConfiguration);
        yarnClient.start();
    }
}
