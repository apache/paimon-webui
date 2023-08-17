package org.apache.paimon.web.server.data.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "flink-env")
@Getter
@Setter
public class FlinkEnv {
    private String paimonActionJarPath;
    private String flinkConfPath;
    private String hdfsFlinkLibPath;
    private String hadoopConfPath;
    private String flinkLibPath;
}
