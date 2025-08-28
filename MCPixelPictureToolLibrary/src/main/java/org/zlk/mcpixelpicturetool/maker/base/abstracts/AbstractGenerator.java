package org.zlk.mcpixelpicturetool.maker.base.abstracts;

import org.zlk.mcpixelpicturetool.maker.base.ability.Clearable;
import org.zlk.mcpixelpicturetool.maker.base.interfaces.Exporter;
import org.zlk.mcpixelpicturetool.maker.base.interfaces.Generator;
import org.zlk.mcpixelpicturetool.maker.base.interfaces.Loader;

import java.util.Collection;
import java.util.HashMap;

public abstract class AbstractGenerator implements Generator {

    private final String name;
    protected final HashMap<String, Loader<?,?>> loaders = new HashMap<>();
    protected final HashMap<String, Exporter<?,?>> exporters = new HashMap<>();
    protected boolean generated = false;

    protected AbstractGenerator(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void addLoader(Loader<?, ?> loader) {
        loaders.put(loader.getName(),loader);
    }

    @Override
    public void addExporter(Exporter<?,?> exporter) {
        exporters.put(exporter.getName(),exporter);
    }

    @Override
    public Loader<?, ?> getLoader(String name) {
        return loaders.get(name);
    }

    @Override
    public Exporter<?, ?> getExporter(String name) {
        return exporters.get(name);
    }

    @Override
    public Collection<Loader<?, ?>> getAllLoader() {
        return loaders.values();
    }

    @Override
    public Collection<Exporter<?, ?>> getAllExporter() {
        return exporters.values();
    }

    @Override
    public abstract void generate();

    @Override
    public boolean isGenerated() {
        return generated;
    }

    @Override
    public void clear() {
        generated = false;
    }

    public void clearAll() {
        clear();
        getAllLoader().forEach(Clearable::clear);
        getAllExporter().forEach(Clearable::clear);
    }
}
