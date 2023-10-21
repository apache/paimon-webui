package org.apache.paimon.web.spark.task;

import org.apache.paimon.web.common.data.vo.SubmitResult;
import org.apache.paimon.web.flink.task.SparkTask;

import org.junit.jupiter.api.Test;

public class SparkTaskTests {
    @Test
    public void taskExecutorTest() throws Exception {
        SparkTask sparkTask = new SparkTask();
        SubmitResult execute =
                sparkTask.execute(
                        "SELECT order_id, price FROM (VALUES (1, 2.0), (2, 3.1))  AS t (order_id, price)\n");
        System.out.println(execute);
    }
}
