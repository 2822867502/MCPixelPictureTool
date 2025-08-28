package org.zlk.mcpixelpicturetool.maker.base.interfaces;

import org.zlk.mcpixelpicturetool.maker.base.type.Context;

@FunctionalInterface
public interface Processor extends DataProcessor<Context>{
    void process(Context context) throws Exception;
}
