package org.apache.paimon.flink.gateway.yarn;

import cn.hutool.core.collection.CollUtil;
import org.apache.flink.yarn.configuration.YarnLogConfigUtil;
import org.apache.paimon.flink.gateway.BaseGateway;

import org.apache.paimon.web.common.data.FlinkActionParam;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.yarn.YarnClientYarnClusterInformationRetriever;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.flink.yarn.configuration.YarnDeploymentTarget;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.paimon.flink.gateway.result.FlinkRun;

public abstract class YarnGateway extends BaseGateway {
    ClusterSpecification clusterSpecification =
            createClusterSpecificationBuilder().createClusterSpecification();
    YarnClient yarnClient = YarnClient.createYarnClient();
    YarnConfiguration yarnConfiguration = new YarnConfiguration();

   protected void init(){
       Configuration configuration = flinkActionParam.getConfiguration();
       configuration.set(YarnConfigOptions.APPLICATION_TYPE, "Paimon Flink");
       configuration.set(DeploymentOptions.TARGET, YarnDeploymentTarget.APPLICATION.getName());
       configuration.set(PipelineOptions.JARS, flinkActionParam.getJars());
       YarnLogConfigUtil.setLogConfigFileInConfig(configuration, flinkActionParam.getFlinkConfDir());
       configuration.set(YarnConfigOptions.SHIP_FILES, CollUtil.newArrayList(flinkActionParam.getHadoopConfDir()+"/yarn-site.xml"));

   }

    protected YarnClusterDescriptor getYarnClusterDescriptor() {
        return new YarnClusterDescriptor(
                flinkActionParam.getConfiguration(),
                yarnConfiguration,
                yarnClient,
                YarnClientYarnClusterInformationRetriever.create(yarnClient),
                true);
    }

    public YarnGateway(FlinkActionParam flinkActionParam) {
        super(flinkActionParam);
    }

    public abstract FlinkRun deployCluster();
}
