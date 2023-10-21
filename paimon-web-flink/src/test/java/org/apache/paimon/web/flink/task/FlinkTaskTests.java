package org.apache.paimon.web.flink.task;

import org.apache.paimon.web.common.data.vo.SubmitResult;

import org.junit.Test;

public class FlinkTaskTests {
    @Test
    public void taskExecutorTest() throws Exception {
        FlinkTask flinkTask = new FlinkTask();
        SubmitResult execute =
                flinkTask.execute(
                        "SELECT order_id, price FROM (VALUES (1, 2.0), (2, 3.1))  AS t (order_id, price)\n");
        System.out.println(execute);
    }
}
