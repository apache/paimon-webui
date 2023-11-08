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

package org.apache.paimon.web.task;

import org.apache.paimon.shade.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.paimon.web.common.data.constant.SqlConstants;
import org.apache.paimon.web.common.data.vo.SubmitResult;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseTaskTests {
    private final ObjectMapper objectMapper = new ObjectMapper();
    List<Map<String, String>> data = new ArrayList<>();

    {
        data.add(
                new HashMap<String, String>() {
                    {
                        put("order_id", "1");
                        put("price", "2.0");
                    }
                });
        data.add(
                new HashMap<String, String>() {
                    {
                        put("order_id", "2");
                        put("price", "3.1");
                    }
                });
    }

    protected void assertTask(SubmitJob submitJob) throws Exception {
        SubmitResult submitResult = submitJob.execute(SqlConstants.VALIDATE_SQL);
        verifyData(submitResult.getData());
    }

    protected void verifyData(List<Map<String, Object>> dataList) {
        try {
            // Verify data is consistent
            Validate.isTrue(
                    objectMapper
                            .writeValueAsString(dataList)
                            .equalsIgnoreCase(objectMapper.writeValueAsString(data)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
