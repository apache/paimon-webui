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

package org.apache.paimon.web.flink.utils;

import org.apache.paimon.web.flink.config.DBConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Database connection utility class. */
public class DBUtil {
    private static final Logger log = LoggerFactory.getLogger(DBUtil.class);

    private static Connection getConnection(DBConfig config) throws IOException {
        Connection conn = null;
        try {
            Class.forName(config.getDriver());
            conn =
                    DriverManager.getConnection(
                            config.getUrl(), config.getUsername(), config.getPassword());
        } catch (SQLException | ClassNotFoundException e) {
            log.error("getConnection error!", e);
        }
        return conn;
    }

    private static void close(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            log.error("close conn error!", e);
        }
    }

    public static Map<String, String> executeSql(String sql, DBConfig config)
            throws SQLException, IOException {
        Connection conn = getConnection(config);
        HashMap<String, String> map = new HashMap();
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            List<String> columnList = new ArrayList<>();
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                columnList.add(rs.getMetaData().getColumnLabel(i + 1));
            }
            if (rs.next()) {
                for (int i = 0; i < columnList.size(); i++) {
                    map.put(columnList.get(i), rs.getString(i + 1));
                }
            }
        }
        close(conn);
        return map;
    }
}
