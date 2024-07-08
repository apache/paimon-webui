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

package org.apache.paimon.web.server.controller;

import org.apache.paimon.web.server.data.model.ClusterInfo;
import org.apache.paimon.web.server.data.result.PageR;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.service.ClusterService;
import org.apache.paimon.web.server.util.PageSupport;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Cluster api controller. */
@RestController
@RequestMapping("/api/cluster")
public class ClusterController {

    @Autowired private ClusterService clusterService;

    @SaCheckPermission("system:cluster:query")
    @GetMapping("/{id}")
    public R<ClusterInfo> getCluster(@PathVariable("id") Integer id) {
        ClusterInfo clusterInfo = clusterService.getById(id);
        if (clusterInfo == null) {
            return R.failed(Status.CLUSTER_NOT_EXIST);
        }
        return R.succeed(clusterInfo);
    }

    @SaCheckPermission("system:cluster:list")
    @GetMapping("/list")
    public PageR<ClusterInfo> listClusters(ClusterInfo clusterInfo) {
        IPage<ClusterInfo> page = PageSupport.startPage();
        List<ClusterInfo> clusterInfos = clusterService.listUsers(page, clusterInfo);
        return PageR.<ClusterInfo>builder()
                .success(true)
                .total(page.getTotal())
                .data(clusterInfos)
                .build();
    }

    @SaCheckPermission("system:cluster:add")
    @PostMapping
    public R<Void> add(@Validated @RequestBody ClusterInfo clusterInfo) {
        if (!clusterService.checkClusterNameUnique(clusterInfo)) {
            return R.failed(Status.CLUSTER_NAME_ALREADY_EXISTS, clusterInfo.getClusterName());
        }

        return clusterService.save(clusterInfo) ? R.succeed() : R.failed();
    }

    @SaCheckPermission("system:cluster:update")
    @PutMapping
    public R<Void> update(@Validated @RequestBody ClusterInfo clusterInfo) {
        if (!clusterService.checkClusterNameUnique(clusterInfo)) {
            return R.failed(Status.CLUSTER_NAME_ALREADY_EXISTS, clusterInfo.getClusterName());
        }

        return clusterService.updateById(clusterInfo) ? R.succeed() : R.failed();
    }

    @SaCheckPermission("system:cluster:delete")
    @DeleteMapping("/{clusterIds}")
    public R<Void> delete(@PathVariable Integer[] clusterIds) {
        return clusterService.deleteClusterByIds(clusterIds) > 0 ? R.succeed() : R.failed();
    }

    @SaIgnore
    @PostMapping("/check")
    public R<Void> check(@Validated @RequestBody ClusterInfo clusterInfo) {
        return clusterService.checkClusterHeartbeatStatus(clusterInfo) ? R.succeed() : R.failed();
    }
}
