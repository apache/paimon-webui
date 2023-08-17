package org.apache.paimon.flink.gateway;

import cn.hutool.core.lang.JarClassLoader;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CustomClassLoader extends JarClassLoader {
    /** 存放java文件对应的字节码，findClass之前存入，defineClass之后移除 */
    public static Map<String, byte[]> CLASS_BYTES_TEMP = new ConcurrentHashMap();

    /** MyClassLoader单例模式：DCL方式 */
    private static volatile CustomClassLoader instance;

    private CustomClassLoader() {}

    public static CustomClassLoader getInstance() {
        if (null == instance) {
            synchronized (CustomClassLoader.class) {
                if (null == instance) {
                    instance = new CustomClassLoader();
                }
            }
        }
        return instance;
    }

    /** 重置classloader，返回新的classLoader */
    public static synchronized void createNewClassLoader() {
        instance = new CustomClassLoader();
    }

    /** 编译java文件为字节码数据，并存入缓存中，供后续使用 */
    public void compiler(String name, byte[] byteClazz) {
        CLASS_BYTES_TEMP.put(name, byteClazz);
    }

    /** 重写findClass，自定义ClassLoader */
    @Override
    public Class<?> findClass(String packageName) {
        try {
            log.info(" loadClass [{}] from findClass ！", packageName);
            byte[] byteClazz;
            // 从本地缓存获取byte字节码信息
            byteClazz = CLASS_BYTES_TEMP.get(packageName);
            if (byteClazz != null && byteClazz.length > 10) {
                return defineClass(packageName, byteClazz, 0, byteClazz.length);
            }
            return null;
        } finally {
            // 从本地缓存中移除字节码信息
            CLASS_BYTES_TEMP.remove(packageName);
        }
    }
}
