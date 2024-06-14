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

import cn.dev33.satoken.stp.StpUtil;
import org.apache.paimon.web.api.action.context.ActionContext;
import org.apache.paimon.web.api.action.context.factory.ActionContextFactoryServiceLoadUtil;
import org.apache.paimon.web.api.action.context.factory.FlinkCdcActionContextFactory;
import org.apache.paimon.web.api.action.context.options.FlinkCdcOptions;
import org.apache.paimon.web.api.action.service.ActionService;
import org.apache.paimon.web.api.action.service.FlinkCdcActionService;
import org.apache.paimon.web.api.catalog.PaimonServiceFactory;
import org.apache.paimon.web.api.enums.FlinkCdcSyncType;
import org.apache.paimon.web.common.util.JSONUtils;
import org.apache.paimon.web.server.data.dto.CdcJobDefinitionDTO;
import org.apache.paimon.web.server.data.dto.CdcJobSubmitDTO;
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.model.CdcJobDefinition;
import org.apache.paimon.web.server.data.model.ClusterInfo;
import org.apache.paimon.web.server.data.model.cdc.CdcGraph;
import org.apache.paimon.web.server.data.model.cdc.CdcNode;
import org.apache.paimon.web.server.data.result.PageR;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.data.vo.UserInfoVO;
import org.apache.paimon.web.server.mapper.CdcJobDefinitionMapper;
import org.apache.paimon.web.server.service.CatalogService;
import org.apache.paimon.web.server.service.CdcJobDefinitionService;
import org.apache.paimon.web.server.service.ClusterService;
import org.apache.paimon.web.server.service.UserService;
import org.apache.paimon.web.server.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** CdcJobDefinitionServiceImpl. */
@Service
public class CdcJobDefinitionServiceImpl
        extends ServiceImpl<CdcJobDefinitionMapper, CdcJobDefinition>
        implements CdcJobDefinitionService {

    @Autowired private CatalogService catalogService;

    @Autowired private ClusterService clusterService;
    @Autowired private UserService userService;

    @Override
    public R<Void> create(CdcJobDefinitionDTO cdcJobDefinitionDTO) {
        String name = cdcJobDefinitionDTO.getName();
        QueryWrapper<CdcJobDefinition> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        if (baseMapper.exists(queryWrapper)) {
            return R.failed(Status.CDC_JOB_EXIST_ERROR);
        }
        String jobCreateUser;
        if(StringUtils.isBlank(cdcJobDefinitionDTO.getCreateUser())){
            int loginId = StpUtil.getLoginIdAsInt();
            UserInfoVO   userInfoVo  = (UserInfoVO) StpUtil.getSession().get(Integer.toString(loginId));
            jobCreateUser=userInfoVo.getUser().getUsername();
        }else {
            jobCreateUser=cdcJobDefinitionDTO.getCreateUser();
        }
        CdcJobDefinition cdcJobDefinition =
                CdcJobDefinition.builder()
                        .name(cdcJobDefinitionDTO.getName())
                        .config(cdcJobDefinitionDTO.getConfig())
                        .cdcType(cdcJobDefinitionDTO.getCdcType())
                        .createUser(jobCreateUser)
                        .description(cdcJobDefinitionDTO.getDescription())
                        .build();
        baseMapper.insert(cdcJobDefinition);
        return R.succeed();
    }

    @Override
    public PageR<CdcJobDefinition> listAll(
            String name, boolean withConfig, long currentPage, long pageSize) {
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
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
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
        FlinkCdcSyncType flinkCdcSyncType = FlinkCdcSyncType.valueOf(cdcJobDefinition.getCdcType());
        ActionService actionService = new FlinkCdcActionService();
        CdcGraph cdcGraph = CdcGraph.fromCdcGraphJsonString(config);
        FlinkCdcActionContextFactory factory =
                ActionContextFactoryServiceLoadUtil.getFlinkCdcActionContextFactory(
                        cdcGraph.getSource().getType(),
                        cdcGraph.getTarget().getType(),
                        flinkCdcSyncType);
        ObjectNode actionConfigs = JSONUtils.createObjectNode();
        String clusterId = cdcJobSubmitDTO.getClusterId();
        ClusterInfo clusterInfo = clusterService.getById(clusterId);
        actionConfigs.put(
                FlinkCdcOptions.SESSION_URL,
                String.format("http://%s:%s", clusterInfo.getHost(), clusterInfo.getPort()));
        handleCdcGraphNodeData(actionConfigs, cdcGraph.getSource());
        handleCdcGraphNodeData(actionConfigs, cdcGraph.getTarget());
        ActionContext actionContext = factory.getActionContext(actionConfigs);
        try {
            actionService.execute(actionContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return R.succeed();
    }

    private void handleCdcGraphNodeData(ObjectNode actionConfigs, CdcNode node) {
        String type = node.getType();
        switch (type) {
            case "Paimon":
                handlePaimonNodeData(actionConfigs, node.getData());
                break;
            case "MySQL":
                handleMysqlNodeData(actionConfigs, node.getData());
                break;
        }
    }

    private void handleMysqlNodeData(ObjectNode actionConfigs, ObjectNode mysqlData) {
        String otherConfigs = JSONUtils.getString(mysqlData, "other_configs");
        List<String> mysqlConfList;
        if (StringUtils.isBlank(otherConfigs)) {
            mysqlConfList = new ArrayList<>();
        } else {
            mysqlConfList = new ArrayList<>(Arrays.asList(otherConfigs.split(";")));
        }
        mysqlConfList.add(buildKeyValueString("hostname", JSONUtils.getString(mysqlData, "host")));
        mysqlConfList.add(
                buildKeyValueString("username", JSONUtils.getString(mysqlData, "username")));
        mysqlConfList.add(buildKeyValueString("port", JSONUtils.getString(mysqlData, "port")));
        mysqlConfList.add(
                buildKeyValueString("database-name", JSONUtils.getString(mysqlData, "database")));
        mysqlConfList.add(
                buildKeyValueString("table-name", JSONUtils.getString(mysqlData, "table_name")));
        mysqlConfList.add(
                buildKeyValueString("password", JSONUtils.getString(mysqlData, "password")));
        actionConfigs.putPOJO(FlinkCdcOptions.MYSQL_CONF, mysqlConfList);
    }

    private void handlePaimonNodeData(ObjectNode actionConfigs, ObjectNode paimonData) {
        Integer catalog = JSONUtils.getInteger(paimonData, "catalog");
        CatalogInfo catalogInfo = catalogService.getById(catalog);
        actionConfigs.put(FlinkCdcOptions.WAREHOUSE, catalogInfo.getWarehouse());
        actionConfigs.put(FlinkCdcOptions.TABLE, JSONUtils.getString(paimonData, "table_name"));
        actionConfigs.put(FlinkCdcOptions.DATABASE, JSONUtils.getString(paimonData, "database"));
        actionConfigs.put(
                FlinkCdcOptions.PRIMARY_KEYS, JSONUtils.getString(paimonData, "primary_key"));
        String otherConfigs = JSONUtils.getString(paimonData, "other_configs2");
        if (StringUtils.isBlank(otherConfigs)) {
            actionConfigs.putPOJO(FlinkCdcOptions.TABLE_CONF, new ArrayList<>());
        } else {
            actionConfigs.putPOJO(
                    FlinkCdcOptions.TABLE_CONF, Arrays.asList(otherConfigs.split(";")));
        }
        List<String> catalogConfList = new ArrayList<>();
        Map<String, String> options = catalogInfo.getOptions();
        PaimonServiceFactory.convertToPaimonOptions(options)
                .toMap()
                .forEach(
                        (k, v) -> {
                            catalogConfList.add(buildKeyValueString(k, v));
                        });

        actionConfigs.putPOJO(FlinkCdcOptions.CATALOG_CONF, catalogConfList);
    }

    private String buildKeyValueString(String key, String value) {
        return key + "=" + value;
    }
}
