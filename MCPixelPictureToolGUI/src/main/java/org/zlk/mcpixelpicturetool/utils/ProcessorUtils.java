package org.zlk.mcpixelpicturetool.utils;

import org.zlk.mcpixelpicturetool.maker.ProcessorManager;

import java.util.concurrent.Executors;

public class ProcessorUtils {
    public static final ProcessorManager PROCESSOR_MANAGER = new ProcessorManager();

    static {
        updateExecutorService();
        PROCESSOR_MANAGER.setExceptionHandler(ProcessorUtils::handleException);
    }

    public static void updateExecutorService() {
        //todo 从配置文件加载
        PROCESSOR_MANAGER.setExecutorService(Executors.newCachedThreadPool());
    }
    private static void handleException(Exception e) {
        //warn 仅供测试
        e.printStackTrace();
    }
}
