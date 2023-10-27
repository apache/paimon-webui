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

package org.apache.paimon.web.flink.task;

import org.apache.paimon.web.common.data.vo.SubmitResult;
import org.apache.paimon.web.task.SubmitTask;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.RowData;
import org.apache.flink.types.Row;
import org.apache.flink.util.CloseableIterator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FlinkTask implements SubmitTask {
    private final StreamExecutionEnvironment env;
    private final TableEnvironment tEnv;
    private static final String nullColumn = "";

    public FlinkTask(StreamExecutionEnvironment env, TableEnvironment tEnv) {
        this.env = env;
        this.tEnv = tEnv;
    }

    public void executeDDL(String statement) throws Exception {
        tEnv.executeSql(statement);
    }

    @Override
    public SubmitResult execute(String statement) throws Exception {
        TableResult tableResult = tEnv.executeSql(statement);
        List<String> columns = tableResult.getResolvedSchema().getColumnNames();
        try (CloseableIterator<Row> it = tableResult.collect()) {
            List<Map<String, Object>> rows = rowsToList(columns, it);
            return SubmitResult.builder().data(rows).build();
        }
    }

    public static List<Map<String, Object>> rowsToList(
            List<String> columns, CloseableIterator<Row> it) {
        List<Map<String, Object>> rows = new ArrayList<>();
        while (it.hasNext()) {
            Map<String, Object> map = new LinkedHashMap<>();
            Row row = it.next();
            for (int i = 0; i < row.getArity(); ++i) {
                Object field = row.getField(i);
                if (field == null) {
                    map.put(columns.get(i), nullColumn);
                } else {
                    map.put(columns.get(i), field.toString());
                }
            }
            rows.add(map);
        }
        return rows;
    }

    public static List<Map<String, Object>> rowDatasToList(
            List<String> columns, List<RowData> rowDataList) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (RowData rowData : rowDataList) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (int i = 0; i < rowData.getArity(); ++i) {
                if (rowData instanceof GenericRowData) {
                    GenericRowData data = (GenericRowData) rowData;
                    Object field = data.getField(i);
                    if (field == null) {
                        map.put(columns.get(i), nullColumn);
                    } else {
                        map.put(columns.get(i), field.toString());
                    }
                } else {
                    throw new IllegalArgumentException("rowData is not GenericData");
                }
            }
            rows.add(map);
        }
        return rows;
    }

    @Override
    public boolean stop(String statement) throws Exception {
        return false;
    }

    @Override
    public boolean checkStatus() {
        try {
            execute("select 1");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
