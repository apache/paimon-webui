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

import org.apache.paimon.web.server.data.model.History;
import org.apache.paimon.web.server.data.result.PageR;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.service.HistoryService;
import org.apache.paimon.web.server.util.PageSupport;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** History api controller. */
@RestController
@RequestMapping("/api/history")
public class HistoryController {

    @Autowired private HistoryService historyService;

    @SaCheckPermission("playground:history:query")
    @GetMapping("/{id}")
    public R<History> getStatement(@PathVariable("id") Integer id) {
        return R.succeed(historyService.getById(id));
    }

    @SaCheckPermission("playground:history:list")
    @GetMapping("/list")
    public PageR<History> listSelectHistories(History selectHistory) {
        IPage<History> page = PageSupport.startPage();
        List<History> selectHistories = historyService.listHistories(page, selectHistory);
        return PageR.<History>builder()
                .success(true)
                .total(page.getTotal())
                .data(selectHistories)
                .build();
    }

    @SaCheckPermission("playground:history:query")
    @GetMapping("/all")
    public R<List<History>> all() {
        return R.succeed(historyService.list());
    }
}
