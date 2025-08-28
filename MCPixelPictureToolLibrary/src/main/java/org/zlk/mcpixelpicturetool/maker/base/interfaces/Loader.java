package org.zlk.mcpixelpicturetool.maker.base.interfaces;

import org.zlk.mcpixelpicturetool.maker.base.ability.Clearable;
import org.zlk.mcpixelpicturetool.maker.base.ability.GetNameable;
import org.zlk.mcpixelpicturetool.maker.base.ability.InputAble;
import org.zlk.mcpixelpicturetool.maker.base.ability.OutputAble;

import java.io.IOException;

public interface Loader<I,O> extends GetNameable, Clearable, InputAble<I>, OutputAble<O>{
    void load() throws IOException;
    boolean isLoaded();
}
