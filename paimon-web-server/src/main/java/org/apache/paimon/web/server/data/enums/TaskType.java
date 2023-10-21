package org.apache.paimon.web.server.data.enums;

import org.apache.paimon.web.flink.task.FlinkTask;
import org.apache.paimon.web.flink.task.SparkTask;
import org.apache.paimon.web.task.SubmitTask;

public enum TaskType {
    SPARK() {
        @Override
        public SubmitTask getTask() {
            return new SparkTask();
        }
    },
    FLINK() {
        @Override
        public SubmitTask getTask() {
            return new FlinkTask();
        }
    };

    public abstract SubmitTask getTask();
}
