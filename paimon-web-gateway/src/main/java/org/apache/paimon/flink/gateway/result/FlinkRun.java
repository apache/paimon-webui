package org.apache.paimon.flink.gateway.result;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlinkRun {
    private Boolean success;
    private String ApplicationId;
    private String webInterfaceURL;
    private String rmProxyWebURL;
    private String log;
}
