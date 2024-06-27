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
import org.apache.paimon.web.server.service.impl.ClusterServiceImpl;
import org.apache.paimon.web.server.util.ObjectMapperUtils;
import org.apache.paimon.web.server.util.SpringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Test for {@link ClusterController}. */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClusterControllerTest extends ControllerTestBase {

    private static final String clusterPath = "/api/cluster";

    private static final int clusterId = 1;
    private static final String clusterName = "flink_test_cluster";

    @Test
    @Order(1)
    public void testAddCluster() throws Exception {
        ClusterInfo cluster = new ClusterInfo();
        cluster.setId(clusterId);
        cluster.setClusterName(clusterName);
        cluster.setHost("127.0.0.1");
        cluster.setPort(8083);
        cluster.setType("Flink");
        cluster.setEnabled(true);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(clusterPath)
                                .cookie(cookie)
                                .content(ObjectMapperUtils.toJSON(cluster))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(2)
    public void testGetCluster() throws Exception {
        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(clusterPath + "/" + clusterId)
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<ClusterInfo> r =
                ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<ClusterInfo>>() {});
        assertEquals(200, r.getCode());
        assertNotNull(r.getData());
        assertEquals(clusterName, r.getData().getClusterName());
        assertEquals("127.0.0.1", r.getData().getHost());
        assertEquals(8083, r.getData().getPort());
        assertEquals("Flink", r.getData().getType());
        assertTrue(r.getData().getEnabled());
    }

    @Test
    @Order(3)
    public void testListClusters() throws Exception {
        List<ClusterInfo> clustersWithoutConditions = listClusters("");
        assertTrue(clustersWithoutConditions.size() > 0);
        ClusterInfo clusterInfo = clustersWithoutConditions.get(0);
        assertEquals(clusterName, clusterInfo.getClusterName());
        assertEquals("127.0.0.1", clusterInfo.getHost());
        assertEquals(8083, clusterInfo.getPort());
        assertEquals("Flink", clusterInfo.getType());
        assertTrue(clusterInfo.getEnabled());

        List<ClusterInfo> clustersWithConditionsFlink = listClusters("Flink");
        assertTrue(clustersWithConditionsFlink.size() > 0);
        ClusterInfo clusterInfo1 = clustersWithConditionsFlink.get(0);
        assertEquals(clusterName, clusterInfo1.getClusterName());
        assertEquals("127.0.0.1", clusterInfo1.getHost());
        assertEquals(8083, clusterInfo1.getPort());
        assertEquals("Flink", clusterInfo1.getType());
        assertTrue(clusterInfo1.getEnabled());

        List<ClusterInfo> clustersWithConditionsSpark = listClusters("Spark");
        assertEquals(0, clustersWithConditionsSpark.size());
    }

    @Test
    @Order(4)
    public void testUpdateCluster() throws Exception {
        String newClusterName = clusterName + "-edit";
        ClusterInfo cluster = new ClusterInfo();
        cluster.setId(clusterId);
        cluster.setClusterName(newClusterName);
        cluster.setHost("127.0.0.1");
        cluster.setPort(8083);
        cluster.setType("Flink");
        cluster.setEnabled(true);

        mockMvc.perform(
                        MockMvcRequestBuilders.put(clusterPath)
                                .cookie(cookie)
                                .content(ObjectMapperUtils.toJSON(cluster))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(clusterPath + "/" + clusterId)
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<ClusterInfo> r =
                ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<ClusterInfo>>() {});
        assertEquals(200, r.getCode());
        assertNotNull(r.getData());
        assertEquals(r.getData().getClusterName(), newClusterName);
    }

    @Test
    @Order(5)
    public void testDeleteCluster() throws Exception {
        String delResponseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.delete(
                                                clusterPath + "/" + clusterId + "," + clusterId)
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<?> result = ObjectMapperUtils.fromJSON(delResponseString, R.class);
        assertEquals(200, result.getCode());
    }

    private List<ClusterInfo> listClusters(String params) throws Exception {
        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(clusterPath + "/list")
                                        .cookie(cookie)
                                        .param("type", params)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        PageR<ClusterInfo> r =
                ObjectMapperUtils.fromJSON(
                        responseString, new TypeReference<PageR<ClusterInfo>>() {});

        assertTrue(
                r.getData() != null
                        && ((r.getTotal() > 0 && r.getData().size() > 0)
                                || (r.getTotal() == 0 && r.getData().size() == 0)));
        return r.getData();
    }

    @Test
    @Order(6)
    public void testCheckClusterOfFlinkSqlGateway() throws Exception {
        ClusterInfo cluster =
                ClusterInfo.builder()
                        .clusterName("test")
                        .enabled(true)
                        .type("Flink SQL GateWay")
                        .host("192.168.226.123")
                        .port(8083)
                        .build();

        String delResponseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(clusterPath + "/" + "check")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(cluster))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<?> result = ObjectMapperUtils.fromJSON(delResponseString, R.class);
        assertEquals(200, result.getCode());
    }

    @Test
    @Order(6)
    public void testCheckClusterOfFlinkSession() throws Exception {
        ClusterInfo cluster =
                ClusterInfo.builder()
                        .clusterName("test")
                        .enabled(true)
                        .type("Flink Session")
                        .host("192.168.226.123")
                        .port(34407)
                        .build();

        String delResponseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(clusterPath + "/" + "check")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(cluster))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<?> result = ObjectMapperUtils.fromJSON(delResponseString, R.class);
        assertEquals(200, result.getCode());
    }

    @Test
    @Order(7)
    public void testCheckClusterStatus() {
        ClusterServiceImpl bean = SpringUtils.getBean(ClusterServiceImpl.class);
        bean.checkClusterStatus();
    }
}
