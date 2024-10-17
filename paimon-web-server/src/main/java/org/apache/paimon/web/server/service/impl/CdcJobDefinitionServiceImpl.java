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

import org.apache.paimon.web.api.action.context.ActionContext;
import org.apache.paimon.web.api.action.context.ActionExecutionResult;
import org.apache.paimon.web.api.action.context.factory.ActionContextFactoryServiceLoadUtil;
import org.apache.paimon.web.api.action.context.factory.FlinkCdcActionContextFactory;
import org.apache.paimon.web.api.action.context.options.FlinkCdcOptions;
import org.apache.paimon.web.api.action.service.ActionService;
import org.apache.paimon.web.api.action.service.FlinkCdcActionService;
import org.apache.paimon.web.api.catalog.PaimonServiceFactory;
import org.apache.paimon.web.api.enums.FlinkCdcDataSourceType;
import org.apache.paimon.web.api.enums.FlinkCdcSyncType;
import org.apache.paimon.web.api.enums.SyncMode;
import org.apache.paimon.web.common.util.JSONUtils;
import org.apache.paimon.web.engine.flink.common.status.JobStatus;
import org.apache.paimon.web.engine.flink.sql.gateway.client.SessionClusterClient;
import org.apache.paimon.web.engine.flink.sql.gateway.model.JobOverviewEntity;
import org.apache.paimon.web.engine.flink.sql.gateway.model.TriggerIdEntity;
import org.apache.paimon.web.server.data.dto.CdcJobDefinitionDTO;
import org.apache.paimon.web.server.data.dto.CdcJobSubmitDTO;
import org.apache.paimon.web.server.data.model.CatalogInfo;
import org.apache.paimon.web.server.data.model.CdcJobDefinition;
import org.apache.paimon.web.server.data.model.CdcJobLog;
import org.apache.paimon.web.server.data.model.ClusterInfo;
import org.apache.paimon.web.server.data.model.cdc.CdcGraph;
import org.apache.paimon.web.server.data.model.cdc.CdcNode;
import org.apache.paimon.web.server.data.result.PageR;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.data.vo.ActionExecutionResultVo;
import org.apache.paimon.web.server.data.vo.CdcJobDefinitionVO;
import org.apache.paimon.web.server.data.vo.UserInfoVO;
import org.apache.paimon.web.server.mapper.CdcJobDefinitionMapper;
import org.apache.paimon.web.server.service.CatalogService;
import org.apache.paimon.web.server.service.CdcJobDefinitionService;
import org.apache.paimon.web.server.service.CdcJobLogService;
import org.apache.paimon.web.server.service.ClusterService;
import org.apache.paimon.web.server.util.StringUtils;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.shaded.guava31.com.google.common.cache.CacheBuilder;
import org.apache.flink.shaded.guava31.com.google.common.cache.CacheLoader;
import org.apache.flink.shaded.guava31.com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/** CdcJobDefinitionServiceImpl. */
@Service
@Slf4j
public class CdcJobDefinitionServiceImpl
        extends ServiceImpl<CdcJobDefinitionMapper, CdcJobDefinition>
        implements CdcJobDefinitionService {

    @Autowired private CatalogService catalogService;
    @Autowired private ClusterService clusterService;
    @Autowired private CdcJobLogService cdcJobLogService;

    final LoadingCache<Integer, ReentrantLock> cacher =
            CacheBuilder.newBuilder()
                    .expireAfterAccess(1, TimeUnit.MINUTES)
                    .build(
                            new CacheLoader<Integer, ReentrantLock>() {
                                @Override
                                public ReentrantLock load(Integer id) {
                                    return new ReentrantLock();
                                }
                            });

    @Override
    public R<Void> create(CdcJobDefinitionDTO cdcJobDefinitionDTO) {
        // to valid config
        CdcGraph.fromCdcGraphJsonString(cdcJobDefinitionDTO.getConfig());
        // to update entity
        String name = cdcJobDefinitionDTO.getName();
        QueryWrapper<CdcJobDefinition> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        if (baseMapper.exists(queryWrapper)) {
            return R.failed(Status.CDC_JOB_EXIST_ERROR);
        }
        String jobCreateUser;
        if (StringUtils.isBlank(cdcJobDefinitionDTO.getCreateUser())) {
            int loginId = StpUtil.getLoginIdAsInt();
            UserInfoVO userInfoVo =
                    (UserInfoVO) StpUtil.getSession().get(Integer.toString(loginId));
            jobCreateUser = userInfoVo.getUser().getUsername();
        } else {
            jobCreateUser = cdcJobDefinitionDTO.getCreateUser();
        }
        CdcJobDefinition cdcJobDefinition =
                CdcJobDefinition.builder()
                        .name(cdcJobDefinitionDTO.getName())
                        .config(cdcJobDefinitionDTO.getConfig())
                        .cdcType(cdcJobDefinitionDTO.getCdcType())
                        .createUser(jobCreateUser)
                        .description(cdcJobDefinitionDTO.getDescription())
                        .dataDelay(cdcJobDefinitionDTO.getDataDelay())
                        .build();
        baseMapper.insert(cdcJobDefinition);
        return R.succeed();
    }

    @Override
    public R<Void> deleteById(Integer id) {
        CdcJobLog cdcJobLog = cdcJobLogService.findLast(id);
        if (cdcJobLog == null) {
            removeById(id);
            return R.succeed();
        }
        R<JobStatus> jobStatusR = status(id, cdcJobLog.getId());
        if (isStatusOnIng(jobStatusR.getData())) {
            return R.failed(Status.CDC_JOB_CAN_NOT_DELETE);
        } else {
            removeById(id);
            return R.succeed();
        }
    }

    @Override
    public PageR<CdcJobDefinitionVO> listAll(
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
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        Page<CdcJobDefinition> resPage = baseMapper.selectPage(page, queryWrapper);
        Page<CdcJobDefinitionVO> voPage = toCdcJobDefinitionVOPage(resPage);
        return new PageR<>(voPage.getTotal(), true, voPage.getRecords());
    }

    private Page<CdcJobDefinitionVO> toCdcJobDefinitionVOPage(Page<CdcJobDefinition> resPage) {
        List<CdcJobDefinitionVO> voList =
                resPage.getRecords().stream()
                        .map(
                                item -> {
                                    Integer id = item.getId();
                                    CdcJobLog cdcJobLog = cdcJobLogService.findLast(id);
                                    String jobStatus =
                                            cdcJobLog == null
                                                    ? JobStatus.FRESHED.getValue()
                                                    : cdcJobLog.getCurrentStatus();
                                    return CdcJobDefinitionVO.builder()
                                            .id(id)
                                            .cdcType(item.getCdcType())
                                            .isDelete(item.isDelete())
                                            .name(item.getName())
                                            .config(item.getConfig())
                                            .createTime(item.getCreateTime())
                                            .updateTime(item.getUpdateTime())
                                            .description(item.getDescription())
                                            .createUser(item.getCreateUser())
                                            .dataDelay(item.getDataDelay())
                                            .currentStatus(jobStatus)
                                            .build();
                                })
                        .collect(Collectors.toList());

        Page<CdcJobDefinitionVO> voPage =
                new Page<>(resPage.getCurrent(), resPage.getSize(), resPage.getSize());
        return voPage.setRecords(voList);
    }

    @Override
    public R<Void> update(CdcJobDefinitionDTO cdcJobDefinitionDTO) {
        // to valid config
        CdcGraph.fromCdcGraphJsonString(cdcJobDefinitionDTO.getConfig());
        // to update entity
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
                        .dataDelay(cdcJobDefinitionDTO.getDataDelay())
                        .createUser(cdcJobDefinitionDTO.getCreateUser())
                        .description(cdcJobDefinitionDTO.getDescription())
                        .build();
        cdcJobDefinition.setId(cdcJobDefinitionDTO.getId());
        baseMapper.updateById(cdcJobDefinition);
        return R.succeed();
    }

    /**
     * to submit cdc job to cluster Use ReentrantLock to ensure that submission, cancellation, and
     * status retrieval for the same job are atomic operations.
     *
     * @see CdcJobDefinitionServiceImpl#cancel(Integer)
     * @see CdcJobDefinitionServiceImpl#status(Integer, Integer)
     * @param id cdc job id
     * @param cdcJobSubmitDTO cdc job dto
     * @return action execution result
     */
    @Override
    public R<ActionExecutionResultVo> submit(Integer id, CdcJobSubmitDTO cdcJobSubmitDTO) {
        ReentrantLock lock = cacher.getUnchecked(id);
        if (lock.isLocked()) {
            return R.of(
                    null,
                    Status.CDC_JOB_FLINK_JOB_LOCKED.getCode(),
                    Status.CDC_JOB_FLINK_JOB_LOCKED.getMsg());
        }
        lock.lock();
        try {
            CdcJobDefinition cdcJobDefinition = baseMapper.selectById(id);
            log.info("to submit job {}: {}", cdcJobDefinition.getId(), cdcJobDefinition.getName());
            FlinkCdcSyncType flinkCdcSyncType =
                    FlinkCdcSyncType.valueOf(cdcJobDefinition.getCdcType());
            CdcGraph cdcGraph = CdcGraph.fromCdcGraphJsonString(cdcJobDefinition.getConfig());
            String clusterId = cdcJobSubmitDTO.getClusterId();
            ObjectNode actionConfigs = JSONUtils.createObjectNode();
            handBaseActionConfig(actionConfigs, clusterId, cdcJobDefinition);
            handleCdcGraphNodeData(
                    actionConfigs, cdcGraph.getSource(), flinkCdcSyncType, cdcJobSubmitDTO);
            handleCdcGraphNodeData(
                    actionConfigs, cdcGraph.getTarget(), flinkCdcSyncType, cdcJobSubmitDTO);

            FlinkCdcActionContextFactory factory =
                    ActionContextFactoryServiceLoadUtil.getFlinkCdcActionContextFactory(
                            cdcGraph.getSource().getType(),
                            cdcGraph.getTarget().getType(),
                            flinkCdcSyncType);
            ActionContext actionContext = factory.getActionContext(actionConfigs);

            ActionService actionService = new FlinkCdcActionService();
            ActionExecutionResult result = actionService.execute(actionContext);
            return createCdcJobLog(result, clusterId, id);
        } catch (Exception e) {
            log.error("error while submit job of {} to cluster", id, e);
            return R.failed(Status.UNKNOWN_ERROR);
        } finally {
            lock.unlock();
        }
    }

    /**
     * to cancel cdc job from cluster Use ReentrantLock to ensure that submission, cancellation, and
     * status retrieval for the same job are atomic operations.
     *
     * @see CdcJobDefinitionServiceImpl#submit(Integer, CdcJobSubmitDTO)
     * @see CdcJobDefinitionServiceImpl#status(Integer, Integer)
     * @param id cdc job id
     * @return action execution result
     */
    @Override
    public R<ActionExecutionResultVo> cancel(Integer id) {
        log.info("to cancel job {}", id);
        ReentrantLock lock = cacher.getUnchecked(id);
        if (lock.isLocked()) {
            return R.of(
                    null,
                    Status.CDC_JOB_FLINK_JOB_LOCKED.getCode(),
                    Status.CDC_JOB_FLINK_JOB_LOCKED.getMsg());
        }
        lock.lock();
        try {
            CdcJobLog cdcJobLog = cdcJobLogService.findLast(id);
            String flinkJobId = cdcJobLog.getFlinkJobId();
            if (flinkJobId == null) {
                return R.of(
                        null,
                        Status.CDC_JOB_FLINK_JOB_ID_NOT_EXISTS.getCode(),
                        Status.CDC_JOB_FLINK_JOB_ID_NOT_EXISTS.getMsg());
            }
            // request remote to cancel flink job
            ClusterInfo clusterInfo = clusterService.getById(cdcJobLog.getClusterId());
            SessionClusterClient client =
                    new SessionClusterClient(clusterInfo.getHost(), clusterInfo.getPort());
            TriggerIdEntity triggerIdEntity = client.stopWithSavePoint(flinkJobId);
            if (triggerIdEntity != null) {
                cdcJobLog.setCurrentStatus(JobStatus.CANCELLING.getValue());
                cdcJobLogService.updateById(cdcJobLog);
                ActionExecutionResultVo actionExecutionResultVo =
                        new ActionExecutionResultVo(
                                cdcJobLog.getId(),
                                true,
                                Status.SUCCESS.getMsg(),
                                JobStatus.CANCELLING);
                return R.succeed(actionExecutionResultVo);
            } else {
                throw new RuntimeException("cancel job return null triggerIdEntity");
            }
        } catch (Exception ex) {
            log.error("An exception occurred while cancel job status of {}", id, ex);
            return R.failed(Status.UNKNOWN_ERROR);
        } finally {
            lock.unlock();
        }
    }

    /**
     * to fetch cdc job status from cluster Use ReentrantLock to ensure that submission,
     * cancellation, and status retrieval for the same job are atomic operations.
     *
     * @see CdcJobDefinitionServiceImpl#submit(Integer, CdcJobSubmitDTO)
     * @see CdcJobDefinitionServiceImpl#cancel(Integer)
     * @param id cdc job id
     * @return action execution result
     */
    @Override
    public R<JobStatus> status(Integer id, Integer logId) {
        log.info("to ask status of jobId {}, logId {}", id, logId);
        ReentrantLock lock = cacher.getUnchecked(id);
        if (lock.isLocked()) {
            return R.of(
                    null,
                    Status.CDC_JOB_FLINK_JOB_LOCKED.getCode(),
                    Status.CDC_JOB_FLINK_JOB_LOCKED.getMsg());
        }
        lock.lock();
        CdcJobLog cdcJobLog = cdcJobLogService.getById(logId);
        try {
            ClusterInfo clusterInfo = clusterService.getById(cdcJobLog.getClusterId());
            String flinkJobId = cdcJobLog.getFlinkJobId();
            if (flinkJobId == null) {
                throw new RuntimeException("flink job id is null");
            } else {
                SessionClusterClient client =
                        new SessionClusterClient(clusterInfo.getHost(), clusterInfo.getPort());
                JobOverviewEntity jobOverview = client.jobOverview(cdcJobLog.getFlinkJobId());
                if (jobOverview != null && jobOverview.getState() != null) {
                    JobStatus jobStatus = JobStatus.fromValue(jobOverview.getState());
                    Instant instant = Instant.ofEpochMilli(jobOverview.getEndTime());
                    LocalDateTime terminateTime =
                            LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                    cdcJobLog.setCurrentStatus(jobStatus.getValue());
                    cdcJobLog.setTerminateTime(terminateTime);
                    cdcJobLogService.updateById(cdcJobLog);
                    return R.succeed(jobStatus);
                } else {
                    throw new RuntimeException("find null jobOverview");
                }
            }
        } catch (Exception ex) {
            log.error("An exception occurred while refresh job status of {}-{}", id, logId, ex);
            cdcJobLog.setCurrentStatus(JobStatus.UNKNOWN.getValue());
            cdcJobLogService.updateById(cdcJobLog);
            return R.failed(JobStatus.UNKNOWN);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public R<Integer> copy(Integer id) {
        CdcJobDefinition cdcJobDefinition = baseMapper.selectById(id);
        String createUser = getLoginUser().getUser().getUsername();
        int maxId = getMaxId() + 1;
        CdcJobDefinition entity =
                CdcJobDefinition.builder()
                        .cdcType(cdcJobDefinition.getCdcType())
                        .dataDelay(cdcJobDefinition.getDataDelay())
                        .name(String.format("%s-%s", cdcJobDefinition.getName(), maxId))
                        .config(cdcJobDefinition.getConfig())
                        .description(cdcJobDefinition.getDescription())
                        .createUser(createUser)
                        .build();

        int rowId = baseMapper.insert(entity);
        return R.succeed(rowId);
    }

    private Integer getMaxId() {
        QueryWrapper<CdcJobDefinition> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("MAX(id) as max_id").last(" OR is_delete=1");
        return baseMapper.selectObjs(queryWrapper).stream()
                .map(item -> (Integer) item)
                .findFirst()
                .orElse(0);
    }

    private UserInfoVO getLoginUser() {
        return (UserInfoVO) StpUtil.getSession().get(Integer.toString(StpUtil.getLoginIdAsInt()));
    }

    private R<ActionExecutionResultVo> createCdcJobLog(
            ActionExecutionResult result, String clusterId, Integer id) {
        String createUser = getLoginUser().getUser().getUsername();
        CdcJobLog cdcJobLog;
        JobStatus jobStatus = result.isSuccess() ? JobStatus.SUBMITTING : JobStatus.FAILED;
        String errorMsg = result.isSuccess() ? R.succeed().getMsg() : R.failed().getMsg();
        String flinkJobId = result.getFlinkJobId();
        cdcJobLog =
                CdcJobLog.builder()
                        .clusterId(Integer.parseInt(clusterId))
                        .cdcJobDefinitionId(id)
                        .createUser(createUser)
                        .flinkJobId(flinkJobId)
                        .currentStatus(jobStatus.getValue())
                        .build();
        cdcJobLogService.save(cdcJobLog);
        ActionExecutionResultVo actionExecutionResultVo =
                ActionExecutionResultVo.builder()
                        .jobStatus(jobStatus)
                        .success(result.isSuccess())
                        .errorMsg(errorMsg)
                        .logId(cdcJobLog.getId())
                        .build();
        return R.succeed(actionExecutionResultVo);
    }

    private void handBaseActionConfig(
            ObjectNode actionConfigs, String clusterId, CdcJobDefinition cdcJobDefinition) {
        ClusterInfo clusterInfo = clusterService.getById(clusterId);
        actionConfigs.put(
                FlinkCdcOptions.SESSION_URL,
                String.format("%s:%s", clusterInfo.getHost(), clusterInfo.getPort()));
        actionConfigs.put(FlinkCdcOptions.PIPELINE_NAME, cdcJobDefinition.getName());
        actionConfigs.put(FlinkCdcOptions.EXE_CP_INTERVAL, cdcJobDefinition.getDataDelay());
    }

    private void handleCdcGraphNodeData(
            ObjectNode actionConfigs,
            CdcNode node,
            FlinkCdcSyncType cdcSyncType,
            CdcJobSubmitDTO cdcJobSubmitDTO) {
        FlinkCdcDataSourceType cdcDataSourceType = FlinkCdcDataSourceType.of(node.getType());
        Preconditions.checkNotNull(
                cdcDataSourceType,
                String.format("CDC datasource type should not be null.", node.getType()));
        switch (cdcDataSourceType) {
            case PAIMON:
                handlePaimonNodeData(actionConfigs, node.getData(), cdcSyncType);
                break;
            case MYSQL:
                handleMysqlNodeData(actionConfigs, node.getData(), cdcSyncType, cdcJobSubmitDTO);
                break;
            case POSTGRESQL:
                handlePostgresNodeData(actionConfigs, node.getData());
                break;
        }
    }

    private List<String> getByKeyToList(ObjectNode node, String key) {
        String stringValue = JSONUtils.getString(node, key);
        return Optional.of(stringValue)
                .map(item -> new ArrayList<>(Arrays.asList(item.split(";"))))
                .orElse(new ArrayList<>());
    }

    private void handlePostgresNodeData(ObjectNode actionConfigs, ObjectNode postgresData) {
        List<String> computedColumnList =
                getByKeyToList(postgresData, FlinkCdcOptions.COMPUTED_COLUMN);
        actionConfigs.putPOJO(FlinkCdcOptions.COMPUTED_COLUMN, computedColumnList);
        actionConfigs.put(
                FlinkCdcOptions.METADATA_COLUMN,
                JSONUtils.getString(postgresData, FlinkCdcOptions.METADATA_COLUMN));
        actionConfigs.put(
                FlinkCdcOptions.TYPE_MAPPING, JSONUtils.getString(postgresData, "type_mapping"));

        List<String> postgresConfList = getByKeyToList(postgresData, "other_configs");
        postgresConfList.add(
                buildKeyValueString("hostname", JSONUtils.getString(postgresData, "host")));
        postgresConfList.add(
                buildKeyValueString("port", JSONUtils.getString(postgresData, "port")));
        postgresConfList.add(
                buildKeyValueString("username", JSONUtils.getString(postgresData, "username")));
        postgresConfList.add(
                buildKeyValueString("password", JSONUtils.getString(postgresData, "password")));
        postgresConfList.add(
                buildKeyValueString(
                        "database-name", JSONUtils.getString(postgresData, "database")));
        postgresConfList.add(
                buildKeyValueString(
                        "schema-name", JSONUtils.getString(postgresData, "schema_name")));
        postgresConfList.add(
                buildKeyValueString("table-name", JSONUtils.getString(postgresData, "table_name")));
        postgresConfList.add(
                buildKeyValueString("slot.name", JSONUtils.getString(postgresData, "slot_name")));
        actionConfigs.putPOJO(FlinkCdcOptions.POSTGRES_CONF, postgresConfList);
    }

    private void handleMysqlNodeData(
            ObjectNode actionConfigs,
            ObjectNode mysqlData,
            FlinkCdcSyncType cdcSyncType,
            CdcJobSubmitDTO cdcJobSubmitDTO) {
        List<String> computedColumnList =
                getByKeyToList(mysqlData, FlinkCdcOptions.COMPUTED_COLUMN);
        actionConfigs.putPOJO(FlinkCdcOptions.COMPUTED_COLUMN, computedColumnList);
        actionConfigs.put(
                FlinkCdcOptions.METADATA_COLUMN,
                JSONUtils.getString(mysqlData, FlinkCdcOptions.METADATA_COLUMN));
        String typeMapping = String.join(",", JSONUtils.getList(mysqlData, "type_mapping"));
        actionConfigs.put(FlinkCdcOptions.TYPE_MAPPING, typeMapping);

        List<String> mysqlConfList = getByKeyToList(mysqlData, "other_configs");
        mysqlConfList.add(buildKeyValueString("hostname", JSONUtils.getString(mysqlData, "host")));
        mysqlConfList.add(
                buildKeyValueString("username", JSONUtils.getString(mysqlData, "username")));
        mysqlConfList.add(buildKeyValueString("port", JSONUtils.getString(mysqlData, "port")));
        mysqlConfList.add(
                buildKeyValueString("database-name", JSONUtils.getString(mysqlData, "database")));
        if (cdcSyncType == FlinkCdcSyncType.SINGLE_TABLE_SYNC) {
            mysqlConfList.add(
                    buildKeyValueString(
                            "table-name", JSONUtils.getString(mysqlData, "table_name")));
        }
        mysqlConfList.add(
                buildKeyValueString("password", JSONUtils.getString(mysqlData, "password")));

        SyncMode syncMode = SyncMode.valueOf(cdcJobSubmitDTO.getStartupMode());
        switch (syncMode) {
            case INCREMENTAL_SYNC:
                mysqlConfList.add(buildKeyValueString("scan.startup.mode", "latest-offset"));
                break;
            case FULL_SYNC:
                mysqlConfList.add(buildKeyValueString("scan.startup.mode", "initial"));
                break;
            case TS_SYNC:
                mysqlConfList.add(buildKeyValueString("scan.startup.mode", "timestamp"));
                mysqlConfList.add(
                        buildKeyValueString(
                                "scan.startup.timestamp-millis",
                                String.valueOf(cdcJobSubmitDTO.getStartupTimestamp())));
                break;
        }

        actionConfigs.putPOJO(FlinkCdcOptions.MYSQL_CONF, mysqlConfList);
    }

    private void handlePaimonNodeData(
            ObjectNode actionConfigs, ObjectNode paimonData, FlinkCdcSyncType cdcSyncType) {
        actionConfigs.put(
                FlinkCdcOptions.PARTITION_KEYS,
                JSONUtils.getString(paimonData, "partition_column"));

        Integer catalog = JSONUtils.getInteger(paimonData, "catalog");
        CatalogInfo catalogInfo = catalogService.getById(catalog);
        actionConfigs.put(FlinkCdcOptions.WAREHOUSE, catalogInfo.getWarehouse());
        if (cdcSyncType == FlinkCdcSyncType.SINGLE_TABLE_SYNC) {
            actionConfigs.put(FlinkCdcOptions.TABLE, JSONUtils.getString(paimonData, "table_name"));
        }
        actionConfigs.put(FlinkCdcOptions.DATABASE, JSONUtils.getString(paimonData, "database"));
        List<String> configList = getByKeyToList(paimonData, "other_configs2");
        actionConfigs.putPOJO(FlinkCdcOptions.TABLE_CONF, configList);

        List<String> catalogConfList = new ArrayList<>();
        Map<String, String> options = catalogInfo.getOptions();
        PaimonServiceFactory.convertToPaimonOptions(options)
                .toMap()
                .forEach((k, v) -> catalogConfList.add(buildKeyValueString(k, v)));

        actionConfigs.putPOJO(FlinkCdcOptions.CATALOG_CONF, catalogConfList);
    }

    private String buildKeyValueString(String key, String value) {
        return key + "=" + value;
    }

    /** Crontab to check cdc job status. */
    @Scheduled(cron = "0 * * * * ?")
    public R<Void> checkAllStatus() {
        QueryWrapper<CdcJobDefinition> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id");
        List<Integer> cdcJobDefinitionIdList = listObjs(queryWrapper, item -> (Integer) item);
        cdcJobDefinitionIdList.forEach(
                id -> {
                    CdcJobLog cdcJobLog = cdcJobLogService.findLast(id);
                    Optional.of(cdcJobLog)
                            .ifPresent(
                                    item -> {
                                        if (isStatusOnIng(
                                                JobStatus.fromValue(
                                                        cdcJobLog.getCurrentStatus()))) {
                                            status(id, cdcJobLog.getId());
                                        }
                                    });
                });
        return R.succeed();
    }

    private boolean isStatusOnIng(JobStatus jobStatus) {
        return (JobStatus.CANCELLING == jobStatus
                || JobStatus.SUBMITTING == jobStatus
                || JobStatus.RESTARTING == jobStatus
                || JobStatus.RUNNING == jobStatus);
    }
}
