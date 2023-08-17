package org.apache.paimon.flink.gateway.rest.yarn.proxy;

public class FlinkRest {
    public static String getJobOverview(String applicationId) {
        return String.format("/proxy/%s/jobs/overview", applicationId);
    }
}
