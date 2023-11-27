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

import org.apache.paimon.web.server.data.dto.CdcJobDefinitionDTO;
import org.apache.paimon.web.server.data.model.CdcJobDefinition;
import org.apache.paimon.web.server.data.result.PageR;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.service.CdcJobDefinitionService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/cdc-job-definition")
public class CdcJobDefinitionController {

    private CdcJobDefinitionService cdcJobDefinitionService;

    public CdcJobDefinitionController(CdcJobDefinitionService cdcJobDefinitionService) {
        this.cdcJobDefinitionService = cdcJobDefinitionService;
    }

    @PostMapping("create")
    public R<Void> createCdcJob(@RequestBody CdcJobDefinitionDTO cdcJobDefinitionDTO) {
        return cdcJobDefinitionService.create(cdcJobDefinitionDTO);
    }

    @PutMapping("update")
    public R<Void> updateCdcJob(@RequestBody CdcJobDefinitionDTO cdcJobDefinitionDTO) {
        return cdcJobDefinitionService.update(cdcJobDefinitionDTO);
    }

    @GetMapping("list")
    public PageR<CdcJobDefinition> listAllCdcJob(
            @RequestParam(required = false) boolean withConfig,
            @RequestParam long currentPage,
            @RequestParam long pageSize) {
        return cdcJobDefinitionService.listAll(withConfig, currentPage, pageSize);
    }

    @GetMapping("/{id}")
    public R<CdcJobDefinition> getById(@PathVariable Integer id) {
        CdcJobDefinition cdcJobDefinition = cdcJobDefinitionService.getById(id);
        if (cdcJobDefinition == null) {
            return R.failed();
        }
        return R.succeed(cdcJobDefinition);
    }

    @DeleteMapping("{id}")
    public R<Void> deleteById(@PathVariable Integer id) {
        cdcJobDefinitionService.removeById(id);
        return R.succeed();
    }
}
