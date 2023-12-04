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

import org.apache.paimon.web.server.data.model.SelectHistory;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.vo.SelectHistoryVO;
import org.apache.paimon.web.server.service.SelectHistoryService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/** Select history api controller. */
@Slf4j
@RestController
@RequestMapping("/api/select/history")
public class SelectHistoryController {

    @Autowired private SelectHistoryService selectHistoryService;

    /**
     * Handles the GET request to retrieve a paginated list of selection history records.
     *
     * @param current The current page number to be retrieved.
     * @param size The number of records per page.
     * @return A custom response object containing the list of selection history view objects.
     */
    @GetMapping("/list")
    public R<List<SelectHistoryVO>> listHistory(int current, int size) {
        List<SelectHistory> selectHistories = selectHistoryService.listHistoryByPage(current, size);
        List<SelectHistoryVO> selectHistoryVOList =
                selectHistories.stream()
                        .map(this::convertToSelectHistoryVO)
                        .collect(Collectors.toList());
        return R.succeed(selectHistoryVOList);
    }

    /**
     * Converts a SelectHistory entity to a SelectHistoryVO view object.
     *
     * @param selectHistory The SelectHistory entity to convert.
     * @return The corresponding SelectHistoryVO view object.
     */
    private SelectHistoryVO convertToSelectHistoryVO(SelectHistory selectHistory) {
        return SelectHistoryVO.builder()
                .taskType(selectHistory.getTaskType())
                .isStreaming(selectHistory.isStreaming())
                .sessionId(selectHistory.getSessionId())
                .statements(selectHistory.getStatements())
                .createTime(selectHistory.getCreateTime())
                .build();
    }
}
