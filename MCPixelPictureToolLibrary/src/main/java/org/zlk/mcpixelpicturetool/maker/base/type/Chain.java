package org.zlk.mcpixelpicturetool.maker.base.type;

import org.zlk.mcpixelpicturetool.maker.ProcessorManager;
import org.zlk.mcpixelpicturetool.maker.base.ability.Clearable;
import org.zlk.mcpixelpicturetool.maker.base.ability.GetNameable;
import org.zlk.mcpixelpicturetool.maker.base.abstracts.ProcessorNode;
import org.zlk.mcpixelpicturetool.maker.base.interfaces.DataProcessor;
import org.zlk.mcpixelpicturetool.maker.base.interfaces.Processor;
import org.zlk.mcpixelpicturetool.maker.base.node.ListProcessorNode;
import org.zlk.mcpixelpicturetool.maker.base.node.SimpleProcessorNode;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

public class Chain implements GetNameable, Clearable {
    public static final int STATE_FREE = 0;
    public static final int STATE_PROCESSING = 1;
    private final String name;
    private final ProcessorNode head;
    private final Context context;
    private int state;
    private String processing = null;
    private final ArrayList<Runnable> processChangeListeners = new ArrayList<>();

    public Chain(String name, ProcessorNode head, Context context) {
        this.name = name;
        this.head = head;
        this.context = context;
    }

    public Chain(ProcessorNode head, Context context) {
        this(head.getName(), head, context);//没有名字就用头节点的名字
    }

    public Chain(ProcessorNode head) {
        this(head, new Context());//没有上下文就新建一个
    }

    public void chainProcess(ProcessorManager manager, boolean isSync, boolean useManagerExecutor, boolean useManagerExceptionHandler) {
        state = STATE_PROCESSING;
        Executor executor = isSync ? manager.getExecutorService() : Runnable::run;
        executor.execute(() -> {
            try {
                forEachNode((node) -> {
                    processing = node.getName();
                    processChangeListeners.stream().filter(Objects::nonNull).forEach(Runnable::run);

                    if (useManagerExecutor && node instanceof ListProcessorNode) {
                        ((ListProcessorNode) node).setExecutor(manager.getExecutorService());
                    }
                    if (useManagerExceptionHandler && node instanceof ListProcessorNode) {
                        ((ListProcessorNode) node).setExceptionHandler(manager.getExceptionHandler());
                    }

                    node.process(context);
                });
            } catch (Exception e) {
                manager.getExceptionHandler().handle(e);
            }
        });
    }

    private void forEachNode(DataProcessor<ProcessorNode> processor) throws Exception {
            ProcessorNode node = head;
            while (node != null) {
                processor.process(node);
                node = node.getAfterNode();
            }
    }
    public static ProcessorNode createProcessorChainHead(Processor... processors) {
        if (processors.length == 0) return null;
        ProcessorNode node = createProcessor(processors[0]);
        for (int i = 1; i < processors.length; i++) {
            createProcessor(processors[i]).setBeforeNode(createProcessor(processors[i - 1]));
        }
        return node;
    }

    private static ProcessorNode createProcessor(Processor processor) {
        if (processor instanceof ProcessorNode) {
            return (ProcessorNode) processor;
        } else {
            return new SimpleProcessorNode(processor);
        }
    }

    public Object addContext(String key, Object value) {
        return context.put(key, value);
    }

    public void addContexts(Map<? extends String, ?> t) {
        context.putAll(t);
    }

    public int getState() {
        return state;
    }

    public String getProcessing() {
        return processing;
    }

    @Override
    public String getName() {
        return name;
    }

    public synchronized void addProcessChangeListener(Runnable listener) {
        processChangeListeners.add(listener);
    }

    public synchronized void removeProcessChangeListener(Runnable listener) {
        processChangeListeners.remove(listener);
    }

    @Override
    public void clear() {
        try {
            forEachNode(ProcessorNode::clear);
        } catch (Exception e) {
            throw new RuntimeException(e);
            //todo 或许这里可以改改？
        }
    }
}
