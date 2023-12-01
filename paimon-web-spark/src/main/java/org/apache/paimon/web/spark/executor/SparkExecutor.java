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

package org.apache.paimon.web.spark.executor;

import org.apache.paimon.web.common.executor.Executor;
import org.apache.paimon.web.common.result.SubmitResult;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructField;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The spark implementation of the {@link Executor}. */
public class SparkExecutor implements Executor {

    private final SparkSession sparkSession;

    public SparkExecutor(SparkSession sparkSession) {
        this.sparkSession = sparkSession;
    }

    @Override
    public SubmitResult executeSql(String multiStatement) throws Exception {
        // TODO parse spark multiStatement,if multiple execution sql are passed in.
        List<Row> rows = sparkSession.sql(multiStatement).collectAsList();
        List<Map<String, Object>> result = new ArrayList<>();
        rows.stream()
                .forEach(
                        row -> {
                            Map<String, Object> map = new LinkedHashMap<>();
                            row.schema()
                                    .foreach(
                                            (StructField field) -> {
                                                map.put(
                                                        field.name(),
                                                        row.getAs(field.name()).toString());
                                                return field;
                                            });
                            result.add(map);
                        });
        return SubmitResult.builder().data(result).build();
    }

    @Override
    public boolean stop(String jobId, boolean withSavepoint, boolean withDrain) throws Exception {
        // TODO need to be implemented here
        return false;
    }
}
