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

package org.apache.paimon.web.server.data.model.cdc;

import org.apache.paimon.web.common.util.JSONUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** CdcGraph. */
@Getter
public class CdcGraph {

    private CdcNode source;

    private CdcNode target;

    public static CdcGraph fromCdcGraphJsonString(String json) {
        CdcGraph cdcGraph = new CdcGraph();
        try {
            List<JsonNode> edges = new ArrayList<>();
            List<JsonNode> nodes = new ArrayList<>();
            ObjectMapper objectMapper = JSONUtils.getObjectMapper();
            JsonNode cdcGraphJson = objectMapper.readTree(json);
            ArrayNode cellsJson = (ArrayNode) cdcGraphJson.findValue("cells");
            cellsJson.forEach(
                    e -> {
                        String shape = e.get("shape").asText();
                        if ("dag-edge".equals(shape)) {
                            edges.add(e);
                        } else {
                            nodes.add(e);
                        }
                    });
            if (edges.size() != 1 && nodes.size() != 2) {
                throw new RuntimeException("The number of cdc graph nodes is abnormal.");
            }
            JsonNode jsonNode = edges.get(0);
            JsonNode sourceTypeJson = jsonNode.get("source");
            String sourceType = sourceTypeJson.get("cell").asText();
            JsonNode targetTypeJson = jsonNode.findValue("target");
            String targetType = targetTypeJson.get("cell").asText();
            CdcNode source = new CdcNode();
            CdcNode target = new CdcNode();
            for (JsonNode node : nodes) {
                ObjectNode data = (ObjectNode) node.get("data");
                String type = node.get("id").asText();
                if (Objects.equals(sourceType, type)) {
                    source.setType(type);
                    source.setData(data);
                }
                if (Objects.equals(targetType, type)) {
                    target.setType(type);
                    target.setData(data);
                }
            }
            cdcGraph.source = source;
            cdcGraph.target = target;
        } catch (Exception e) {
            throw new RuntimeException("CdcGraph is not supported:" + e.getMessage(), e);
        }
        return cdcGraph;
    }
}
