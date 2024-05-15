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

import cn.hutool.core.util.EnumUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.paimon.predicate.In;
import org.apache.paimon.utils.JsonSerdeUtil;
import org.apache.paimon.web.api.action.context.ActionContext;
import org.apache.paimon.web.api.action.context.factory.ActionContextFactoryServiceLoadUtil;
import org.apache.paimon.web.api.action.context.factory.FlinkCdcActionContextFactory;
import org.apache.paimon.web.api.action.context.options.FlinkCdcOptions;
import org.apache.paimon.web.api.action.service.ActionService;
import org.apache.paimon.web.api.action.service.FlinkCdcActionService;
import org.apache.paimon.web.api.enums.FlinkCdcType;
import org.apache.paimon.web.common.util.JSONUtils;
import org.apache.paimon.web.server.data.dto.CdcJobDefinitionDTO;
import org.apache.paimon.web.server.data.dto.CdcJobSubmitDTO;
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.model.CdcJobDefinition;
import org.apache.paimon.web.server.data.model.cdc.CdcGraph;
import org.apache.paimon.web.server.data.model.cdc.CdcNode;
import org.apache.paimon.web.server.data.result.PageR;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.data.vo.UserVO;
import org.apache.paimon.web.server.mapper.CdcJobDefinitionMapper;
import org.apache.paimon.web.server.service.CatalogService;
import org.apache.paimon.web.server.service.CdcJobDefinitionService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.paimon.web.server.util.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CdcJobDefinitionServiceImpl.
 */
@Service
public class CdcJobDefinitionServiceImpl
        extends ServiceImpl<CdcJobDefinitionMapper, CdcJobDefinition>
        implements CdcJobDefinitionService {

    @Autowired
    private CatalogService catalogService;

    @Override
    public R<Void> create(CdcJobDefinitionDTO cdcJobDefinitionDTO) {
        String name = cdcJobDefinitionDTO.getName();
        QueryWrapper<CdcJobDefinition> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        if (baseMapper.exists(queryWrapper)) {
            return R.failed(Status.CDC_JOB_EXIST_ERROR);
        }
        CdcJobDefinition cdcJobDefinition =
                CdcJobDefinition.builder()
                        .name(cdcJobDefinitionDTO.getName())
                        .config(cdcJobDefinitionDTO.getConfig())
                        .cdcType(cdcJobDefinitionDTO.getCdcType())
                        .createUser(cdcJobDefinitionDTO.getCreateUser())
                        .description(cdcJobDefinitionDTO.getDescription())
                        .build();
        baseMapper.insert(cdcJobDefinition);
        return R.succeed();
    }

    @Override
    public PageR<CdcJobDefinition> listAll(boolean withConfig, long currentPage, long pageSize) {
        Page<CdcJobDefinition> page = new Page<>(currentPage, pageSize);
        QueryWrapper<CdcJobDefinition> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(
                "id",
                "name",
                "cdc_type",
                "create_user",
                "description",
                "update_time",
                "create_time");
        if (!withConfig) {
            queryWrapper.select(
                    "name",
                    "id",
                    "description",
                    "cdc_type",
                    "create_user",
                    "update_time",
                    "create_time");
        }
        Page<CdcJobDefinition> resPage = baseMapper.selectPage(page, queryWrapper);
        return new PageR<>(resPage.getTotal(), true, resPage.getRecords());
    }

    @Override
    public R<Void> update(CdcJobDefinitionDTO cdcJobDefinitionDTO) {
        QueryWrapper<CdcJobDefinition> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", cdcJobDefinitionDTO.getId());
        if (!baseMapper.exists(queryWrapper)) {
            return R.failed(Status.CDC_JOB_NO_EXIST_ERROR);
        }
        CdcJobDefinition cdcJobDefinition =
                CdcJobDefinition.builder()
                        .name(cdcJobDefinitionDTO.getName())
                        .config(cdcJobDefinitionDTO.getConfig())
                        .cdcType(cdcJobDefinitionDTO.getCdcType())
                        .createUser(cdcJobDefinitionDTO.getCreateUser())
                        .description(cdcJobDefinitionDTO.getDescription())
                        .build();
        cdcJobDefinition.setId(cdcJobDefinitionDTO.getId());
        baseMapper.updateById(cdcJobDefinition);
        return R.succeed();
    }

    @Override
    public R<Void> submit(Integer id, CdcJobSubmitDTO cdcJobSubmitDTO) {
        CdcJobDefinition cdcJobDefinition = baseMapper.selectById(id);
        String config = cdcJobDefinition.getConfig();
        FlinkCdcType flinkCdcType = FlinkCdcType.valueOf(cdcJobDefinition.getCdcType());
        ActionService actionService = new FlinkCdcActionService();
        CdcGraph cdcGraph = CdcGraph.fromCdcGraphJsonString(config);
        FlinkCdcActionContextFactory factory = ActionContextFactoryServiceLoadUtil
                .getFlinkCdcActionContextFactory(cdcGraph.getSource().getType(), cdcGraph.getTarget().getType(), flinkCdcType);
        ObjectNode actionConfigs = JSONUtils.createObjectNode();
        actionConfigs.put(FlinkCdcOptions.SESSION_URL, cdcJobSubmitDTO.getFlinkSessionUrl());
        handleCdcGraphNodeData(actionConfigs,cdcGraph.getSource());
        handleCdcGraphNodeData(actionConfigs,cdcGraph.getTarget());
        ActionContext actionContext = factory.getActionContext(actionConfigs);
        try {
            actionService.execute(actionContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return R.succeed();
    }

    private void handleCdcGraphNodeData(ObjectNode actionConfigs,CdcNode node){
        String type = node.getType();
        switch (type){
            case "Paimon":
                handlePaimonNodeData(actionConfigs,node.getData());
                break;
        }
    }

    private void handlePaimonNodeData(ObjectNode actionConfigs,ObjectNode paimonData){
        Integer catalog = JSONUtils.getInteger(paimonData, "catalog");
        CatalogInfo catalogInfo = catalogService.getById(catalog);
        List<String> catalogConfList = new ArrayList<>();
        actionConfigs.put(FlinkCdcOptions.WAREHOUSE,catalogInfo.getWarehouse());
        actionConfigs.put(FlinkCdcOptions.TABLE,JSONUtils.getString(paimonData,"table_name"));
        actionConfigs.put(FlinkCdcOptions.DATABASE,JSONUtils.getString(paimonData,"database"));
        actionConfigs.put(FlinkCdcOptions.PRIMARY_KEYS,JSONUtils.getString(paimonData,"primary_key"));
        actionConfigs.putPOJO(FlinkCdcOptions.CATALOG_CONF,catalogConfList);
    }
}
