package org.apache.paimon.web.server;

import cn.hutool.core.lang.JarClassLoader;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.paimon.flink.action.Action;
import org.apache.paimon.flink.action.FlinkActions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class ActionTests {
    @Test
    public void test1() throws Exception {
        JarClassLoader jarClassLoader = new JarClassLoader();
            jarClassLoader.addJar(new File("D:/soft/develop/flink/flink-1.16.2/lib"));
        String[] args = Arrays.stream(StrUtil.splitToArray("mysql-sync-table \n" +
                "--warehouse hdfs:///save \n" +
                "    --database cdc-test \n" +
                "    --table cdc_test1 \n" +
                "    --primary-keys id \n" +
                "    --mysql-conf hostname=121.5.136.161 \n" +
                "    --mysql-conf port=3371 \n" +
                "    --mysql-conf username=root \n" +
                "    --mysql-conf password=dinky \n" +
                "    --mysql-conf database-name=cdc-test \n" +
                "    --mysql-conf table-name=table_1\n" +
                "    --table-conf bucket=4 \n" +
                "    --table-conf changelog-producer=input \n" +
                "    --table-conf sink.parallelism=4", "\n")).map(String::trim).flatMap(x -> Stream.of(StrUtil.splitToArray(x, " "))).toArray(String[]::new);
        Thread.currentThread().setContextClassLoader(jarClassLoader);
        Optional<Action> action = Action.Factory.create(args);
        action.get().run();


    }
}
