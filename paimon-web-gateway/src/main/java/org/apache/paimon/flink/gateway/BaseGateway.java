package org.apache.paimon.flink.gateway;

import org.apache.paimon.web.common.data.FlinkActionParam;

import lombok.RequiredArgsConstructor;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.TaskManagerOptions;

@RequiredArgsConstructor
public abstract class BaseGateway {
    protected final FlinkActionParam flinkActionParam;

    protected ClusterSpecification.ClusterSpecificationBuilder createClusterSpecificationBuilder() {
        Configuration configuration = flinkActionParam.getConfiguration();
        ClusterSpecification.ClusterSpecificationBuilder clusterSpecificationBuilder =
                new ClusterSpecification.ClusterSpecificationBuilder();
        if (configuration.contains(JobManagerOptions.TOTAL_PROCESS_MEMORY)) {
            clusterSpecificationBuilder.setMasterMemoryMB(
                    configuration.get(JobManagerOptions.TOTAL_PROCESS_MEMORY).getMebiBytes());
        }
        if (configuration.contains(TaskManagerOptions.TOTAL_PROCESS_MEMORY)) {
            clusterSpecificationBuilder.setTaskManagerMemoryMB(
                    configuration.get(TaskManagerOptions.TOTAL_PROCESS_MEMORY).getMebiBytes());
        }
        if (configuration.contains(TaskManagerOptions.NUM_TASK_SLOTS)) {
            clusterSpecificationBuilder
                    .setSlotsPerTaskManager(configuration.get(TaskManagerOptions.NUM_TASK_SLOTS))
                    .createClusterSpecification();
        }
        return clusterSpecificationBuilder;
    }
}
