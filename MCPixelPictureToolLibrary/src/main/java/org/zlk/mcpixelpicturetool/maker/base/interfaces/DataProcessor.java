package org.zlk.mcpixelpicturetool.maker.base.interfaces;

@FunctionalInterface
public interface DataProcessor<D> {
    void process(D data) throws Exception;
}
