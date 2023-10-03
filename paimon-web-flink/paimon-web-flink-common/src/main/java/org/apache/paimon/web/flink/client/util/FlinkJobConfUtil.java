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

package org.apache.paimon.web.flink.client.util;

import org.apache.paimon.web.flink.client.db.DBConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/** flink job task information configuration to obtain tool class. */
public class FlinkJobConfUtil {

    private static final Logger log = LoggerFactory.getLogger(FlinkJobConfUtil.class);

    /**
     * get flink job SQL statements.
     *
     * @param id flink job task id
     * @return SQL statements
     * @throws SQLException
     */
    private static String getJobTaskConfigSql(Integer id) throws SQLException {
        if (id == null) {
            throw new SQLException("Specify the flink job task ID");
        }
        return "SELECT job_name, execution_runtime_mode, checkpoint_path,checkpoint_interval, savepoint_path, parallelism, flink_sql, other_params FROM flink_job_task WHERE id = "
                + id;
    }

    /**
     * get flink job task config info.
     *
     * @param id flink job task id
     * @param config Database connection information configuration
     * @return flinkJobInfoMap
     */
    public static Map<String, String> getJobTaskConfig(Integer id, DBConfig config) {
        Map<String, String> flinkJobInfoMap = new HashMap<>();
        try {
            flinkJobInfoMap = DBUtil.executeSql(getJobTaskConfigSql(id), config);
        } catch (IOException | SQLException e) {
            log.error(
                    "{} --> Get FlinkSQL configuration exception, ID is {}, connection information is: {}, exception information is: {} ",
                    LocalDateTime.now(),
                    id,
                    config.toString(),
                    e.getMessage(),
                    e);
        }
        return flinkJobInfoMap;
    }
}
