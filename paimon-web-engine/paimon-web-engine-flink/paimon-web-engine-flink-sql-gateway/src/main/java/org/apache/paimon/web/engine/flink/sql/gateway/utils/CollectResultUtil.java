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

package org.apache.paimon.web.engine.flink.sql.gateway.utils;

import org.apache.paimon.web.engine.flink.common.result.ExecutionResult;

import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.gateway.rest.serde.ResultInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Collect result util. */
public class CollectResultUtil {

    public static ExecutionResult.Builder collectSqlGatewayResult(ResultInfo resultInfo) {
        List<RowData> data = resultInfo.getData();
        List<Map<String, Object>> results =
                rowDatasToList(resultInfo.getResultSchema().getColumnNames(), data);
        return ExecutionResult.builder().data(results);
    }

    private static List<Map<String, Object>> rowDatasToList(
            List<String> columns, List<RowData> rowDataList) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (RowData rowData : rowDataList) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (int i = 0; i < rowData.getArity(); ++i) {
                if (rowData instanceof GenericRowData) {
                    GenericRowData data = (GenericRowData) rowData;
                    Object field = data.getField(i);
                    if (Objects.nonNull(field)) {
                        map.put(columns.get(i), field.toString());
                    }
                } else {
                    throw new IllegalArgumentException("RowData is not GenericData.");
                }
            }
            rows.add(map);
        }
        return rows;
    }
}
