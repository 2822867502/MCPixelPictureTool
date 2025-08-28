package org.zlk.mcpixelpicturetool.maker.base.node;

import org.zlk.mcpixelpicturetool.maker.base.ability.InputAble;
import org.zlk.mcpixelpicturetool.maker.base.abstracts.ProcessorNode;
import org.zlk.mcpixelpicturetool.maker.base.interfaces.DataProcessor;
import org.zlk.mcpixelpicturetool.maker.base.interfaces.ExceptionHandler;
import org.zlk.mcpixelpicturetool.maker.base.interfaces.Processor;
import org.zlk.mcpixelpicturetool.maker.base.type.UseThreadCalculator;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class VirtualListProcessorNode<D> extends ListProcessorNode implements InputAble<D[]> {
    private DataProcessor<D> dataProcessor;

    public VirtualListProcessorNode(String name, ProcessorNode before, DataProcessor<D> dataProcessor, Executor executor, ExceptionHandler handler, UseThreadCalculator calculator) {
        super(name, before, null, executor, handler, calculator);
        this.dataProcessor = dataProcessor;
    }

    public VirtualListProcessorNode(String name, ProcessorNode before, DataProcessor<D> dataProcessor) {
        super(name, before, null);
        this.dataProcessor = dataProcessor;
    }

    public VirtualListProcessorNode(ProcessorNode before,  DataProcessor<D> dataProcessor) {
        super(before, null);
        this.dataProcessor = dataProcessor;
    }

    public VirtualListProcessorNode(String name, DataProcessor<D> dataProcessor) {
        super(name, null);
        this.dataProcessor = dataProcessor;
    }

    public void setDataProcessor(DataProcessor<D> dataProcessor) {
        this.dataProcessor = dataProcessor;
    }

    @Override
    public void setInput(D[] input) {
        processors.clear();
        processors.addAll(
                Arrays.stream(input)
                        .map((d -> (Processor) context -> dataProcessor.process(d)))
                        .collect(Collectors.toList()));
    }

}
