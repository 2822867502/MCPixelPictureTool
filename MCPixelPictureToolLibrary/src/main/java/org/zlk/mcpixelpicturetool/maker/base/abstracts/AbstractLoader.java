package org.zlk.mcpixelpicturetool.maker.base.abstracts;

import org.zlk.mcpixelpicturetool.maker.base.interfaces.Generator;
import org.zlk.mcpixelpicturetool.maker.base.interfaces.Loader;

import java.io.IOException;
import java.util.Objects;

public abstract class AbstractLoader<I, O> implements Loader<I, O> {

    private final String name;
    protected I input;
    protected O output;
    protected Generator generator;
    public AbstractLoader(String name,Generator generator) {
        this.name = name;
        this.generator = generator;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public abstract void load() throws IOException;


    @Override
    public void clear() {
        this.output = null;
    }

    public void clearAll() {
        clear();
        if (generator.isGenerated()) generator.clear();
    }

    @Override
    public boolean isLoaded() {
        return output != null;
    }

    @Override
    public void setInput(I input) {
        this.input = input;
        clear();
    }

    @Override
    public O getOutput() {
        return Objects.requireNonNull(output,name + ": Please load first");
    }
}
