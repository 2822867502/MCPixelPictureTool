package org.zlk.mcpixelpicturetool.maker.base.interfaces;

import org.zlk.mcpixelpicturetool.maker.base.ability.Clearable;
import org.zlk.mcpixelpicturetool.maker.base.ability.GetNameable;

import java.io.IOException;
import java.util.Collection;

public interface Generator extends GetNameable, Clearable {
    void addLoader(Loader<?,?> loader);
    void addExporter(Exporter<?,?> exporter);
    Loader<?,?> getLoader(String name);
    Collection<Loader<?,?>> getAllLoader();
    default void loadAll() throws IOException {
        for (Loader<?, ?> loader : getAllLoader()) {
            loader.load();
        }
    }
    Exporter<?,?> getExporter(String name);
    Collection<Exporter<?,?>> getAllExporter();
    default void exportAll() throws IOException {
        for (Exporter<?,?> exporter : getAllExporter()) {
            exporter.export();
        }
    }
    void generate();
    boolean isGenerated();
}
