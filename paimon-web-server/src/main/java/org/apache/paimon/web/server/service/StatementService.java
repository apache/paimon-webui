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

import org.apache.paimon.web.server.data.model.StatementInfo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** Statement Service. */
public interface StatementService extends IService<StatementInfo> {

    /**
     * Check if the statement name is unique.
     *
     * @param statementInfo the statement to check
     * @return true if unique, false otherwise
     */
    boolean checkStatementNameUnique(StatementInfo statementInfo);

    /**
     * Saves a statement information entity.
     *
     * @param statementInfo The statement information to save.
     * @return true if the statement was saved successfully, false otherwise.
     */
    boolean saveStatement(StatementInfo statementInfo);

    /**
     * Retrieves a paginated list of statement information entities.
     *
     * @param page the pagination information
     * @param statement the filter criteria
     * @return A list of StatementInfo entities for the specified page
     */
    List<StatementInfo> listStatements(
            IPage<StatementInfo> page, @Param("statement") StatementInfo statement);
}
