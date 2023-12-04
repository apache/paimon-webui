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

import org.apache.paimon.web.server.data.dto.StatementDTO;
import org.apache.paimon.web.server.data.model.StatementInfo;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.data.vo.StatementVO;
import org.apache.paimon.web.server.service.StatementService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/** Statement api controller. */
@Slf4j
@RestController
@RequestMapping("/api/statement")
public class StatementController {

    @Autowired private StatementService statementService;

    /**
     * Saves a statement record received in the form of a StatementDTO.
     *
     * @param statementDTO The data transfer object containing the statement details.
     * @return A generic response object indicating the result of the save operation.
     */
    @PostMapping("/save")
    public R<Void> saveStatement(@RequestBody StatementDTO statementDTO) {
        try {
            StatementInfo statementInfo =
                    StatementInfo.builder()
                            .sessionId(statementDTO.getSessionId())
                            .statements(statementDTO.getStatements())
                            .isStreaming(statementDTO.isStreaming())
                            .taskType(statementDTO.getTaskType())
                            .build();
            return statementService.saveStatement(statementInfo) ? R.succeed() : R.failed();
        } catch (Exception e) {
            log.error("Exception with saving a statement.", e);
            return R.failed(Status.STATEMENT_SAVE_ERROR);
        }
    }

    /**
     * Retrieves a paginated list of statement records.
     *
     * @param current The current page number to be retrieved.
     * @param size The number of records per page.
     * @return A generic response object containing the list of statement view objects.
     */
    @GetMapping("/list")
    public R<List<StatementVO>> listStatements(int current, int size) {
        List<StatementInfo> statementInfos = statementService.listStatementsByPage(current, size);
        List<StatementVO> statementVOList =
                statementInfos.stream()
                        .map(this::convertToStatementVO)
                        .collect(Collectors.toList());
        return R.succeed(statementVOList);
    }

    /**
     * Converts a StatementInfo entity to a StatementVO view object.
     *
     * @param statementInfo The StatementInfo entity to convert.
     * @return The corresponding StatementVO view object.
     */
    private StatementVO convertToStatementVO(StatementInfo statementInfo) {
        return StatementVO.builder()
                .taskType(statementInfo.getTaskType())
                .isStreaming(statementInfo.isStreaming())
                .sessionId(statementInfo.getSessionId())
                .statements(statementInfo.getStatements())
                .createTime(statementInfo.getCreateTime())
                .build();
    }
}
