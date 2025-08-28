package org.zlk.mcpixelpicturetool.maker.base.abstracts;

import org.zlk.mcpixelpicturetool.maker.base.interfaces.Generator;

public abstract class NullOutputExporter<I> extends AbstractExporter<I,Object> {
    protected NullOutputExporter(String name, Generator generator) {
        super(name,generator);
    }

    @Override
    public Object getOutput() {
        return null;
    }
}
