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
package org.apache.paimon.web.server.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Test for CatalogController. */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskControllerTest {
    @Resource private TaskController taskController;
    List<Map<String, Object>> data = new ArrayList<>();

    @BeforeEach
    public void before() {
        data.add(Dict.create().set("order_id", "1").set("price", "2.0"));
        data.add(Dict.create().set("order_id", "2").set("price", "3.1"));
    }

    @Test
    public void testTaskSql() {
        String sql =
                "SELECT order_id, price FROM (VALUES (1, 2.0), (2, 3.1))  AS t (order_id, price)\n";
        List<Map<String, Object>> flinkData =
                taskController.submitExecuteSql(sql, "flink").getData();
        List<Map<String, Object>> sparkData =
                taskController.submitExecuteSql(sql, "spark").getData();
        ThreadUtil.sleep(100000 * 1000);
        verifyData(flinkData);
        verifyData(sparkData);
        taskController.submitExecuteSql(sql, "flink").getData();
        taskController.submitExecuteSql(sql, "spark").getData();
    }

    private void verifyData(List<Map<String, Object>> dataList) {
        assertEquals(new JSONArray(dataList).toString(), new JSONArray(data).toString());
    }
}
