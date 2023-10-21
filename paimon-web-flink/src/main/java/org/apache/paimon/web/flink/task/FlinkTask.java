package org.apache.paimon.web.flink.task;

import org.apache.paimon.web.common.data.vo.SubmitResult;
import org.apache.paimon.web.task.SubmitTask;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.types.Row;
import org.apache.flink.util.CloseableIterator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FlinkTask implements SubmitTask {
    private static final StreamExecutionEnvironment env =
            StreamExecutionEnvironment.getExecutionEnvironment();
    private static final TableEnvironment tEnv = TableEnvironment.create(new Configuration());
    private static final String nullColumn = "";

    public void executeDDL(String statement) throws Exception {
        tEnv.executeSql(statement);
    }

    @Override
    public SubmitResult execute(String statement) throws Exception {
        TableResult tableResult = tEnv.executeSql(statement);
        try (CloseableIterator<Row> it = tableResult.collect()) {
            List<String> columns = tableResult.getResolvedSchema().getColumnNames();
            List<Map<String, Object>> rows = new ArrayList<>();
            while (it.hasNext()) {
                Map<String, Object> map = new LinkedHashMap<>();
                Row row = it.next();
                for (int i = 0; i < row.getArity(); ++i) {
                    Object field = row.getField(i);
                    if (field == null) {
                        map.put(columns.get(i), nullColumn);
                    } else {
                        map.put(columns.get(i), field.toString());
                    }
                }
                rows.add(map);
            }
            return SubmitResult.builder().data(rows).build();
        }
    }

    @Override
    public boolean stop(String statement) throws Exception {
        return false;
    }
}
