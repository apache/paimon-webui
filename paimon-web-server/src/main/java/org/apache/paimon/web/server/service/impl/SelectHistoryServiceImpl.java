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

package org.apache.paimon.web.server.service.impl;

import org.apache.paimon.web.server.data.model.SelectHistory;
import org.apache.paimon.web.server.mapper.SelectHistoryMapper;
import org.apache.paimon.web.server.service.SelectHistoryService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/** The implementation of {@link SelectHistoryService}. */
@Service
public class SelectHistoryServiceImpl extends ServiceImpl<SelectHistoryMapper, SelectHistory>
        implements SelectHistoryService {

    private static final String PIPELINE_NAME_PATTERN = "(?i)SET 'pipeline\\.name' = '[^']+';";

    @Autowired private SelectHistoryMapper selectHistoryMapper;

    @Override
    public boolean saveSelectHistory(SelectHistory selectHistory) {
        return this.save(selectHistory);
    }

    @Override
    public List<SelectHistory> listHistoryByPage(int current, int size) {
        QueryWrapper<SelectHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        Page<SelectHistory> page = new Page<>(current, size);
        IPage<SelectHistory> selectHistoryPage = selectHistoryMapper.selectPage(page, queryWrapper);
        List<SelectHistory> records = selectHistoryPage.getRecords();
        records.forEach(
                record -> {
                    String content = record.getStatements();
                    String updatedContent = content.replaceAll(PIPELINE_NAME_PATTERN, "").trim();
                    record.setStatements(updatedContent);
                });
        return records;
    }
}
