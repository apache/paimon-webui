package org.apache.paimon.web.flink.executor;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.paimon.web.common.executor.Executor;
import org.apache.paimon.web.common.executor.ExecutorFactory;

public class FlinkExecutorFactory implements ExecutorFactory {
    @Override
    public Executor createExecutor() {
        return null;
    }

    private EnvironmentSettings getEnvironmentSettings(RuntimeExecutionMode mode) {
        return mode == RuntimeExecutionMode.BATCH
                ? EnvironmentSettings.newInstance().inBatchMode().build()
                : EnvironmentSettings.newInstance().inStreamingMode().build();
    }
}
