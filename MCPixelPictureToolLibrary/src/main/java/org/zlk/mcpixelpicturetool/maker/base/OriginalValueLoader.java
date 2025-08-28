package org.zlk.mcpixelpicturetool.maker.base;

import org.zlk.mcpixelpicturetool.maker.base.abstracts.AbstractLoader;
import org.zlk.mcpixelpicturetool.maker.base.interfaces.Generator;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;

public class OriginalValueLoader<V> extends AbstractLoader<V,V> {
    public OriginalValueLoader(String name, Generator generator) {
        super(name,generator);
    }

    @Override
    public void load() throws IOException {
        output = input;
    }
}
