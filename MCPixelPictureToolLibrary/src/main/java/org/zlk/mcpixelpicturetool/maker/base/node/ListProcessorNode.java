package org.zlk.mcpixelpicturetool.maker.base.node;

import org.zlk.mcpixelpicturetool.maker.base.abstracts.ProcessorNode;
import org.zlk.mcpixelpicturetool.maker.base.interfaces.ExceptionHandler;
import org.zlk.mcpixelpicturetool.maker.base.interfaces.Processor;
import org.zlk.mcpixelpicturetool.maker.base.type.Context;
import org.zlk.mcpixelpicturetool.maker.base.type.UseThreadCalculator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

public class ListProcessorNode extends ProcessorNode {
    protected final Vector<Processor> processors;
    protected Executor executor;
    protected UseThreadCalculator calculator;
    protected ExceptionHandler handler;

    public ListProcessorNode(String name, ProcessorNode before, Processor[] processors, Executor executor, ExceptionHandler handler, UseThreadCalculator calculator) {
        super(name, before, null);
        this.processors = processors == null ? new Vector<>() : new Vector<>(Arrays.asList(processors));
        setExecutor(executor);
        setCalculator(calculator);
        setExceptionHandler(handler);
    }

    public ListProcessorNode(String name, ProcessorNode before, Processor[] processors) {
        this(name, before, processors,null,null,null);
    }

    public ListProcessorNode(ProcessorNode before, Processor[] processors) {
        this(null,before,processors);
    }
    public ListProcessorNode(String name,Processor[] processors) {
        this(name,null,processors);
    }

    public void setExecutor(Executor executor) {
        this.executor = executor != null ? executor : Runnable::run;
    }

    public void setCalculator(UseThreadCalculator calculator) {
        this.calculator = calculator != null ?
                calculator : new UseThreadCalculator(1, UseThreadCalculator.SET_PER_THREAD_TASK);
    }

    public void setExceptionHandler(ExceptionHandler handler) {
        this.handler = handler != null ?
                handler : e -> {
            throw new RuntimeException("Unhandled exception", e);
        };
    }

    @Override
    public void process(Context context) throws Exception {
        Processor[] processorsArray;
        synchronized (processors) {
            processorsArray = processors.toArray(new Processor[0]);
        }
        calculator.setTaskCount(processorsArray.length);
        int useThread = calculator.getUseThread();
        CountDownLatch count = new CountDownLatch(useThread);
        for (int i = 0; i < useThread; i++) {//举个例子 共70个Processor 每次处理32 则useThread = 2 + 1
            int finalI = calculator.getStartIndex(i);//第一次取0 * 32 第二次取1 * 32 第三次取2 * 64
            int finalEnd = Math.min(calculator.getEndIndex(i), calculator.getTaskCount());
            executor.execute(() -> {
                for (int j = finalI; j < finalEnd; j++) {
                    //j 第一次从0到31 第二次从32到63 第三次从64到69
                    processPer(processorsArray[j], context);
                }
                count.countDown();
            });
        }
        count.await();
    }

    private void processPer(Processor processor, Context context) {
        try {
            processor.process(context);
        } catch (Exception e) {
            handler.handle(e);
        }
    }

    public boolean add(Processor processor) {
        synchronized (processors) {
            return processors.add(processor);
        }
    }

    public boolean addAll(Collection<? extends Processor> c) {
        synchronized (processors) {
            return processors.addAll(c);
        }
    }

    //todo 我觉得应该加个注解，实现不重写这个方法就异常，不然我这个蠢材会忘
    @Override
    public void clear() {
        processors.clear();
    }
}
