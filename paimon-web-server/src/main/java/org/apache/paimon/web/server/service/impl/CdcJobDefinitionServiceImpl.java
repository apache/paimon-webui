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
import org.apache.paimon.utils.JsonSerdeUtil;
import org.apache.paimon.web.api.action.context.ActionContext;
import org.apache.paimon.web.api.action.context.factory.ActionContextFactoryServiceLoadUtil;
import org.apache.paimon.web.api.action.context.factory.FlinkCdcActionContextFactory;
import org.apache.paimon.web.api.action.service.ActionService;
import org.apache.paimon.web.api.action.service.FlinkCdcActionService;
import org.apache.paimon.web.api.enums.FlinkCdcType;
import org.apache.paimon.web.server.data.dto.CdcJobDefinitionDTO;
import org.apache.paimon.web.server.data.dto.CdcJobSubmitDTO;
import org.apache.paimon.web.server.data.model.CdcJobDefinition;
import org.apache.paimon.web.server.data.result.PageR;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.data.vo.UserVO;
import org.apache.paimon.web.server.mapper.CdcJobDefinitionMapper;
import org.apache.paimon.web.server.service.CdcJobDefinitionService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.paimon.web.server.util.ObjectMapperUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/** CdcJobDefinitionServiceImpl. */
@Service
public class CdcJobDefinitionServiceImpl
        extends ServiceImpl<CdcJobDefinitionMapper, CdcJobDefinition>
        implements CdcJobDefinitionService {

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
        String description = cdcJobDefinition.getDescription();

        Map<String, Object> graphMap = ObjectMapperUtils.fromJSON(description, new TypeReference<Map<String, Object>>() {
        });
        Integer cdcType = cdcJobDefinition.getCdcType();
        ActionService actionService = new FlinkCdcActionService();
//        FlinkCdcActionContextFactory factory = ActionContextFactoryServiceLoadUtil.getFlinkCdcActionContextFactory();
//        ActionContext actionContext = factory.getActionContext();
//        try {
//            actionService.execute(actionContext);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        return R.succeed();
    }
}
