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

import org.apache.paimon.web.server.data.model.History;
import org.apache.paimon.web.server.mapper.HistoryMapper;
import org.apache.paimon.web.server.service.HistoryService;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/** The implementation of {@link HistoryService}. */
@Service
public class HistoryServiceImpl extends ServiceImpl<HistoryMapper, History>
        implements HistoryService {

    @Autowired private HistoryMapper historyMapper;

    @Override
    public boolean saveHistory(History selectHistory) {
        return this.save(selectHistory);
    }

    @Override
    public List<History> listHistories(IPage<History> page, History history) {
        return historyMapper.listHistories(page, history);
    }
}
