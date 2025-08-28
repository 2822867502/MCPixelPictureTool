package org.zlk.mcpixelpicturetool.maker.base.abstracts;

import org.zlk.mcpixelpicturetool.maker.base.interfaces.Exporter;
import org.zlk.mcpixelpicturetool.maker.base.interfaces.Generator;

import java.io.IOException;
import java.util.Objects;

public abstract class AbstractExporter<I,O> implements Exporter<I,O> {
    private final String name;
    protected I input;
    protected O output;
    protected Generator generator;

    protected AbstractExporter(String name, Generator generator) {
        this.name = name;
        this.generator = generator;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public abstract void export() throws IOException;


    @Override
    public boolean isExported() {
        return output != null;
    }

    @Override
    public void clear() {
        this.output = null;
    }
    public void clearAll() {
        clear();
        if (generator.isGenerated()) generator.clear();
    }

    @Override
    public void setInput(I input) {
        this.input = input;
//        clear();
//        事实上讲，Exporter的input改变不代表需要重新生成，因此此处不能clear
    }

    @Override
    public O getOutput() {
        return Objects.requireNonNull(output,name + ": Please export first");
    }
}
