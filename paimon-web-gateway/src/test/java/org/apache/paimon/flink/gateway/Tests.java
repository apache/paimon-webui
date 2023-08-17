package org.apache.paimon.flink.gateway;

import org.apache.paimon.flink.gateway.yarn.YarnApplicationGateway;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.GlobalConfiguration;
import org.junit.jupiter.api.Test;

public class Tests {
    @Test
    public void yarnApplicationTest() throws ClassNotFoundException {
        Configuration configuration =
                GlobalConfiguration.loadConfiguration("D:/soft/develop/flink/conf");

        //        JarClassLoader jarClassLoader = JarClassLoader.load(new
        // File("D:/soft/develop/flink/flink-1.17.1/lib"));
        //        Thread.currentThread().setContextClassLoader(jarClassLoader);
        //        Class<?> aClass =
        // jarClassLoader.loadClass("org.apache.paimon.flink.gateway.yarn.YarnApplicationGateway");
        //        Object o = ReflectUtil.newInstance(aClass, configuration);
        //        ReflectUtil.invoke(o, "deployCluster");
//        new YarnApplicationGateway(configuration).deployCluster();
        System.out.println();
    }
}
