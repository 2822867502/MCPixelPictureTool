package org.zlk.mcpixelpicturetool.maker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.zlk.mcpixelpicturetool.maker.base.type.Context;
import org.zlk.mcpixelpicturetool.maker.base.node.ListProcessorNode;
import org.zlk.mcpixelpicturetool.maker.base.node.SimpleProcessorNode;
import org.zlk.mcpixelpicturetool.maker.base.node.VirtualListProcessorNode;
import org.zlk.mcpixelpicturetool.maker.base.abstracts.ProcessorNode;
import org.zlk.mcpixelpicturetool.maker.base.interfaces.DataProcessor;
import org.zlk.mcpixelpicturetool.maker.base.interfaces.ExceptionHandler;
import org.zlk.mcpixelpicturetool.maker.base.interfaces.Processor;
import org.zlk.mcpixelpicturetool.maker.base.type.UseThreadCalculator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ProcessorManagerTest {

    // 基础处理器：增加计数器
    static class CounterProcessor implements Processor {
        private final AtomicInteger counter;
        private final int increment;

        CounterProcessor(AtomicInteger counter, int increment) {
            this.counter = counter;
            this.increment = increment;
        }

        @Override
        public void process(Context context) {
            counter.addAndGet(increment);
        }
    }

    // 上下文写入处理器
    static class ContextWriter implements Processor {
        private final String key;
        private final Object value;

        ContextWriter(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public void process(Context context) {
            context.put(key, value);
        }
    }

    // 延迟处理器（用于测试异步）
    static class DelayProcessor implements Processor {
        private final long delayMs;

        DelayProcessor(long delayMs) {
            this.delayMs = delayMs;
        }

        @Override
        public void process(Context context) throws InterruptedException {
            Thread.sleep(delayMs);
        }
    }

    // 异常处理器
    static class ErrorProcessor implements Processor {
        @Override
        public void process(Context context) throws Exception {
            throw new RuntimeException("Test error");
        }
    }

    // 自定义异常处理器（收集异常）
    static class CollectingExceptionHandler implements ExceptionHandler {
        private Exception lastException;

        @Override
        public void handle(Exception e) {
            this.lastException = e;
        }
    }
    void waitUntilEnd(ProcessorManager manager) throws InterruptedException {
        while (!manager.isAllFree()) Thread.sleep(0);
    }
    // 测试1：单处理器链执行
    @Test
    @Timeout(10)
    void testSingleChain() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);

        ProcessorManager manager = new ProcessorManager();
        ProcessorNode node = new SimpleProcessorNode("test", new CounterProcessor(counter, 5));
        manager.addHeadNode(node);

        manager.processAwait("test");

        assertEquals(5, counter.get());
        assertArrayEquals(new String[]{"test"}, manager.getProcessing()); // 验证状态重置
    }

    // 测试2：链式处理器顺序执行
    @Test
    @Timeout(10)
    void testChainedProcessors() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        Context context = new Context();

        // 构建链: A -> B -> C
        ProcessorNode nodeC = new SimpleProcessorNode("C", new CounterProcessor(counter, 3));
        ProcessorNode nodeB = new SimpleProcessorNode("B", nodeC, new ContextWriter("step", "B"));
        ProcessorNode nodeA = new SimpleProcessorNode("A", nodeB, new CounterProcessor(counter, 2));

        ProcessorManager manager = new ProcessorManager(
                Executors.newSingleThreadExecutor(),
                new CollectingExceptionHandler()
        );
        manager.addHeadNode(nodeC,context);
        manager.processAwait("C");

        // 验证执行顺序和结果
        assertEquals(5, counter.get()); // 2+3
        assertEquals("B", context.get("step")); // 最后执行的写入
        assertArrayEquals(new String[]{"A"}, manager.getProcessing());
    }

    // 测试3：ListProcessorNode并行处理
    @Test
    @Timeout(10) // 超时保护
    void testListProcessorNode() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        int processorCount = 100;
        Processor[] processors = new Processor[processorCount];

        // 创建100个增加1的处理器
        for (int i = 0; i < processorCount; i++) {
            processors[i] = new CounterProcessor(counter, 1);
        }

        // 使用4线程并行处理
        ExecutorService executor = Executors.newFixedThreadPool(4);
        ListProcessorNode listNode = new ListProcessorNode(
                "listNode",
                null,
                processors,
                executor,
                new CollectingExceptionHandler(),
                new UseThreadCalculator(25,UseThreadCalculator.SET_PER_THREAD_TASK)// 每个任务处理25个处理器

        );

        ProcessorManager manager = new ProcessorManager();
        manager.addHeadNode(listNode);
        manager.processAwait("listNode");


        assertEquals(processorCount, counter.get());
        executor.shutdown();
    }

    // 测试4：VirtualListProcessor动态处理
    @Test
    @Timeout(10) // 超时保护
    void testVirtualListProcessor() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        CollectingExceptionHandler exceptionHandler = new CollectingExceptionHandler();

        // 创建数据处理器
        DataProcessor<Integer> dataProcessor = counter::addAndGet;
        VirtualListProcessorNode<Integer> virtualNode = new VirtualListProcessorNode<>(
                "virtualNode",
                dataProcessor
        );
        virtualNode.setExceptionHandler(exceptionHandler);

        // 设置输入数据
        Integer[] inputs = {1, 2, 3, 4, 5};
        virtualNode.setInput(inputs);

        ProcessorManager manager = new ProcessorManager(
                Executors.newSingleThreadExecutor(),
                exceptionHandler
        );
        manager.addHeadNode(virtualNode);
        manager.processAwait("virtualNode");

        assertEquals(15, counter.get()); // 1+2+3+4+5
    }

    // 测试5：异常处理机制
    @Test
    void testExceptionHandling() throws InterruptedException {
        CollectingExceptionHandler exceptionHandler = new CollectingExceptionHandler();

        // 正常处理器 -> 异常处理器 -> 正常处理器
        AtomicInteger counter = new AtomicInteger(0);
        ProcessorNode nodeC = new SimpleProcessorNode("C", new CounterProcessor(counter, 10));
        ProcessorNode nodeB = new SimpleProcessorNode("B", nodeC, new ErrorProcessor());
        ProcessorNode nodeA = new SimpleProcessorNode("A", nodeB, new CounterProcessor(counter, 5));

        ProcessorManager manager = new ProcessorManager(
                Executors.newSingleThreadExecutor(),
                exceptionHandler
        );
        manager.addHeadNode(nodeC);
        manager.processAwait("C");

        // 验证：第一个处理器执行，异常处理器中断链
        assertEquals(10, counter.get());
        assertNotNull(exceptionHandler.lastException);
        assertEquals("Test error", exceptionHandler.lastException.getMessage());
    }
}