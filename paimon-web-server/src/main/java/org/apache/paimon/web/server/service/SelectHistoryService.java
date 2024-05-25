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

package org.apache.paimon.web.server.service;

import org.apache.paimon.web.server.data.model.SelectHistory;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** SelectHistory Service. */
public interface SelectHistoryService extends IService<SelectHistory> {

    /**
     * Saves a selection history record.
     *
     * @param selectHistory The selection history record to save
     * @return true if the record was saved successfully, false otherwise
     */
    boolean saveSelectHistory(SelectHistory selectHistory);

    /**
     * Retrieves a paginated list of selection history records.
     *
     * @param page the pagination information
     * @param history the filter criteria
     * @return A list of SelectHistory entities for the specified page
     */
    List<SelectHistory> listSelectHistories(
            IPage<SelectHistory> page, @Param("history") SelectHistory history);
}
