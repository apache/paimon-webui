package org.apache.paimon.web.server.service.impl;

import org.apache.paimon.flink.action.Action;
import org.apache.paimon.flink.gateway.result.FlinkRun;
import org.apache.paimon.flink.gateway.yarn.YarnApplicationGateway;
import org.apache.paimon.web.common.data.FlinkActionParam;
import org.apache.paimon.web.server.data.dto.PageDto;
import org.apache.paimon.web.server.data.enums.FlinkType;
import org.apache.paimon.web.server.data.enums.SubmitStatus;
import org.apache.paimon.web.server.data.model.CdcManage;
import org.apache.paimon.web.server.data.properties.FlinkEnv;
import org.apache.paimon.web.server.mapper.CdcManageMapper;
import org.apache.paimon.web.server.service.CdcManageService;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.JarClassLoader;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.GlobalConfiguration;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class CdcMangeServiceImpl extends ServiceImpl<CdcManageMapper, CdcManage>
        implements CdcManageService {
    final FlinkEnv flinkEnv;

    public List<CdcManage> queryById(Long id, int num, boolean isAsc) {
        LambdaQueryChainWrapper<CdcManage> wrapper =
                lambdaQuery()
                        .func(
                                x ->
                                        (isAsc
                                                        ? x.gt(CdcManage::getId, id)
                                                        : x.lt(CdcManage::getId, id))
                                                .last(" limit " + num));
        return list(wrapper);
    }

    @Override
    public Page<CdcManage> queryTable(PageDto<Void> pageDto) {
        return page(new Page<>(pageDto.getCurrentPage(), pageDto.getPageSize()));
    }

    @Override
    public CdcManage run(Long id) {
        CdcManage cdcManage = getById(id);
        Assert.notNull(cdcManage, "mission is not exists");
        Assert.notBlank(cdcManage.getRunArgs(), "run args is null");
        Assert.isFalse(
                cdcManage.getSubmitStatus().equals(SubmitStatus.PROCESSING),
                "mission is processing");
        String[] actionParams = strToArgs(cdcManage.getRunArgs());
        Optional<Action> action = Action.Factory.create(actionParams);
        Assert.isTrue(
                action.isPresent(),
                "action is not exists,Please check the parameters and troubleshoot through the system log");
        cdcManage.setSubmitStatus(SubmitStatus.PROCESSING);
        cdcManage.setMissionStatus(SubmitStatus.PROCESSING);
        updateById(cdcManage);
        if (cdcManage.getType().equals(FlinkType.YARN_APPLICATION)) {
            Assert.notBlank(flinkEnv.getHadoopConfPath(), "hadoop conf path is null");
            Assert.notBlank(flinkEnv.getHdfsFlinkLibPath(), "hdfs flink lib path is null");
            Configuration configuration =
                    GlobalConfiguration.loadConfiguration(flinkEnv.getFlinkConfPath());

            configuration.setString("yarn.provided.lib.dirs", flinkEnv.getHdfsFlinkLibPath());

            JarClassLoader jarClassLoader = new JarClassLoader();
            //            jarClassLoader.addJar(new File("D:/soft/develop/flink/flink-1.16.2/lib"));
            Thread.currentThread().setContextClassLoader(jarClassLoader);
            Class<?> clazz = null;
            try {
                clazz =
                        jarClassLoader.loadClass(
                                "org.apache.paimon.flink.gateway.yarn.YarnApplicationGateway");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            FlinkActionParam flinkActionParam =
                    FlinkActionParam.builder()
                            .jars(ListUtil.list(false, flinkEnv.getPaimonActionJarPath()))
                            .actionParams(actionParams)
                            .configuration(configuration)
                            .hadoopConfDir(flinkEnv.getHadoopConfPath())
                            .flinkConfDir(flinkEnv.getFlinkConfPath())
                            .handlerJobId(
                                    jobId -> {
                                        cdcManage.setJobId(jobId);
                                        cdcManage.setMissionStatus(SubmitStatus.RUNNING);
                                        updateById(cdcManage);
                                    })
                            .build();
            Object o = ReflectUtil.newInstance(clazz, flinkActionParam);
            FlinkRun run = ReflectUtil.invoke(o, "deployCluster");
            if (run.getSuccess()) {
                cdcManage.setSubmitStatus(SubmitStatus.SUCCESS);
                cdcManage.setMissionStatus(SubmitStatus.PROCESSING);
                cdcManage.setApplicationId(run.getApplicationId());
                cdcManage.setYarnAddr(run.getRmProxyWebURL());
                cdcManage.setWebAddr(run.getWebInterfaceURL());
                cdcManage.setRunLog("");
            } else {
                cdcManage.setSubmitStatus(SubmitStatus.SUCCESS);
                cdcManage.setMissionStatus(SubmitStatus.FAILED);
                cdcManage.setRunLog(run.getLog());
            }
            updateById(cdcManage);
        }
        return cdcManage;
    }

    @Override
    public CdcManage stop(Long id) {
        CdcManage cdcManage = getById(id);
        if (cdcManage.getMissionStatus().equals(SubmitStatus.RUNNING)) {
            FlinkActionParam flinkActionParam =
                    FlinkActionParam.builder()
                            .hadoopConfDir(flinkEnv.getHadoopConfPath())
                            .configuration(
                                    GlobalConfiguration.loadConfiguration(
                                            flinkEnv.getFlinkConfPath()))
                            .flinkConfDir(flinkEnv.getFlinkConfPath())
                            .build();
            boolean isKill =
                    new YarnApplicationGateway(flinkActionParam)
                            .killCluster(cdcManage.getApplicationId());
            cdcManage.setMissionStatus(isKill ? SubmitStatus.SUCCESS : SubmitStatus.RUNNING);
            cdcManage.setSubmitStatus(isKill ? SubmitStatus.NEVER : SubmitStatus.SUCCESS);
            updateById(cdcManage);
        }
        return cdcManage;
    }

    private static String[] strToArgs(String args) {
        return StrUtil.split(args, "\n").stream()
                .map(String::trim)
                .flatMap(x -> Stream.of(StrUtil.splitToArray(x, " ")))
                .toArray(String[]::new);
    }
}
