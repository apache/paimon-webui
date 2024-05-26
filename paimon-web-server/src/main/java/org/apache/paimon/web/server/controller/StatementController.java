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

import org.apache.paimon.web.server.data.model.StatementInfo;
import org.apache.paimon.web.server.data.result.PageR;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.service.StatementService;
import org.apache.paimon.web.server.util.PageSupport;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Statement api controller. */
@RestController
@RequestMapping("/api/statement")
public class StatementController {

    @Autowired private StatementService statementService;

    @SaCheckPermission("playground:statement:query")
    @GetMapping("/{id}")
    public R<StatementInfo> getStatement(@PathVariable("id") Integer id) {
        StatementInfo statementInfo = statementService.getById(id);
        if (statementInfo == null) {
            return R.failed(Status.STATEMENT_NOT_EXIST);
        }
        return R.succeed(statementInfo);
    }

    @SaCheckPermission("playground:statement:list")
    @GetMapping("/list")
    public PageR<StatementInfo> listStatements(StatementInfo statementInfo) {
        IPage<StatementInfo> page = PageSupport.startPage();
        List<StatementInfo> statementInfos = statementService.listStatements(page, statementInfo);
        return PageR.<StatementInfo>builder()
                .success(true)
                .total(page.getTotal())
                .data(statementInfos)
                .build();
    }

    @SaCheckPermission("playground:statement:query")
    @GetMapping("/all")
    public R<List<StatementInfo>> all() {
        return R.succeed(statementService.list());
    }

    @SaCheckPermission("playground:statement:add")
    @PostMapping
    public R<Void> add(@RequestBody StatementInfo statementInfo) {
        if (!statementService.checkStatementNameUnique(statementInfo)) {
            return R.failed(Status.STATEMENT_NAME_ALREADY_EXISTS, statementInfo);
        }
        return statementService.saveStatement(statementInfo) ? R.succeed() : R.failed();
    }

    @SaCheckPermission("playground:statement:update")
    @PutMapping
    public R<Void> update(@RequestBody StatementInfo statementInfo) {
        if (!statementService.checkStatementNameUnique(statementInfo)) {
            return R.failed(Status.STATEMENT_NAME_ALREADY_EXISTS, statementInfo);
        }
        return statementService.updateById(statementInfo) ? R.succeed() : R.failed();
    }

    @SaCheckPermission("system:cluster:delete")
    @DeleteMapping("/{statementId}")
    public R<Void> delete(@PathVariable Integer statementId) {
        return statementService.removeById(statementId) ? R.succeed() : R.failed();
    }
}
