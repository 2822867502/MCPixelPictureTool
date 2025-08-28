package org.zlk.mcpixelpicturetool.maker.base.abstracts;

import org.zlk.mcpixelpicturetool.maker.base.ability.Clearable;
import org.zlk.mcpixelpicturetool.maker.base.ability.GetNameable;
import org.zlk.mcpixelpicturetool.maker.base.interfaces.Processor;
import org.zlk.mcpixelpicturetool.maker.base.type.Context;

public abstract class ProcessorNode implements Processor, GetNameable, Clearable {
    protected ProcessorNode beforeNode;
    protected ProcessorNode afterNode;
    protected final Processor processor;
    private final String name;

    public ProcessorNode(String name, ProcessorNode before, Processor processor) {
        this.name = name == null ? "Unnamed Processor" : name;
        this.processor = processor;
        if (before != null) before.setAfterNode(this);
        this.beforeNode = before;
    }

    public ProcessorNode(ProcessorNode before, Processor processor) {
        this(null, before, processor);
    }

    public ProcessorNode(String name, Processor processor) {
        this(name, null, processor);
    }

    public ProcessorNode(Processor processor) {
        this((String) null, processor);
    }

    public ProcessorNode(String name) {
        this(name, null);
    }

    @Override
    public String getName() {
        return name;
    }

    public ProcessorNode getBeforeNode() {
        return beforeNode;
    }

    public ProcessorNode getAfterNode() {
        return afterNode;
    }

    public void setBeforeNode(ProcessorNode beforeNode) {
        this.beforeNode = beforeNode;
        if (beforeNode.afterNode != this) beforeNode.setAfterNode(this);
    }

    public void setAfterNode(ProcessorNode afterNode) {
        this.afterNode = afterNode;
        if (afterNode.beforeNode != this) afterNode.setBeforeNode(this);
    }

    @Override
    public void process(Context context) throws Exception {
        if (processor != null) processor.process(context);
    }

    @Override
    public abstract void clear();
}
