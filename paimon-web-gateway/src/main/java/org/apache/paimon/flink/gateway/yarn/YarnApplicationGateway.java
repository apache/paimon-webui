package org.apache.paimon.flink.gateway.yarn;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import org.apache.paimon.web.common.data.FlinkActionParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.paimon.flink.gateway.rest.yarn.proxy.FlinkRest;
import org.apache.paimon.flink.gateway.result.FlinkRun;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
public class YarnApplicationGateway extends YarnGateway {
    private final List<String> HADOOP_CONF_FILE =
            CollUtil.newArrayList(
                    YarnConfiguration.YARN_SITE_CONFIGURATION_FILE,
                    YarnConfiguration.CORE_SITE_CONFIGURATION_FILE,
                    "hdfs-site.xml");

    public YarnApplicationGateway(FlinkActionParam flinkActionParam) {
        super(flinkActionParam);
    }

    @Override
    public FlinkRun deployCluster() {
        init();
        initYarnClient();
        FlinkRun flinkRun = new FlinkRun();
        ApplicationConfiguration applicationConfiguration =
                new ApplicationConfiguration(
                        flinkActionParam.getActionParams(),
                        "org.apache.paimon.flink.action.FlinkActions");
        try {
            ClusterClientProvider<ApplicationId> clusterClientProvider =
                    getYarnClusterDescriptor().deployApplicationCluster(clusterSpecification, applicationConfiguration);
            ClusterClient<ApplicationId> clusterClient = clusterClientProvider.getClusterClient();
            String applicationId = clusterClient.getClusterId().toString();
            String webInterfaceURL = clusterClient.getWebInterfaceURL();
            String yarnRmAddress = yarnConfiguration.get(YarnConfiguration.RM_ADDRESS);
            clusterClient.listJobs().whenCompleteAsync((jobStatusMessages, throwable) -> {
                if (CollUtil.isNotEmpty(jobStatusMessages)) {
                    flinkActionParam.getHandlerJobId().accept(CollUtil.get(jobStatusMessages, 0).getJobId().toHexString());
                } else {
                    HttpUtil.createGet(yarnConfiguration.get(YarnConfiguration.RM_WEBAPP_ADDRESS) + FlinkRest.getJobOverview(applicationId)).then(x -> {
                        JSONArray jobs = JSONUtil.parseObj(x.body()).getJSONArray("jobs");
                        flinkActionParam.getHandlerJobId().accept(jobs.getJSONObject(0).getStr("jid"));
                    });
                }
            });
            flinkRun.setApplicationId(applicationId);
            flinkRun.setWebInterfaceURL(webInterfaceURL);
            flinkRun.setRmProxyWebURL(yarnRmAddress);
            flinkRun.setSuccess(true);

        } catch (Exception e) {
            flinkRun.setSuccess(false);
            flinkRun.setLog(ExceptionUtil.getRootCauseMessage(e));
        }
        return flinkRun;
    }

    protected void initYarnClient() {
        String hadoopConfDir = flinkActionParam.getHadoopConfDir();
        List<File> findFile =
                FileUtil.loopFiles(
                        hadoopConfDir, file -> HADOOP_CONF_FILE.contains(file.getName()));
        log.debug("find hadoop config file:{}", findFile);
        findFile.forEach(file -> yarnConfiguration.addResource(new Path(file.getAbsolutePath())));
        yarnClient.init(yarnConfiguration);
        yarnClient.start();
    }

    public boolean killCluster(String applicationId) {
        initYarnClient();
        try {
            yarnClient.killApplication(ApplicationId.fromString(applicationId));
            return true;
        } catch (YarnException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
