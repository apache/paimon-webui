package org.apache.paimon.web.flink.task;

import org.apache.paimon.web.common.data.vo.SubmitResult;
import org.apache.paimon.web.task.SubmitTask;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructField;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SparkTask implements SubmitTask {
    private static final SparkSession sparkSeesion =
            SparkSession.builder().master("local[*]").getOrCreate();

    @Override
    public SubmitResult execute(String statement) throws Exception {
        List<Row> rows = sparkSeesion.sql(statement).collectAsList();
        List<Map<String, Object>> result = new ArrayList<>();
        rows.stream()
                .forEach(
                        row -> {
                            Map<String, Object> map = new LinkedHashMap<>();
                            row.schema()
                                    .foreach(
                                            (StructField field) -> {
                                                map.put(
                                                        field.name(),
                                                        row.getAs(field.name()).toString());
                                                return field;
                                            });
                            result.add(map);
                        });
        return SubmitResult.builder().data(result).build();
    }

    @Override
    public boolean stop(String statement) throws Exception {
        return false;
    }
}
