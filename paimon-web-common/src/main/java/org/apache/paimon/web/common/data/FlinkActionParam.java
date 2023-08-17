package org.apache.paimon.web.common.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.flink.configuration.Configuration;

import java.util.List;
import java.util.function.Consumer;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlinkActionParam {
    Configuration configuration;
    String[] actionParams;
    String hadoopConfDir;
    String flinkConfDir;
    List<String> jars;
    Consumer<String> handlerJobId;
}
