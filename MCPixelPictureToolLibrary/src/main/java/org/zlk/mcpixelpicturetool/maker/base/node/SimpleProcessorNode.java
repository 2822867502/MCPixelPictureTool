package org.zlk.mcpixelpicturetool.maker.base.node;

import org.zlk.mcpixelpicturetool.maker.base.abstracts.ProcessorNode;
import org.zlk.mcpixelpicturetool.maker.base.interfaces.Processor;

public class SimpleProcessorNode extends ProcessorNode {


    public SimpleProcessorNode(String name, ProcessorNode before, Processor processor) {
        super(name, before, processor);
    }

    public SimpleProcessorNode(ProcessorNode before, Processor processor) {
        super(before, processor);
    }

    public SimpleProcessorNode(String name, Processor processor) {
        super(name, processor);
    }

    public SimpleProcessorNode(Processor processor) {
        super(processor);
    }

    //todo 我觉得应该加个注解，实现不重写这个方法就异常，不然我这个蠢材会忘
    @Override
    public void clear() {
        //do nothing
    }
}
