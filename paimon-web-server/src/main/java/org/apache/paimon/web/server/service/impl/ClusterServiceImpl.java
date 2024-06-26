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

package org.apache.paimon.web.server.service.impl;

import org.apache.paimon.web.engine.flink.sql.gateway.client.ClusterAction;
import org.apache.paimon.web.engine.flink.sql.gateway.client.SessionClusterClient;
import org.apache.paimon.web.engine.flink.sql.gateway.client.SqlGatewayClient;
import org.apache.paimon.web.gateway.enums.EngineType;
import org.apache.paimon.web.server.data.model.ClusterInfo;
import org.apache.paimon.web.server.mapper.ClusterMapper;
import org.apache.paimon.web.server.service.ClusterService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

/** The implementation of {@link ClusterService}. */
@Service
@Slf4j
public class ClusterServiceImpl extends ServiceImpl<ClusterMapper, ClusterInfo>
        implements ClusterService {

    @Autowired private ClusterMapper clusterMapper;

    @Override
    public List<ClusterInfo> listUsers(IPage<ClusterInfo> page, ClusterInfo cluster) {
        return clusterMapper.listClusters(page, cluster);
    }

    @Override
    public boolean checkClusterNameUnique(ClusterInfo cluster) {
        int clusterId = cluster.getId() == null ? -1 : cluster.getId();
        ClusterInfo info =
                this.lambdaQuery().eq(ClusterInfo::getClusterName, cluster.getClusterName()).one();
        return info == null || info.getId() == clusterId;
    }

    @Override
    public int deleteClusterByIds(Integer[] clusterIds) {
        return clusterMapper.deleteBatchIds(Arrays.asList(clusterIds));
    }

    @Override
    public boolean checkClusterStatus(ClusterInfo clusterInfo) {
        try {
            ImmutablePair<ClusterAction.ClusterStatus, Long> clusterHeartbeatEntity =
                    this.getClusterActionFactory(clusterInfo).checkClusterHeartbeat();
            return clusterHeartbeatEntity.getLeft().equals(ClusterAction.ClusterStatus.RUNNING);
        } catch (Exception e) {
            throw new RuntimeException("Checking cluster status error.", e);
        }
    }

    /**
     * Check the cluster status regularly. Execute every 10 seconds through CRON expression. Query
     * all enabled cluster information, and then check the heartbeat of each cluster one by one. For
     * engine types that do not support status check, record warning logs and skip. For supported
     * engine types, update the latest status information of the cluster. If an exception is
     * encountered during the check, record the error log.
     */
    @Scheduled(cron = "0 * * * * ?")
    public void checkClusterStatus() {
        QueryWrapper<ClusterInfo> queryWrapper =
                new QueryWrapper<ClusterInfo>().eq("enabled", true);
        for (ClusterInfo clusterInfo : clusterMapper.selectList(queryWrapper)) {
            log.info(
                    "Starting a scheduled job to check cluster: `{}` status ...",
                    clusterInfo.getClusterName());
            if (EngineType.SPARK.name().equals(clusterInfo.getType().toUpperCase())) {
                log.warn(
                        "Current engine type: {} doesn't support checking Cluster status.",
                        clusterInfo.getType());
                continue;
            }
            try {
                ImmutablePair<ClusterAction.ClusterStatus, Long> heartbeat =
                        this.getClusterActionFactory(clusterInfo).checkClusterHeartbeat();
                this.buildClusterInfo(clusterInfo, heartbeat);
                clusterMapper.updateById(clusterInfo);
            } catch (Exception e) {
                log.error(
                        "Failed to check Cluster: {} status by executor: {}",
                        clusterInfo.getClusterName(),
                        e.getMessage(),
                        e);
            }
        }
    }
    /**
     * Get the corresponding cluster operation instance based on the cluster information.
     *
     * <p>This method determines which type of cluster operation instance to create based on the
     * engine type specified in the cluster information. Supported engine types are Flink Session
     * cluster and Flink SQL Gateway. If the specified engine type is not supported, an
     * UnsupportedOperationException will be thrown.
     *
     * @param clusterInfo Cluster information, including type, host, and port.
     * @return Returns a cluster operation instance for interacting with a specific type of cluster.
     * @throws Exception If the specified engine type is not supported, an exception will be thrown.
     */
    private ClusterAction getClusterActionFactory(ClusterInfo clusterInfo) throws Exception {
        EngineType engineType = EngineType.format(clusterInfo.getType().toUpperCase());
        switch (engineType) {
            case FLINK_SESSION:
                return new SessionClusterClient(clusterInfo.getHost(), clusterInfo.getPort());
            case FLINK_SQL_GATEWAY:
                return new SqlGatewayClient(clusterInfo.getHost(), clusterInfo.getPort());
            default:
                throw new UnsupportedOperationException(
                        engineType + " engine is not currently supported.");
        }
    }

    private void buildClusterInfo(
            ClusterInfo clusterInfo, ImmutablePair<ClusterAction.ClusterStatus, Long> result) {
        clusterInfo.setClusterStatus(result.getLeft().name());
        clusterInfo.setUpdateTime(LocalDateTime.now());
        if (ClusterAction.ClusterStatus.RUNNING.equals(result.getLeft())) {
            LocalDateTime dateTime =
                    LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(result.getRight()), ZoneId.systemDefault());
            clusterInfo.setLastHeartbeat(dateTime);
        }
    }
}
