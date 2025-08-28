package org.zlk.mcpixelpicturetool.maker;

import org.zlk.mcpixelpicturetool.maker.base.ability.Clearable;
import org.zlk.mcpixelpicturetool.maker.base.abstracts.ProcessorNode;
import org.zlk.mcpixelpicturetool.maker.base.interfaces.ExceptionHandler;
import org.zlk.mcpixelpicturetool.maker.base.type.Chain;
import org.zlk.mcpixelpicturetool.maker.base.type.Context;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProcessorManager implements Closeable, Clearable {
    private ExecutorService executorService;
    private ExceptionHandler exceptionHandler;
    private final Hashtable<String, Chain> chains;

    public ProcessorManager(ExecutorService executorService, ExceptionHandler exceptionHandler) {
        this.executorService = executorService == null ? Executors.newSingleThreadExecutor() : executorService;
        this.exceptionHandler = exceptionHandler == null ? (e) -> {
            throw new RuntimeException(e);
        } : exceptionHandler;
        this.chains = new Hashtable<>();
    }

    public ProcessorManager() {
        this(null, null);
    }

    public void addChain(Chain chain) {
        chains.put(chain.getName(), chain);
    }

    public void addHeadNode(ProcessorNode processorNode, Context context) {
        addChain(new Chain(processorNode, context));
    }

    public void addHeadNode(ProcessorNode processorNode) {
        addChain(new Chain(processorNode));
    }

    public void addHeadNodes(Collection<? extends ProcessorNode> c) {
        c.forEach(this::addHeadNode);
    }

    public Object addContext(String chainName, String key, Object value) {
        return chains.get(chainName).addContext(key, value);
    }

    public void addContexts(String chainName, Map<? extends String, ?> t) {
        chains.get(chainName).addContexts(t);
    }

    public void addProcessChangeListener(String chainName, Runnable listener) {
        chains.get(chainName).addProcessChangeListener(listener);
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService == null ? Executors.newSingleThreadExecutor() : executorService;
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler == null ? (e) -> {
            throw new RuntimeException(e);
        } : exceptionHandler;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public String getProcessing(String name) {
        return chains.get(name).getProcessing();
    }

    public boolean isFree(String name) {
        return chains.get(name).getState() == Chain.STATE_FREE;
    }

    public String[] getProcessing() {
        return chains.keySet().stream().map(this::getProcessing).toArray(String[]::new);
    }

    public boolean isAllFree() {
        for (String s : chains.keySet()) {
            if (!isFree(s)) return false;
        }
        return true;
    }

    public void process(String name, boolean isSync,boolean useManagerExecutor,boolean useManagerExceptionHandler) throws InterruptedException {
        chains.get(name).chainProcess(this,isSync,useManagerExecutor,useManagerExceptionHandler);
    }

    public void processAwait(String name,boolean useManagerExecutor,boolean useManagerExceptionHandler) {
        try {
            process(name, false,useManagerExecutor,useManagerExceptionHandler);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void processAwait(String name) {
        processAwait(name,false,false);
    }

    public void processAsync(String name,boolean useManagerExecutor,boolean useManagerExceptionHandler) {
        try {
            process(name, true,useManagerExecutor,useManagerExceptionHandler);
        } catch (InterruptedException e) {
            throw new Error("A really unexpected error,the exception except not to throw forever", e);

        }
    }
    public void processAsync(String name) {
        processAsync(name,false,false);
    }

    public synchronized void processAll() {
        chains.keySet().forEach(this::processAsync);
    }

    @Override
    public void close() throws IOException {
        executorService.shutdown();
    }

    public void clear(String name) {
        chains.get(name);
    }
    @Override
    public void clear() {
        chains.keySet().forEach(this::clear);
    }
}
